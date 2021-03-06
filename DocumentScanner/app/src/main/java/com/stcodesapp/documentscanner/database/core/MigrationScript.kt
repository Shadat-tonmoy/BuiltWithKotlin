package com.stcodesapp.documentscanner.database.core

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object  : Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE image ADD COLUMN filterName TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_6_7 = object  : Migration(6,7){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE image ADD COLUMN filterJson TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_7_8 = object  : Migration(7,8){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE image ADD COLUMN customFilterJson TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_8_9 = object  : Migration(8,9){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE image ADD COLUMN paperEffectJson TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_9_10 = object  : Migration(9,10){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE image ADD COLUMN originalCropArea TEXT NOT NULL DEFAULT ''")
    }
}

val migrationScripts = arrayOf(MIGRATION_1_2, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)