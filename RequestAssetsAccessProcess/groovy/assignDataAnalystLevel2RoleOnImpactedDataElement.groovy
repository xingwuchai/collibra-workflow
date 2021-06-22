import com.collibra.dgc.core.api.dto.instance.responsibility.AddResponsibilityRequest
import com.collibra.dgc.core.api.dto.user.FindUsersRequest
import com.collibra.dgc.core.api.model.user.User

User user = userApi.getUserByUsername(requester)
  
def asset = assetApi.getAsset(dataElement)

responsibilityApi.addResponsibility(AddResponsibilityRequest.builder()
  .ownerId(user.id)
  .resourceId(asset.id)
  .roleId(string2Uuid(execution.getVariable('gvRoleIdDataAnalystlevel2')))
  .resourceType(asset.resourceType)
  .build())
