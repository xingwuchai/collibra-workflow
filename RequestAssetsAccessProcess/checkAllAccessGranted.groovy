String prefix = 'scripttask, checkAllAccessGranted, '
loggerApi.info(prefix + 'started')

int total = execution.getVariable('techStewards').size()
int currentCount = execution.getVariable('grantedAccessCounter')
execution.setVariable('grantedAccessCounter', ++currentCount)
if (currentCount == total) {
  execution.setVariable('allAccessGranted', true)
}

loggerApi.info(prefix + 'ended')
