import com.collibra.dgc.core.api.model.user.User
import com.collibra.dgc.core.api.model.instance.Comment
import com.collibra.dgc.core.api.dto.instance.comment.AddCommentRequest

Optional<User> actingDataTrustee = userApi.getCurrentUser()
User user = actingDataTrustee.get()
loggerApi.info('acting data trustee= ' + user.getUserName())
execution.setVariable('gvActingDataTrustee', user.getUserName())

commentApi.addComment(AddCommentRequest.builder()
  .content(execution.getVariable('commentApproveRejectByDataTrustee').toString())
  .baseResourceId(item.id)
  .baseResourceType(item.getType())
  .build())
  