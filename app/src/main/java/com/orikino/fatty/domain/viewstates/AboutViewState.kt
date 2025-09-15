package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.model.CurrencyVO
import com.orikino.fatty.domain.responses.*


sealed class AboutViewState {

    object OnLoadUpdateProfile : AboutViewState()
    data class OnSuccessUpdateProfile(val data : UpdateUserInfoResponse) : AboutViewState()
    data class OnFailUpdateProfile(val message: String?) : AboutViewState()

    object OnLoadingUpdateName : AboutViewState()
    data class OnSuccessUpdateName(val data: UpdateUserInfoResponse) : AboutViewState()
    data class OnFailUpdateName(val message: String?) : AboutViewState()
    object OnLoadingAbout : AboutViewState()
    data class OnSuccessAbout(val data: AboutAppResponse) : AboutViewState()
    data class OnFailAbout(val message: String) : AboutViewState()

    object OnLoadingTermCondition : AboutViewState()
    data class OnSuccessTermCondition(val data: TermConditionResponse) :AboutViewState()
    data class OnFailTermCondition(val message: String) : AboutViewState()

    object OnLoadingPrivacyPolicy : AboutViewState()
    data class OnSuccessPrivacyPolicy(val data: PrivacyPolicyResponse) : AboutViewState()
    data class OnFailPrivacyPolicy(val message: String) : AboutViewState()

    object OnLoadingHelpCenter : AboutViewState()
    data class OnSuccessHelpCenter(val data: AppHelpCenterResponse) : AboutViewState()
    data class OnFailHelpCenter(val message: String) : AboutViewState()

    object OnLoadingTutorial : AboutViewState()
    data class OnSuccessTutorial(val data: TutorialResponse) : AboutViewState()
    data class OnFailTutorial(val message: String?) : AboutViewState()

    object OnLoadingCurrency : AboutViewState()
    data class OnSuccessCurrency(val data: List<CurrencyVO>) : AboutViewState()
    data class OnFailCurrency(val message: String) : AboutViewState()

    object OnLoadingVersionUpdate : AboutViewState()
    data class OnSuccessVersionUpdate(val data: VersionUpdateResponse) : AboutViewState()
    data class OnFailVersionUpdate(val message: String) : AboutViewState()

    object OnLoadingLogout : AboutViewState()
    data class OnSuccessLogout(val data: LogoutResponse) : AboutViewState()
    data class OnFailLogout(val message: String) : AboutViewState()

    object OnLoadingDelete : AboutViewState()
    data class OnSuccessDeleteAccount(val data : DeleteAccountResponse) : AboutViewState()
    data class OnFailDeleteAccount(val message: String) : AboutViewState()
}

sealed class TutorialViewState {
    object OnLoadingTutorial : TutorialViewState()
    data class OnSuccessTutorial(val data: TutorialResponse) : TutorialViewState()
    data class OnFailTutorial(val message: String) : TutorialViewState()
}

sealed class PrivacyPolicyViewState {
    object OnLoadingPrivacyPolicy : PrivacyPolicyViewState()
    data class OnSuccessPrivacyPolicy(val data: PrivacyPolicyResponse) : PrivacyPolicyViewState()
    data class OnFailPrivacyPolicy(val message: String) : PrivacyPolicyViewState()

    object OnLoadingTermCondition : TermConditionViewState()
    data class OnSuccessTermCondition(val data: TermConditionResponse) : TermConditionViewState()
    data class OnFailTermCondition(val message: String) : TermConditionViewState()
}

sealed class TermConditionViewState {
    object OnLoadingTermCondition : TermConditionViewState()
    data class OnSuccessTermCondition(val data: TermConditionResponse) : TermConditionViewState()
    data class OnFailTermCondition(val message: String) : TermConditionViewState()
}

sealed class AccountViewState {
    object OnLoadingLogout : AccountViewState()
    data class OnSuccessLogout(val data: LogoutResponse) : AccountViewState()
    data class OnFailLogout(val message: String) : AccountViewState()
}

sealed class BaseViewState {
    data class OnSuccessVersionUpdate(val data: VersionUpdateResponse) : BaseViewState()
    data class OnFailVersionUpdate(val message: String) : BaseViewState()

    data class OnBoardAdSuccess(val data: OnBoardAdResponse) : BaseViewState()
    data class OnBoardAdFail(val message: String) : BaseViewState()

    data class AdsEngagementSuccess(val data: AdsEngagementResponse) : BaseViewState()
    data class AdsEngagementFail(val message: String) : BaseViewState()
}

sealed class ChooseCurrencyViewState() {
    data class OnSuccessCurrency(val data: CurrencyResponse) : ChooseCurrencyViewState()
    data class OnFailCurrency(val message: String) : ChooseCurrencyViewState()
}
