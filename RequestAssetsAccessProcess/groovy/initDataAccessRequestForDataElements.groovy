import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest
import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.model.instance.Relation
import com.collibra.dgc.core.api.model.user.User
import com.collibra.dgc.core.exceptions.DGCException
import com.collibra.catalog.api.component.businessmodel.dto.FindDataElementsRequest
import com.collibra.dgc.core.api.dto.instance.responsibility.FindResponsibilitiesRequest
import com.collibra.dgc.core.api.model.ResourceType

ASSET_IS_ESSENTIAL_FOR_DATA_USAGE = '00000000-0000-0000-0000-000000007061'
META_DATA_SET = '00000000-0000-0000-0001-000400000001'
OWNER_ROLE_ID = '00000000-0000-0000-0000-000000005040'

String prefix = 'scripttask, initDataAccessRequestForDataElements, '
loggerApi.info(prefix + 'started')

List relations = relationApi.findRelations(FindRelationsRequest.builder()
  .targetId(item.id)
  .relationTypeId(string2Uuid(ASSET_IS_ESSENTIAL_FOR_DATA_USAGE))
  .build()).getResults()

Set validators = new HashSet()
for (Relation relation : relations) {
  // the data set is the source of the relation
  Asset sourceDataSet = assetApi.getAsset(relation.source.id)
  if (sourceDataSet != null && hasParentDataSetType(sourceDataSet)) {
    validators.addAll(findValidatorsForDataset(sourceDataSet))
  }
}

// has to be double quotes here
List asUserExpression = validators.collect {"user(${it})"}
execution.setVariable('approverUserExpression', utility.toCsv(asUserExpression))

loggerApi.info('data element approver list=' + asUserExpression.join(','))
loggerApi.info(prefix + 'ended')

def findValidatorsForDataset(Asset sourceDataSet) {
  def validators = []
  for (Asset col : findDataElements(sourceDataSet.id)) {
    def responsibilities = responsibilityApi.findResponsibilities(FindResponsibilitiesRequest.builder()
	  .roleIds([string2Uuid(OWNER_ROLE_ID)])
	  .resourceIds([col.id])
	  .limit(Integer.MAX_VALUE)
	  .build()).getResults()
	def owners = responsibilities.collect {getUserFromResponsibility(it)}.findAll()
	// is it possible the data elements are not columns ?
    //User validator = columnApi.findValidatorForColumn(col.id)
    //if (validator == null) {
	if (owners == null) {
      loggerApi.error('EXCEPTION, couldn\'t find a suitable approver for data access request on data set \'' + sourceDataSet.getName() + '\' (no steward on asset and no creator id)')
	  String errorDescription = '<p>There is no suitable approver</p><p>Please click X button to quit the current workflow</p><p>Please contact the system administrator for this issue</p>'
	  def dgcError = new DGCException(errorDescription)
	  dgcError.setTitleCode('ERROR: no suitable approver for the data access')
	  throw dgcError
    }
	//loggerApi.info('validator=' + validator.getUserName() + ' for column ' + col.id)
    //validators.add(validator.getUserName())
	for (User owner : owners) {
	  loggerApi.info('owner=' + owner.getUserName() + ' for column ' + col.getName() + ', ' + col.id)
	  validators.add(owner.getUserName())
	}
  }
  return validators
}

def hasParentDataSetType(Asset asset) {
  string2Uuid(META_DATA_SET) == asset.type.id ||
  assetTypeApi.findParentTypes(asset.type.id).any { string2Uuid(META_DATA_SET) == it.id }
}

def findDataElements(dataSetId) {
  return dataSetApi.findDataElements(FindDataElementsRequest.builder()
    .dataSetId(dataSetId)
	.limit(Integer.MAX_VALUE)
    .build()).results
}

def getUserFromResponsibility(responsibility) {
  if (ResourceType.User == responsibility.owner.resourceType) {
    return userApi.getUser(responsibility.owner.id)
  } else if (ResourceType.UserGroup == responsibility.owner.resourceType) {
    return userApi.findUsers(FindUsersRequest.builder()
  	  .groupId(responsibility.owner.id)
	  .limit(1)
	  .build()).getResults()
	  .find()
  }
  return null
}
