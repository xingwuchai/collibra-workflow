import com.collibra.dgc.core.api.dto.user.FindUsersRequest
import com.collibra.dgc.core.api.dto.instance.responsibility.FindResponsibilitiesRequest
import com.collibra.dgc.core.api.model.ResourceType
import com.collibra.dgc.core.api.model.user.User

User user = userApi.getUserByUsername(requester)

def dataElementId = execution.getVariable('dataElement')

def result
  try {
    def responsibilities = responsibilityApi.findResponsibilities(FindResponsibilitiesRequest.builder()
      .roleIds([string2Uuid(execution.getVariable('gvRoleIdDataAnalystlevel2'))])
      .resourceIds([dataElementId])
      .ownerIds([user.id])
      .build()).results
    def users = responsibilities.collect { getUserFromResponsibility(it) }.findAll()
    result = users as boolean
  } catch (com.collibra.common.api.exception.ApiEntityNotFoundException e) {
    result = false
  }

  execution.setVariable('gvDataAnalystRoleAssigned', result)

  def getUserFromResponsibility(responsibility) {
    if (ResourceType.User == responsibility.owner.resourceType) {
      return userApi.getUser(responsibility.owner.id)
    } else if (ResourceType.UserGroup == responsibility.owner.resourceType) {
      return userApi.findUsers(FindUsersRequest.builder()
        .groupId(responsibility.owner.id)
        .limit(1)
        .build()).getResults()
        .find()
    }
    return null
  }

result

