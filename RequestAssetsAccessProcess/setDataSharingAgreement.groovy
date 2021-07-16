import com.collibra.dgc.core.api.dto.instance.attribute.FindAttributesRequest
import com.collibra.dgc.core.api.model.instance.attribute.Attribute
import com.collibra.dgc.core.api.dto.instance.attachment.FindAttachmentsRequest
import com.collibra.dgc.core.api.model.instance.Attachment
import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest
import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.model.instance.Relation


List relations = relationApi.findRelations(FindRelationsRequest.builder()
  .sourceId(item.id)
  .relationTypeId(string2Uuid(execution.getVariable('gvRelationTypeIdRoleRequires')))
  .limit(Integer.MAX_VALUE)
  .build())
  .getResults()
if (relations != null && relations.size() > 0) {
  Asset dataSharingAgreement = assetApi.getAsset(relations.first().target.id)
  loggerApi.info('DSA, id= ' + dataSharingAgreement.getId().toString() + ', name=' + dataSharingAgreement.getName())
  List attachments = attachmentApi.findAttachments(FindAttachmentsRequest.builder()
    .baseResourceId(relations.first().target.id)
    .limit(Integer.MAX_VALUE)
    .build())
    .getResults()
  if (attachments != null && attachments.size() > 0) {
    loggerApi.info('DSA link id=' + attachments.first().getId().toString())
    String dsaLink = attachments.first().getId().toString()
    String dsaValue =
    '<p>Please review the attached data sharing aggreement.</p>' +
    '<p>By clicking "I accept" buton, you agree the data sharing agreement</p>' +
    '<p>If you are not sure what to do now, you may use the X button on the upper-right corner to stop here, then come back later by click the "Tasks" icon</p>' +
    '<a href="/rest/2.0/attachments/' + dsaLink + '/file">Data Sharing Agreement</a>'
    execution.setVariable('gvDataSharingAgreement', dsaValue)
    execution.setVariable('gvHasDataSharingAgreement', true)
  } else {
    loggerApi.info('no DSA links found for the data access')
    execution.setVariable('gvHasDataSharingAgreement', false)
  }
} else {
  loggerApi.info('no DSA asset found for the data access')
  execution.setVariable('gvHasDataSharingAgreement', false)
}
