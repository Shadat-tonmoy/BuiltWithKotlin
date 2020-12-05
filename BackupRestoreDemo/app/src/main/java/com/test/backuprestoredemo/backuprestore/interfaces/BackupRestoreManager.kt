package com.test.backuprestoredemo.backuprestore.interfaces

import com.test.backuprestoredemo.backuprestore.models.BackupInfo
import com.test.backuprestoredemo.backuprestore.models.FilePath


interface BackupRestoreManager {
    fun sync(excludeList: List<String>)
    fun restore(includeList : List<String>, excludeList: List<String>, rootDir : FilePath)
    fun getAvailableBackup(userId : String, deviceId : String) : List<BackupInfo>
    fun setRootDirsToBackup(rootDirs : List<FilePath>)
    fun setIdentityMap(identityMap : HashMap<String,String>)
    fun createFolder(name: String)
}