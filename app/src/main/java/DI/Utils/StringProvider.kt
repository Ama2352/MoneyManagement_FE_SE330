package DI.Utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.moneymanagement_frontend.R

@Composable
fun rememberAppStrings(): AppStrings {
    return AppStrings(
        // Transaction types
        income = stringResource(R.string.income),
        expense = stringResource(R.string.expense),
        
        // Common actions
        add = stringResource(R.string.add),
        save = stringResource(R.string.save),
        cancel = stringResource(R.string.cancel),
        delete = stringResource(R.string.delete),
        edit = stringResource(R.string.edit),
        close = stringResource(R.string.close),
        back = stringResource(R.string.back),
        
        // Transaction UI
        transactions = stringResource(R.string.transactions),
        addTransaction = stringResource(R.string.add_transaction),
        recentTransactions = stringResource(R.string.recent_transactions),
        noTransactionsFound = stringResource(R.string.no_transactions_found),
        quickActions = stringResource(R.string.quick_actions),
        addIncome = stringResource(R.string.add_income),
        addExpense = stringResource(R.string.add_expense),
        netBalance = stringResource(R.string.net_balance),
        detailedAdd = stringResource(R.string.detailed_add),
          
        // Filters
        filters = stringResource(R.string.filters),
        
        // Filter values (for comparison/logic)
        filterAllValue = stringResource(R.string.filter_all_value),
        filterTodayValue = stringResource(R.string.filter_today_value),
        filterThisWeekValue = stringResource(R.string.filter_this_week_value),
        filterThisMonthValue = stringResource(R.string.filter_this_month_value),
        filterIncomeValue = stringResource(R.string.filter_income_value),
        filterExpenseValue = stringResource(R.string.filter_expense_value),
        
        // Form fields
        amount = stringResource(R.string.amount),
        title = stringResource(R.string.title),
        description = stringResource(R.string.description),
        category = stringResource(R.string.category),
        wallet = stringResource(R.string.wallet),
        dateTime = stringResource(R.string.date_time),
        
        // Selection
        selectCategory = stringResource(R.string.select_category),
        selectWallet = stringResource(R.string.select_wallet),


          // Quick Add Dialog specific
        descriptionOptional = stringResource(R.string.description_optional),
        balance = stringResource(R.string.available_balance),

        // Actions
        analytics = stringResource(R.string.analytics),
        search = stringResource(R.string.search),
        refresh = stringResource(R.string.refresh),


        // Message strings
        noTransactionsYet = stringResource(R.string.no_transactions_yet),
        startAddingTransactions = stringResource(R.string.start_adding_transactions),
        viewAllTransactions = stringResource(R.string.view_all_transactions),

        
        // General UI
        all = stringResource(R.string.all),
        clear = stringResource(R.string.clear),
        
        // Search specific strings (need to check if these exist in strings.xml)
        type = stringResource(R.string.type),
        timeRange = stringResource(R.string.time_range),
        dayOfWeek = stringResource(R.string.day_of_week),
        startDate = stringResource(R.string.start_date),
        endDate = stringResource(R.string.end_date),
        

          // Days of week
        monday = stringResource(R.string.monday),
        tuesday = stringResource(R.string.tuesday),
        wednesday = stringResource(R.string.wednesday),
        thursday = stringResource(R.string.thursday),
        friday = stringResource(R.string.friday),
        saturday = stringResource(R.string.saturday),
        sunday = stringResource(R.string.sunday),


        unknownWallet = stringResource(R.string.wallet_form_unknown_wallet),
          // Transaction Screen specific strings
        trackYourMoneyFlow = stringResource(R.string.track_your_money_flow),
        totalSummary = stringResource(R.string.total_summary),
        showMore = stringResource(R.string.show_more),
        more = stringResource(R.string.more),
        recentTransactionsCount = stringResource(R.string.recent_transactions_count),
        searchTransactionsDesc = stringResource(R.string.search_transactions_desc),
        addTransactionsDesc = stringResource(R.string.add_transactions_desc),
        loadingTransactions = stringResource(R.string.loading_transactions),
        error = stringResource(R.string.error),
        unknown = stringResource(R.string.unknown)
    )
}

data class AppStrings(
    // Transaction types
    val income: String = "",
    val expense: String = "",
    
    // Common actions
    val add: String = "",
    val save: String = "",
    val cancel: String = "",
    val delete: String = "",
    val edit: String = "",
    val close: String = "",
    val back: String = "",
    
    // Transaction UI
    val transactions: String = "",
    val addTransaction: String = "",
    val recentTransactions: String = "",
    val noTransactionsFound: String = "",
    val quickActions: String = "",
    val addIncome: String = "",
    val addExpense: String = "",
    val netBalance: String = "",
    val detailedAdd: String = "",

    // Filters
    val filters: String = "",
    val filterAll: String = "",
    val filterToday: String = "",
    val filterThisWeek: String = "",
    val filterThisMonth: String = "",
    val filterIncome: String = "",
    val filterExpense: String = "",
    
    // Filter values (for comparison/logic)
    val filterAllValue: String = "",
    val filterTodayValue: String = "",
    val filterThisWeekValue: String = "",
    val filterThisMonthValue: String = "",
    val filterIncomeValue: String = "",
    val filterExpenseValue: String = "",
    
    // Form fields
    val amount: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val wallet: String = "",
    val dateTime: String = "",
    
    // Selection
    val selectCategory: String = "",
    val selectWallet: String = "",

    // Messages
    val noDescription: String = "",
    val unknownCategory: String = "",
    val loading: String = "",

    // Quick Add Dialog specific
    val quickAdd: String = "",
    val descriptionOptional: String = "",
    val quickAddTransactionDescription: String = "",
    val selectCategoryDialog: String = "",
    val selectWalletDialog: String = "",
    val balance: String = "",

    // Actions
    val analytics: String = "",
    val search: String = "",
    val refresh: String = "",
    val searchTransactions: String = "",

    // Message String = ""s
    val noTransactionsYet: String = "",
    val startAddingTransactions: String = "",
    val viewAllTransactions: String = "",
    val noTransactionsFoundSimple: String = "",
    val addFirstTransaction: String = "",
    
    // General UI
    val all: String = "",
    val clear: String = "",
    
    // Search specific String = ""s
    val searchTransactionsPlaceholder: String = "",
    val type: String = "",
    val amountRange: String = "",
    val timeRange: String = "",
    val dayOfWeek: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val transactionsFound: String = "",
    
    // Amount ranges
    val amount0To50: String = "",
    val amount50To100: String = "",
    val amount100To500: String = "",
    val amount500Plus: String = "",
    
    // Time ranges
    val morning: String = "",
    val afternoon: String = "",
    val evening: String = "",

    // Days of week
    val monday: String = "",
    val tuesday: String = "",
    val wednesday: String = "",
    val thursday: String = "",
    val friday: String = "",
    val saturday: String = "",
    val sunday: String = "",

    // Transaction Detail Screen
    val transactionDetails: String = "",
    val deleteTransaction: String = "",
    val deleteTransactionConfirmation: String = "",
    val deleting: String = "",
    val amountLabel: String = "",
    val categoryLabel: String = "",
    val descriptionLabel: String = "",
    val dateTimeLabel: String = "",
    val walletLabel: String = "",
    val noDescriptionProvided: String = "",

    val unknownWallet: String = "",

    // Transaction Screen specific String = ""s
    val trackYourMoneyFlow: String = "",
    val totalSummary: String = "",
    val showMore: String = "",
    val more: String = "",
    val recentTransactionsCount: String = "",
    val searchTransactionsDesc: String = "",
    val addTransactionsDesc: String = "",
    val loadingTransactions: String = "",
    val error: String = "",
    val unknown: String = ""
) {

}