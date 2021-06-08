import com.collibra.dgc.core.api.dto.instance.responsibility.AddResponsibilityRequest
import com.collibra.dgc.core.api.dto.user.FindUsersRequest

REQUESTER_ROLE_ID = string2Uuid('00000000-0000-0000-0000-000000005033')

String prefix = 'scripttask, assignRequesterRoleToDataAccess, '
loggerApi.info(prefix + 'started')

def user = userApi.findUsers(FindUsersRequest.builder()
  .name(requester)
  .build())
  .getResults()
  .first()

responsibilityApi.addResponsibility(AddResponsibilityRequest.builder()
  .ownerId(user.id)
  .resourceId(item.id)
  .roleId(REQUESTER_ROLE_ID)
  .resourceType(item.type)
  .build())

loggerApi.info(prefix + 'ended')
