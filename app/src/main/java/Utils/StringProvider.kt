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
        ok = stringResource(R.string.ok),
        
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
          
        // Search specific strings
        searchTransactions = stringResource(R.string.search_transactions),
        searchTransactionsPlaceholder = stringResource(R.string.search_transactions_placeholder),
        type = stringResource(R.string.type),
        amountRange = stringResource(R.string.amount_range),
        timeRange = stringResource(R.string.time_range),
        dayOfWeek = stringResource(R.string.day_of_week),
        startDate = stringResource(R.string.start_date),
        endDate = stringResource(R.string.end_date),
        transactionsFound = stringResource(R.string.transactions_found),
        
        // Amount ranges
        amount0To50 = stringResource(R.string.amount_0_to_50),
        amount50To100 = stringResource(R.string.amount_50_to_100),
        amount100To500 = stringResource(R.string.amount_100_to_500),
        amount500Plus = stringResource(R.string.amount_500_plus),
        
        // Time ranges
        lateNight = stringResource(R.string.late_night),
        morning = stringResource(R.string.morning),
        afternoon = stringResource(R.string.afternoon),
        evening = stringResource(R.string.evening),
        

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
        unknown = stringResource(R.string.unknown),
        date = stringResource(R.string.date),
        time = stringResource(R.string.time),
        
        // Add Transaction Screen
        transactionAmount = stringResource(R.string.transaction_amount),
        transactionDescription = stringResource(R.string.transaction_description),
        selectType = stringResource(R.string.select_type),
        selectDate = stringResource(R.string.select_date),
        selectTime = stringResource(R.string.select_time),
        transactionTypeIncome = stringResource(R.string.transaction_type_income),
        transactionTypeExpense = stringResource(R.string.transaction_type_expense),
        requiredField = stringResource(R.string.required_field),
        invalidAmount = stringResource(R.string.invalid_amount),
        saveTransaction = stringResource(R.string.save_transaction),
        creatingTransaction = stringResource(R.string.creating_transaction),
        transactionCreatedSuccessfully = stringResource(R.string.transaction_created_successfully),
        fillRequiredFields = stringResource(R.string.fill_required_fields),

        // Additional search strings
        noResultsFound = stringResource(R.string.no_results_found),
        tryDifferentFilters = stringResource(R.string.try_different_filters),
        applyFilters = stringResource(R.string.apply_filters),
        clearFilters = stringResource(R.string.clear_filters),
        dateRange = stringResource(R.string.date_range),

        // Edit Transaction Screen
        transactionTypeUnchangeable = stringResource(R.string.transaction_type_cannot_change),

        // Transaction Detail Screen
        transactionDetails = stringResource(R.string.transaction_details),
        deleteTransaction = stringResource(R.string.delete_transaction),
        deleteTransactionConfirmation = stringResource(R.string.delete_transaction_confirm_message),
        transactionId = stringResource(R.string.transaction_id),
        createdOn = stringResource(R.string.created_on),
        transactionInfo = stringResource(R.string.transaction_info),
        noDescriptionProvided = stringResource(R.string.no_description_provided),
        deleting = stringResource(R.string.deleting),        // Transaction Search Screen
        findYourTransactions = stringResource(R.string.find_your_transactions),
          // Calendar Analysis
        aggregate = stringResource(R.string.aggregate),
        pieCharts = stringResource(R.string.pie_charts),
        noDataAvailable = stringResource(R.string.no_data_available),
        selectDateRangeViewAnalytics = stringResource(R.string.select_date_range_to_view_analytics),
        
        // Budget Management
        budget = stringResource(R.string.budget),
        myBudgets = stringResource(R.string.my_budgets),
        addBudget = stringResource(R.string.add_budget),
        createBudget = stringResource(R.string.create_budget),
        editBudget = stringResource(R.string.edit_budget),
        budgetOverview = stringResource(R.string.budget_overview),
        totalBudgets = stringResource(R.string.total_budgets),
        activeBudgets = stringResource(R.string.active_budgets),
        completedBudgets = stringResource(R.string.completed_budgets),
        noBudgetsYet = stringResource(R.string.no_budgets_yet),
        createFirstBudget = stringResource(R.string.create_first_budget),
        currentSpending = stringResource(R.string.current_spending),
        budgetLimit = stringResource(R.string.budget_limit),
        budgetCategory = stringResource(R.string.budget_category),
        budgetWallet = stringResource(R.string.budget_wallet),
        budgetDeadline = stringResource(R.string.budget_deadline),
        unknownCategory = stringResource(R.string.unknown_category),
        budgetDescription = stringResource(R.string.budget_description),
        budgetDescriptionPlaceholder = stringResource(R.string.budget_description_placeholder),
        budgetLimitLabel = stringResource(R.string.budget_limit_label),
        processing = stringResource(R.string.processing),
        limitAmount = stringResource(R.string.limit_amount),
        saveBudget = stringResource(R.string.save_budget),
        deleteBudget = stringResource(R.string.delete_budget),
        deleteBudgetConfirm = stringResource(R.string.delete_budget_confirm),
        deleteBudgetConfirmMessage = stringResource(R.string.delete_budget_confirm_message),
        budgetDeleteDescription = stringResource(R.string.budget_delete_description),
        budgetCannotUndo = stringResource(R.string.budget_cannot_undo),
        confirmDelete = stringResource(R.string.confirm_delete),
        
        // Budget Status
        budgetNotStarted = stringResource(R.string.budget_not_started),
        budgetOnTrack = stringResource(R.string.budget_on_track),
        budgetUnderBudget = stringResource(R.string.budget_under_budget),
        budgetMinimalSpending = stringResource(R.string.budget_minimal_spending),
        budgetWarning = stringResource(R.string.budget_warning),
        budgetCritical = stringResource(R.string.budget_critical),
        budgetOverBudget = stringResource(R.string.budget_over_budget),
        budgetNearlyMaxed = stringResource(R.string.budget_nearly_maxed),
        budgetUnknownStatus = stringResource(R.string.budget_unknown_status),
          // Budget Actions
        budgetEdit = stringResource(R.string.edit_budget),
        budgetCreateNew = stringResource(R.string.budget_create_new),
        budgetUpdate = stringResource(R.string.budget_update),
        budgetCreate = stringResource(R.string.create_budget),
        successMessage = stringResource(R.string.success_message),
        backButton = stringResource(R.string.back),
        budgetValidationTitle = stringResource(R.string.budget_validation_title),
        
        // Budget Success/Error Messages
        budgetCreatedSuccess = stringResource(R.string.budget_created_success),
        budgetUpdatedSuccess = stringResource(R.string.budget_updated_success),
        budgetDeletedSuccess = stringResource(R.string.budget_deleted_success),
        budgetOperationError = stringResource(R.string.budget_operation_error),
        unknownError = stringResource(R.string.unknown_error),
          // Budget Form Validation
        budgetDescriptionRequired = stringResource(R.string.budget_description_required),
        budgetLimitRequired = stringResource(R.string.budget_limit_required),
        budgetCategoryRequired = stringResource(R.string.budget_category_required),
        budgetWalletRequired = stringResource(R.string.budget_wallet_required),
        budgetEndDateInvalid = stringResource(R.string.budget_end_date_invalid),
        budgetAmountInvalid = stringResource(R.string.budget_amount_invalid),
        
        // Date Utils
        dateExpired = stringResource(R.string.date_expired),
        dateToday = stringResource(R.string.date_today),
        dateOneDayLeft = stringResource(R.string.date_one_day_left),
        dateDaysLeft = stringResource(R.string.date_days_left),
        dateMonthsLeft = stringResource(R.string.date_months_left),
        dateMonthsDaysLeft = stringResource(R.string.date_months_days_left),
        
        // Date Status
        dateStatusOverdue = stringResource(R.string.date_status_overdue),
        dateStatusCompleted = stringResource(R.string.date_status_completed),
        dateStatusUrgent = stringResource(R.string.date_status_urgent),
        dateStatusWarning = stringResource(R.string.date_status_warning),
        dateStatusOnTrack = stringResource(R.string.date_status_on_track),
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
    val ok: String = "",
    
    // Transaction UI
    val transactions: String = "",
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
    val date: String = "",
    val time: String = "",
    
    // Selection
    val selectCategory: String = "",
    val selectWallet: String = "",    // Messages
    val noDescription: String = "",
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

    // Message String = ""s
    val noTransactionsYet: String = "",
    val startAddingTransactions: String = "",
    val viewAllTransactions: String = "",
    val noTransactionsFoundSimple: String = "",
    val addFirstTransaction: String = "",
    
    // General UI
    val all: String = "",
    val clear: String = "",
      
    // Search specific strings
    val searchTransactions: String = "",
    val searchTransactionsPlaceholder: String = "",
    val type: String = "",
    val amountRange: String = "",
    val timeRange: String = "",
    val dayOfWeek: String = "",
    val transactionsFound: String = "",
    
    // Amount ranges
    val amount0To50: String = "",
    val amount50To100: String = "",
    val amount100To500: String = "",
    val amount500Plus: String = "",
    
    // Time ranges
    val lateNight: String = "",
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
    val transactionId: String = "",
    val createdOn: String = "",
    val transactionInfo: String = "",

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
    val unknown: String = "",
    
    // Add Transaction Screen
    val addTransaction: String = "",
    val transactionAmount: String = "",
    val transactionDescription: String = "",
    val selectType: String = "",
    val selectDate: String = "",
    val selectTime: String = "",
    val transactionTypeIncome: String = "",
    val transactionTypeExpense: String = "",
    val requiredField: String = "",
    val invalidAmount: String = "",
    val saveTransaction: String = "",
    val creatingTransaction: String = "",
    val transactionCreatedSuccessfully: String = "",
    val fillRequiredFields: String = "",    
    
    // Additional search strings
    val noResultsFound: String = "",
    val tryDifferentFilters: String = "",
    val applyFilters: String = "",
    val clearFilters: String = "",
    val dateRange: String = "",
    val findYourTransactions: String = "",    // Edit Transaction Screen
    val transactionTypeUnchangeable: String = "",
      // Calendar Analysis
    val aggregate: String = "",
    val pieCharts: String = "",
    val noDataAvailable: String = "",
    val selectDateRangeViewAnalytics: String = "",
    
    // Budget Management
    val budget: String = "",
    val myBudgets: String = "",
    val addBudget: String = "",
    val createBudget: String = "",
    val editBudget: String = "",
    val budgetOverview: String = "",
    val totalBudgets: String = "",
    val activeBudgets: String = "",
    val completedBudgets: String = "",
    val noBudgetsYet: String = "",
    val createFirstBudget: String = "",
    val currentSpending: String = "",
    val budgetLimit: String = "",
    val budgetCategory: String = "",
    val budgetWallet: String = "",
    val budgetDeadline: String = "",
    val unknownCategory : String = "",
    val budgetDescription: String = "",
    val budgetDescriptionPlaceholder: String = "",
    val budgetLimitLabel: String = "",

    val processing: String = "",
    val limitAmount: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val saveBudget: String = "",
    val deleteBudget: String = "",
    val deleteBudgetConfirm: String = "",
    val deleteBudgetConfirmMessage: String = "",
    val budgetDeleteDescription: String = "",
    val budgetCannotUndo: String = "",
    val confirmDelete: String = "",
    
    // Budget Status
    val budgetNotStarted: String = "",
    val budgetOnTrack: String = "",
    val budgetUnderBudget: String = "",
    val budgetMinimalSpending: String = "",
    val budgetWarning: String = "",
    val budgetCritical: String = "",
    val budgetOverBudget: String = "",
    val budgetNearlyMaxed: String = "",
    val budgetUnknownStatus: String = "",
      // Budget Actions
    val budgetEdit: String = "",
    val budgetCreateNew: String = "",
    val budgetUpdate: String = "",
    val budgetCreate: String = "",
    val successMessage: String = "",
    val backButton: String = "",
    val budgetValidationTitle: String = "",
    
    // Budget Success/Error Messages
    val budgetCreatedSuccess: String = "",
    val budgetUpdatedSuccess: String = "",
    val budgetDeletedSuccess: String = "",
    val budgetOperationError: String = "",
    val unknownError: String = "",
      // Budget Form Validation
    val budgetDescriptionRequired: String = "",
    val budgetLimitRequired: String = "",
    val budgetCategoryRequired: String = "",
    val budgetWalletRequired: String = "",
    val budgetEndDateInvalid: String = "",
    val budgetAmountInvalid: String = "",
    
    // Date Utils
    val dateExpired: String = "",
    val dateToday: String = "",
    val dateOneDayLeft: String = "",
    val dateDaysLeft: String = "",
    val dateMonthsLeft: String = "",
    val dateMonthsDaysLeft: String = "",
    
    // Date Status
    val dateStatusOverdue: String = "",
    val dateStatusCompleted: String = "",
    val dateStatusUrgent: String = "",
    val dateStatusWarning: String = "",
    val dateStatusOnTrack: String = "",
) {

}