import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.dto.meta.assettype.FindAssetTypesRequest
import com.collibra.dgc.core.api.dto.meta.assettype.FindSubAssetTypesRequest
import com.collibra.dgc.core.api.dto.instance.responsibility.FindResponsibilitiesRequest
import com.collibra.dgc.core.exceptions.DGCException
import com.collibra.dgc.core.api.model.user.User
import com.collibra.dgc.core.api.dto.user.FindUsersRequest
import com.collibra.dgc.core.api.model.usergroup.UserGroup
import com.collibra.dgc.core.api.dto.usergroup.FindUserGroupsRequest
import com.collibra.dgc.core.api.dto.MatchMode
import com.collibra.catalog.api.component.businessmodel.dto.FindDataElementsRequest

def findResponsibilityByHierarchy(dataAssetId, roleId) {
  Set assetResponsibilities = []
  Set domainResponsibilities = []
  Set communityResponsibilities = []
  
  List responsibilities = responsibilityApi.findResponsibilities(FindResponsibilitiesRequest.builder()
    .roleIds([string2Uuid(roleId)])
    .resourceIds([dataAssetId])
	.includeInherited(true)
	.limit(Integer.MAX_VALUE)
	.build()).getResults()
  
  loggerApi.info('responsibilities for role ' + roleId + ', on ' + dataAssetId.toString() + ', size=' + responsibilities.size())

  if (responsibilities == null || responsibilities.size() == 0) {
    return assetResponsibilities
  }
  
  responsibilities.each { responsibility ->
	def userId = responsibility.getOwner().getId()
	User user = userApi.getUser(userId)
    loggerApi.info('responsibility ' + responsibility.getId() + ', baseResource ' + responsibility.getBaseResource().getResourceType() + ', owner ' + user.getUserName())
    if (responsibility.getBaseResource().getResourceType().toString() == 'Asset') {
  	  assetResponsibilities.add(user.getUserName())
	} else if (responsibility.getBaseResource().getResourceType().toString() == 'Domain') {
	  domainResponsibilities.add(user.getUserName())
	} else if (responsibility.getBaseResource().getResourceType().toString() == 'Community') {
	  communityResponsibilities.add(user.getUserName())
	}
  }
  
  loggerApi.info('community=' + communityResponsibilities.size())
  loggerApi.info('domain=' + domainResponsibilities.size())
  loggerApi.info('asset=' + assetResponsibilities.size())
  
  Set finalResponsibilities = []
  if (assetResponsibilities.isEmpty()) {
    if (domainResponsibilities.isEmpty()) {
	  loggerApi.info('use community level=' + communityResponsibilities.size())
  	  finalResponsibilities = communityResponsibilities.clone()
	} else {
	  loggerApi.info('use domain level=' + domainResponsibilities.size())
	  finalResponsibilities = domainResponsibilities.clone()
	}
  } else {
    loggerApi.info('use asset level=' + assetResponsibilities.size())
    finalResponsibilities = assetResponsibilities.clone()
  }
  return finalResponsibilities
}

// check data set/report
def findReportsChildrenAssetTypesRequest = FindSubAssetTypesRequest.builder()
  .includeParent(true)
  .assetTypeId(string2Uuid(execution.getVariable('gvAssetTypeIdReport')))
  .build()

def findDataSetChildrenAssetTypesRequests = FindSubAssetTypesRequest.builder()
  .includeParent(true)
  .assetTypeId(string2Uuid(execution.getVariable('gvAssetTypeIdDataSet')))
  .build()

def reportChildrenTypes = assetTypeApi.findSubTypes(findReportsChildrenAssetTypesRequest).collect { it.getId() }
def dataSetChildrenTypes = assetTypeApi.findSubTypes(findDataSetChildrenAssetTypesRequests).collect { it.getId() }

Asset dataAsset = assetApi.getAsset(string2Uuid(execution.getVariable('gvRequestedDataAssetId')))
def dataAssetTypeId = dataAsset.getType().getId()

if (dataAssetTypeId in dataSetChildrenTypes) {
  loggerApi.info('data asset in the basket, ' + dataAsset.getId())
  execution.setVariable('gvDataSetAsset', dataAsset)
  execution.setVariable('gvContainsDataSet', true)
  execution.setVariable('gvContainsReport', false)
  List dataElements = dataSetApi.findDataElements(FindDataElementsRequest.builder()
  .dataSetId(dataAsset.getId())
  .limit(Integer.MAX_VALUE)
  .build()).getResults()
  execution.setVariable('gvDataElements', dataElements)
  Set technicalDataStewardsForDataElements = []
  if (dataElements != null && dataElements.size()> 0) {
	dataElements.each { dataElement ->
	  List responsibilities = responsibilityApi.findResponsibilities(FindResponsibilitiesRequest.builder()
		.roleIds([string2Uuid(execution.getVariable('gvRoleIdTechnicalDataSteward'))])
		.resourceIds([dataElement.getId()])
		.includeInherited(false)
		.limit(Integer.MAX_VALUE)
		.build()).getResults()
	  if (responsibilities != null && responsibilities.size()> 0) {
	    responsibilities.each { responsibility ->
		  def userId = responsibility.getOwner().getId()
		  User user = userApi.getUser(userId)
		  technicalDataStewardsForDataElements.add(user.getUserName())
	    }
	  } else {
  	    loggerApi.warn('this data element ' + dataElement.getId() + ' does not have Technical Data Stewards')  
	  }	
	}
  }
  execution.setVariable('gvTechnicalDataStewardsForDataElements', technicalDataStewardsForDataElements)
} else if (dataAssetTypeId in reportChildrenTypes) {
  loggerApi.info('report in the basket, ' + dataAsset.getId())
  execution.setVariable('gvReportAsset', dataAsset)
  execution.setVariable('gvContainsReport', true)
  execution.setVariable('gvContainsDataSet', false)
} else {
  execution.setVariable('gvValidDataAsset', false)
  execution.setVariable('gvReasonForInvalidDataAsset5', 'workflowDataBasketUnsupportedAssetTypes')
}

Boolean validDataAsset = true
// check business data steward
def businessDataStewards = findResponsibilityByHierarchy(dataAsset.getId(), execution.getVariable('gvRoleIdBusinessDataSteward'))
if (businessDataStewards != null && businessDataStewards.size() > 0) {
  List userExpression = businessDataStewards.collect {"user(${it})"}
  execution.setVariable('gvDataAccessBusinessDataStewards', userExpression.join(','))
  loggerApi.info('gvDataAccessBusinessDataStewards=' + userExpression.join(','))
} else {
  validDataAsset = false
  execution.setVariable('gvReasonForInvalidDataAsset1', 'workflowNoBusinessDataStewardForDataAsset')
}

// check technical data steward
Set allTechnicalDataStewards = []
def technicalDataStewards = findResponsibilityByHierarchy(dataAsset.getId(), execution.getVariable('gvRoleIdTechnicalDataSteward'))
if (technicalDataStewards != null && technicalDataStewards.size() > 0) {
  allTechnicalDataStewards = technicalDataStewards.clone()
}
if (execution.getVariable('gvContainsDataSet')) {
  def technicalDataStewardsForDataElements = execution.getVariable('gvTechnicalDataStewardsForDataElements')
  allTechnicalDataStewards = technicalDataStewards.plus(technicalDataStewardsForDataElements)
}
if (allTechnicalDataStewards != null && allTechnicalDataStewards.size() > 0) {
  List userExpression = allTechnicalDataStewards.collect {"user(${it})"}
  execution.setVariable('gvAllDataAccessTechnicalDataStewards', userExpression.join(','))
  loggerApi.info('gvAllDataAccessTechnicalDataStewards=' + userExpression.join(','))
} else {
  validDataAsset = false
  execution.setVariable('gvReasonForInvalidDataAsset2', 'workflowNoTechnicalDataStewardForDataAsset')
}

// check data trustee
def dataTrustees = findResponsibilityByHierarchy(dataAsset.getId(), execution.getVariable('gvRoleIdDataTrustee'))
if (dataTrustees != null && dataTrustees.size() > 0) {
  List userExpression = dataTrustees.collect {"user(${it})"}
  execution.setVariable('gvDataAccessDataTrustees', userExpression.join(','))
  loggerApi.info('gvDataAccessDataTrustees=' + userExpression.join(','))
} else {
  validDataAsset = false
  execution.setVariable('gvReasonForInvalidDataAsset3', 'workflowNoDataTrusteeForDataAsset')
}

// check data privacy officer
List<UserGroup> userGroupList = userGroupApi.findUserGroups(FindUserGroupsRequest.builder()
  .name(execution.getVariable('gvUserGroupDataPrivacyOfficer'))
  .nameMatchMode(MatchMode.EXACT)
  .build())
  .getResults()

if (userGroupList != null && userGroupList.size() > 0) {
  List<User> users = userApi.findUsers(FindUsersRequest.builder()
    .groupId(userGroupList.first().getId())
    .build())
    .getResults()
  Set dataPrivacyOfficers = new HashSet()
  if (users != null && users.size() > 0) {
    users.each { user->
	  dataPrivacyOfficers.add(user.getUserName())
    }
    List userExpression = dataPrivacyOfficers.collect {"user(${it})"}
    execution.setVariable('gvDataPrivacyOfficerUsers', userExpression.join(','))
    loggerApi.info('gvDataPrivacyOfficerUsers=' + userExpression.join(','))
  } else {
    validDataAsset = false
    execution.setVariable('gvReasonForInvalidDataAsset4', 'workflowNoDataPrivacyOfficers')
    loggerApi.info('No users in the data privacy officer')
  }
} else {
  validDataAsset = false
  execution.setVariable('gvReasonForInvalidDataAsset4', 'workflowNoDataPrivacyOfficers')
  loggerApi.info('No data privacy officer group')
}

execution.setVariable('gvValidDataAsset', validDataAsset)
