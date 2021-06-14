import com.collibra.dgc.core.api.model.instance.Comment
import com.collibra.dgc.core.api.dto.instance.comment.FindCommentsRequest
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

ASSET_TYPE_APPROVAL_DECISION = '0af2eb43-7aba-4948-9994-9d362091ebcf'
ATTRIBUTE_TYPE_ID_APPROVER = '21bafa26-6d74-497b-93e9-b877f6496967'
ATTRIBUTE_TYPE_ID_APPROVED = '260f2b90-37a6-4218-8cd3-5c5caa9a5826'
ATTRIBUTE_TYPE_ID_APPROVAL_DATE = '00000000-0000-0000-0000-000000000272'
ATTRIBUTE_TYPE_ID_DECISION_REASON = '8b0f84e0-30c1-4b71-8293-8b747aec6565'
RELATION_TYPE_DECIDES = '98b4e737-7b1b-40d2-9ef8-90edc47001b4'
OWNER_ROLE_ID = '00000000-0000-0000-0000-000000005040'

String prefix = 'scripttask, recordDecisions, '
loggerApi.info(prefix + 'started')

boolean votingSuccess = execution.getVariable('votingSuccess')
loggerApi.info(prefix + 'votingSuccess=' + votingSuccess)

List<Map> votingResults = execution.getVariable('votingResult')
loggerApi.info(prefix + 'votingResult=' + votingResult.size())

for (Map votingResult : votingResults) {
  boolean approved = votingResult.approved
  String voterName = votingResult.name
  String voterComment = votingResult.comment
  loggerApi.info(prefix + 'voter=' + voterName + ', approved=' + approved + ', comment=' + voterComment)
 
  User user = userApi.getUserByUsername(voterName)
	
  loggerApi.info(prefix + 'found user=' + user.id + ' ' + user.getLastName() + ' ' + user.getFirstName())
  Comment comment = commentApi.findComments(FindCommentsRequest.builder()
    .baseResourceId(item.id)
    .userId(user.id)
    .rootComment(true)
	.limit(1)
	.sortOrder(SortOrder.ASC)
	.build())
    .getResults()
	.first()
  
  loggerApi.info(prefix + 'found comment for user=' + comment.getCreatedBy() + ', on ' + comment.getCreatedOn() + ', content=' + comment.getContent())
  if (comment.getContent() == voterComment) {
    loggerApi.info(prefix + 'found the comment')
  } else {
	loggerApi.warn(prefix + 'the comment does not match')
  }
  
  // create Approval decision
  // we may want to put the Approval decisions to another place, (default is Business Analyst Cimmunity)
  // we are doing the same plave as the Data Access for now
  // we also need a naming standard here
  Asset dataAccess = assetApi.getAsset(item.id)
  String dataAccessName = dataAccess.getName()
  def domainId = dataAccess.getDomain().getId()
  Asset approvalDecision = assetApi.addAsset(AddAssetRequest.builder()
	.name(user.getUserName() + ' ' + dataAccessName)
	.displayName(user.getUserName() + ' ' + dataAccessName)
	.typeId(string2Uuid(ASSET_TYPE_APPROVAL_DECISION))
	.domainId(domainId)
	.build())
	
  // add attributes
  attributeApi.addAttribute(AddAttributeRequest.builder()
    .assetId(approvalDecision.getId())
    .typeId(string2Uuid(ATTRIBUTE_TYPE_ID_APPROVED))
    .value(approved)
    .build())
  
  // current date
  Date commentDate = new Date(comment.getCreatedOn())
  SimpleDateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd')
  String stringDate= dateFormat.format(commentDate)
  
  loggerApi.info(prefix + 'comment date in long=' + comment.getCreatedOn())
  loggerApi.info(prefix + 'comment date in string=' + stringDate)
  
  attributeApi.addAttribute(AddAttributeRequest.builder()
	.assetId(approvalDecision.getId())
	.typeId(string2Uuid(ATTRIBUTE_TYPE_ID_APPROVER))
	.value(user.getFirstName() + ' ' + user.getLastName())
	.build())
  
  SimpleDateFormat dateFormatDatetime = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss Z')
  String stringDatetime= dateFormatDatetime.format(commentDate)
  loggerApi.info(prefix + 'comment date time in string=' + stringDatetime)
  
  attributeApi.addAttribute(AddAttributeRequest.builder()
    .assetId(approvalDecision.getId())
    .typeId(string2Uuid(ATTRIBUTE_TYPE_ID_APPROVAL_DATE))
	.value(stringDate)
	.build())

  attributeApi.addAttribute(AddAttributeRequest.builder()
    .assetId(approvalDecision.getId())
    .typeId(string2Uuid(ATTRIBUTE_TYPE_ID_DECISION_REASON))
	.value(comment.getContent())
	.build())
  
  // add owner
  responsibilityApi.addResponsibility(AddResponsibilityRequest.builder()
    .ownerId(user.id)
    .resourceId(approvalDecision.getId())
    .roleId(string2Uuid(OWNER_ROLE_ID))
    .resourceType(approvalDecision.getResourceType())
    .build())
  
  // add relation
  relationApi.addRelation(AddRelationRequest.builder()
	.typeId(string2Uuid(RELATION_TYPE_DECIDES))
	.sourceId(approvalDecision.getId())
	.targetId(item.id)
	.build()
  )
}

loggerApi.info(prefix + 'ended')
