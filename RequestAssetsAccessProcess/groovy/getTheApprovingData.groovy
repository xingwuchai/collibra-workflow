import java.time.format.DateTimeFormatter
import java.time.LocalDate

Boolean approvedApproveRejectReroutePrivacy = execution.getVariable('approveApproveRejectReroutePrivacy')
Boolean rejectedApproveRejectReroutePrivacy = execution.getVariable('rejectApproveRejectReroutePrivacy')
Boolean reroutedPrivacyApproveRejectReroutePrivacy = execution.getVariable('reroutePrivacyApproveRejectReroutePrivacy')

Boolean approvedApproveRejectRerouteDataTrustee = execution.getVariable('approveApproveRejectRerouteDataTrustee')
Boolean rejectedApproveRejectRerouteDataTrustee = execution.getVariable('rejectApproveRejectRerouteDataTrustee')
Boolean reroutedDataTrusteeApproveRejectRerouteDataTrustee = execution.getVariable('retouteDataTrusteeApproveRejectRerouteDataTrustee')

Boolean approvedApproveRejectByDataTrustee = execution.getVariable('approveApproveRejectByDataTrustee')
Boolean rejectedApproveRejectByDataTrustee = execution.getVariable('rejectApproveRejectByDataTrustee')

def decisionMaker = null
Boolean approved = false
String comment = ''

// current date
FORMATTER = DateTimeFormatter.ofPattern('yyyy-MM-dd')
LocalDate date = LocalDate.now()
String currentDate = date.format(FORMATTER)

if (approvedApproveRejectByDataTrustee || rejectedApproveRejectByDataTrustee) {
  // rerouted to data trustee, and approved/rejected there
  if (approvedApproveRejectByDataTrustee) {
    approved = true
  } else {
    approved = false
  }
  decisionMaker = execution.getVariable('gvActingDataTrustee')
  comment = execution.getVariable('commentApproveRejectByDataTrustee').toString()
} else {
  // rerouted to Data Privacy Officer, and approved/rejected by the actingBusinessDataSteward
  if (approvedApproveRejectRerouteDataTrustee || rejectedApproveRejectRerouteDataTrustee) {
	if (approvedApproveRejectRerouteDataTrustee) {
	  approved = true
	} else {
	  approved = false
	}
    decisionMaker = execution.getVariable('gvActingBusinessDataSteward')
	comment = execution.getVariable('commentApproveRejectRerouteDataTrustee').toString() 
  } else {
	// approved/rejected by the actingBusinessDataSteward without rerouting
    if (approvedApproveRejectReroutePrivacy || rejectedApproveRejectReroutePrivacy) {
      if (approvedApproveRejectReroutePrivacy) {
	    approved = true
      } else {
	    approved = false
      }
      decisionMaker = execution.getVariable('gvActingBusinessDataSteward')
      comment = execution.getVariable('commentApproveRejectReroutePrivacy').toString()
    }
  }
}

execution.setVariable('gvDecisionMaker', decisionMaker)
execution.setVariable('gvDecisionDate', currentDate)
execution.setVariable('gvDecisionApproved', approved)
execution.setVariable('gvDecisionComment', comment)
