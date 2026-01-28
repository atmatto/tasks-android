package org.atmatto.tasks.data.fs

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

// There are two types of app-specific file directories: internal and external.
// After thinking about them for a very long time, I concluded that only these
// arguments are sufficiently important to consider:
// - Internal files are encrypted, external files can be accessed by the user
//   e.g. after connecting the phone to a computer and browsing the storage.
// - If the user has no SD card, or its not adopted, it should almost always
//   be the case that both internal and external app-specific files are stored
//   on the same partition and there is no difference in storage quota.
// - If I understand it correctly: Consider the case that the user has adopted
//   an SD card as internal storage. The user can choose whether to keep some
//   data on internal flash memory or the external adopted storage. In case of
//   internal app-specific files, the app has to allow moving it to the SD card
//   for this to be possible. In case of external app-specific files, the choice
//   is of the user.
// - Generic files from external app-specific storage shouldn't appear as media
//   e.g. in the Gallery, but theoretically it's more likely than in internal
//   app-specific storage. (I don't want them to be treated as media)
// I decided on the following:
// - Allowing for the application to be stored on external storage can entice
//   unexpected edge cases, and dealing with them is not worth it for this app.
// - The database isn't browsable by the user. Therefore, backup options should
//   be provided as features of the app (besides built-in Android/Google backup).
//   There is no significant reason for the attachments to be treated differently.
// - I don't need to beware of limited storage quota of internal app-specific
//   storage. I could give the user the ability to offload the data to adopted
//   external storage, but it's not a priority because I don't expect the
//   attachments to take up huge amounts of space.
// Therefore, I thinks going forward with internal app-specific storage is
// a perfectly reasonable choice, considering that it is a bit more predictable
// and has less potential edge cases. QED

private const val TAG = "AttachmentFileStorage"

@Singleton
class AttachmentFileStorage @Inject constructor(
	@param:ApplicationContext private val context: Context
) {
	val directory: File
		get() = File(context.filesDir, "attachments").apply { mkdirs() }

	suspend fun save(attachmentId: String, uri: Uri): SaveResult = withContext(Dispatchers.IO) {
		Log.v(TAG, "Received request to save $attachmentId from $uri")
		val file = File(directory, attachmentId)
		context.contentResolver.openInputStream(uri)?.use { input ->
			file.outputStream().use { output ->
				input.copyTo(output)
			}
		}
		Log.v(TAG, "Saved $attachmentId from $uri")

		SaveResult(
			getFileName(uri) ?: "Unnamed",
			context.contentResolver.getType(uri) ?: "application/octet-stream"
		)
	}

	data class SaveResult(
		val name: String,
		val mimeType: String,
	)

	private fun getFileName(uri: Uri): String? {
		return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
			val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
			cursor.moveToFirst()
			cursor.getString(nameIndex)
		}
	}

	fun open(attachmentId: String, mimeType: String) {
		Log.v(TAG, "Received request to open $attachmentId")

		val file = File(directory, attachmentId)
		val uri = FileProvider.getUriForFile(context, "${context.packageName}.FileProvider", file)
		val intent = Intent(Intent.ACTION_VIEW).apply {
			setDataAndType(uri, mimeType)
			flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
		}

		try {
			Log.v(TAG, "Opening  $attachmentId")
			context.startActivity(intent)
		} catch (e: ActivityNotFoundException) {
			Log.e(TAG, "No app found to open $attachmentId", e)
		}
	}

	fun delete(attachmentId: String) {
		Log.v(TAG, "Received request to delete $attachmentId")

		val file = File(directory, attachmentId)
		file.delete()

		Log.v(TAG, "Deleted $attachmentId")
	}
}
