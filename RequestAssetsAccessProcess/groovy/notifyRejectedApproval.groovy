
String prefix = 'scripttask, notifyRejectedApproval, '
loggerApi.info(prefix + 'started')
	
def usersIds = users.getUserIds("user(${requester})")
mail.sendMails(usersIds, 'usageRefused', null, execution)

loggerApi.info(prefix + 'ended')
