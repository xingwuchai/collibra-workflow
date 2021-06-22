import com.collibra.dgc.core.api.model.user.User

Optional<User> actingBusinessDataSteward = userApi.getCurrentUser()
User user = actingBusinessDataSteward.get()
String userExpression = 'user(' + user.getUserName() + ')'
loggerApi.info('acting business data steward= ' + userExpression)
execution.setVariable('gvActingBusinessDataSteward', user.getUserName())
execution.setVariable('gvActingBusinessDataStewardUserExpression', userExpression)
