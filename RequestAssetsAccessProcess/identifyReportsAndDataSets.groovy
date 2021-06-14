import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest
import com.collibra.dgc.core.api.dto.meta.assettype.FindAssetTypesRequest
import com.collibra.dgc.core.api.dto.meta.assettype.FindSubAssetTypesRequest
import com.collibra.dgc.core.exceptions.DGCException

def accessId = item.id
Set dataSets = []
Set reports = []
ASSET_IS_ESSENTIAL_FOR_DATA_USAGE = '00000000-0000-0000-0000-000000007061'
REPORT_ASSET_TYPE_ID = '00000000-0000-0000-0000-000000031102'
DATA_SET_ASSET_TYPE_ID = '00000000-0000-0000-0001-000400000001'

String prefix = 'scripttask, identifyReportsAndDataSets, '
loggerApi.info(prefix + 'started')

def relations = relationApi.findRelations(FindRelationsRequest.builder()
  .targetId(accessId)
  .relationTypeId(string2Uuid(ASSET_IS_ESSENTIAL_FOR_DATA_USAGE))
  .limit(Integer.MAX_VALUE)
  .build()).getResults()

def findReportsChildrenAssetTypesRequest = FindSubAssetTypesRequest.builder()
  .includeParent(true)
  .assetTypeId(string2Uuid(REPORT_ASSET_TYPE_ID))
  .build()

def findDataSetChildrenAssetTypesRequests = FindSubAssetTypesRequest.builder()
  .includeParent(true)
  .assetTypeId(string2Uuid(DATA_SET_ASSET_TYPE_ID))
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
	loggerApi.error(prefix + 'EXCEPTION, the data basket has unsupported asset types')
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

loggerApi.info(prefix + 'containsDataSets=' + dataSets.size())
loggerApi.info(prefix + 'containsReports=' + reports.size())

loggerApi.info(prefix + 'ended')
