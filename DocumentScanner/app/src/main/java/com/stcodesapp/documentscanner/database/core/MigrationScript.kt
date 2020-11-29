package com.stcodesapp.documentscanner.database.core

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object  : Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE image ADD COLUMN filterName TEXT NOT NULL DEFAULT ''")
    }
}

val migrationScripts = arrayOf(MIGRATION_1_2)