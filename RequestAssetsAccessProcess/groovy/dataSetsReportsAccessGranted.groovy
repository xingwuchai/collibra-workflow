String prefix = 'scripttask, dataSetsReportsAccessGranted, '
loggerApi.info(prefix + 'started')

execution.setVariable('gvDataSetsReportsAccessGranted', true)

loggerApi.info(prefix + 'ended')

