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


ASSET_IS_ESSENTIAL_FOR_DATA_USAGE = '00000000-0000-0000-0000-000000007061'

String prefix = 'scripttask, generateDataAccessName, '
loggerApi.info(prefix + 'started')

// the new naming standard, CommunityName.AssetName.RequesterLastName.CurrentDate.Sequence
// we just try to get them through API here

String requestedDataSetId = execution.getVariable('gvRequestedDataSetId')
Asset requestedDataSet = assetApi.getAsset(string2Uuid(requestedDataSetId))
def domainId = requestedDataSet.getDomain().getId()
def communityId = domainApi.getDomain(domainId).getCommunity().getId()

// the community name
String communityName = domainApi.getDomain(domainId).getCommunity().getName()

// the requester last name
User user = userApi.findUsers(FindUsersRequest.builder()
  .name(requester)
  .build())
  .getResults()
  .first()
String lastName = user.getLastName()

// current date
FORMATTER = DateTimeFormatter.ofPattern('yyyy-MM-dd')
LocalDate date = LocalDate.now()
String currentDate = date.format(FORMATTER)

// sequence
String namePrefix = communityName + '.' + requestedDataSet.getName() + '.' + lastName + '.' + currentDate
List result = assetApi.findAssets(FindAssetsRequest.builder()
  //.communityId(communityId)
  //.domainId(domainId)
  .name(namePrefix)
  .nameMatchMode(MatchMode.START)
  .excludeMeta(false)
  .limit(Integer.MAX_VALUE)
  .build()).getResults()

int sequence = result.size() + 1

loggerApi.info(prefix + 'found ' + result.size() + ' starts with ' + namePrefix)

String name = communityName + '.' + requestedDataSet.getName() + '.' + lastName + '.' + currentDate + '.' + sequence

execution.setVariable('gvRequestedDataAccessName', name)

loggerApi.info(prefix + 'Access name is going to be set to ' + name)

loggerApi.info(prefix + 'ended')
