import com.collibra.dgc.core.api.dto.instance.asset.ChangeAssetRequest

def changedAsset = assetApi.changeAsset(ChangeAssetRequest.builder()
  .id(item.id)
  .statusId(string2Uuid(execution.getVariable('gvStatusIdAccepted')))
  .build())
