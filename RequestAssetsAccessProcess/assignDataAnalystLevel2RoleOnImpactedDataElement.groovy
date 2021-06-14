import com.collibra.dgc.core.api.dto.instance.responsibility.AddResponsibilityRequest
import com.collibra.dgc.core.api.dto.user.FindUsersRequest
import com.collibra.dgc.core.api.model.user.User

String prefix = 'scripttask, assignDataAnalystLevel2RoleOnImpactedDataElement, '
loggerApi.info(prefix + 'started')

User user = userApi.getUserByUsername(requester)
  
def asset = assetApi.getAsset(string2Uuid(dataElement))

DATA_ANALYST_LEVEL_2_ROLE_ID = string2Uuid('00000000-0000-0000-0000-000000005062')

responsibilityApi.addResponsibility(AddResponsibilityRequest.builder()
  .ownerId(user.id)
  .resourceId(asset.id)
  .roleId(DATA_ANALYST_LEVEL_2_ROLE_ID)
  .resourceType(asset.resourceType)
  .build())

loggerApi.info(prefix + 'ended')
