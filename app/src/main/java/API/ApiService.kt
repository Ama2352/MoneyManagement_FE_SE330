package API

import DI.Models.Analysis.BarChart.DailySummary
import DI.Models.Analysis.BarChart.MonthlySummary
import DI.Models.Analysis.BarChart.WeeklySummary
import DI.Models.Analysis.BarChart.YearlySummary
import DI.Models.Analysis.CategoryBreakdown
import DI.Models.Auth.RefreshTokenRequest
import DI.Models.Auth.RefreshTokenResponse
import DI.Models.Auth.SignInRequest
import DI.Models.Auth.SignUpRequest
import DI.Models.Category.AddCategoryRequest
import DI.Models.Category.Category
import DI.Models.Transaction.Transaction
import DI.Models.Transaction.TransactionDetail
import DI.Models.Transaction.CreateTransactionRequest
import DI.Models.Transaction.UpdateTransactionRequest
import DI.Models.Category.UpdateCategoryRequest
import DI.Models.Ocr.OcrData
import DI.Models.UserInfo.AvatarUploadResponse
import DI.Models.UserInfo.Profile
import DI.Models.UserInfo.UpdatedProfile
import DI.Models.Wallet.AddWalletRequest
import DI.Models.Wallet.Wallet
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Authentication
    @POST("Accounts/SignUp")
    suspend fun signUp(@Body request: SignUpRequest): Response<ResponseBody>

    @POST("Accounts/SignIn")
    suspend fun signIn(@Body request: SignInRequest): Response<ResponseBody>

    @POST("Accounts/RefreshToken")
    suspend fun refreshToken(@Body token: RefreshTokenRequest): RefreshTokenResponse

    // Categories
    @GET("Categories")
    suspend fun getCategories(): List<Category>

    @POST("Categories")
    suspend fun addCategory(@Body request: AddCategoryRequest): Response<Category>

    @GET("Categories/{id}")
    suspend fun getCategoryById(@Path("id") id: String): Response<Category>

    @PUT("Categories")
    suspend fun updateCategory(@Body request: UpdateCategoryRequest): Response<Category>

    @DELETE("Categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): Response<ResponseBody>



    // Wallets
    @GET("Wallets")
    suspend fun getWallets(): List<Wallet>

    @GET("Wallets/{id}")
    suspend fun getWalletById(@Path("id") id: String): Response<Wallet>

    @POST("Wallets")
    suspend fun createWallet(@Body request: AddWalletRequest): Response<Wallet>

    @PUT("Wallets")
    suspend fun updateWallet(@Body wallet: Wallet): Response<Wallet>

    @DELETE("Wallets/{id}")
    suspend fun deleteWallet(@Path("id") id: String): Response<ResponseBody>

    // Transactions
    @GET("Transactions")
    suspend fun getAllTransactions(): Response<List<Transaction>>

    @GET("Transactions/wallet/{walletId}")
    suspend fun getTransactionsByWalletId(@Path("walletId") walletId: String): Response<List<Transaction>>

    @GET("Transactions/{id}")
    suspend fun getTransactionById(@Path("id") id: String): Response<Transaction>

    @POST("Transactions")
    suspend fun createTransaction(@Body transaction: CreateTransactionRequest): Response<Transaction>

    @PUT("Transactions")
    suspend fun updateTransaction(@Body transaction: UpdateTransactionRequest): Response<Transaction>

    @DELETE("Transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String): Response<String>

    @GET("Transactions/date-range")
    suspend fun getTransactionsByDateRange(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("type") type: String? = null,
        @Query("category") category: String? = null,
        @Query("timeRange") timeRange: String? = null,
        @Query("dayOfWeek") dayOfWeek: String? = null
    ): Response<List<TransactionDetail>>

    @GET("Transactions/search")
    suspend fun searchTransactions(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("type") type: String? = null,
        @Query("categoryName") categoryName: String? = null,
        @Query("walletName") walletName: String? = null,
        @Query("amountRange") amountRange: String? = null,
        @Query("keywords") keywords: String? = null,
        @Query("timeRange") timeRange: String? = null,
        @Query("dayOfWeek") dayOfWeek: String? = null
    ): Response<List<TransactionDetail>>

    @GET("Statistics/category-breakdown")
    suspend fun getCategoryBreakdown(@Query("startDate") startDate: String, @Query("endDate") endDate: String): List<CategoryBreakdown>

    // Ocr
    @POST("Gemini/extract-ocr")
    suspend fun extractOcr(@Body ocrString: String): OcrData

    // Profile
    @GET("Accounts/profile")
    suspend fun getProfile(): Profile

    @Multipart
    @POST("Accounts/avatar")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): Response<AvatarUploadResponse>

    @PUT("Accounts/profile")
    suspend fun updateProfile(@Body updatedProfile: UpdatedProfile): Response<Void>

    @GET("Accounts/users/{userId}")
    suspend fun getOtherUserProfile(@Path("userId") userId: String): Profile

    // Calendar
    @GET("Calendar/daily")
    suspend fun getDailySummary(@Query("date") date: String): DailySummary

    @GET("Calendar/weekly")
    suspend fun getWeeklySummary(@Query("startDate") startDate: String): WeeklySummary

    @GET("Calendar/monthly")
    suspend fun getMonthlySummary(@Query("year") year: String, @Query("month") month: String): MonthlySummary

    @GET("Calendar/yearly")
    suspend fun getYearlySummary(@Query("year") year: String): YearlySummary
}
