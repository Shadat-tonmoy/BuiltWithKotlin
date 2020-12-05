package com.test.backuprestoredemo.backuprestore.models

import com.test.backuprestoredemo.backuprestore.models.FilePath

data class FileToBackup(var filePath : String, var rootDir : FilePath, var fileSizeInBytes : Long)