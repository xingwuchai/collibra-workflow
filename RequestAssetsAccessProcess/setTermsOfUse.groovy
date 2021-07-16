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
	    '<p>Please review the attached terms of use.</p>' +
	    '<p>By clicking "I accept" buton, you agree the terms of use</p>' +
	    '<p>If you are not sure what to do now, you may use the X button on the upper-right corner to stop here, then come back later by click the "Tasks" icon</p>' +
	    '<a href="/rest/2.0/attachments/' +  attachments.first().getId().toString() + '/file">Terms of use</a>'
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
