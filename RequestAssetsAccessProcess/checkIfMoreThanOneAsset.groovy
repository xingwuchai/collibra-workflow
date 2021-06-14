import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest
import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.model.instance.Relation
import com.collibra.dgc.core.exceptions.DGCException

ASSET_IS_ESSENTIAL_FOR_DATA_USAGE='00000000-0000-0000-0000-000000007061'

String prefix = 'scripttask, checkIfMoreThanOneAsset, '
loggerApi.info(prefix + 'started')

// sanity check
if (item == null) {
  loggerApi.error(prefix + 'EXCEPTION, Cannot execute workflow. The context doesn\'t provide a valid Data Access asset (no item)')
  String errorDescription = '<p>There is no valid data Access asset in the data basket</p><p>Please click X button to quit the current workflow</p><p>Please contact the system administrator for this issue</p>'
  def dgcError = new DGCException(errorDescription)
  dgcError.setTitleCode('ERROR: no valid Data Access asset')
  throw dgcError
}
if (item.id == null) {
  loggerApi.error(prefix + 'EXCEPTION, Cannot execute workflow. The context doesn\'t provide a valid Data Access asset (item id is null)')
  String errorDescription = '<p>There is no valid data Access asset in the data basket</p><p>Please click X button to quit the current workflow</p><p>Please contact the system administrator for this issue</p>'
  def dgcError = new DGCException(errorDescription)
  dgcError.setTitleCode('ERROR: no valid Data Access asset')
  throw dgcError
}
    
Asset dataBasket = assetApi.getAsset(item.id)

loggerApi.info(prefix + 'found data basket=' + dataBasket.getName())

List relations = relationApi.findRelations(FindRelationsRequest.builder()
  .targetId(dataBasket.id)
  .relationTypeId(string2Uuid(ASSET_IS_ESSENTIAL_FOR_DATA_USAGE))
  .limit(Integer.MAX_VALUE)
  .build()).getResults()

boolean dataSetCount1 = (relations.size() == 1)
execution.setVariable('gvDataAssetCount1', dataSetCount1)
loggerApi.info(prefix + 'gvDataAssetCount1=' + dataSetCount1)
loggerApi.info(prefix + 'found relations ' + relations.size())

int i = 0
String requestedDataSetId = ''
for (Relation relation : relations) {
  Asset sourceDataSet = assetApi.getAsset(relation.source.id)
  if (i == 0) {
	requestedDataSetId = sourceDataSet.getId().toString()
  }
  loggerApi.info(prefix + 'relation ' + i + ', id= ' + sourceDataSet.getId().toString() + ', name=' + sourceDataSet.getName())
  i++
}

if (requestedDataSetId != '') {
  execution.setVariable('gvRequestedDataSetId', requestedDataSetId)
} else {
  def dgcError = new DGCException('Found no item in the basket')
  dgcError.setTitleCode('No item in the basket')
  loggerApi.info(prefix + 'Found no item in the basket')
  throw dgcError
}

// if only one in the basket, get the allowedvalues for the audience dropdown
AUDIENCE_ATTRIBUTE_TYPE_ID = 'a7d57e54-eaec-4579-ba5e-1db5390ec3a2'

if (dataSetCount1) {
  loggerApi.info(prefix + 'Only one item in the basket, get the audience')
	
  List<String> audienceAllowedValuesList = attributeTypeApi.getAttributeType(string2Uuid(AUDIENCE_ATTRIBUTE_TYPE_ID)).getAllowedValues()
  def map = [:]
  for (String value : audienceAllowedValuesList) {
	  loggerApi.info(prefix + ',value=' + value)
	  map[value] = value
  }
  execution.setVariable('gvAudienceAllowedValues', map)
} else {
  loggerApi.info(prefix + 'EXCEPTION, more than one item in the basket')
  String errorDescription = '<p>You can only put one item into the basket.</p><p>Please click X button to quit the current workflow</p><p>Make sure there is only one item in the basket and try again</p>'
  def dgcError = new DGCException(errorDescription)
  dgcError.setTitleCode ('ERROR: more than one item in the basket')
  throw dgcError
}

loggerApi.info(prefix + 'ended')
