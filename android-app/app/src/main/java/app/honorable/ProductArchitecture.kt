package app.honorable

enum class Screen { ONBOARDING, PERMISSIONS, HOME, MEMORIES, AI_SEARCH, RESULTS, VIEWER, TERMS, TERMS_ANALYSIS, ACTIVITY, SETTINGS, PRIVACY, GOOGLE_SIGN_IN, PAYWALL, INDEXING, EMPTY, ERROR }
enum class Plan { FREE, PLUS }
enum class PurchasePeriod { MONTHLY, ANNUAL }
sealed interface SignInState { data object SignedOut : SignInState; data object ConfigurationRequired : SignInState; data class SignedIn(val displayName: String) : SignInState }
sealed interface PurchaseState { data object StoreConfigurationRequired : PurchaseState; data object Free : PurchaseState; data class Plus(val period: PurchasePeriod) : PurchaseState }

/** UI-safe integration boundaries. Implementations must use Google OAuth/store SDK configuration supplied outside source control. */
interface AccountService { val state: SignInState; suspend fun signIn(): SignInState; suspend fun signOut() }
interface SubscriptionService { val state: PurchaseState; suspend fun purchase(period: PurchasePeriod): PurchaseState; suspend fun restore(): PurchaseState }

class UnconfiguredAccountService : AccountService {
    override val state = SignInState.ConfigurationRequired
    override suspend fun signIn() = state
    override suspend fun signOut() = Unit
}
class UnconfiguredSubscriptionService : SubscriptionService {
    override val state = PurchaseState.StoreConfigurationRequired
    override suspend fun purchase(period: PurchasePeriod) = state
    override suspend fun restore() = state
}
