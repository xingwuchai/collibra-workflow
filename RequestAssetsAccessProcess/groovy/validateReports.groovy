import com.collibra.dgc.core.api.dto.instance.responsibility.FindResponsibilitiesRequest
import com.collibra.dgc.core.api.model.ResourceType

String prefix = 'scripttask, validateReprts, '
loggerApi.info(prefix + 'started')

Set reports = execution.getVariable('reportsAssetList')

def reportDataStewardsChecks = []
//OWNER_ROLE_ID = '00000000-0000-0000-0000-000000005040'

// check if reports have Business data steward
BUSINESS_DATA_STEWARD_ROLE_ID = '7942c273-5edf-4919-aba1-6a3ef3e31549'

reports.each {
  loggerApi.info('validate report=' + it.toString())
  def responsibilities = responsibilityApi.findResponsibilities(FindResponsibilitiesRequest.builder()
    .roleIds([string2Uuid(BUSINESS_DATA_STEWARD_ROLE_ID)])
    .resourceIds([it])
    .includeInherited(false)
    .limit(Integer.MAX_VALUE)
    .build()).getResults()

  def businessDataStewards = responsibilities.collect {getUserFromResponsibility(it)}.findAll()
  reportBusinessDataStewardsChecks << businessDataStewards.asBoolean()
}

execution.setVariable('validReports', reportBusinessDataStewardsChecks.every { it == true })

loggerApi.info(prefix + 'validReports=' + execution.getVariable('validReports'))
loggerApi.info(prefix + 'ended')

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
