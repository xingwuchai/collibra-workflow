import com.collibra.dgc.core.api.model.user.User

Optional<User> actingDataTrustee = userApi.getCurrentUser()
User user = actingDataTrustee.get()
loggerApi.info('acting data trustee= ' + user.getUserName())
execution.setVariable('gvActingDataTrustee', user.getUserName())
