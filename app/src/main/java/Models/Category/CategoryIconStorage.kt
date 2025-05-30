package DI.Models.Category

import android.content.Context
import com.example.moneymanagement_frontend.R
import androidx.core.content.edit

class CategoryIconStorage(context: Context) {
    private val prefs = context.getSharedPreferences("category_icons", Context.MODE_PRIVATE)

    fun saveIcon(categoryId: String, iconKey: String) {
        prefs.edit().putString(categoryId, iconKey).apply()
    }

    fun getIconKey(categoryId: String): String {
        return prefs.getString(categoryId, "ic_more") ?: "ic_more"
    }

    fun removeIcon(categoryId: String) {
        prefs.edit().remove(categoryId).apply()
    }
}
