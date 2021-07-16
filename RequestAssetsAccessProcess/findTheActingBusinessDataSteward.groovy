import com.collibra.dgc.core.api.model.user.User
import com.collibra.dgc.core.api.model.instance.Comment
import com.collibra.dgc.core.api.dto.instance.comment.AddCommentRequest

Optional<User> actingBusinessDataSteward = userApi.getCurrentUser()
User user = actingBusinessDataSteward.get()
String userExpression = 'user(' + user.getUserName() + ')'
loggerApi.info('acting business data steward= ' + userExpression)
execution.setVariable('gvActingBusinessDataSteward', user.getUserName())
execution.setVariable('gvActingBusinessDataStewardUserExpression', userExpression)

commentApi.addComment(AddCommentRequest.builder()
  .content(execution.getVariable('commentApproveRejectReroutePrivacy').toString())
  .baseResourceId(item.id)
  .baseResourceType(item.getType())
  .build())
