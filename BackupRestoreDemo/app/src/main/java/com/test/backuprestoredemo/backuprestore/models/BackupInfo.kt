package com.test.backuprestoredemo.backuprestore.models

//user id, device id, device name
data class BackupInfo(var usserid : String = "", var deviceId : String = "", var deviceName : String="", var lastBackupTime : Long = 0, var totalBackupSize : Long = 0)