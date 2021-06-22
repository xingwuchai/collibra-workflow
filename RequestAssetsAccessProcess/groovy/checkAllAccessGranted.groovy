
int total = execution.getVariable('gvAllDataAccessTechnicalDataStewards').size()
int currentCount = execution.getVariable('gvGrantedAccessCounter')
execution.setVariable('gvGrantedAccessCounter', ++currentCount)
if (currentCount == total) {
  execution.setVariable('gvAllAccessGranted', true)
}
