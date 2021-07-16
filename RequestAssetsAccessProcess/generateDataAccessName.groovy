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

// the community name and acronym
String communityName = domainApi.getDomain(domainId).getCommunity().getName()
String nameFirst = communityName
List communityEntries = assetApi.findAssets(FindAssetsRequest.builder()
  .typeIds([string2Uuid(execution.getVariable('gvAssetTypeIdCommunityEntry'))])
  .name(communityName)
  .nameMatchMode(MatchMode.EXACT)
  .limit(Integer.MAX_VALUE)
  .build())
  .getResults()
if (communityEntries != null && !communityEntries.isEmpty()) {
  List acronyms = relationApi.findRelations(FindRelationsRequest.builder()
	.sourceId(communityEntries.first().getId())
	.relationTypeId(string2Uuid(execution.getVariable('gvRelationTypeIdRoleHasAcronym')))
	.limit(Integer.MAX_VALUE)
	.build())
	.getResults()
  if (acronyms != null && !acronyms.isEmpty()) {
	nameFirst = acronyms.first().target.getName().toString()
	loggerApi.info('found the acronym, ' + nameFirst)
  } else {
	loggerApi.info('No acronym found for the community, use the full name ' + nameFirst)
  }
} else {
  loggerApi.info('No community entry found for the community, use the full name ' + nameFirst)
}

// the requester last name
// User user = userApi.getUserByUsername(requester)
// String lastName = user.getLastName()

// current date
FORMATTER = DateTimeFormatter.ofPattern('yyyy-MM-dd')
LocalDate date = LocalDate.now()
String currentDate = date.format(FORMATTER)

// sequence
//String namePrefix = nameFirst + '.' + requestedDataAsset.getName() + '.' + lastName + '.' + currentDate
String namePrefix = nameFirst + '.' + currentDate
List dataAccesses = assetApi.findAssets(FindAssetsRequest.builder()
  //.communityId(communityId)
  //.domainId(domainId)
  .typeIds([string2Uuid(execution.getVariable('gvAssetTypeIdDataAccess'))])
  .name(namePrefix)
  .nameMatchMode(MatchMode.START)
  .excludeMeta(false)
  .limit(Integer.MAX_VALUE)
  .build()).getResults()

int maxIndex = 0
if (dataAccesses != null && dataAccesses.size() > 0) {
  loggerApi.info('found ' + dataAccesses.size() + ' data access(es) which start(s) with ' + namePrefix)
  for (Asset dataAccess : dataAccesses) {
    int index = dataAccess.getName().tokenize('.')[-1] as Integer
	maxIndex = Math.max(maxIndex, index)
  }
} else {
  loggerApi.info('found 0 data access which starts with ' + namePrefix)	
}

int sequence = maxIndex + 1
//String name = nameFirst + '.' + requestedDataAsset.getName() + '.' + lastName + '.' + currentDate + '.' + sequence
String name = nameFirst + '.' + currentDate + '.' + sequence

execution.setVariable('gvRequestedDataAccessName', name)

loggerApi.info('Access name is going to be set to ' + name)
