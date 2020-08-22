package com.stcodesapp.documentscanner.constants

class RequestCode
{
    companion object{
        const val REQUEST_CAMERA_PERMISSION = 1
        const val EDIT_GROUP_INFO = 5
        const val ADD_MEMBER_TO_GROUP = 6
        const val REQUEST_STORAGE_WRITE_PERMISSION = 7
        const val OPEN_CONVERSATION_THREAD: Int = 8
        const val CAPTURE_IMAGE_FROM_CAMERA = 9
        const val PICK_FILE_FROM_GALLERY = 11 // here file can be image/video
        const val SEND_VIDEO_FROM_PREVIEW = 12
        const val PICK_AUDIO_FROM_GALLERY = 13
        const val PICK_DOCUMENT = 14 // here document can be pdf, txt, json, apk etc
        const val PICK_IMAGE_FROM_GALLERY = 15
        const val REQUEST_FINE_LOCATION = 16
        const val PICK_CONTACT = 17
        const val CREATE_NEW_GROUP = 18
        const val REQUEST_STORAGE_WRITE_FOR_AUDIO = 19
        const val REQUEST_STORAGE_WRITE_FOR_IMAGE = 20
        const val REQUEST_STORAGE_WRITE_FOR_DOCUMENT = 21
        const val UPDATE_GROUP_NAME_AND_IMAGE = 22
        const val REQUEST_STORAGE_WRITE_FOR_VIDEO = 23
        const val PICK_VIDEO_FROM_GALLERY = 24
    }
}