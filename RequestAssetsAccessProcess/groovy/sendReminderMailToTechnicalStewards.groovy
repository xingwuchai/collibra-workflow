String prefix = 'scripttask, sendRemindermailToTechnicalStewards, '
loggerApi.info(prefix + 'started')

def usersIds = users.getUserIds("user(${techSteward})")
if (usersIds.isEmpty()){
  loggerApi.warn(prefix + 'No users to send a mail to, no mail will be send')
} else {
  mail.sendMails(usersIds, 'reminder', null, execution)
}

loggerApi.info(prefix + 'ended')
