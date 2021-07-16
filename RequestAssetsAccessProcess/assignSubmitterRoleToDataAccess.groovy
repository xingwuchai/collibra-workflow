import com.collibra.dgc.core.api.dto.instance.responsibility.AddResponsibilityRequest
import com.collibra.dgc.core.api.dto.user.FindUsersRequest
import com.collibra.dgc.core.api.model.user.User

User user = userApi.getUserByUsername(requester)

responsibilityApi.addResponsibility(AddResponsibilityRequest.builder()
  .ownerId(user.id)
  .resourceId(item.id)
  .roleId(string2Uuid(execution.getVariable('gvRoleIdSubmitter')))
  .resourceType(item.type)
  .build())
