import com.collibra.dgc.core.api.dto.instance.responsibility.AddResponsibilityRequest
import com.collibra.dgc.core.api.dto.user.FindUsersRequest

String prefix = 'scripttask, assignDataAnalystLevel2RoleOnImpactedDataElement, '
loggerApi.info(prefix + 'started')

def user = userApi.findUsers(FindUsersRequest.builder()
  .name(requester)
  .build())
  .getResults()
  .first()

def asset = assetApi.getAsset(string2Uuid(dataElement))

DATA_ANALYST_LEVEL_2_ROLE_ID = string2Uuid('00000000-0000-0000-0000-000000005062')

responsibilityApi.addResponsibility(AddResponsibilityRequest.builder()
  .ownerId(user.id)
  .resourceId(asset.id)
  .roleId(DATA_ANALYST_LEVEL_2_ROLE_ID)
  .resourceType(asset.resourceType)
  .build())

loggerApi.info(prefix + 'ended')
