import com.collibra.dgc.core.api.dto.user.FindUsersRequest
import com.collibra.dgc.core.api.dto.instance.responsibility.FindResponsibilitiesRequest
import com.collibra.dgc.core.api.model.ResourceType

String prefix = 'scripttask, checkIfDataAnalystRoleIsAlreadyAssigned, '
loggerApi.info(prefix + 'started')

def requesterName = execution.getVariable('requester')

def requester = userApi.findUsers(FindUsersRequest.builder()
  .name(requesterName)
  .build()).getResults().get(0).id

def dataElement = execution.getVariable('dataElement')

DATA_ANALYST_LEVEL_2_ROLE_ID = string2Uuid('00000000-0000-0000-0000-000000005062')

def result
  try {
    def responsibilities = responsibilityApi.findResponsibilities(FindResponsibilitiesRequest.builder()
      .roleIds([DATA_ANALYST_LEVEL_2_ROLE_ID])
      .resourceIds([string2Uuid(dataElement)])
      .ownerIds([requester])
      .build()).results
    def users = responsibilities.collect { getUserFromResponsibility(it) }.findAll()
    result = users as boolean
  } catch (com.collibra.common.api.exception.ApiEntityNotFoundException e) {
    result = false
  }

  execution.setVariable('dataAnalystRoleAssigned', result)

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

loggerApi.info(prefix + 'ended')
