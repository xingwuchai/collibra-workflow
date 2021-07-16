import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.dto.instance.asset.SetAssetAttributesRequest
import com.collibra.dgc.core.api.dto.instance.attribute.AddAttributeRequest
import com.collibra.dgc.core.api.dto.instance.attribute.FindAttributesRequest
import com.collibra.dgc.core.api.dto.instance.relation.AddRelationRequest
import java.awt.event.ItemEvent
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import com.collibra.dgc.core.api.dto.instance.asset.ChangeAssetRequest
import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest

def setAssetAttributesByVariableName(assetId, attributeTypeId, variableName) {
  loggerApi.info('setAssetAttributesByVariableName, ' + attributeTypeId + ', ' + variableName)
  def attributeValue = execution.getVariable(variableName.toString()).toString()
  if (attributeValue) {
	assetApi.setAssetAttributes(SetAssetAttributesRequest.builder()
  	  .assetId(assetId)
	  .typeId(string2Uuid(attributeTypeId))
	  .values([attributeValue])
	  .build())
  }
}

def setAssetAttributesByValue(assetId, attributeTypeId, value) {
  loggerApi.info('setAssetAttributesByValue, ' + attributeTypeId + ', ' + value)
  assetApi.setAssetAttributes(SetAssetAttributesRequest.builder()
    .assetId(assetId)
  	.typeId(string2Uuid(attributeTypeId))
	.values([value])
	.build())
}
  
def setAssetTimeAttribute(assetId, attributeTypeId, variableName) {
  loggerApi.info('setAssetTimeAttribute, ' + attributeTypeId + ', ' + variableName)	
  def attributeValue = execution.getVariable(variableName.toString())
  attributeApi.addAttribute(AddAttributeRequest.builder()
	.assetId(assetId)
	.typeId(string2Uuid(attributeTypeId))
	.value(attributeValue.getTime() as String)
	.build())
}
	
execution.getVariable('gvAttributeTypeIdRequesterName')

setAssetAttributesByVariableName(item.id, execution.getVariable('gvAttributeTypeIdRequestShortName'), 'requestShortName')
setAssetAttributesByVariableName(item.id, execution.getVariable('gvAttributeTypeIdAccessBusinessPurpose'), 'accessRequestReason')
setAssetTimeAttribute(item.id, execution.getVariable('gvAttributeTypeIdEffectiveStartDate'), 'startTime')
setAssetTimeAttribute(item.id, execution.getVariable('gvAttributeTypeIdEffectiveEndDate'), 'endTime')
setAssetAttributesByVariableName(item.id, execution.getVariable('gvAttributeTypeIdRequesterName'), 'requesterName')
setAssetAttributesByVariableName(item.id, execution.getVariable('gvAttributeTypeIdRequesterOrganization'), 'requesterOrganization')
setAssetAttributesByVariableName(item.id, execution.getVariable('gvAttributeTypeIdAudience'), 'audience')
setAssetAttributesByVariableName(item.id, execution.getVariable('gvAttributeTypeIdDataAggregation'), 'dataAggregation')
setAssetAttributesByVariableName(item.id, execution.getVariable('gvAttributeTypeIdDataFilters'), 'dataFilters')

// current date
FORMATTER = DateTimeFormatter.ofPattern('yyyy-MM-dd')
LocalDate date = LocalDate.now()
String currentDate = date.format(FORMATTER)
setAssetAttributesByValue(item.id, execution.getVariable('gvAttributeTypeIdSubmissionDate'), currentDate)

def requestedDataAssetId = string2Uuid(execution.getVariable('gvRequestedDataAssetId'))
Asset requestedDataAsset = assetApi.getAsset(requestedDataAssetId)
List securityClassifications = attributeApi.findAttributes(FindAttributesRequest.builder()
  .assetId(requestedDataAssetId)
  .typeIds([string2Uuid(execution.getVariable('gvAttributeTypeIdSecurityClassification'))])
  .build())
  .getResults()
if (securityClassifications != null && !securityClassifications.isEmpty()) {
  loggerApi.info('security classification=' + securityClassifications.first().getValueAsString())
  setAssetAttributesByValue(item.id, execution.getVariable('gvAttributeTypeIdSecurityClassification'), securityClassifications.first().getValueAsString())
}

List policyGoverns = relationApi.findRelations(FindRelationsRequest.builder()
	.targetId(requestedDataAssetId)
	.relationTypeId(string2Uuid(execution.getVariable('gvRelationTypeIdRolePolicyGoverns')))
	.limit(Integer.MAX_VALUE)
	.build())
	.getResults()
if (policyGoverns != null && policyGoverns.size() > 0) {
  loggerApi.info('policy governs source=' + policyGoverns.first().source.name)
  loggerApi.info('policy governs target=' + policyGoverns.first().target.name)
  relationApi.addRelation(AddRelationRequest.builder()
    .typeId(string2Uuid(execution.getVariable('gvRelationTypeIdRolePolicyGoverns')))
    .sourceId(policyGoverns.first().source.id)
    .targetId(item.id)
    .build()
  )
}
  
def domainId = requestedDataAsset.getDomain().getId()
Asset dataAccess = assetApi.getAsset(item.id)
String dataAccessName = execution.getVariable('gvRequestedDataAccessName')
assetApi.changeAsset(ChangeAssetRequest.builder()
  .name(dataAccessName)
  .displayName(dataAccessName)
  .statusId(string2Uuid(execution.getVariable('gvStatusIdInvalid')))
  .domainId(domainId)
  .id(item.id)
  .build())

if (requestedDataAsset.getType().getName().toString() == 'Data Set') {
  relationApi.addRelation(AddRelationRequest.builder()
	.typeId(string2Uuid(execution.getVariable('gvRelationTypeIdRoleHas')))
	.sourceId(requestedDataAsset.getId())
	.targetId(item.id)
	.build()
  )
}
