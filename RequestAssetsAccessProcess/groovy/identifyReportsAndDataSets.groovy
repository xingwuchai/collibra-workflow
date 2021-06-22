import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest
import com.collibra.dgc.core.api.dto.meta.assettype.FindAssetTypesRequest
import com.collibra.dgc.core.api.dto.meta.assettype.FindSubAssetTypesRequest
import com.collibra.dgc.core.exceptions.DGCException

def accessId = item.id
Set dataSets = []
Set reports = []

def relations = relationApi.findRelations(FindRelationsRequest.builder()
  .targetId(accessId)
  .relationTypeId(string2Uuid(execution.getVariable('gvRelationTypeIdRoleIsessentialfor')))
  .limit(Integer.MAX_VALUE)
  .build()).getResults()

def findReportsChildrenAssetTypesRequest = FindSubAssetTypesRequest.builder()
  .includeParent(true)
  .assetTypeId(string2Uuid(execution.getVariable('gvAssetTypeIdReport')))
  .build()

def findDataSetChildrenAssetTypesRequests = FindSubAssetTypesRequest.builder()
  .includeParent(true)
  .assetTypeId(string2Uuid(execution.getVariable('gvAssetTypeIdDataSet')))
  .build()

def reportChildrenTypes = assetTypeApi.findSubTypes(findReportsChildrenAssetTypesRequest).collect { it.getId() }
def dataSetChildrenTypes = assetTypeApi.findSubTypes(findDataSetChildrenAssetTypesRequests).collect { it.getId() }

relations.each {
  def source = it.getSource()
  def sourceAssetTypeId = assetApi.getAsset(source.id).getType().getId()

  if (sourceAssetTypeId in dataSetChildrenTypes) {
	loggerApi.info('data set in the basket, ' + source.getId())
    dataSets.add(source.getId())
  } else if (sourceAssetTypeId in reportChildrenTypes) {
	loggerApi.info('report in the basket, ' + source.getId())
    reports.add(source.getId())
  } else {
	loggerApi.error('EXCEPTION, the data basket has unsupported asset types')
	String errorDescription = '<p>The data basket has unsupported asset types</p><p>Please click X button to quit the current workflow</p><p>Please contact the system administrator for this issue</p>'
	def dgcError = new DGCException(errorDescription)
	dgcError.setTitleCode('ERROR: the data basket has unsupported asset types')
	throw dgcError
  }
}

execution.setVariable('dataSetsAssetsList', dataSets)
execution.setVariable('reportsAssetList', reports)
execution.setVariable('containsDataSets', dataSets.size() > 0)
execution.setVariable('containsReports', reports.size() > 0)
execution.setVariable('dataSetsAccessGranted', dataSets.size() == 0)

loggerApi.info('containsDataSets=' + dataSets.size())
loggerApi.info('containsReports=' + reports.size())

