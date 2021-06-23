import com.collibra.dgc.core.api.model.instance.Asset

Set assetIds = []

Boolean isDataSet = execution.getVariable('gvContainsDataSet')
if (isDataSet) {
  Asset datasetAsset = execution.getVariable('gvDataSetAsset')
  assetIds.add(datasetAsset.getId())
  List dataElements = execution.getVariable('gvDataElements')
  if (dataElements != null && dataElements.size()> 0) {
    dataElements.each { dataElement ->
      assetIds.add(dataElement.getId())
    }
  }
} else {
  Asset reportAsset = execution.getVariable('gvReportAsset')
  assetIds.add(reportAsset.getId())
}

execution.setVariable('gvDataAnalystLevel2Assets', assetIds)
