package com.example.notes_crud.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.notes_crud.model.Note

@Database(version = 1, entities = [Note::class])
abstract class NotesDatabase : RoomDatabase() {
    abstract fun NotesDao(): NotesDao
}