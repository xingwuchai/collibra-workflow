import com.collibra.dgc.core.api.dto.instance.asset.ChangeAssetRequest

String prefix = 'scripttask, markAsApprovalPending, '
loggerApi.info(prefix + 'started')

PENDING_STATUS_ID = string2Uuid('00000000-0000-0000-0000-000000005023')

def changedAsset = assetApi.changeAsset(ChangeAssetRequest.builder()
  .id(item.id)
  .statusId(PENDING_STATUS_ID)
  .build())

loggerApi.info(prefix + 'ended')
