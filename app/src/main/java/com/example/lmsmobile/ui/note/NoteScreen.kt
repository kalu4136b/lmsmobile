package com.example.lmsmobile.ui.note

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lmsmobile.data.model.NoteDTO
import com.example.lmsmobile.ui.components.DashboardTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val BASE_FILE_URL = "http://192.168.144.93:8080"

@Composable
fun NoteScreen(
    viewModel: LmsViewModel,
    subjectId: Long,
    subjectName: String,
    navController: NavController
) {
    val notes: List<NoteDTO> by viewModel.notes.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var noteText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var capturedFile by remember { mutableStateOf<File?>(null) }
    var pdfUri by remember { mutableStateOf<Uri?>(null) }

    var editingNoteId by remember { mutableStateOf<Long?>(null) }
    var editingText by remember { mutableStateOf("") }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
        capturedFile = null
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            scope.launch {
                delay(300)
                imageUri = tempCameraUri
            }
        }
    }

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        pdfUri = uri
    }

    fun createImageUri(): Uri {
        val imageFile = File(context.filesDir, "note_${System.currentTimeMillis()}.jpg")
        capturedFile = imageFile
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    }

    fun fileToMultipart(file: File?): MultipartBody.Part? {
        file ?: return null
        return try {
            val bytes = file.readBytes()
            val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
            MultipartBody.Part.createFormData("image", file.name, requestBody)
        } catch (e: Exception) {
            Log.e("NoteScreen", "Failed to convert file to multipart", e)
            null
        }
    }

    fun pdfToMultipart(uri: Uri?): MultipartBody.Part? {
        uri ?: return null
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            val requestBody = bytes?.let {
                RequestBody.create("application/pdf".toMediaTypeOrNull(), it)
            }
            requestBody?.let {
                MultipartBody.Part.createFormData("pdf", "note.pdf", it)
            }
        } catch (e: Exception) {
            Log.e("NoteScreen", "Failed to convert PDF", e)
            null
        }
    }

    LaunchedEffect(subjectId) {
        viewModel.loadNotes(subjectId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        DashboardTopBar(
            title = subjectName,
            showBackButton = true,
            onBackClick = { navController.popBackStack() },
            scope = scope,
            drawerState = null,
            showNotificationIcon = false,
            showProfileIcon = false
        )

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Add a note") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp), // extra vertical breathing room
                horizontalArrangement = Arrangement.spacedBy(16.dp) // wider gap between buttons
            ) {
                Button(onClick = { galleryLauncher.launch("image/*") }) {
                    Text("Gallery")
                }
                Button(onClick = {
                    val uri = createImageUri()
                    tempCameraUri = uri
                    cameraLauncher.launch(uri)
                }) {
                    Text("Camera")
                }
                Button(onClick = { pdfLauncher.launch("application/pdf") }) {
                    Text("PDF")
                }
            }

            imageUri?.let {
                Spacer(Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }

            pdfUri?.let {
                Spacer(Modifier.height(8.dp))
                Text("📄 PDF attached: ${it.lastPathSegment}")
            }

            Button(
                onClick = {
                    if (noteText.isNotBlank()) {
                        val imagePart = capturedFile?.let { fileToMultipart(it) }
                            ?: imageUri?.let { uri ->
                                val inputStream = context.contentResolver.openInputStream(uri)
                                val bytes = inputStream?.readBytes()
                                val requestBody = bytes?.let {
                                    RequestBody.create("image/*".toMediaTypeOrNull(), it)
                                }
                                requestBody?.let {
                                    MultipartBody.Part.createFormData("image", "note.jpg", it)
                                }
                            }

                        val pdfPart = pdfToMultipart(pdfUri)

                        val textPart = RequestBody.create("text/plain".toMediaTypeOrNull(), noteText)
                        val subjectPart = RequestBody.create("text/plain".toMediaTypeOrNull(), subjectId.toString())

                        viewModel.uploadNote(
                            text = textPart,
                            subjectId = subjectPart,
                            image = imagePart,
                            pdf = pdfPart,
                            subjectIdLong = subjectId
                        )

                        noteText = ""
                        imageUri = null
                        pdfUri = null
                        capturedFile = null
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Save Note")
            }

            Spacer(Modifier.height(24.dp))

            Text("Your Notes", style = MaterialTheme.typography.titleMedium)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(notes) { note ->
                    if (editingNoteId == note.id) {
                        EditableNoteCard(
                            note = note,
                            editingText = editingText,
                            onTextChange = { editingText = it },
                            onSave = {
                                viewModel.updateNote(note.id!!, editingText, subjectId)
                                editingNoteId = null
                            },
                            onCancel = { editingNoteId = null },
                            navController = navController
                        )
                    } else {
                        NoteCard(
                            note = note,
                            onEdit = {
                                editingNoteId = note.id
                                editingText = note.text
                            },
                            onDelete = {
                                viewModel.deleteNote(note.id!!, subjectId)
                            },
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditableNoteCard(
    note: NoteDTO,
    editingText: String,
    onTextChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = editingText,
                onValueChange = onTextChange,
                label = { Text("Edit note") },
                modifier = Modifier.fillMaxWidth()
            )

            note.imageUri?.let {
                val fullImageUrl = "$BASE_FILE_URL$it"
                Spacer(Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(fullImageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clickable {
                            navController.navigate("image_view/${Uri.encode(fullImageUrl)}")
                        }
                )
            }

            note.pdfUri?.let {
                val fullPdfUrl = "$BASE_FILE_URL$it"
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "📄 Open PDF",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(Uri.parse(fullPdfUrl), "application/pdf")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, "No PDF viewer installed", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) { Text("Cancel") }
                TextButton(onClick = onSave) { Text("Save") }
            }
        }
    }
}

@Composable
fun NoteCard(
    note: NoteDTO,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val formatter = remember { SimpleDateFormat("yyyy EEE MMM dd HH.mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(note.text, style = MaterialTheme.typography.bodyMedium)

            note.imageUri?.let {
                val fullImageUrl = "$BASE_FILE_URL$it"
                Spacer(Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(fullImageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clickable {
                            navController.navigate("image_view/${Uri.encode(fullImageUrl)}")
                        }
                )
            }

            note.pdfUri?.let {
                val fullPdfUrl = "$BASE_FILE_URL$it"
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "📄 Open PDF",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(Uri.parse(fullPdfUrl), "application/pdf")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, "No PDF viewer installed", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            note.timestamp?.let {
                val formatted = formatter.format(Date(it))
                Text("Added: $formatted", style = MaterialTheme.typography.labelSmall)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) { Text("Edit") }
                TextButton(onClick = onDelete) { Text("Delete") }
            }
        }
    }
}