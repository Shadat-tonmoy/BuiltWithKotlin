package com.test.fffmpeglite.helper

class Values{
    companion object
    {
        const val MAX_CONVERSATION_TO_LOAD = 20
        const val INVALID_CONVERSATION_ID = -1L
        const val NEW_USER = 1
        const val EXISTING_USER = 2
        const val UNKNOWN_USER = -1
        const val GROUP_EDIT_API_RESPONSE_KEY = "Message"
        const val FILE_UPLOAD_API_RESPONSE_KEY = "file_id"
        const val GROUP_EDIT_API_RESPONSE_KEY_FOR_REMOVE_GROUP = "message"
        const val GROUP_NAME_UPDATE_SUCCESS = "Group name successfully updated"
        const val GROUP_MEMBER_ADD_SUCCESS = "Group members added successfully"
        const val GROUP_MEMBER_REMOVE_SUCCESS = "Group members removed successfully"
        const val GROUP_REMOVE_SUCCESS = "Group successfully removed"
        const val GROUP_ALREADY_DELETED = "Group does not exists or already deleted"
        const val BACKUP_FILE_NAME = "backup.json"
        const val BACKUP_FILE_TYPE = ".json"
        const val BACKUP_MEDIA = "media_info"
        const val ZIP_FILE_TYPE = ".zip"
        const val BACKUP_FOLDER_NAME = "backup"
        const val MEDIA_FOLDER_NAME = "media"
        const val CAMERA = 1
        const val IMAGE = 2
        const val DOCUMENT = 3
        const val AUDIO = 4
        const val LOCATION = 5
        const val CONTACT = 6
        const val VIDEO = 7

        const val IMAGE_FILE = "image/jpeg"
        const val DOCUMENT_FILE = "document"
        const val AUDIO_FILE = "audio/mpeg"
        const val IMAGE_FILE_DIR = "images"
        const val VIDEO_FILE = "video/mp4"
        const val VIDEO_FILE_DIR = "videos"
        const val AUDIO_FILE_DIR = "audios"
        const val DOCUMENT_FILE_DIR = "documents"
        const val CONTACT_FILE_DIR = "contacts"
        const val DOWNLOAD_FILE_DIR = "Download"
        const val CONTACT_FILE = "text/x-vcard"
        const val JPEG_FILE_EXTENSION = ".jpeg"
        const val PNG_FILE_EXTENSION = ".png"
        const val SAVED_IMAGE_FILE_EXTENSION = JPEG_FILE_EXTENSION
        const val SUCCESS = 1
        const val FAILURE = -1
        const val INVALID_SESSION_CLEAR = -1L
        const val PINNED_SUCCESS = 1
        const val UNPINNED_SUCCESS = 2
        const val MUTE_SUCCESS = 1
        const val UNMUTE_SUCCESS = 2
        const val MUTE_UNMUTE_FAILED = -1
        const val UNBLOCK_SUCCESS = 1
        const val INVALID = -1
        const val ALL_DATA_BACKUP_SUCCESS = 1
        const val LATEST_DATA_BACKUP_SUCCESS = 2
        const val NOTHING_TO_BACKUP = 3
        const val DRIVE_PERMISSION_DENIED = -3
        const val BACKUP_FAILED = -1
        const val RESTORE_SUCCESS = 1
        const val RESTORE_FAILURE = -1
        const val NO_BACKUP = -2
        const val BACKUP_FILE_FOUND = 1
        const val MAX_GROUP_NAME_LENGTH = 25
        const val CALL_TTL = 3600

        var FULL_MONTHS = arrayOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )
        var TRIMMED_MONTHS = arrayOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )
        var DAYS_OF_WEEK = arrayOf(
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday"
        )
        var TRIMMED_DAYS_OF_WEEK =
            arrayOf("Sun", "Mon", "Tue", "Wed", "Thr", "Fri", "Sat")
        var AM_PM = arrayOf("AM", "PM")
        const val IMAGE_MIME_PREFIX = "image/"
        const val AUDIO_MIME_PREFIX = "audio/"
        const val VIDEO_MIME_PREFIX = "video/"
        const val DOCUMENT_MIME_PREFIX = "document/"
        const val TEXT_MIME_PREFIX = "text/"
        const val AUDIO_CALL_TYPE = "AUDIO"
        const val VEDIO_CALL_TYPE = "VIDEO"
        const val COMMUNICATOR_VIDEOS_DIR = "Communicator Videos"
        const val COMMUNICATOR_IMAGES_DIR = "Communicator Images"
        const val COMMUNICATOR_AUDIOS_DIR = "Communicator Audios"
        const val COMMUNICATOR_CONTACTS_DIR = "Communicator Contacts"
        const val COMMUNICATOR_DOCUMENTS_DIR = "Communicator Documents"
        const val COMMUNICATOR_SENT_DIR_SUFFIX = "Sent"



    }
}