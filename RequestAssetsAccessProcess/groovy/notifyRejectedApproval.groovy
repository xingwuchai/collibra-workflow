
def usersIds = users.getUserIds("user(${requester})")
mail.sendMails(usersIds, 'usageRefused', null, execution)
