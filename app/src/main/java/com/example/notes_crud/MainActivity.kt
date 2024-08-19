package com.example.notes_crud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dbtechprojects.photonotes.Constants
import com.example.notes_crud.ui.NoteCreate.CreateNoteScreen
import com.example.notes_crud.ui.NoteDetail.NoteDetailPage
import com.example.notes_crud.ui.NoteEdit.NoteEditScreen
import com.example.notes_crud.ui.NoteList.NoteListScreen
import com.example.notes_crud.ui.theme.NOTESCRUDTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: NotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = NoteViewModelFactory(PhotoNotesApp.getDao()).create(NotesViewModel::class.java)
//        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = Constants.NAVIGATION_NOTES_LIST) {
                // Notes List
                composable(Constants.NAVIGATION_NOTES_LIST) {
                    NoteListScreen(navController = navController, viewModel = viewModel)
                }

                // note detail page
                composable(
                    Constants.NAVIGATION_NOTE_DETAIL,
                    arguments = listOf(navArgument(Constants.NAVIGATION_NOTE_ID_Argument){
                        type = NavType.IntType
                    })
                ) {navBackStackEntry ->
                    navBackStackEntry.arguments?.getInt(Constants.NAVIGATION_NOTE_ID_Argument)?.let { 
                        NoteDetailPage(noteId = it, navController = navController, viewModel = viewModel)
                    }
                }

                // note edit page
                composable(
                    Constants.NAVIGATION_NOTE_EDIT,
                    arguments = listOf(navArgument(Constants.NAVIGATION_NOTE_ID_Argument){
                        type = NavType.IntType
                    })
                ) {navBackStackEntry ->
                    navBackStackEntry.arguments?.getInt(Constants.NAVIGATION_NOTE_ID_Argument)?.let {
                        NoteEditScreen(noteId = it, navController = navController, viewModel = viewModel)
                    }
                }

                // note create page
                composable(
                    Constants.NAVIGATION_NOTES_CREATE
                ) {
                    CreateNoteScreen(navController = navController, viewModel = viewModel)
                }

            }
        }
    }
}

