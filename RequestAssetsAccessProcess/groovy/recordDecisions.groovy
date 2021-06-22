import com.collibra.dgc.core.api.model.instance.Comment
import com.collibra.dgc.core.api.dto.instance.comment.FindCommentsRequest
import com.collibra.dgc.core.api.dto.instance.comment.AddCommentRequest
import com.collibra.dgc.core.api.model.user.User
import com.collibra.dgc.core.api.dto.user.FindUsersRequest
import com.collibra.dgc.core.api.dto.SortOrder
import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.dto.instance.asset.AddAssetRequest
import com.collibra.dgc.core.api.dto.instance.attribute.AddAttributeRequest
import com.collibra.dgc.core.api.dto.instance.relation.AddRelationRequest
import com.collibra.dgc.core.api.dto.instance.responsibility.AddResponsibilityRequest
import java.util.Date
import java.text.SimpleDateFormat

Asset dataAccess = assetApi.getAsset(item.id)
String dataAccessName = dataAccess.getName()
def domainId = dataAccess.getDomain().getId()
User decisionMaker = userApi.getUserByUsername(execution.getVariable('gvDecisionMaker'))
String requestShortName = execution.getVariable('requestShortName')

commentApi.addComment(AddCommentRequest.builder()
  .content(execution.getVariable('gvDecisionComment').toString())
  .baseResourceId(item.id)
  .baseResourceType(item.getType())
  .build())

Asset approvalDecision = assetApi.addAsset(AddAssetRequest.builder()
  .name(dataAccessName + '.' + decisionMaker.getUserName())
  .displayName(dataAccessName + '.' + decisionMaker.getUserName())
  .typeId(string2Uuid(execution.getVariable('gvAssetTypeIdApprovalDecision')))
  .domainId(domainId)
  .build())
	  
attributeApi.addAttribute(AddAttributeRequest.builder()
  .assetId(approvalDecision.getId())
  .typeId(string2Uuid(execution.getVariable('gvAttributeTypeIdAccessDecision')))
  .value(execution.getVariable('gvDecisionApproved'))
  .build())

attributeApi.addAttribute(AddAttributeRequest.builder()
  .assetId(approvalDecision.getId())
  .typeId(string2Uuid(execution.getVariable('gvAttributeTypeIdDecisionShortName')))
  .value(requestShortName + '.' + decisionMaker.getUserName())
  .build())

attributeApi.addAttribute(AddAttributeRequest.builder()
  .assetId(approvalDecision.getId())
  .typeId(string2Uuid(execution.getVariable('gvAttributeTypeIdDecisionMaker')))
  .value(decisionMaker.getFirstName() + ' ' + decisionMaker.getLastName())
  .build())
  
attributeApi.addAttribute(AddAttributeRequest.builder()
  .assetId(approvalDecision.getId())
  .typeId(string2Uuid(execution.getVariable('gvAttributeTypeIdAccessDecisionDate')))
  .value(execution.getVariable('gvDecisionDate'))
  .build())

attributeApi.addAttribute(AddAttributeRequest.builder()
  .assetId(approvalDecision.getId())
  .typeId(string2Uuid(execution.getVariable('gvAttributeTypeIdDecisionReason')))
  .value(execution.getVariable('gvDecisionComment').toString())
  .build())

responsibilityApi.addResponsibility(AddResponsibilityRequest.builder()
  .ownerId(decisionMaker.getId())
  .resourceId(approvalDecision.getId())
  .roleId(string2Uuid(execution.getVariable('gvRoleIdOwner')))
  .resourceType(approvalDecision.getResourceType())
  .build())
 
relationApi.addRelation(AddRelationRequest.builder()
  .typeId(string2Uuid(execution.getVariable('gvRelationTypeIdRoleDecides')))
  .sourceId(approvalDecision.getId())
  .targetId(item.id)
  .build()
)
