import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.model.user.User

String prefix = 'scripttask, prepareSnTicketEmail, '
loggerApi.info(prefix + 'started')

loggerApi.info(prefix + 'requester: ' + requester)
loggerApi.info(prefix + 'techSteward: ' + techSteward)

User techSteward = userApi.getUserByUsername(techSteward)
String techStewardEmail = techSteward.getEmailAddress()
String techStewardName = techSteward.getFirstName() + ' ' + techSteward.getLastName()

User requester = userApi.getUserByUsername(requester)
String requesterName = requester.getFirstName() + ' ' + requester.getLastName()
String requesterEmail = requester.getEmailAddress()

Asset dataAccess = assetApi.getAsset(item.getId())
String dataAccessName = dataAccess.getDisplayName()

// hard-coded the assignment group here, find it from the user information?
String assignmentGroup = 'Apps Web/Enterprise'
// Service-Now requires the exact same name, and do we have to assign this ticket to a person
String assignedTo = 'Xingwu Chai'
// this is the Collibra in Service-Now
String configurationItemForCollibra = 'b638e7f2db10cc5472924b9f299619da'

String emailTo = 'multdev@service-now.com'
String emailFrom = 'no-reply@collibra.com'

// need to have a standard language for the ticket title
String emailSubject = 'Collibra data access request task for ' + dataAccessName

// need to have a standard language for the ticket description
String emailBody =  '<div>Dear ' + techStewardName + ',</div>'
emailBody += '<div>' + requesterName + ' has requested this Data Access ' + dataAccessName + ' in Collibra, and the request has been approved</div>'
emailBody += '<div>As a technical steward, you are asked to finish all the necessary tasks for the request</div>'
emailBody +=  '<div>assignmentgroup:' + assignmentGroup + '</div>'
emailBody += '<div>assignto:' + assignedTo + '</div>'
emailBody +=  '<div>category:Applications</div>'
emailBody +=  '<div>configuration_item:' + configurationItemForCollibra + '</div>'
emailBody +=  '<div>requested_for:' + requesterEmail + '</div>'
emailBody +=  '<div>work:</div>'

execution.setVariable('emailTo', emailTo)
execution.setVariable('emailFrom', emailFrom)
execution.setVariable('emailSubject', emailSubject)
execution.setVariable('emailBody', emailBody)

loggerApi.info(prefix + 'emailFrom' + emailFrom)
loggerApi.info(prefix + 'emailTo' + emailTo)
loggerApi.info(prefix + 'emailSubject' + emailSubject)
loggerApi.info(prefix + 'emailBody' + emailBody)

loggerApi.info(prefix + 'ended')
