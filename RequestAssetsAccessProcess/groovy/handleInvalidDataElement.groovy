import com.collibra.dgc.core.exceptions.DGCException

String prefix = 'scripttask, handleInvalidDataElement, '
loggerApi.info(prefix + 'started')

if (execution.getVariable("reasonForInvalidDataElements") == 'workflowNoBusinessDataStewardForDataSet') {
  loggerApi.error(prefix + 'EXCEPTION, the data set does not have a Business Data Steward assigned to it.')
  String errorDescription = '<p>The data set does not have a Business Data Steward assigned to it.</p><p>Please click X button to quit the current workflow</p><p>Please contact the system administrator for this issue</p>'
  def dgcError = new DGCException(errorDescription)
  dgcError.setTitleCode('ERROR: no Business Data Steward assigned for the data set')
  throw dgcError
}
if (execution.getVariable("reasonForInvalidDataElements") == 'workflowNoBusinessDataStewardForColumns') {
  loggerApi.error(prefix + 'EXCEPTION, the data columns do not have Business Data Stewards assigned to them.')
  String errorDescription = '<p>The data columns do not have Business Data Stewards assigned to them.</p><p>Please click X button to quit the current workflow</p><p>Please contact the system administrator for this issue</p>'
  def dgcError = new DGCException(errorDescription)
  dgcError.setTitleCode('ERROR: no Business Data Steward assigned for the data columns')
  throw dgcError
}
