import java.time.format.DateTimeFormatter
import java.time.LocalDate
import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.model.instance.Domain
import com.collibra.dgc.core.api.model.instance.Community
import com.collibra.dgc.core.api.model.instance.Relation
import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest
import com.collibra.dgc.core.api.model.user.User
import com.collibra.dgc.core.api.dto.user.FindUsersRequest
import com.collibra.dgc.core.api.dto.MatchMode
import com.collibra.dgc.core.api.dto.instance.asset.FindAssetsRequest
import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.exceptions.DGCException

Asset dataBasket = assetApi.getAsset(item.id)

loggerApi.info('found data basket=' + dataBasket.getName())

List relations = relationApi.findRelations(FindRelationsRequest.builder()
  .targetId(dataBasket.id)
  .relationTypeId(string2Uuid(execution.getVariable('gvRelationTypeIdRoleIsessentialfor')))
  .limit(Integer.MAX_VALUE)
  .build()).getResults()

int i = 0
String requestedDataAssetId = ''
Asset requestedDataAsset = null
for (Relation relation : relations) {
  Asset sourceDataAsset = assetApi.getAsset(relation.source.id)
  if (i == 0) {
	requestedDataAssetId = sourceDataAsset.getId().toString()
	requestedDataAsset = sourceDataAsset
  }
  loggerApi.info('relation ' + i + ', id= ' + sourceDataAsset.getId().toString() + ', name=' + sourceDataAsset.getName())
  i++
}

if (requestedDataAssetId != '') {
  execution.setVariable('gvRequestedDataAssetId', requestedDataAssetId)
} else {
  def dgcError = new DGCException('Found no item in the basket')
  dgcError.setTitleCode('No item in the basket')
  loggerApi.info('Found no item in the basket')
  throw dgcError
}

// the new naming standard, CommunityName.AssetName.RequesterLastName.CurrentDate.Sequence
// we just try to get them through API here
//String requestedDataAssetId = execution.getVariable('gvRequestedDataAssetId')
//Asset requestedDataAsset = assetApi.getAsset(string2Uuid(requestedDataAssetId))

def domainId = requestedDataAsset.getDomain().getId()
def communityId = domainApi.getDomain(domainId).getCommunity().getId()

// the community name
String communityName = domainApi.getDomain(domainId).getCommunity().getName()

// the requester last name
User user = userApi.getUserByUsername(requester)
String lastName = user.getLastName()

// current date
FORMATTER = DateTimeFormatter.ofPattern('yyyy-MM-dd')
LocalDate date = LocalDate.now()
String currentDate = date.format(FORMATTER)

// sequence
String namePrefix = communityName + '.' + requestedDataAsset.getName() + '.' + lastName + '.' + currentDate
List result = assetApi.findAssets(FindAssetsRequest.builder()
  //.communityId(communityId)
  //.domainId(domainId)
  .name(namePrefix)
  .nameMatchMode(MatchMode.START)
  .excludeMeta(false)
  .limit(Integer.MAX_VALUE)
  .build()).getResults()

int sequence = result.size() + 1

loggerApi.info('found ' + result.size() + ' starts with ' + namePrefix)

String name = communityName + '.' + requestedDataAsset.getName() + '.' + lastName + '.' + currentDate + '.' + sequence

execution.setVariable('gvRequestedDataAccessName', name)

loggerApi.info('Access name is going to be set to ' + name)

