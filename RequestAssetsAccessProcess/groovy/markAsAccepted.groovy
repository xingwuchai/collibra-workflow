import com.collibra.dgc.core.api.dto.instance.asset.ChangeAssetRequest

ACCEPTED_STATUS_ID = string2Uuid('00000000-0000-0000-0000-000000005009')

String prefix = 'scripttask, markAsAccepted, '
loggerApi.info(prefix + 'started')

def changedAsset = assetApi.changeAsset(ChangeAssetRequest.builder()
  .id(item.id)
  .statusId(ACCEPTED_STATUS_ID)
  .build())

loggerApi.info(prefix + 'ended')
