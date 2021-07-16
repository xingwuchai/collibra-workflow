import com.collibra.dgc.core.exceptions.DGCException

String errorDescription = ''
String reason1 = execution.getVariable('gvReasonForInvalidDataAsset1').toString()
String reason2 = execution.getVariable('gvReasonForInvalidDataAsset2').toString()
String reason3 = execution.getVariable('gvReasonForInvalidDataAsset3').toString()
String reason4 = execution.getVariable('gvReasonForInvalidDataAsset4').toString()
String reason5 = execution.getVariable('gvReasonForInvalidDataAsset5').toString()

loggerApi.info('reason1=' + reason1)
loggerApi.info('reason2=' + reason2)
loggerApi.info('reason3=' + reason3)
loggerApi.info('reason4=' + reason4)
loggerApi.info('reason5=' + reason5)

if (reason1 != null && reason1 == 'workflowNoBusinessDataStewardForDataAsset') {
  loggerApi.error('EXCEPTION, the data asset does not have a Business Data Steward assigned to it.')
  errorDescription += '<div>The data asset does not have a Business Data Steward assigned to it.</div>'
}

if (reason2 != null && reason2 ==  'workflowNoTechnicalDataStewardForDataAsset') {
  loggerApi.error('EXCEPTION, the data asset does not have a Technical Data Steward assigned to it.')
  errorDescription += '<div>The data asset does not have a Technical Data Steward assigned to it.</div>'
}
  
if (reason3 != null && reason3 == 'workflowNoDataTrusteeForDataAsset') {
  loggerApi.error('EXCEPTION, the data asset does not have a Data Trustee assigned to it.')
  errorDescription += '<div>The data asset does not have a Data Trustee assigned to it.</div>'
}

if (reason4 != null && reason4 == 'workflowNoDataPrivacyOfficers') {
  loggerApi.error('EXCEPTION, there are no data privacy officers.')
  errorDescription += '<div>There are no data privacy officers.</div>'
}

if (reason5 != null && reason5 == 'workflowDataBasketUnsupportedAssetTypes') {
  loggerApi.error('EXCEPTION, The data basket has unsupported asset types.')
  errorDescription += '<div>The data basket has unsupported asset types.</div>'
}

loggerApi.info('errorDescription=' + errorDescription)

def dgcError = new DGCException(errorDescription)
dgcError.setTitleCode('There are some error(s), Please contact the Data Governance Program Manager for help')
throw dgcError
