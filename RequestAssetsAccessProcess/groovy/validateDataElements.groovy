import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest
import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.model.instance.Relation
import com.collibra.dgc.core.api.model.user.User
import com.collibra.catalog.api.component.businessmodel.dto.FindDataElementsRequest
import com.collibra.dgc.core.api.dto.instance.responsibility.FindResponsibilitiesRequest

String prefix = 'scripttask, validateDataElements, '
loggerApi.info(prefix + 'started')

ASSET_IS_ESSENTIAL_FOR_DATA_USAGE = '00000000-0000-0000-0000-000000007061'
META_DATA_SET = '00000000-0000-0000-0001-000400000001'

BUSINESS_DATA_STEWARD_ROLE_ID = '7942c273-5edf-4919-aba1-6a3ef3e31549'

boolean valid = false
Asset access = assetApi.getAsset(item.id)
Set dataSets = execution.getVariable('dataSetsAssetsList')

// check if the dataset has a business data steward here
valid = hasBusinessDataSteward(dataSets.first())
if (!valid) {
  execution.setVariable('reasonForInvalidDataElements', 'workflowNoBusinessDataStewardForDataSet')
  loggerApi.info(prefix + 'workflowNoBusinessDataStewardForDataSet')
} else {
  List columns = getColumns(access)
  valid = checkDataElements(columns)
  if (!valid) {
    execution.setVariable('reasonForInvalidDataElements', 'workflowNoBusinessDataStewardForColumns')
    loggerApi.info(prefix + 'workflowNoBusinessDataStewardForColumns')
  }
}
execution.setVariable('validDataElements', valid)

loggerApi.info(prefix + 'validDataElement=' + valid)
loggerApi.info(prefix + 'ended')


def getColumns(Asset access) {
  List columns = []
  // access is a Data Asset asset, which holds relations to Data Sets
  List relations = relationApi.findRelations(FindRelationsRequest.builder()
    .targetId(access.id)
    .relationTypeId(string2Uuid(ASSET_IS_ESSENTIAL_FOR_DATA_USAGE))
    .limit(Integer.MAX_VALUE)
    .build()).getResults()
  for (Relation relation : relations) {
    // the data set is the source of the relation
    Asset sourceDataSet = assetApi.getAsset(relation.source.id)
    if (hasParentDataSetType(sourceDataSet)) {
      columns.addAll(findDataElements(sourceDataSet.id))
    }
  }
  return columns
}

def findDataElements(dataSetId) {
  return dataSetApi.findDataElements(FindDataElementsRequest.builder()
    .dataSetId(dataSetId)
	.limit(Integer.MAX_VALUE)
    .build()).results
}

def hasParentDataSetType(Asset asset) {
  string2Uuid(META_DATA_SET) == asset.type.id ||
  assetTypeApi.findParentTypes(asset.type.id).any { string2Uuid(META_DATA_SET) == it.id }
}

private boolean checkDataElements(List columns) {
  // check if each column has a business data steward here
  for (Asset col : columns) {
    loggerApi.info('finding business data steward for ' + col.getId())
	if (!hasBusinessDataSteward(col.getId())) {
      loggerApi.info('NOT found')	
	  return false
	}
  }
  loggerApi.info('ALL found')
  return true
}

def hasBusinessDataSteward(id) {
  def responsibilities = responsibilityApi.findResponsibilities(FindResponsibilitiesRequest.builder()
	.roleIds([string2Uuid(BUSINESS_DATA_STEWARD_ROLE_ID)])
	.resourceIds([id])
	.includeInherited(false)
	.limit(Integer.MAX_VALUE)
	.build()).getResults()
  if (responsibilities.size() > 0) {
	  return true
  } else {
	  return false
  }
}
