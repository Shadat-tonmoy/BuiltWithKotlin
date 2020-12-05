package com.test.backuprestoredemo.backuprestore.models

/**
 * @param storagePath storage path of the file  like  storage/32FA-1D18/Android/data/com.sil.communicator/files/
 * @param dataDir path of the data directory like Media/
 * @param dataDir is not currently used and set to empty string "" This parameter can be used to start Backup
 * from sub folders like {storage/32FA-1D18/Android/data/com.sil.communicator/files/Media} or {storage/32FA-1D18/Android/data/com.sil.communicator/files/Media/Communicator Image}
 * */
data class FilePath(var storagePath : String, var dataDir : String)