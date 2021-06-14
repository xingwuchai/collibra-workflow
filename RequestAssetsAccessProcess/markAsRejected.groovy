import com.collibra.dgc.core.api.dto.instance.asset.ChangeAssetRequest

REJECTED_STATUS_ID = string2Uuid('00000000-0000-0000-0000-000000005010')

String prefix = 'scripttask, markAsRejected, '
loggerApi.info(prefix + 'started')

def changedAsset = assetApi.changeAsset(ChangeAssetRequest.builder()
  .id(item.id)
  .statusId(REJECTED_STATUS_ID)
  .build())

loggerApi.info(prefix + 'ended')
