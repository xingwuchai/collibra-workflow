
def usersIds = users.getUserIds("user(${requester})")
mail.sendMails(usersIds, "usageAccepted", null, execution)
