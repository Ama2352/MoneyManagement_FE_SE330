package DI.Navigation

object Routes {
    const val Login = "login"
    const val Register = "register"
    const val Main = "main"
    const val Auth = "auth"
    const val Calendar = "calendar"
    const val ChatMessage = "chat_message/{friendId}"
    const val EditProfile = "edit_profile"
    const val FriendProfile = "friend_profile/{friendId}"
    const val AddTransaction = "add_transaction"
    const val TransactionDetail = "transaction_detail/{transactionId}"
    const val TransactionEdit = "transaction_edit/{transactionId}"

    val all = setOf(Login, Register, Main, Auth, Calendar, ChatMessage)
}