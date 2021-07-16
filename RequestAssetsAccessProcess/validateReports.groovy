import com.collibra.dgc.core.api.dto.instance.responsibility.FindResponsibilitiesRequest
import com.collibra.dgc.core.api.model.ResourceType

Set reports = execution.getVariable('reportsAssetList')

def reportDataStewardsChecks = []

reports.each {
  loggerApi.info('validate report=' + it.toString())
  def responsibilities = responsibilityApi.findResponsibilities(FindResponsibilitiesRequest.builder()
    .roleIds([string2Uuid(execution.getVariable('gvRoleIdBusinessDataSteward'))])
    .resourceIds([it])
    .includeInherited(false)
    .limit(Integer.MAX_VALUE)
    .build()).getResults()

  def businessDataStewards = responsibilities.collect {getUserFromResponsibility(it)}.findAll()
  reportBusinessDataStewardsChecks << businessDataStewards.asBoolean()
}

execution.setVariable('validReports', reportBusinessDataStewardsChecks.every { it == true })

loggerApi.info('validReports=' + execution.getVariable('validReports'))

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
