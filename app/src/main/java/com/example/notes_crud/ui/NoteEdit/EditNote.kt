package com.example.notes_crud.ui.NoteEdit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.dbtechprojects.photonotes.Constants
import com.example.notes_crud.NotesViewModel
import com.example.notes_crud.PhotoNotesApp
import com.example.notes_crud.R
import com.example.notes_crud.model.Note
import com.example.notes_crud.ui.GenericAppBar
import com.example.notes_crud.ui.NoteList.NotesFab
import com.example.notes_crud.ui.theme.NOTESCRUDTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Int,
    navController: NavController,
    viewModel: NotesViewModel
) {
    val scope = rememberCoroutineScope()
    val note = remember {
        mutableStateOf(Constants.noteDetailPlaceHolder)
    }

    val currentNote = remember {
        mutableStateOf(note.value.note)
    }
    val currentTitle = remember {
        mutableStateOf(note.value.title)
    }
    val currentPhotos = remember {
        mutableStateOf(note.value.imageUri)
    }
    val saveButtonState = remember {
        mutableStateOf(false)
    }

    val getImageRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                PhotoNotesApp.getUriPermission(uri)
            }
            currentPhotos.value = uri.toString()
            if (currentPhotos.value != note.value.imageUri) {
                saveButtonState.value = true
            }
        }
    )
    LaunchedEffect(key1 = true) {
        scope.launch(Dispatchers.IO) {
            note.value = viewModel.getNote(id = noteId ?: 0) ?: Constants.noteDetailPlaceHolder
            currentNote.value = note.value.note
            currentTitle.value = note.value.title
            currentPhotos.value = note.value.imageUri
        }
    }

    NOTESCRUDTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    GenericAppBar(
                        title = "Edit Note",
                        onIconClick = {
                            viewModel.updateNote(
                                Note(
                                    id = note.value.id,
                                    note = currentNote.value,
                                    title = currentTitle.value,
                                    imageUri = currentPhotos.value
                                )
                            )
                            navController.popBackStack()
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = com.example.notes_crud.R.drawable.save),
                                contentDescription = stringResource(
                                    R.string.save_note
                                ),
                                tint = Color.Black
                            )
                        },
                        iconState = saveButtonState,
                        navController
                    )
                },
                floatingActionButton = {
                    NotesFab(
                        contentDescription = stringResource(id = R.string.add_photo),
                        action = {
                            getImageRequest.launch(arrayOf("image/*"))
                        },
                        icon = R.drawable.camera
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .padding( start = 12.dp, end = 12.dp)
                        .fillMaxSize()
                ) {
                    if (currentPhotos.value != null && currentPhotos.value!!.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest
                                    .Builder(LocalContext.current)
                                    .data(data = Uri.parse(currentPhotos.value))
                                    .build()
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxHeight(0.3f)
                                .fillMaxWidth()
                                .padding(6.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = currentTitle.value,
                        colors = TextFieldDefaults.textFieldColors(
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black
                        ),
                        onValueChange = { value ->
                            currentTitle.value = value
                            if (currentTitle.value != note.value.title) {
                                saveButtonState.value = true
                            } else if( currentTitle.value != note.value.title &&
                                currentNote.value == note.value.note) {
                                saveButtonState.value = false
                            }
                        },
                        label = { Text("Title") },
                    )

                    Spacer(modifier = Modifier.padding(12.dp))

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = currentNote.value,
                        colors = TextFieldDefaults.textFieldColors(
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black
                        ),
                        onValueChange = { value ->
                            currentNote.value = value
                            if (currentNote.value != note.value.note) {
                                saveButtonState.value = true
                            } else if( currentTitle.value != note.value.title &&
                                currentNote.value == note.value.note) {
                                saveButtonState.value = false
                            }
                        },
                        label = { Text("Body") },
                    )

                }
            }
        }
    }
}