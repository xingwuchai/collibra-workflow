import com.collibra.dgc.core.api.dto.MatchMode
import com.collibra.dgc.core.api.dto.meta.assettype.FindAssetTypesRequest
import com.collibra.dgc.core.api.dto.meta.relationtype.FindRelationTypesRequest
import com.collibra.dgc.core.api.dto.role.FindRolesRequest

// relatiions
RELATION_TYPE_ROLE_ISESSENTIALFOR = 'is essential for'
RELATION_TYPE_COROLE_ISESSENTIALFOR = 'requires'
RELATION_TYPE_ROLE_DECIDES = 'Decides'
RELATION_TYPE_ROLE_DECIDES_SOUTCE_TYPE_NAME = 'Approval Decision'
RELATION_TYPE_ROLE_GOVERNS = 'governs'
RELATION_TYPE_ROLE_GOVERNS_SOURCE_TYPE_NAME = 'Terms of Use'

// asset types
ASSET_TYPE_DATA_SET = 'Data Set'
ASSET_TYPE_REPORT = 'Report'
ASSET_TYPE_APPROVAL_DECISION = 'Approval Decision'

// statuses
STATUS_ACCEPTED = 'Accepted'
STATUS_REJECTED = 'Rejected'
STATUS_INVALID = 'Invalid'
STATUS_PENDING = 'Approval Pending'
STATUS_ACCESS_GRANTED = 'Access Granted'

// roles
ROLE_OWNER = 'Owner'
ROLE_BUSINESS_DATA_STEWARD = 'Business Data Steward'
ROLE_TECHNICAL_DATA_STEWARD = 'Technical Data Steward'
ROLE_DATA_TRUSTEE = 'Data Trustee'
ROLE_SUBMITTER = 'Submitter'
ROLE_DATA_ANALYST_LEVEL_2 = 'Data Analyst Level 2'

// attribute types
ATTRIBUTE_TYPE_PURPOSE = 'Purpose'
ATTRIBUTE_TYPE_EFFECTIVE_START_DATE = 'Effective Start Date'
ATTRIBUTE_TYPE_EFFECTIVE_END_DATE = 'Effective End Date'
ATTRIBUTE_TYPE_REQUESTER_NAME = 'Requester Name'
ATTRIBUTE_TYPE_REQUESTER_ORGANIZATION = 'Requester Organization'
ATTRIBUTE_TYPE_AUDIENCE = 'Audience'
ATTRIBUTE_TYPE_DATA_AGGREGATION = 'Data Aggregation'
ATTRIBUTE_TYPE_DATA_FILTERS = 'Data Filters'
ATTRIBUTE_TYPE_ACCESS_DECISION = 'Access Decision'
ATTRIBUTE_TYPE_DECISION_MAKER = 'Decision Maker'
ATTRIBUTE_TYPE_ACCESS_DECISION_DATE = 'Access Decision date'
ATTRIBUTE_TYPE_DECISION_REASON = 'Decision Reason'
ATTRIBUTE_TYPE_DECISION_SHORT_NAME = 'Access Decision Short Name'
ATTRIBUTE_TYPE_REQUEST_SHORT_NAME = 'Request Short Name'
ATTRIBUTE_TYPE_SUBMISSION_DATE = 'Submission Date'
ATTRIBUTE_TYPE_SECURITY_CLASSIFICATION = 'Security Classification'

def getAttributeTypeIdByName(name) {
  return attributeTypeApi.getAttributeTypeByName(name).getId().toString()
}

def getStatusIdByName(name) {
  return statusApi.getStatusByName(name).getId().toString()
}

def getAssetTypeIdByName(name) {
  List assetTypes = assetTypeApi.findAssetTypes(FindAssetTypesRequest.builder()
	.name(name)
	.nameMatchMode(MatchMode.EXACT)
	.build())
	.getResults()
  if (assetTypes != null && assetTypes.size() > 0) {
	return assetTypes.first().getId().toString()
  } else {
    return null
  }
}

def getRelationTypeId(sourceTypeName, roleName, coRoleName) {
  def findRelationTypesRequest = FindRelationTypesRequest.builder().role(roleName)
  if (sourceTypeName != null) {
	findRelationTypesRequest.sourceTypeName(sourceTypeName)  
  }
  if (coRoleName != null ) {
	findRelationTypesRequest.coRole(coRoleName)
  }
  
  List relationTypes = relationTypeApi.findRelationTypes(findRelationTypesRequest.build()).getResults()
  if (relationTypes != null && !relationTypes.isEmpty()) {
	return relationTypes.first().getId().toString()
  } else {
	return null
  }
}
  
def getRoleIdByName(name) {
  List roles = roleApi.findRoles(FindRolesRequest.builder()
	.name(name)
	.build())
	.getResults()
  if (roles != null && !roles.isEmpty()) {
	return roles.first().getId().toString()
  } else {
    return null
  }	  
}

execution.setVariable('gvRelationTypeIdRoleIsessentialfor', getRelationTypeId(null, RELATION_TYPE_ROLE_ISESSENTIALFOR, RELATION_TYPE_COROLE_ISESSENTIALFOR))
execution.setVariable('gvRelationTypeIdRoleDecides', getRelationTypeId(RELATION_TYPE_ROLE_DECIDES_SOUTCE_TYPE_NAME, RELATION_TYPE_ROLE_DECIDES, null))
execution.setVariable('gvRelationTypeIdRoleGoverns', getRelationTypeId(RELATION_TYPE_ROLE_GOVERNS_SOURCE_TYPE_NAME, RELATION_TYPE_ROLE_GOVERNS, null))

execution.setVariable('gvAssetTypeIdDataSet', getAssetTypeIdByName(ASSET_TYPE_DATA_SET))
execution.setVariable('gvAssetTypeIdReport', getAssetTypeIdByName(ASSET_TYPE_REPORT))
execution.setVariable('gvAssetTypeIdApprovalDecision', getAssetTypeIdByName(ASSET_TYPE_APPROVAL_DECISION))

execution.setVariable('gvStatusIdAccepted', getStatusIdByName(STATUS_ACCEPTED))
execution.setVariable('gvStatusIdRejected', getStatusIdByName(STATUS_REJECTED))
execution.setVariable('gvStatusIdInvalid', getStatusIdByName(STATUS_INVALID))
execution.setVariable('gvStatusIdPending', getStatusIdByName(STATUS_PENDING))
execution.setVariable('gvStatusIdAccessGranted', getStatusIdByName(STATUS_ACCESS_GRANTED))

execution.setVariable('gvRoleIdOwner', getRoleIdByName(ROLE_OWNER))
execution.setVariable('gvRoleIdBusinessDataSteward', getRoleIdByName(ROLE_BUSINESS_DATA_STEWARD))
execution.setVariable('gvRoleIdTechnicalDataSteward', getRoleIdByName(ROLE_TECHNICAL_DATA_STEWARD))
execution.setVariable('gvRoleIdDataTrustee', getRoleIdByName(ROLE_DATA_TRUSTEE))
execution.setVariable('gvRoleIdSubmitter', getRoleIdByName(ROLE_SUBMITTER))
execution.setVariable('gvRoleIdDataAnalystlevel2', getRoleIdByName(ROLE_DATA_ANALYST_LEVEL_2))

execution.setVariable('gvAttributeTypeIdPurpose', getAttributeTypeIdByName(ATTRIBUTE_TYPE_PURPOSE))
execution.setVariable('gvAttributeTypeIdEffectiveStartDate', getAttributeTypeIdByName(ATTRIBUTE_TYPE_EFFECTIVE_START_DATE))
execution.setVariable('gvAttributeTypeIdEffectiveEndDate', getAttributeTypeIdByName(ATTRIBUTE_TYPE_EFFECTIVE_END_DATE))
execution.setVariable('gvAttributeTypeIdRequesterName', getAttributeTypeIdByName(ATTRIBUTE_TYPE_REQUESTER_NAME))
execution.setVariable('gvAttributeTypeIdRequesterOrganization', getAttributeTypeIdByName(ATTRIBUTE_TYPE_REQUESTER_ORGANIZATION))
execution.setVariable('gvAttributeTypeIdAudience', getAttributeTypeIdByName(ATTRIBUTE_TYPE_AUDIENCE))
execution.setVariable('gvAttributeTypeIdDataAggregation', getAttributeTypeIdByName(ATTRIBUTE_TYPE_DATA_AGGREGATION))
execution.setVariable('gvAttributeTypeIdDataFilters', getAttributeTypeIdByName(ATTRIBUTE_TYPE_DATA_FILTERS))
execution.setVariable('gvAttributeTypeIdAccessDecision', getAttributeTypeIdByName(ATTRIBUTE_TYPE_ACCESS_DECISION))
execution.setVariable('gvAttributeTypeIdDecisionMaker', getAttributeTypeIdByName(ATTRIBUTE_TYPE_DECISION_MAKER))
execution.setVariable('gvAttributeTypeIdAccessDecisionDate', getAttributeTypeIdByName(ATTRIBUTE_TYPE_ACCESS_DECISION_DATE))
execution.setVariable('gvAttributeTypeIdDecisionReason', getAttributeTypeIdByName(ATTRIBUTE_TYPE_DECISION_REASON))
execution.setVariable('gvAttributeTypeIdDecisionShortName', getAttributeTypeIdByName(ATTRIBUTE_TYPE_DECISION_SHORT_NAME))
execution.setVariable('gvAttributeTypeIdRequestShortName', getAttributeTypeIdByName(ATTRIBUTE_TYPE_REQUEST_SHORT_NAME))
execution.setVariable('gvAttributeTypeIdSubmissionDate', getAttributeTypeIdByName(ATTRIBUTE_TYPE_SUBMISSION_DATE))
execution.setVariable('gvAttributeTypeIdSecurityClassification', getAttributeTypeIdByName(ATTRIBUTE_TYPE_SECURITY_CLASSIFICATION))

execution.setVariable('gvUserGroupDataPrivacyOfficer', 'Data Privacy Officer')

String userTaskDocApproveRejectReroutePrivacy =
  '<div>Please approve or reject the request, and add your comment here.</div>' +
  '<div>If you need to consult with the Privacy officer, please click the Reroute button.</div>'
execution.setVariable('gvUserTaskDocApproveRejectReroutePrivacy', userTaskDocApproveRejectReroutePrivacy)

String userTaskDocApproveRejectRerouteDataTrustee =
  '<div>Please approve or reject the request, and add your comment here.</div>' +
  '<div>If you need to consult with the Data Trustee, please click the Reroute button.</div>'
execution.setVariable('gvUserTaskDocApproveRejectRerouteDataTrustee', userTaskDocApproveRejectRerouteDataTrustee)

String userTaskDocApproveRejectByDataTrustee = '<div>Please approve or reject the request, and add your comment here.</div>'
execution.setVariable('gvUserTaskDocApproveRejectByDataTrustee', userTaskDocApproveRejectByDataTrustee)

String userTaskDocReviewRequestAddComment =
'<div>The business data steward has asked you to review the request.</div>' +
'<div>Please review the request and add your comment, then click the DONE button.</div>'
execution.setVariable('gvUserTaskDocReviewRequestAddComment', userTaskDocReviewRequestAddComment)

loggerApi.info('gvRelationTypeIdRoleIsessentialfor=' + execution.getVariable('gvRelationTypeIdRoleIsessentialfor'))
loggerApi.info('gvRelationTypeIdRoleDecides=' + execution.getVariable('gvRelationTypeIdRoleDecides'))
loggerApi.info('gvRelationTypeIdRoleGoverns=' + execution.getVariable('gvRelationTypeIdRoleGoverns'))

loggerApi.info('gvAssetTypeIdDataSet=' + execution.getVariable('gvAssetTypeIdDataSet'))
loggerApi.info('gvAssetTypeIdReport=' + execution.getVariable('gvAssetTypeIdReport'))
loggerApi.info('gvAssetTypeIdApprovalDecision=' + execution.getVariable('gvAssetTypeIdApprovalDecision'))

loggerApi.info('gvStatusIdAccepted=' + execution.getVariable('gvStatusIdAccepted'))
loggerApi.info('gvStatusIdRejected=' + execution.getVariable('gvStatusIdRejected'))
loggerApi.info('gvStatusIdInvalid=' + execution.getVariable('gvStatusIdInvalid'))
loggerApi.info('gvStatusIdPending=' + execution.getVariable('gvStatusIdPending'))
loggerApi.info('gvStatusIdAccessGranted=' + execution.getVariable('gvStatusIdAccessGranted'))

loggerApi.info('gvRoleIdOwner=' + execution.getVariable('gvRoleIdOwner'))
loggerApi.info('gvRoleIdBusinessDataSteward=' + execution.getVariable('gvRoleIdBusinessDataSteward'))
loggerApi.info('gvRoleIdTechnicalDataSteward=' + execution.getVariable('gvRoleIdTechnicalDataSteward'))
loggerApi.info('gvRoleIdDataTrustee=' + execution.getVariable('gvRoleIdDataTrustee'))
loggerApi.info('gvRoleIdSubmitter=' + execution.getVariable('gvRoleIdSubmitter'))
loggerApi.info('gvRoleIdDataAnalystlevel2=' + execution.getVariable('gvRoleIdDataAnalystlevel2'))

loggerApi.info('gvAttributeTypeIdPurpose=' + execution.getVariable('gvAttributeTypeIdPurpose'))
loggerApi.info('gvAttributeTypeIdEffectiveStartDate=' + execution.getVariable('gvAttributeTypeIdEffectiveStartDate'))
loggerApi.info('gvAttributeTypeIdEffectiveEndDate=' + execution.getVariable('gvAttributeTypeIdEffectiveEndDate'))
loggerApi.info('gvAttributeTypeIdRequesterName=' + execution.getVariable('gvAttributeTypeIdRequesterName'))
loggerApi.info('gvAttributeTypeIdRequesterOrganization=' + execution.getVariable('gvAttributeTypeIdRequesterOrganization'))
loggerApi.info('gvAttributeTypeIdAudience=' + execution.getVariable('gvAttributeTypeIdAudience'))
loggerApi.info('gvAttributeTypeIdDataAggregation=' + execution.getVariable('gvAttributeTypeIdDataAggregation'))
loggerApi.info('gvAttributeTypeIdDataFilters=' + execution.getVariable('gvAttributeTypeIdDataFilters'))
loggerApi.info('gvAttributeTypeIdAccessDecision=' + execution.getVariable('gvAttributeTypeIdAccessDecision'))
loggerApi.info('gvAttributeTypeIdDecisionMaker=' + execution.getVariable('gvAttributeTypeIdDecisionMaker'))
loggerApi.info('gvAttributeTypeIdAccessDecisionDate=' + execution.getVariable('gvAttributeTypeIdAccessDecisionDate'))
loggerApi.info('gvAttributeTypeIdDecisionReason=' + execution.getVariable('gvAttributeTypeIdDecisionReason'))
loggerApi.info('gvAttributeTypeIdDecisionShortName=' + execution.getVariable('gvAttributeTypeIdDecisionShortName'))
loggerApi.info('gvAttributeTypeIdRequestShortName=' + execution.getVariable('gvAttributeTypeIdRequestShortName'))
loggerApi.info('gvAttributeTypeIdSubmissionDate=' + execution.getVariable('gvAttributeTypeIdSubmissionDate'))
loggerApi.info('gvAttributeTypeIdSecurityClassification=' + execution.getVariable('gvAttributeTypeIdSecurityClassification'))
