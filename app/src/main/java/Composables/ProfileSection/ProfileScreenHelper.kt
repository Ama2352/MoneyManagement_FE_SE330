package DI.Composables.ProfileSection

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import java.io.File
import javax.sql.DataSource

// Define your color
val MainColor = Color(0xFF53dba9)
val BackgroundColor = Color(0xFF53dba9)
val CardColor = Color.White
val TextPrimaryColor = Color(0xFF333333)
val TextSecondaryColor = Color(0xFF757575)
val DividerColor = Color(0xFFEEEEEE)

// Optimized uriToFile
fun uriToFile(uri: Uri, context: Context): File? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val tempFile = File.createTempFile("avatar", null, context.cacheDir)
            tempFile.outputStream().use { output ->
                input.copyTo(output, bufferSize = 64 * 1024) // 64KB buffer
            }
            tempFile
        }
    } catch (e: Exception) {
        Log.e("uriToFile", "Failed to convert URI to file", e)
        null
    }
}

@Composable
fun FriendAvatar(url: String) {
    Log.d("FriendAvatarCall", "URL: $url")
    val context = LocalContext.current
    AndroidView(
        factory = {
            ImageView(context).apply {
                Glide.with(context)
                    .load(url)
                    .into(this)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        update = { imageView ->
            Glide.with(context)
                .load(url)
                .into(imageView)
        },
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape),
    )
}

@Composable
fun AvatarImage(url: String, version: String = "v1", )
{
    Log.d("AvatarImageCall", "URL: $url, Version: $version")
    val context = LocalContext.current
    AndroidView(
        factory = {
            ImageView(context).apply {
                Glide.with(context)
                    .load(url)
                    .into(this)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        update = { imageView ->
            Glide.with(context)
                .load(url)
                .signature(ObjectKey(version))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("OnAvatarLoadFailed", "Failed to loaded avatar")
                        return false // Glide will show the error placeholder
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("OnAvatarLoad", "Successfully loaded avatar")
                        return false // Glide will set the image into the ImageView
                    }
                })
                .into(imageView)
        },
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape),
    )
}
