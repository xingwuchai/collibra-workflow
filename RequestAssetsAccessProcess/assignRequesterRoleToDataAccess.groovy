import com.collibra.dgc.core.api.dto.instance.responsibility.AddResponsibilityRequest
import com.collibra.dgc.core.api.dto.user.FindUsersRequest
import com.collibra.dgc.core.api.model.user.User

REQUESTER_ROLE_ID = string2Uuid('00000000-0000-0000-0000-000000005033')

String prefix = 'scripttask, assignRequesterRoleToDataAccess, '
loggerApi.info(prefix + 'started')

User user = userApi.getUserByUsername(requester)

responsibilityApi.addResponsibility(AddResponsibilityRequest.builder()
  .ownerId(user.id)
  .resourceId(item.id)
  .roleId(REQUESTER_ROLE_ID)
  .resourceType(item.type)
  .build())

loggerApi.info(prefix + 'ended')
