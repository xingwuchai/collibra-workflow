import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.dto.instance.asset.SetAssetAttributesRequest
import com.collibra.dgc.core.api.dto.instance.attribute.AddAttributeRequest
import com.collibra.dgc.core.api.dto.instance.attribute.FindAttributesRequest
import java.awt.event.ItemEvent
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import com.collibra.dgc.core.api.dto.instance.asset.ChangeAssetRequest

// we removed the validate status (must be candidate) from this task

STATUS_INVALID = '00000000-0000-0000-0000-000000005022'

ATTRIBUTE_TYPE_REQUEST_SHORT_NAME = 'Request Short Name'
ATTRIBUTE_TYPE_PURPOSE = 'Purpose'
ATTRIBUTE_TYPE_EFFECTIVE_START_DATE = 'Effective Start Date'
ATTRIBUTE_TYPE_EFFECTIVE_END_DATE = 'Effective End Date'
ATTRIBUTE_TYPE_REQUESTER_NAME = 'Requester Name'
ATTRIBUTE_TYPE_REQUESTER_ORGANIZATION = 'Requester Organization'
ATTRIBUTE_TYPE_AUDIENCE = 'Audience'
ATTRIBUTE_TYPE_DATA_AGGREGATION = 'Data Aggregation'
ATTRIBUTE_TYPE_DATA_FILTERS = 'Data Filters'
ATTRIBUTE_TYPE_SUBMISSION_DATE = 'Submission Date'
ATTRIBUTE_TYPE_SECURITY_CLASSIFICATION = 'Security Classification'

def setAssetAttributesByVariableName(assetId, attributeTypeName, variableName) {
  loggerApi.info('setAssetAttributesByVariableName, ' + attributeTypeName + ', ' + variableName)	
  def attributeValue = execution.getVariable(variableName.toString()).toString()
  if (attributeValue) {
	def attributeTypeId = attributeTypeApi.getAttributeTypeByName(attributeTypeName).getId()
	assetApi.setAssetAttributes(SetAssetAttributesRequest.builder()
  	  .assetId(assetId)
	  .typeId(attributeTypeId)
	  .values([attributeValue])
	  .build())
  }
}

def setAssetAttributesByValue(assetId, attributeTypeName, value) {
  loggerApi.info('setAssetAttributesByValue, ' + attributeTypeName + ', ' + value)	
  def attributeTypeId = attributeTypeApi.getAttributeTypeByName(attributeTypeName).getId()
  assetApi.setAssetAttributes(SetAssetAttributesRequest.builder()
    .assetId(assetId)
  	.typeId(attributeTypeId)
	.values([value])
	.build())
}
  
def setAssetTimeAttribute(assetId, attributeTypeName, variableName) {
  loggerApi.info('setAssetTimeAttribute, ' + attributeTypeName + ', ' + variableName)	
  def attributeValue = execution.getVariable(variableName.toString())
  def attributeTypeId = attributeTypeApi.getAttributeTypeByName(attributeTypeName).getId()
  attributeApi.addAttribute(AddAttributeRequest.builder()
	.assetId(assetId)
	.typeId(attributeTypeId)
	.value(attributeValue.getTime() as String)
	.build())
}
	
	
String prefix = 'scripttask, initDataAccessRequest, '
loggerApi.info(prefix + 'started')

setAssetAttributesByVariableName(item.id, ATTRIBUTE_TYPE_REQUEST_SHORT_NAME, 'requestShortName')
setAssetAttributesByVariableName(item.id, ATTRIBUTE_TYPE_PURPOSE, 'accessRequestReason')
setAssetTimeAttribute(item.id, ATTRIBUTE_TYPE_EFFECTIVE_START_DATE, 'startTime')
setAssetTimeAttribute(item.id, ATTRIBUTE_TYPE_EFFECTIVE_END_DATE, 'endTime')
setAssetAttributesByVariableName(item.id, ATTRIBUTE_TYPE_REQUESTER_NAME, 'requesterName')
setAssetAttributesByVariableName(item.id, ATTRIBUTE_TYPE_DATA_AGGREGATION, 'requesterOrganization')
setAssetAttributesByVariableName(item.id, ATTRIBUTE_TYPE_AUDIENCE, 'audience')
setAssetAttributesByVariableName(item.id, ATTRIBUTE_TYPE_DATA_AGGREGATION, 'dataAggregation')
setAssetAttributesByVariableName(item.id, ATTRIBUTE_TYPE_DATA_FILTERS, 'dataFilters')

// current date
FORMATTER = DateTimeFormatter.ofPattern('yyyy-MM-dd')
LocalDate date = LocalDate.now()
String currentDate = date.format(FORMATTER)
setAssetAttributesByValue(item.id, ATTRIBUTE_TYPE_SUBMISSION_DATE, currentDate)

String requestedDataSetId = execution.getVariable('gvRequestedDataSetId')
Asset requestedDataSet = assetApi.getAsset(string2Uuid(requestedDataSetId))
List securityClassifications = attributeApi.findAttributes(FindAttributesRequest.builder()
  .assetId(string2Uuid(requestedDataSetId))
  .typeIds([attributeTypeApi.getAttributeTypeByName(ATTRIBUTE_TYPE_SECURITY_CLASSIFICATION).getId()])
  .build())
  .getResults()
if (securityClassifications != null && !securityClassifications.isEmpty()) {
  loggerApi.info(prefix + 'security classification=' + securityClassifications.first().getValueAsString())
  setAssetAttributesByValue(item.id, ATTRIBUTE_TYPE_SECURITY_CLASSIFICATION, securityClassifications.first().getValueAsString())
}

// we may want to move the Data Access to another place, (default is Business Analyst Cimmunity)
// we are doing the same plave as the Data Set for now
def domainId = requestedDataSet.getDomain().getId()
Asset dataAccess = assetApi.getAsset(item.id)
String dataAccessName = execution.getVariable('gvRequestedDataAccessName')
assetApi.changeAsset(ChangeAssetRequest.builder()
  .name(dataAccessName)
  .displayName(dataAccessName)
  .statusId(string2Uuid(STATUS_INVALID))
  .domainId(domainId)
  .id(item.id)
  .build())
execution.setVariable('dataAccess', dataAccess)

loggerApi.info(prefix + 'ended')
