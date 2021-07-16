import com.collibra.dgc.core.api.model.user.User
import com.collibra.dgc.core.api.model.instance.Comment
import com.collibra.dgc.core.api.dto.instance.comment.AddCommentRequest

commentApi.addComment(AddCommentRequest.builder()
  .content(execution.getVariable('commentApproveRejectRerouteDataTrustee').toString())
  .baseResourceId(item.id)
  .baseResourceType(item.getType())
  .build())
