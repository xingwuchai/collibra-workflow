import com.collibra.dgc.core.exceptions.DGCException

String prefix = 'scripttask, handleInvalidDataElement, '
loggerApi.info(prefix + 'started')

loggerApi.error(prefix + 'EXCEPTION, the reports do not have Business Data Stewards assigned to them.')
String errorDescription = '<p>The reports do not have Business Data Stewards assigned to them.</p><p>Please click X button to quit the current workflow</p><p>Please contact the system administrator for this issue</p>'
def dgcError = new DGCException(errorDescription)
dgcError.setTitleCode('ERROR: no Business Data Steward assigned for the data reports')
throw dgcError
