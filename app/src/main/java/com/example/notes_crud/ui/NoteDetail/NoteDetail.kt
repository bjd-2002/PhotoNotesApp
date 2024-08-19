package com.example.notes_crud.ui.NoteDetail

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.dbtechprojects.photonotes.Constants
import com.dbtechprojects.photonotes.Constants.noteDetailPlaceHolder
import com.example.notes_crud.NotesViewModel
import com.example.notes_crud.R
import com.example.notes_crud.ui.GenericAppBar
import com.example.notes_crud.ui.theme.NOTESCRUDTheme
import kotlinx.coroutines.launch

@Composable
fun NoteDetailPage(
    noteId: Int,
    navController: NavController,
    viewModel: NotesViewModel
    ) {
    val scope = rememberCoroutineScope()
    val note = remember {
        mutableStateOf(noteDetailPlaceHolder)
    }

    LaunchedEffect(key1 = true) {
        scope.launch {
            note.value = viewModel.getNote(noteId) ?: noteDetailPlaceHolder
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
                        title = note.value.title,
                        onIconClick = {
                            navController.navigate(Constants.noteEditNavigation(note.value.id ?: 0))
                        },
                        icon = { 
                            Icon(
                                imageVector = ImageVector.vectorResource(id = com.example.notes_crud.R.drawable.edit_note),
                                contentDescription = stringResource(
                                    R.string.edit_note
                                ),
                                tint = Color.Black
                            )
                        },
                        iconState = remember {
                            mutableStateOf(true)
                        },
                        navController
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                ) {
                    if (note.value.imageUri != null && note.value.imageUri!!.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest
                                    .Builder(LocalContext.current)
                                    .data(data = Uri.parse(note.value.imageUri))
                                    .build()
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxHeight(0.3f)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Text(
                        text = note.value.title,
                        modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = note.value.dateUpdated,
                        modifier = Modifier.padding(top = 24.dp, start = 12.dp, end = 24.dp),
                        color = Color.Gray
                    )

                    Text(
                        text = note.value.note,
                        modifier = Modifier.padding(12.dp),
                    )

                }
            }
        }
    }
}
