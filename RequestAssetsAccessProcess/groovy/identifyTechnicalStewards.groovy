import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest
import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.model.instance.Relation
import com.collibra.dgc.core.api.model.instance.Responsibility
import com.collibra.dgc.core.api.model.user.User
import com.collibra.dgc.core.api.dto.instance.responsibility.FindResponsibilitiesRequest
import com.collibra.dgc.core.api.model.ResourceType
import com.collibra.catalog.api.component.businessmodel.dto.FindDataElementsRequest


USER_LIMIT = 1000
TECHNICAL_STEWARD_ROLE_ID = '00000000-0000-0000-0000-000000005038'
OWNER_ROLE_ID = '00000000-0000-0000-0000-000000005040'
ASSET_IS_ESSENTIAL_FOR_DATA_USAGE = '00000000-0000-0000-0000-000000007061'
META_DATA_SET = '00000000-0000-0000-0001-000400000001'

def findTechStewardForDataSet(Asset sourceDataSet, Set validators) {
  for (Asset col : findDataElements(sourceDataSet.id)) {
    List validator = findTechnicalStewardsForColumn(col)
    if (!validator.isEmpty()) {
      for (User u : validator) {
        validators.add(u.getUserName())
		loggerApi.info('found validator for data elements, ' + u.getUserName())
      }
    } else {
      loggerApi.warn('couldn\'t find a suitable technical steward for usage request on data set ' + sourceDataSet.getName())
    }
  }
}

def findDataElements(dataSetId) {
  return dataSetApi.findDataElements(FindDataElementsRequest.builder()
    .dataSetId(dataSetId)
	.limit(Integer.MAX_VALUE)
    .build()).results
}

def findTechnicalStewardsForColumn(Asset column) {
  List responsibilities = findResponsibilitiesForRoleOnResourceWithHierarchy(string2Uuid(TECHNICAL_STEWARD_ROLE_ID), column)
  List validators = getUsersFromResponsibilities(responsibilities, USER_LIMIT)
  if (validators.isEmpty()) {
    responsibilities = findResponsibilitiesForRoleOnResourceWithHierarchy(string2Uuid(OWNER_ROLE_ID), column)
    validators = getUsersFromResponsibilities(responsibilities, USER_LIMIT)
  }
  return validators
}

def getUsersFromResponsibilities(responsibilities, limit) {
  result = []
  for (responsibility in responsibilities) {
    result.addAll(getUsersFromResponsibility(responsibility, limit))
    if (result.size >= limit) {
      return result.take(limit)
    }
  }
  return result
}

def getUsersFromResponsibility(responsibility, limit) {
  if (ResourceType.User == responsibility.owner.resourceType) {
    return [userApi.getUser(responsibility.owner.id)]
  } else if (ResourceType.UserGroup == responsibility.owner.resourceType) {
    return userApi.findUsers(FindUsersRequest.builder()
      .groupId(responsibility.owner.id)
      .limit(limit)
      .build()).getResults()
  }
  return []
}

def hasParentDataSetType(Asset asset) {
  string2Uuid(META_DATA_SET) == asset.type.id ||
  assetTypeApi.findParentTypes(asset.type.id).any { string2Uuid(META_DATA_SET) == it.id }
}

List findResponsibilitiesForRoleOnResourceWithHierarchy(UUID roleId, Asset asset) {
  return responsibilityApi.findResponsibilities(FindResponsibilitiesRequest.builder()
    .resourceIds([asset.id])
    .includeInherited(true)
    .roleIds([roleId])
    .limit(Integer.MAX_VALUE)
    .build())
    .getResults()
}

List findResponsibilitiesForRoleOnResource(UUID roleId, Asset asset) {
  return responsibilityApi.findResponsibilities(FindResponsibilitiesRequest.builder()
    .resourceIds([asset.id])
    .includeInherited(true)
    .roleIds([roleId])
    .limit(Integer.MAX_VALUE)
    .build())
    .getResults()
}

String prefix = 'scripttask, identifyTechnicalStewards, '
loggerApi.info(prefix + 'started')

loggerApi.info(prefix + 'find the Tech stewards for Data elements assoicated with the Data set in the basket')

List relations = relationApi.findRelations(FindRelationsRequest.builder()
  .targetId(item.id)
  .relationTypeId(string2Uuid(ASSET_IS_ESSENTIAL_FOR_DATA_USAGE))
  .limit(Integer.MAX_VALUE)
  .build()).getResults()

Set names = new HashSet()

relations.each { relation ->
  Asset sourceAsset = assetApi.getAsset(relation.getSource().getId())
  // find tech stewards only for data sets
  if (hasParentDataSetType(sourceAsset)) {
    findTechStewardForDataSet(sourceAsset, names)
  }
}

loggerApi.info(prefix + 'find the Tech stewards for reports')

Set reportIds = execution.getVariable('reportsAssetList')

reportIds.each { reportId ->
  Asset report = assetApi.getAsset(reportId)
  List responsibilities = findResponsibilitiesForRoleOnResource(string2Uuid(TECHNICAL_STEWARD_ROLE_ID), report)
  List validators = getUsersFromResponsibilities(responsibilities, USER_LIMIT)
  if (validators.isEmpty()) {
    responsibilities = findResponsibilitiesForRoleOnResource(string2Uuid(OWNER_ROLE_ID), report)
    validators = getUsersFromResponsibilities(responsibilities, USER_LIMIT)
  }
  if (!validators.isEmpty()) {
    for (User u : validators) {
      //validators.add(u.getUserName())
	  names.add(u.getUserName())
	  loggerApi.info('found validator for report, ' + u.getUserName())
    }
  } else {
    loggerApi.warn('couldn\'t find a suitable technical steward for the report')
  }
}
  

execution.setVariable('techStewards', names)
execution.setVariable('allAccessGranted', false)
execution.setVariable('grantedAccessCounter', 0)

loggerApi.info(prefix + 'ended')
