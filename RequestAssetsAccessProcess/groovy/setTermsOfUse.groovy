import com.collibra.dgc.core.api.dto.instance.attribute.FindAttributesRequest
import com.collibra.dgc.core.api.model.instance.attribute.Attribute
import com.collibra.dgc.core.api.dto.instance.attachment.FindAttachmentsRequest
import com.collibra.dgc.core.api.model.instance.Attachment
import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest
import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.model.instance.Relation

TERM_TYPE_ID = 'b8be81ef-f0e4-49fd-84b4-b083d81345e2'

String prefix = 'scripttask, setTermsOfUse, '
loggerApi.info(prefix + 'started')

def dataSets = execution.getVariable('dataSetsAssetsList')
def reports = execution.getVariable('reportsAssetList')
def dataItemId = null
if (dataSets.size() > 0) {
  dataItemId = dataSets.first()
}
if (reports.size() > 0) {
  dataItemId = reports.first()
}
if (dataItemId != null) {
  Asset dataItem = assetApi.getAsset(dataItemId)
  loggerApi.info(prefix + 'data item, id= ' + dataItem.toString() + ', name=' + dataItem.getName())
  List relations = relationApi.findRelations(FindRelationsRequest.builder()
	.targetId(dataItemId)
	.relationTypeId(string2Uuid(TERM_TYPE_ID))
	.limit(Integer.MAX_VALUE)
	.build()).getResults()
  if (relations != null && relations.size() > 0) {
	Asset termsOfUse = assetApi.getAsset(relations.first().source.id)
	loggerApi.info(prefix + 'term of use, id= ' + termsOfUse.getId().toString() + ', name=' + termsOfUse.getName())
	List attachments = attachmentApi.findAttachments(FindAttachmentsRequest.builder()
	  .baseResourceId(relations.first().source.id)
	  .limit(Integer.MAX_VALUE)
	  .build()).getResults()
	if (attachments != null && attachments.size() > 0) {
	  loggerApi.info(prefix + 'attachemnt id=' + attachments.first().getId().toString())
	  String termsValue =
	    '<p>Please review the attached terms of use.</p>' +
	    '<p>By clicking "I accept" buton, you agree the terms of use</p>' +
	    '<p>If you are not sure what to do now, you may use the X button on the upper-right corner to stop here, then come back later by click the "Tasks" icon</p>' +
	    '<a href="/rest/2.0/attachments/' +  attachments.first().getId().toString() + '/file">Terms of use</a>'
	  execution.setVariable('termsValue', termsValue)
	  execution.setVariable('hasTermsOfUse', true)
	} else {
	  loggerApi.info(prefix + 'no attachemnt found for the term')
	  execution.setVariable('hasTermsOfUse', false)
	}
  } else {
    execution.setVariable('hasTermsOfUse', false)
  }	
} else {
  loggerApi.info(prefix + 'no terms of use found for the data set')
  execution.setVariable('hasTermsOfUse', false)
}
loggerApi.info(prefix + 'ended')
