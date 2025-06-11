package DI.Composables.CategorySection

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import android.util.Log

// Modern bright green color scheme
object ModernColors {
    val Background = Color(0xFFF0FDF4)          // Ultra Light Green Background
    val Surface = Color.White                    // Pure White
    val SurfaceVariant = Color(0xFFE8F5E9)      // Very Light Green
    val Primary = Color(0xFF22C55E)             // Bright Green (main color)
    val PrimaryLight = Color(0xFF4ADE80)        // Light Bright Green
    val Secondary = Color(0xFF16A34A)           // Medium Green
    val SecondaryLight = Color(0xFF34D399)      // Light Green
    val Accent = Color(0xFF10B981)              // Emerald Green
    val OnSurface = Color(0xFF0F172A)           // Dark text
    val OnSurfaceVariant = Color(0xFF059669)    // Dark Green text
    val Error = Color(0xFFEF4444)               // Bright Red
    val Success = Color(0xFF10B981)             // Emerald Success

    val primaryGradient = Brush.linearGradient(
        colors = listOf(Primary, PrimaryLight)
    )

    val cardGradient = Brush.linearGradient(
        colors = listOf(Color.White, Color(0xFFFAFBFC))
    )

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF8FAFC), Color(0xFFEFF6FF))
    )
}

// Helper function to normalize text for better matching
private fun normalizeText(text: String): String {
    return text.lowercase()
        .replace("[áàảãạăắằẳẵặâấầẩẫậ]".toRegex(), "a")
        .replace("[éèẻẽẹêếềểễệ]".toRegex(), "e")
        .replace("[íìỉĩị]".toRegex(), "i")
        .replace("[óòỏõọôốồổỗộơớờởỡợ]".toRegex(), "o")
        .replace("[úùủũụưứừửữự]".toRegex(), "u")
        .replace("[ýỳỷỹỵ]".toRegex(), "y")
        .replace("đ".toRegex(), "d")
        .trim()
}

// Get category suggestions based on partial input
fun getCategorySuggestions(input: String, limit: Int = 5): List<String> {
    val normalizedInput = normalizeText(input)
    
    val allKeywords = listOf(
        // English suggestions
        "food", "restaurant", "coffee", "groceries", "electronics", "phone", "computer", "clothing", "fashion", 
        "books", "education", "sports", "fitness", "travel", "flight", "music", "entertainment", "health", 
        "medical", "home", "furniture", "car", "transport", "beauty", "cosmetics", "gaming", "business", 
        "work", "finance", "bank", "shopping", "social", "friends", "art", "photography", "tools", "pets",
        // Vietnamese suggestions (normalized - no diacritics)
        "do an", "nha hang", "ca phe", "mua sam", "dien tu", "dien thoai", "may tinh", "quan ao", 
        "thoi trang", "sach", "giao duc", "the thao", "du lich", "am nhac", "giai tri", "suc khoe", 
        "y te", "nha", "noi that", "o to", "lam dep", "choi game", "cong viec", "tai chinh", "ngan hang", 
        "ban be", "nghe thuat", "nhiep anh", "cong cu", "thu cung"
    )
    
    return allKeywords
        .filter { normalizeText(it).contains(normalizedInput) }
        .take(limit)
}

// Enhanced icon mapping with Vietnamese support
fun getCategoryIcon(categoryName: String): ImageVector {
    val normalizedName = normalizeText(categoryName)
    
    Log.d("CategoryIcon", "=== CATEGORY ICON MATCHING ===")
    Log.d("CategoryIcon", "Original category name: '$categoryName'")
    Log.d("CategoryIcon", "Normalized category name: '$normalizedName'")
    Log.d("CategoryIcon", "Split words: ${normalizedName.split(" ")}")

    val keywordIconMap = mapOf(
        // Food & Dining - ALL NORMALIZED
        listOf("food", "cream", "grocery", "dining", "restaurant", "eat", "meal", "snack", "coffee", "drink", "bar", "cafe", "bakery", "fast food", "breakfast", "lunch", "dinner",
               "do an", "thuc an", "an uong", "nha hang", "quan an", "ca phe", "uong", "do uong", "bua an", "an sang", "an trua", "an toi", "banh", "banh mi", "pho", "com") to Icons.Default.Restaurant,
               
        // Electronics & Technology - ALL NORMALIZED
        listOf("electronics", "laptop", "computer", "tech", "technology", "gadget", "device", "headphones", "speaker", "tv", "monitor",
               "dien tu", "may tinh", "cong nghe", "thiet bi", "laptop", "tablet", "tai nghe", "loa", "tivi", "man hinh") to Icons.Default.Computer,
        
        // Mobile & Communication - ALL NORMALIZED
        listOf("phone", "android", "iphone", "smartphone", "mobile", "cell", "communication", "call", "text",
               "dien thoai", "di dong", "goi dien", "nhan tin", "lien lac") to Icons.Default.PhoneAndroid,
        
        // Clothing & Fashion - ALL NORMALIZED
        listOf("clothing", "fashion", "clothes", "shirt", "pants", "dress", "shoes", "accessories", "jewelry", "watch", "bag", "hat", "style", "outfit",
               "quan ao", "thoi trang", "ao", "quan", "vay", "giay", "phu kien", "trang suc", "dong ho", "tui", "mu", "trang phuc") to Icons.Default.Checkroom,
               
        // Education & Books - ALL NORMALIZED
        listOf("books", "education", "study", "school", "university", "course", "learning", "book", "textbook", "notebook", "pen", "pencil", "stationery", "tuition", "fee",
               "sach", "giao duc", "hoc tap", "truong hoc", "dai hoc", "khoa hoc", "hoc", "sach giao khoa", "vo", "but", "van phong pham", "hoc phi") to Icons.AutoMirrored.Filled.MenuBook,
        
        // Sports & Fitness - ALL NORMALIZED
        listOf("sports", "fitness", "gym", "exercise", "workout", "training", "sport", "football", "basketball", "running", "swimming", "yoga", "equipment",
               "the thao", "the duc", "phong gym", "tap luyen", "bong da", "bong ro", "chay bo", "boi loi", "yoga", "dung cu the thao") to Icons.Default.FitnessCenter,
        
        // Travel & Transportation - ALL NORMALIZED
        listOf("travel", "vacation", "flight", "trip", "holiday", "hotel", "transport", "ticket", "airplane", "train", "bus", "taxi", "uber", "grab", "gas", "fuel",
               "du lich", "nghi", "chuyen bay", "khach san", "van chuyen", "ve", "may bay", "tau", "xe buyt", "taxi", "xang", "nhien lieu", "di lai") to Icons.Default.Flight,
        
        // Entertainment & Music - ALL NORMALIZED
        listOf("music", "entertainment", "song", "movie", "cinema", "concert", "show", "streaming", "spotify", "netflix", "youtube", "game", "fun", "hobby",
               "am nhac", "giai tri", "bai hat", "phim", "rap phim", "hoa nhac", "show", "xem phim", "tro choi", "so thich") to Icons.Default.MusicNote,
        
        // Health & Medical - ALL NORMALIZED
        listOf("health", "medical", "hospital", "doctor", "medicine", "pharmacy", "dental", "clinic", "checkup", "insurance", "vitamins", "supplement",
               "suc khoe", "y te", "benh vien", "bac si", "thuoc", "nha khoa", "phong kham", "kham benh", "bao hiem", "vitamin") to Icons.Default.LocalHospital,
        
        // Home & Housing - ALL NORMALIZED
        listOf("home", "furniture", "house", "rent", "utilities", "electricity", "water", "internet", "decoration", "cleaning", "maintenance", "repair",
               "nha", "noi that", "thue nha", "tien ich", "dien", "nuoc", "internet", "trang tri", "don dep", "bao tri", "sua chua") to Icons.Default.Home,        

        // Automotive & Vehicles - ALL NORMALIZED
        listOf("automotive", "cars", "car", "vehicle", "motorcycle", "bike", "maintenance", "parking", "insurance", "registration", "wash",
               "o to", "xe hoi", "xe may", "xe dap", "bao duong", "dau xe", "bao hiem xe", "dang ky xe", "rua xe") to Icons.Default.DirectionsCar,
        
        // Beauty & Personal Care - ALL NORMALIZED
        listOf("beauty", "cosmetics", "makeup", "skincare", "hair", "salon", "spa", "nail", "perfume", "personal care", "hygiene",
               "lam dep", "my pham", "trang diem", "cham soc da", "toc", "salon", "spa", "mong", "nuoc hoa", "ve sinh ca nhan") to Icons.Default.Face,
        
        // Gaming & Hobbies - ALL NORMALIZED
        listOf("gaming", "games", "game", "console", "steam", "playstation", "xbox", "nintendo", "hobby", "collection",
               "choi game", "tro choi", "game", "may choi game", "so thich", "suu tap") to Icons.Default.SportsEsports,
        
        // Business & Work - ALL NORMALIZED
        listOf("business", "work", "office", "meeting", "conference", "professional", "service", "consulting", "freelance", "project",
               "kinh doanh", "cong viec", "van phong", "hop", "hoi nghi", "chuyen nghiep", "dich vu", "tu van", "freelance", "du an") to Icons.Default.Business,
        
        // Finance & Banking - ALL NORMALIZED
        listOf("finance", "money", "bank", "investment", "loan", "credit", "debit", "saving", "payment", "fee", "tax", "insurance",
               "tai chinh", "tien", "ngan hang", "dau tu", "vay", "tin dung", "tiet kiem", "thanh toan", "phi", "thue", "bao hiem") to Icons.Default.AccountBalance,
        
        // Shopping & Retail - ALL NORMALIZED
        listOf("shopping", "shop", "market", "store", "retail", "purchase", "buy", "mall", "online shopping", "delivery",
               "mua sam", "cua hang", "cho", "sieu thi", "mua", "trung tam thuong mai", "mua online", "giao hang") to Icons.Default.ShoppingCart,
        
        // Social & Relationships - ALL NORMALIZED
        listOf("social", "friends", "group", "family", "relationship", "party", "event", "gift", "celebration", "wedding",
               "xa hoi", "ban be", "nhom", "gia dinh", "moi quan he", "tiec", "su kien", "qua", "ky niem", "dam cuoi") to Icons.Default.Group,
        
        // Art & Creativity - ALL NORMALIZED
        listOf("art", "creative", "design", "painting", "drawing", "craft", "photography", "camera", "photo", "creative work",
               "nghe thuat", "sang tao", "thiet ke", "ve", "nhiep anh", "may anh", "chup anh", "thu cong") to Icons.Default.Palette,
        
        // Photography - ALL NORMALIZED
        listOf("photography", "camera", "photo", "picture", "video", "filming", "editing",
               "nhiep anh", "may anh", "anh", "hinh", "video", "quay phim", "chinh sua") to Icons.Default.CameraAlt,
        
        // Tools & Hardware - ALL NORMALIZED
        listOf("tools", "build", "repair", "hardware", "construction", "diy", "equipment", "machinery",
               "cong cu", "xay dung", "sua chua", "phan cung", "xay dung", "thiet bi", "may moc") to Icons.Default.Build,
               
        // Pets & Animals - ALL NORMALIZED
        listOf("pet", "pets", "animal", "dog", "cat", "veterinary", "pet food", "pet care",
               "thu cung", "dong vat", "cho", "meo", "thu y", "thuc an cho thu cung", "cham soc thu cung") to Icons.Default.Pets,
        
        // Miscellaneous - ALL NORMALIZED
        listOf("other", "miscellaneous", "misc", "general", "various", "different",
               "khac", "linh tinh", "tong hop", "da dang", "khac nhau") to Icons.Default.Category    )

    Log.d("CategoryIcon", "Total keyword groups to check: ${keywordIconMap.size}")
    
    // Check each keyword group
    keywordIconMap.entries.forEachIndexed { index, (keywords, icon) ->
        Log.d("CategoryIcon", "Group $index - Icon: ${icon.name}")
        Log.d("CategoryIcon", "  Keywords: $keywords")
            val matchingKeywords = keywords.filter { keyword ->
            // Check exact match first (highest priority)
            val exactMatch = normalizedName == keyword
            
            // Check if any word in the category matches the keyword exactly
            val wordMatch = normalizedName.split(" ").any { word -> word == keyword }
            
            // Check if the keyword is a substantial part (avoid short partial matches)
            val substringMatch = keyword.length >= 3 && normalizedName.contains(keyword) && 
                                (normalizedName.startsWith(keyword) || normalizedName.endsWith(keyword) || 
                                 normalizedName.contains(" $keyword ") || normalizedName.contains(" $keyword") || 
                                 normalizedName.contains("$keyword "))
            
            val isMatch = exactMatch || wordMatch || substringMatch
            
            if (isMatch) {
                when {
                    exactMatch -> Log.d("CategoryIcon", "  ✅ EXACT MATCH: '$keyword' exactly matches '$normalizedName'")
                    wordMatch -> Log.d("CategoryIcon", "  ✅ WORD MATCH: keyword '$keyword' matches individual word in '$normalizedName'")
                    substringMatch -> Log.d("CategoryIcon", "  ✅ SUBSTRING MATCH: '$normalizedName' contains substantial keyword '$keyword'")
                }
            } else {
                // Log why it didn't match for debugging
                if (keyword.length < 3 && normalizedName.contains(keyword)) {
                    Log.d("CategoryIcon", "  ❌ REJECTED: keyword '$keyword' too short for substring match in '$normalizedName'")
                }
            }
            isMatch
        }
        
        if (matchingKeywords.isNotEmpty()) {
            Log.d("CategoryIcon", "🎯 FINAL MATCH: Using ${icon.name} for category '$categoryName'")
            Log.d("CategoryIcon", "  Matching keywords: $matchingKeywords")
            return icon
        }
    }
    
    Log.d("CategoryIcon", "❌ No match found, using default Category icon")
    return Icons.Default.Category
}