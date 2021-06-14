import com.collibra.dgc.core.api.dto.instance.asset.ChangeAssetRequest

ACCESS_GRANTED_STATUS_ID = string2Uuid('00000000-0000-0000-0000-000000005024')

String prefix = 'scripttask, markAsAccessGrantged, '
loggerApi.info(prefix + 'started')

def changedAsset = assetApi.changeAsset(ChangeAssetRequest.builder()
  .id(item.id)
  .statusId(ACCESS_GRANTED_STATUS_ID)
  .build())

loggerApi.info(prefix + 'ended')
