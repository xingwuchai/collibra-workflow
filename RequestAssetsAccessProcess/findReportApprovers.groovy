import com.collibra.dgc.core.api.dto.instance.responsibility.FindResponsibilitiesRequest
import com.collibra.dgc.core.api.model.ResourceType

USER_LIMIT=1000
Set reports = execution.getVariable('reportsAssetList')
Set approversList = []
OWNER_ROLE_ID ='00000000-0000-0000-0000-000000005040'

String prefix = 'scripttask, findReportApprovers, '
loggerApi.info(prefix + 'started')

reports.each { reportId ->
  def responsibilities = responsibilityApi.findResponsibilities(FindResponsibilitiesRequest.builder()
    .roleIds([string2Uuid(OWNER_ROLE_ID)])
    .resourceIds([reportId])
    .limit(Integer.MAX_VALUE)
    .build()).getResults()
  def users = getUsersFromResponsibilities(responsibilities, USER_LIMIT)
  //def users = responsibilities.collectMany {responsibilityApi.getUsers(it.id)}
  approversList.add(users.collect { user -> 'user(' + user.getUserName() + ')'}.join(','))
}

execution.setVariable('reportsApproverUserExpression', approversList.join(','))

loggerApi.info('report approver list=' + approversList.join(','))
loggerApi.info(prefix + 'ended')

def getUsersFromResponsibilities(responsibilities, limit) {
  result = []
  for (responsibility in responsibilities) {
    result.addAll(getUsersFromResponsibility(responsibility, limit))
    if (result.size >= limit) {
      return result.take(limit)
    }
  }
  return result
}

def getUsersFromResponsibility(responsibility, limit) {
  if (ResourceType.User == responsibility.owner.resourceType) {
    return [userApi.getUser(responsibility.owner.id)]
  } else if (ResourceType.UserGroup == responsibility.owner.resourceType) {
    return userApi.findUsers(FindUsersRequest.builder()
      .groupId(responsibility.owner.id)
      .limit(limit)
      .build()).getResults()
  }
  return []
}
