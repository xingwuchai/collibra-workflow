import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest
import com.collibra.catalog.api.component.businessmodel.dto.FindDataElementsRequest

Set reportsAssetsIds = execution.getVariable("reportsAssetList")

def usageId = item.id
Set assets = []

ASSET_IS_ESSENTIAL_FOR_DATA_USAGE='00000000-0000-0000-0000-000000007061'

String prefix = 'scripttask, findAssetsThatNeedToHaveDataAnalystRoleAssigned, '
loggerApi.info(prefix + 'started')

def relations =  relationApi.findRelations(FindRelationsRequest.builder()
  .targetId(usageId)
  .relationTypeId(string2Uuid(ASSET_IS_ESSENTIAL_FOR_DATA_USAGE))
  .limit(Integer.MAX_VALUE)
  .build()).getResults()

relations.each {
  // the data set is the source of the relation
  def term = it.getSource()
  assets.add(term)
  if (!(term.getId() in reportsAssetsIds)) {
    assets.addAll(findDataElements(term.id))
  }
}

def findDataElements(dataSetId) {
return dataSetApi.findDataElements(FindDataElementsRequest.builder()
  .dataSetId(dataSetId)
  .limit(Integer.MAX_VALUE)
  .build()).results
}

def assetIds = assets.collect { uuid2String(it.id) }

execution.setVariable('dataAnalystLevel2Assets', assetIds)

loggerApi.info(prefix + 'ended')
