
String prefix = 'scripttask, notifyRequesterAccepted, '
loggerApi.info(prefix + 'started')

def usersIds = users.getUserIds("user(${requester})")
mail.sendMails(usersIds, "usageAccepted", null, execution)

loggerApi.info(prefix + 'ended')
