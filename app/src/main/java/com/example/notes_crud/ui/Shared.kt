package com.example.notes_crud.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericAppBar(
    title: String,
    onIconClick: (() -> Unit)?,
    icon: @Composable() (() -> Unit)?,
    iconState: MutableState<Boolean>,
    navController: NavController,
    isHomePage: Boolean = false
) {
    TopAppBar(
        title = { Text(title) },
        actions = {
            IconButton(
                onClick = { onIconClick?.invoke() },
                content = {
                    if (iconState.value) {
                        icon?.invoke()
                    }
                }
            )
        },
        navigationIcon = {
            if (!isHomePage) {
                IconButton(onClick = { navController.popBackStack() } ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowLeft,
                        contentDescription = " Go back"
                    )
                }
            }

        }
    )
}