import com.collibra.dgc.core.api.model.user.User

Optional<User> actingDataPrivacyOfficer = userApi.getCurrentUser()
User user = actingDataPrivacyOfficer.get()
loggerApi.info('acting data privacy officer= ' + user.getUserName())
execution.setVariable('gvActingDataPrivacyOfficer', user.getUserName())
