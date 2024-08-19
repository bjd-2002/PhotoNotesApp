package com.example.notes_crud.ui.NoteList

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.dbtechprojects.photonotes.Constants
import com.example.notes_crud.NoteViewModelFactory
import com.example.notes_crud.NotesViewModel
import com.example.notes_crud.PhotoNotesApp
import com.example.notes_crud.R
import com.example.notes_crud.model.Note
import com.example.notes_crud.model.getDay
import com.example.notes_crud.model.orPlaceHolderList
import com.example.notes_crud.ui.GenericAppBar
import com.example.notes_crud.ui.NoteEdit.NoteEditScreen
import com.example.notes_crud.ui.theme.BrightYellow
import com.example.notes_crud.ui.theme.NOTESCRUDTheme
import com.example.notes_crud.ui.theme.NoteYellow
import com.example.notes_crud.ui.theme.Purple200
import com.example.notes_crud.ui.theme.Purple500

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoteListScreen(
    navController: NavController,
    viewModel: NotesViewModel
) {

    val deleteText = remember {
        mutableStateOf("")
    }
    val noteQuery = remember {
        mutableStateOf("")
    }
    val notesToDelete = remember {
        mutableStateOf(listOf<Note>())
    }
    val openDialog = remember {
        mutableStateOf(false)
    }

    val notes = viewModel.notes.observeAsState()
    val context = LocalContext.current

    NOTESCRUDTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background,
//            contentColor = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    GenericAppBar(
                        title = "Create Note",
                        onIconClick = {
                            if (notes.value?.isNotEmpty() == true) {
                                openDialog.value = true
                                deleteText.value = "Are you sure you want to delete all notes"
                                notesToDelete.value = notes.value ?: emptyList()
                            } else {
                                Toast.makeText(context, "No notes found", Toast.LENGTH_SHORT).show()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = com.example.notes_crud.R.drawable.note_delete),
                                contentDescription = stringResource(
                                    R.string.delete_note
                                ),
                                tint = Color.Black
                            )
                        },
                        iconState = remember {
                            mutableStateOf(true)
                        },
                        navController,
                        true
                    )
                },
                floatingActionButton = {
                    NotesFab(
                        contentDescription = stringResource(id = R.string.create_note),
                        action = {
                            navController.navigate(Constants.NAVIGATION_NOTES_CREATE)
                        },
                        icon = R.drawable.note_add_icon
                    )
                }
            ) {
                Column(
                    modifier = Modifier.padding(it)
                ) {
                    SearchBar(noteQuery)
                    NoteList(
                        notes = notes.value.orPlaceHolderList(),
                        query = noteQuery,
                        openDialog = openDialog,
                        deleteText = deleteText,
                        navController = navController,
                        notesToDelete = notesToDelete
                    )
                }

                DeleteDialog(
                    openDialog = openDialog,
                    text = deleteText,
                    action = {
                        notesToDelete.value.forEach {
                            viewModel.deleteNote(it)
                        }
                    },
                    notesToDelete = notesToDelete
                )
            }
        }
    }
}

@Preview
@Composable
fun NoteListScreenPreview() {
    val viewModel = NoteViewModelFactory(PhotoNotesApp.getDao()).create(NotesViewModel::class.java)
    NoteEditScreen(noteId = 0, navController = rememberNavController(), viewModel = viewModel)
}

@Composable
fun SearchBar(query: MutableState<String>) {
    Column(Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 0.dp)){
        TextField(
            value = query.value,
            placeholder = { Text(text = "Search..")},
            maxLines = 1,
            onValueChange = { query.value = it},
            modifier = Modifier
                .background(Color.White)
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(Color.Black),
            trailingIcon = {
                AnimatedVisibility(visible = query.value.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                    IconButton(onClick = { query.value = "" }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.icon_cross),
                            contentDescription = stringResource(id = R.string.clear_search)
                        )
                    }
                }
            }
        )

    }
}

@Composable
fun NoteList(
    notes : List<Note>,
    openDialog : MutableState<Boolean>,
    query: MutableState<String>,
    deleteText: MutableState<String>,
    navController: NavController,
    notesToDelete: MutableState<List<Note>>
) {
    var previousHeader = ""

    LazyColumn(
        contentPadding = PaddingValues(12.dp),
//        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
    ) {
        val queryNotes = if (query.value.isEmpty()) {
            notes
        } else {
            notes.filter { it.note.contains(query.value) || it.title.contains(query.value) }
        }

        itemsIndexed(queryNotes) { index, note ->
            if (note.getDay() != previousHeader) {
                Column (
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = note.getDay(), color = Color.Black)
                }
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp))
                previousHeader = note.getDay()
            }
            NoteListItem(
                note,
                openDialog,
                deleteText,
                navController,
                if (index % 2 == 0) {
                    BrightYellow
                } else {
                    Purple200
                },
                notesToDelete,
            )
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(12.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteListItem(
    note: Note,
    openDialog: MutableState<Boolean>,
    deleteText: MutableState<String>,
    navController: NavController,
    noteBackgroundColor: Color,
    notesToDelete: MutableState<List<Note>>,
) {
    Box(
        modifier = Modifier
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .background(noteBackgroundColor)
                .height(120.dp)
                .fillMaxWidth()
                .combinedClickable(interactionSource = remember {
                    MutableInteractionSource()
                },
                    indication = rememberRipple(bounded = false),
                    onClick = {
                        if (note.id != 0) {
                            navController.navigate(
                                Constants.noteDetailNavigation(
                                    noteId = note.id ?: 0
                                )
                            )
                        }
                    },
                    onLongClick = {
                        if (note.id != 0) {
                            openDialog.value = true
                            deleteText.value = "Are you sure you want to delete this note ?"
                            notesToDelete.value = mutableListOf(note)
                        }
                    }
                )
        ) {
            Row {
                if (note.imageUri != null && note.imageUri.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(model =
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(Uri.parse(note.imageUri))
                            .build()
                        ),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(0.3f),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = note.title,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Text(
                        text = note.note,
                        color = Color.Black,
                        maxLines = 3,
                        modifier = Modifier.padding(12.dp)
                    )

                    Text(
                        text = note.dateUpdated,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

            }
        }
    }

}

@Composable
fun NotesFab(
    contentDescription: String,
    icon: Int,
    action: () -> Unit
) {
    return FloatingActionButton(onClick = { action.invoke() }) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription,
            tint = Color.Black
        )
    }
}

@Composable
fun DeleteDialog(
    openDialog: MutableState<Boolean>,
    text: MutableState<String>,
    action: () -> Unit,
    notesToDelete: MutableState<List<Note>>
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            confirmButton = {
                Button(
                    modifier = Modifier.padding(12.dp),
                    colors = ButtonDefaults.buttonColors(),
                    onClick = {
                        action.invoke()
                        openDialog.value = false
                        notesToDelete.value = mutableListOf()
                    }
                ) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                Button(
                    modifier = Modifier.padding(12.dp),
                    colors = ButtonDefaults.buttonColors(),
                    onClick = {
                        openDialog.value = false
                        notesToDelete.value = mutableListOf()
                    }
                ) {
                    Text(text = "No")
                }
            },
            title = {
                Text(text = "Delete Note")
            },
            text = {
                Column {
                    Text(text = text.value)
                }
            },

        )
    }
}
