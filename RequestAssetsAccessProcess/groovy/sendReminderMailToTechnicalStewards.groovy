
def usersIds = users.getUserIds("user(${techSteward})")
if (usersIds.isEmpty()){
  loggerApi.warn('No users to send a mail to, no mail will be send')
} else {
  mail.sendMails(usersIds, 'reminder', null, execution)
}
