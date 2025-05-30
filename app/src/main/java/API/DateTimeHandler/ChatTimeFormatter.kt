import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object ChatTimeFormatter {
    private val vietnameseLocale = Locale("vi", "VN")

    // Alternative input format with space as separator
    private val spaceInputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // Vietnamese localized time and date formats
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", vietnameseLocale)     // 24h format, e.g., 10:06
    private val dateFormatter = DateTimeFormatter.ofPattern("d MMM", vietnameseLocale)     // e.g., 16 thg 5
    private val fullDateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", vietnameseLocale) // e.g., 16 thg 5 2025

    /**
     * Parses a timestamp string in either ISO-8601 format (with optional fractional seconds and 'Z')
     * or a space-separated format "yyyy-MM-dd HH:mm:ss" and formats it according to Vietnamese locale:
     * - Today: "HH:mm"
     * - Yesterday: "Hôm qua"
     * - Same year: "d MMM"
     * - Different year: "d MMM yyyy"
     */
    fun formatTimestamp(sentAt: String): String {
        // Try ISO parsing first
        val sentDateTime: LocalDateTime = try {
            val sentInstant = Instant.parse(sentAt)
            LocalDateTime.ofInstant(sentInstant, ZoneId.systemDefault())
        } catch (e: DateTimeParseException) {
            // Fallback to space-separated format
            LocalDateTime.parse(sentAt, spaceInputFormatter)
        }

        val now = LocalDateTime.now(ZoneId.systemDefault())
        val sentDate = sentDateTime.toLocalDate()
        val today = now.toLocalDate()
        val yesterday = today.minusDays(1)

        return when {
            sentDate == today -> sentDateTime.format(timeFormatter)
            sentDate == yesterday -> "Hôm qua"
            sentDate.year == today.year -> sentDateTime.format(dateFormatter)
            else -> sentDateTime.format(fullDateFormatter)
        }
    }
}
