String prefix = 'scripttask, consolidateApprovers, '
loggerApi.info(prefix + 'started')

def approverUserExpression = execution.getVariable('approverUserExpression')
def reportsApproverUserExpression = execution.getVariable('reportsApproverUserExpression')

Set userExpresionSet = []

if(approverUserExpression != null) {
  userExpresionSet.add(approverUserExpression)
}

if(reportsApproverUserExpression != null) {
  userExpresionSet.add(reportsApproverUserExpression)
}

execution.setVariable('approverUserExpression', userExpresionSet.join(','))

loggerApi.info(prefix + 'consolidated approvers= ' + userExpresionSet.join(','))
loggerApi.info(prefix + 'ended')
