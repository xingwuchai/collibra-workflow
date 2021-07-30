import com.collibra.dgc.core.api.dto.instance.attribute.FindAttributesRequest
import com.collibra.dgc.core.api.model.instance.attribute.Attribute
import com.collibra.dgc.core.api.dto.instance.attachment.FindAttachmentsRequest
import com.collibra.dgc.core.api.model.instance.Attachment
import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest
import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.model.instance.Relation

def requestedDataAssetId = execution.getVariable('gvRequestedDataAssetId')
if (requestedDataAssetId != null) {
  Asset dataAsset = assetApi.getAsset(string2Uuid(requestedDataAssetId))
  loggerApi.info('data asset, id= ' + dataAsset.toString() + ', name=' + dataAsset.getName())
  List relations = relationApi.findRelations(FindRelationsRequest.builder()
	.targetId(string2Uuid(requestedDataAssetId))
	.relationTypeId(string2Uuid(execution.getVariable('gvRelationTypeIdRoleGoverns')))
	.limit(Integer.MAX_VALUE)
	.build()).getResults()
  if (relations != null && relations.size() > 0) {
	Asset termsOfUse = assetApi.getAsset(relations.first().source.id)
	loggerApi.info('term of use, id= ' + termsOfUse.getId().toString() + ', name=' + termsOfUse.getName())
	List attachments = attachmentApi.findAttachments(FindAttachmentsRequest.builder()
	  .baseResourceId(relations.first().source.id)
	  .limit(Integer.MAX_VALUE)
	  .build()).getResults()
	if (attachments != null && attachments.size() > 0) {
	  loggerApi.info('attachemnt id=' + attachments.first().getId().toString())
	  String termsValue =
	    '<p>Click on the Terms of Use and Accept to gain access to the data set. After you click I Accept, a Technical Data Steward will be assigned to submit your ticket.</p>' +
	    '<p><a href="/rest/2.0/attachments/' +  attachments.first().getId().toString() + '/file">Terms of Use</a></p>'
	  execution.setVariable('gvTermsValue', termsValue)
	  execution.setVariable('gvHasTermsOfUse', true)
	} else {
	  loggerApi.info('no attachemnt found for the term')
	  execution.setVariable('gvHasTermsOfUse', false)
	}
  } else {
    execution.setVariable('gvHasTermsOfUse', false)
  }	
} else {
  loggerApi.info('no terms of use found for the data set')
  execution.setVariable('gvhasTermsOfUse', false)
}
