import com.collibra.dgc.core.api.model.instance.Comment
import com.collibra.dgc.core.api.dto.instance.comment.FindCommentsRequest
import com.collibra.dgc.core.api.dto.instance.comment.AddCommentRequest
import com.collibra.dgc.core.api.model.user.User
import com.collibra.dgc.core.api.dto.user.FindUsersRequest
import com.collibra.dgc.core.api.dto.SortOrder
import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.dto.instance.asset.AddAssetRequest
import com.collibra.dgc.core.api.dto.instance.attribute.FindAttributesRequest
import com.collibra.dgc.core.api.dto.instance.attribute.AddAttributeRequest
import com.collibra.dgc.core.api.dto.instance.relation.AddRelationRequest
import com.collibra.dgc.core.api.dto.instance.responsibility.AddResponsibilityRequest
import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest
import java.util.Date
import java.text.SimpleDateFormat

Asset dataAccess = assetApi.getAsset(item.id)
String dataAccessName = dataAccess.getName()
def domainId = dataAccess.getDomain().getId()
User decisionMaker = userApi.getUserByUsername(execution.getVariable('gvDecisionMaker'))
String requestShortName = execution.getVariable('requestShortName')

Asset accessRequestDecision = assetApi.addAsset(AddAssetRequest.builder()
  .name(dataAccessName + '.' + decisionMaker.getUserName())
  .displayName(dataAccessName + '.' + decisionMaker.getUserName())
  .typeId(string2Uuid(execution.getVariable('gvAssetTypeIdAccessRequestDecision')))
  .domainId(domainId)
  .build())
	  
String accessDecision = ''
if (execution.getVariable('gvDecisionApproved')) {
	accessDecision = 'Approved'
} else {
	accessDecision = 'Rejected'
}
attributeApi.addAttribute(AddAttributeRequest.builder()
  .assetId(accessRequestDecision.getId())
  .typeId(string2Uuid(execution.getVariable('gvAttributeTypeIdAccessDecision')))
  .value(accessDecision)
  .build())

attributeApi.addAttribute(AddAttributeRequest.builder()
  .assetId(accessRequestDecision.getId())
  .typeId(string2Uuid(execution.getVariable('gvAttributeTypeIdDecisionShortName')))
  .value(requestShortName)
  .build())

attributeApi.addAttribute(AddAttributeRequest.builder()
  .assetId(accessRequestDecision.getId())
  .typeId(string2Uuid(execution.getVariable('gvAttributeTypeIdDecisionMaker')))
  .value(decisionMaker.getFirstName() + ' ' + decisionMaker.getLastName())
  .build())
  
attributeApi.addAttribute(AddAttributeRequest.builder()
  .assetId(accessRequestDecision.getId())
  .typeId(string2Uuid(execution.getVariable('gvAttributeTypeIdAccessDecisionDate')))
  .value(execution.getVariable('gvDecisionDate'))
  .build())

attributeApi.addAttribute(AddAttributeRequest.builder()
  .assetId(accessRequestDecision.getId())
  .typeId(string2Uuid(execution.getVariable('gvAttributeTypeIdDecisionReason')))
  .value(execution.getVariable('gvDecisionComment').toString())
  .build())

def requestedDataAssetId = string2Uuid(execution.getVariable('gvRequestedDataAssetId'))
Asset requestedDataAsset = assetApi.getAsset(requestedDataAssetId)
List securityClassifications = attributeApi.findAttributes(FindAttributesRequest.builder()
  .assetId(requestedDataAssetId)
  .typeIds([string2Uuid(execution.getVariable('gvAttributeTypeIdSecurityClassification'))])
  .build())
  .getResults()
if (securityClassifications != null && !securityClassifications.isEmpty()) {
  loggerApi.info('security classification=' + securityClassifications.first().getValueAsString())
  attributeApi.addAttribute(AddAttributeRequest.builder()
    .assetId(accessRequestDecision.getId())
    .typeId(string2Uuid(execution.getVariable('gvAttributeTypeIdSecurityClassification')))
    .value(securityClassifications.first().getValueAsString())
    .build())
}

responsibilityApi.addResponsibility(AddResponsibilityRequest.builder()
  .ownerId(decisionMaker.getId())
  .resourceId(accessRequestDecision.getId())
  .roleId(string2Uuid(execution.getVariable('gvRoleIdOwner')))
  .resourceType(accessRequestDecision.getResourceType())
  .build())
 
relationApi.addRelation(AddRelationRequest.builder()
  .typeId(string2Uuid(execution.getVariable('gvRelationTypeIdRoleDecides')))
  .sourceId(accessRequestDecision.getId())
  .targetId(item.id)
  .build()
)

List policyGoverns = relationApi.findRelations(FindRelationsRequest.builder()
  .targetId(requestedDataAssetId)
  .relationTypeId(string2Uuid(execution.getVariable('gvRelationTypeIdRolePolicyGoverns')))
  .limit(Integer.MAX_VALUE)
  .build())
  .getResults()
if (policyGoverns != null && policyGoverns.size() > 0) {
  relationApi.addRelation(AddRelationRequest.builder()
    .typeId(string2Uuid(execution.getVariable('gvRelationTypeIdRolePolicyGoverns')))
    .sourceId(policyGoverns.first().source.id)
    .targetId(accessRequestDecision.getId())
    .build()
  )
}
