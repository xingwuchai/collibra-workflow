import com.collibra.dgc.core.api.model.instance.Asset

Asset dataAsset = execution.getVariable('gvDataSetAsset')
Set assetIds = []
assetIds.add(dataAsset.getId())

Boolean isDataSet = execution.getVariable('gvContainsDataSet')
List dataElements = execution.getVariable('gvDataElements')
if (isDataSet && dataElements != null && dataElements.size()> 0) {
   dataElements.each { dataElement ->
	 assetIds.add(dataElement.getId())
   }
}
execution.setVariable('gvDataAnalystLevel2Assets', assetIds)
