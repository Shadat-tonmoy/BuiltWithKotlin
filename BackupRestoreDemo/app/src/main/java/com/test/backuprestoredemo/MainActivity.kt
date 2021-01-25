package com.test.backuprestoredemo

import android.Manifest
import android.accounts.Account
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import bg.devlabs.fullscreenvideoview.VideoViewActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.test.backuprestoredemo.backuprestore.BackupRestoreService
import com.test.backuprestoredemo.helper.FileHelper
import com.test.backuprestoredemo.helper.FilePathHelper
import com.test.backuprestoredemo.helper.PermissionHelper
import com.test.backuprestoredemo.videoCompression.FFMPEGHelper
import com.test.backuprestoredemo.videoCompression.VideoInfo
import com.tigerit.filelogger.FileLogger
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.video_compressor_ui.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import kotlin.math.min


class MainActivity : AppCompatActivity(), FFMPEGHelper.Listener, AdapterView.OnItemSelectedListener {

    companion object {
        private const val TAG = "BackupActivity"
        val REQUEST_CODE_SIGN_IN = 147
        val REQUEST_CODE_VIDEO = 148

    }

    private val FFMPEGHelper by lazy {
        FFMPEGHelper(this, this)
    }


    private var chosenFilePath : String? = null
    private var startTime : Long = 0
    private var endTime : Long = 0
    private var selectedVideoBitRate : Int = 0
    private var selectedAudioBitRate : Int = 0
    private var selectedVideoFrameRate : Int = 0
    private var selectedWidth : Long = 0
    private var selectedHeight : Long = 0
    private var originalVideoInfo : VideoInfo? = null
    private var ultraFastMode : Boolean = false
    private var maxVideoBitRate = 2000
    private val maxAudioBitRate = 320
    private val maxVideoFrameRate = 30
    private var maxVideoResolution = 480L
    var userSelectedResolution = false

    private var backupRestoreService : BackupRestoreService? = null
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.e(TAG, "onServiceConnected: called")
            backupRestoreService = (service as BackupRestoreService.BackupRestoreServiceBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            backupRestoreService = null
            Log.e(TAG, "onServiceDisconnected: called")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FFMPEGHelper.loadFFMPEG()
        init()
        getGoogleAccount()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivityResult: Called For ReqCode : $requestCode")
        if (resultCode == Activity.RESULT_OK)
        {
            if(requestCode == REQUEST_CODE_SIGN_IN){
                handleSignInResult(data)
            }
            else if(requestCode == REQUEST_CODE_VIDEO){
                //handleVideoData(data)
                fetchVideoInfo(data)
            }
        }
        else
        {
            Log.e(TAG, "onActivityResult: GoogleSignInResult $resultCode")
        }
    }

    private fun init()
    {
        //initBackupService()
        testButton.setOnClickListener {
            startBackup() }
        signOut.setOnClickListener { signOut() }
//        emailAButton.setOnClickListener { requestSignIn(emailAButton.text.toString()) }
//        emailBButton.setOnClickListener { requestSignIn(emailBButton.text.toString()) }
        /*playVideo.setOnClickListener {
            videoView.visibility = View.VISIBLE
            videoView.setVideoPath(chosenFilePath)
            videoView.start()
            //VideoViewActivity.startVideo(this, chosenFilePath!!,"Sender","Time")
        }
        pickVideo.setOnClickListener { pickVideo() }
        compressButton.setOnClickListener { compressVideo() }
        qualityRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            if(i == R.id.quality360P)
            {
                maxVideoResolution = 360L
                if(originalVideoInfo != null)
                {
                    if(originalVideoInfo!!.width < originalVideoInfo!!.height)
                    {
                        if(originalVideoInfo!!.width > 360)
                        {
                            selectedWidth = 360
                            selectedHeight = -2
                            userSelectedResolution = true
                        }
                        else
                        {
                            selectedHeight = originalVideoInfo!!.height
                            selectedWidth = originalVideoInfo!!.width
                            userSelectedResolution = false
                        }
                    }
                    else
                    {
                        if(originalVideoInfo!!.height > 360)
                        {
                            selectedHeight = 360
                            selectedWidth = -2
                            userSelectedResolution = true
                        }
                        else
                        {
                            selectedHeight = originalVideoInfo!!.height
                            selectedWidth = originalVideoInfo!!.width
                            userSelectedResolution = false
                        }
                    }
                }


            }
            else if(i == R.id.quality480P)
            {
                maxVideoResolution = 480L
                *//*if(originalVideoInfo != null)
                {
                    if(originalVideoInfo!!.width < originalVideoInfo!!.height)
                    {
                        if(originalVideoInfo!!.width > 480)
                        {
                            selectedWidth = 480
                            selectedHeight = -2
                            userSelectedResolution = true
                        }
                        else
                        {
                            selectedHeight = originalVideoInfo!!.height
                            selectedWidth = originalVideoInfo!!.width
                            userSelectedResolution = false
                        }
                    }
                    else
                    {
                        if(originalVideoInfo!!.height > 480)
                        {
                            selectedHeight = 480
                            selectedWidth = -2
                            userSelectedResolution = true
                        }
                        else
                        {
                            selectedHeight = originalVideoInfo!!.height
                            selectedWidth = originalVideoInfo!!.width
                            userSelectedResolution = false
                        }
                    }
                }*//*
            }
            else if(i == R.id.quality720P)
            {
                maxVideoResolution = 720L

                *//*if(originalVideoInfo != null)
                {
                    if(originalVideoInfo!!.width < originalVideoInfo!!.height)
                    {
                        if(originalVideoInfo!!.width > 720)
                        {
                            selectedWidth = 720
                            selectedHeight = -2
                            userSelectedResolution = true
                        }
                        else
                        {
                            selectedHeight = originalVideoInfo!!.height
                            selectedWidth = originalVideoInfo!!.width
                            userSelectedResolution = false
                        }
                    }
                    else
                    {
                        if(originalVideoInfo!!.height > 720)
                        {
                            selectedHeight = 720
                            selectedWidth = -2
                            userSelectedResolution = true
                        }
                        else
                        {
                            selectedHeight = originalVideoInfo!!.height
                            selectedWidth = originalVideoInfo!!.width
                            userSelectedResolution = false
                        }
                    }
                }*//*
            }
            else if(i == R.id.qualityOriginal)
            {
                if(originalVideoInfo != null)
                {
                    selectedHeight = originalVideoInfo!!.height
                    selectedWidth = originalVideoInfo!!.width
                }
                userSelectedResolution = false
            }
        }*/

//        ultraFastCheckBox.setOnCheckedChangeListener { compoundButton, b ->
//            ultraFastMode = b
//        }


    }

    private fun pickVideo()
    {
        val permissionHelper = PermissionHelper(this)
        if(permissionHelper.isReadStoragePermissionGranted())
        {
            openOutputButton.visibility = View.GONE
            startActivityForResult(Intent.createChooser(Intent(Intent.ACTION_GET_CONTENT)
                .setType("video/*"), "Choose Video"), REQUEST_CODE_VIDEO)
        }

    }

    private fun startBackup()
    {

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account != null)
        {
            Log.e(TAG, "startBackup: account : $account")
            //createTestFolder()
        }
        else
        {
            Log.e(TAG, "requesting signin")
            requestSignIn("shadat.tonmoy@gmail.com")
        }
    }

    private fun createTestFolder()
    {
        Log.e(TAG, "createTestFolder: $backupRestoreService")
        backupRestoreService?.createFolder("Test")

    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleVideoData(result: Intent?)
    {
        if(result != null)
        {
            /*val uri: Uri = result.data!!
            val job = FileCompressionJob(this, object  : FileCompressionJob.Listener{
                override fun onCompressionUpdate(msg: String) {
                    runOnUiThread { compressionProgressText.text = msg }
                }

                override fun onCompressionCompleted(msg: String?) {
                    runOnUiThread { compressionCompletedText.text = msg }
                }

                override fun onCompressedFileSaved(msg: String) {
                    runOnUiThread { compressionSavedText.text = msg }
                }

                override fun onCompressionError(msg: String) {
                    runOnUiThread {
                        AlertDialog.Builder(this@MainActivity).setMessage(msg).setTitle("Error").setPositiveButton("Ok"
                        ) { p0, p1 -> p0.dismiss() }.create().show()
                    }
                }

            })
            job.compressFile(uri)

            val realpath: String? = getRealVideoPathFromURI(uri)
            Log.e(TAG, "handleVideoData: RealPath : $realpath path : ${uri.path}")*/

        }

    }

    private fun fetchVideoInfo(result: Intent?)
    {
        if(result != null)
        {
            val uri: Uri = result.data!!
            val path = FilePathHelper().getPath(this, uri)
            chosenFilePath = path
            Log.e(TAG, "fetchVideoInfo: Path : ${uri.path} absolutePath : $path")

            /*var realpath: String? = copyFile(uri)
            chosenFilePath = realpath
            if(realpath == null) realpath = uri.path*/
            startTime = System.currentTimeMillis()
            CoroutineScope(Dispatchers.IO).launch {
                var videoInfo = FFMPEGHelper.getFileInfo(chosenFilePath!!)
                originalVideoInfo = videoInfo

                if(videoInfo != null)
                {
                    selectedWidth = videoInfo!!.width
                    selectedHeight = videoInfo!!.height
                    runOnUiThread { bindVideoInfoToUI(videoInfo) }
                }

            }


            Log.e(TAG, "handleVideoData: RealPath : $chosenFilePath path : ${uri.path}")
        }
    }

    private fun compressVideo()
    {
        if(chosenFilePath != null)
        {
            startTime = System.currentTimeMillis()
            openOutputButton.visibility = View.GONE
            CoroutineScope(Dispatchers.IO).launch {
                val scale = selectedHeight != originalVideoInfo!!.height && selectedWidth != originalVideoInfo!!.width
                //selectedAudioBitRate = audioBitrateField.text.toString().toInt()
                selectedVideoBitRate = videoBitrateField.text.toString().toInt()
                //selectedVideoFrameRate = videoFrameRateField.text.toString().toInt()
                maxVideoBitRate = selectedVideoBitRate

                //selectedAudioBitRate = min(selectedAudioBitRate, maxAudioBitRate)
                //selectedVideoBitRate = min(selectedVideoBitRate, maxVideoBitRate)
                if(selectedVideoBitRate > maxVideoBitRate)
                {
                    selectedVideoBitRate = maxVideoBitRate
                }
                else selectedVideoBitRate = -1
                //selectedVideoFrameRate = min(selectedVideoFrameRate, maxVideoFrameRate)

                if(userSelectedResolution)
                {
                    if(selectedHeight == -2L)
                    {
                        selectedWidth = min(selectedWidth, maxVideoResolution)
                    }
                    else if(selectedWidth == -2L)
                    {
                        selectedHeight = min(selectedHeight, maxVideoResolution)
                    }

                }
                else
                {
                    if(selectedWidth < selectedHeight)
                    {
                        // for portrait video
                        if(selectedWidth > maxVideoResolution)
                        {
                            // need to scale down to 480 as width and height will be as per aspect ratio
                            selectedWidth = maxVideoResolution
                            selectedHeight = -2
                        }
                        else
                        {
                            //no need to scale down. will be ignored by command
                            selectedWidth = -1
                            selectedHeight = -1
                        }


                    }
                    else
                    {
                        // for landscape video
                        if(selectedHeight > maxVideoResolution)
                        {
                            // need to scale down to 480 as height and width will be as per aspect ratio
                            selectedHeight = maxVideoResolution
                            selectedWidth = -2
                        }
                        else
                        {
                            //no need to scale down. will be ignored by command
                            selectedHeight = -1
                            selectedWidth = -1
                        }
                    }
                }

                FileLogger.getInstance()?.log(TAG, "compressVideo: SelectedVideoBitrate : $selectedVideoBitRate, selectedAudioBitRate : $selectedAudioBitRate, selectedVideoFrameRate : $selectedVideoFrameRate selectedWidth : $selectedWidth, selectedHeight : $selectedHeight, ultraFastMode : $ultraFastMode, scale : $scale, OriginalVideoInfo : $originalVideoInfo")



                FFMPEGHelper.compressFile(chosenFilePath!!,-1L, selectedVideoBitRate,selectedVideoFrameRate,selectedWidth.toInt(),selectedHeight.toInt())
            }
        }
        else
        {
            Toast.makeText(this,"Please choose a vide file",Toast.LENGTH_SHORT).show()
        }

    }

    private fun bindVideoInfoToUI(videoInfo: VideoInfo?)
    {
        if (videoInfo != null)
        {
            videoBitrateValue.text = "Original : ${videoInfo.videoBitRate} Kbps"

            videoBitrateField.setText("${videoInfo.videoBitRate}")

            val originalVideoInfoString = "Video Bitrate : ${videoInfo.videoBitRate} Kbps\nAudio Bitrate : ${videoInfo.audioBitrate} \nVideo Frame Rate : ${videoInfo.videoFrameRate}\nResolution : ${videoInfo.width}w x ${videoInfo.height}h\nVideo Codec : ${videoInfo.videoCodec}\nAudio Codec : ${videoInfo.audioCodec}"
            originalVideoInfoValue.text = originalVideoInfoString

            selectedAudioBitRate = videoInfo.audioBitrate
            selectedVideoBitRate = videoInfo.videoBitRate
            selectedVideoFrameRate = videoInfo.videoFrameRate
            selectedWidth = videoInfo.width
            selectedHeight = videoInfo.height
            userSelectedResolution = false

        }
    }


    private fun bindCompressedVideoInfoToUI(videoInfo: VideoInfo?)
    {
        if (videoInfo != null)
        {
            val videoInfoString = "Video Bitrate : ${videoInfo.videoBitRate} Kbps\nAudio Bitrate : ${videoInfo.audioBitrate} \nVideo Frame Rate : ${videoInfo.videoFrameRate}\nResolution : ${videoInfo.width}w x ${videoInfo.height}h\nVideo Codec : ${videoInfo.videoCodec}\nAudio Codec : ${videoInfo.audioCodec}"
            compressedVideoInfoValue.text = videoInfoString

        }
    }

    override fun onCompressionProgress(progress: Float) {
        runOnUiThread {
            val progressValue = String.format("%.2f",progress)
            compressionProgressText.text = "Compression in progress $progressValue %"
            progressBar.progress = progress.toInt()
        }
    }

    override fun onCompressionCompleted(savedFilePath: String) {
        runOnUiThread {
            endTime = System.currentTimeMillis()
            val timeRequired = (endTime - startTime) / 1000
            val inputSize = getFileSizeString(File(chosenFilePath!!).length())
            val compressedSize = getFileSizeString(File(savedFilePath!!).length())
            val msg = "Compression Completed!\nOutput file is saved at : $savedFilePath.\nTotal Time needed : $timeRequired seconds.\nOriginal File Size : $inputSize\nCompressed File Size : $compressedSize"
            compressionProgressText.text = msg
            openOutputButton.visibility = View.VISIBLE
            openOutputButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                val fileURI = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", File(savedFilePath))
                intent.setDataAndType(fileURI, "video/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (intent.resolveActivity(packageManager) != null) {
                    val chooserIntent = Intent.createChooser(intent, "Open With")
                    startActivity(chooserIntent)
                }
                else Toast.makeText(this,"Could not open the file!",Toast.LENGTH_SHORT).show()

            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            var videoInfo = FFMPEGHelper.getFileInfo(savedFilePath)
            if(videoInfo != null)
            {
                runOnUiThread { bindCompressedVideoInfoToUI(videoInfo) }
            }

        }
    }

    fun getRealVideoPathFromURI(contentURI: Uri): String?
    {

        val projection = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.MediaColumns.DATE_ADDED, MediaStore.Video.Media.RELATIVE_PATH)
        val cursor: Cursor? = contentResolver.query(contentURI, projection, null, null,
                null)
        return if (cursor == null) contentURI.getPath() else
        {
            cursor.moveToFirst()
            val relativePathIndex: Int = cursor.getColumnIndex(MediaStore.Video.VideoColumns.RELATIVE_PATH)
            val displayNameIndex: Int = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME)
            val dateIndex: Int = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_ADDED)
            val relativePath = cursor.getString(relativePathIndex)
            val displayName = cursor.getString(displayNameIndex)
            val date = cursor.getString(dateIndex)
            val dir = getExternalFilesDir(null)
            val path = dir?.absolutePath
            Log.e(TAG, "getRealVideoPathFromURI: relativePath : $relativePath dirPath : $path displayName : $displayName date : $date")


            try {
                cursor.getString(relativePathIndex)
            } catch (exception: java.lang.Exception) {
                exception.printStackTrace()
                null
            }
        }
    }

    private fun getExternalStorageDir() : String
    {
        return getExternalFilesDir(null)!!.absolutePath
    }

    fun copyFile(contentDescriber: Uri, useFd : Boolean = false, sent : Boolean = true) : String?
    {
        FileHelper(this).copyFile(contentDescriber)
        val fileType = contentResolver.getType(contentDescriber)
        val backupDirectory = File(getExternalStorageDir(), "test_backup")

        if (!backupDirectory.exists()) backupDirectory.mkdirs()
        val fileName = "${(System.currentTimeMillis())}.mp4"
        val outputFile = File("${backupDirectory.absolutePath}${File.separator}$fileName")

        outputFile.deleteOnExit()
        Log.e(TAG, "output file path: ${outputFile.absolutePath}")

        var inputStream: InputStream? = null
        var out: OutputStream? = null
        val maxBufferSize = 8 * 1024 * 1024
        try
        {
            // open the user-picked file for reading:
            inputStream = if(useFd){
                val fd = contentResolver.openFileDescriptor(contentDescriber, "r")
                FileInputStream(fd?.fileDescriptor!!)
            } else contentResolver.openInputStream(contentDescriber)
            val bytesAvailable = inputStream!!.available()
            val bufferSize = min(bytesAvailable, maxBufferSize)
            // open the output-file:
//            out = FileOutputStream(File("some/path/to/a/writable/file"))
            out = FileOutputStream(outputFile)
            // copy the content:
            val buffer = ByteArray(bufferSize)
            var len = 0
            while (inputStream.read(buffer).also {
                    len = it
                } != -1) {
                out.write(buffer, 0, len)
            }
            // Contents are copied!
            return outputFile.absolutePath
        }
        catch (e : Exception)
        {
            FileLogger.getInstance()?.printStackTrace(e)
            e.printStackTrace();
        }
        finally
        {
            inputStream?.close()
            out?.close()
        }
        return null
    }


    private fun handleSignInResult(result: Intent?) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleAccount ->
                startBackup()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Google account permission is required", Toast.LENGTH_LONG).show()
            }
    }

    private fun initBackupService()
    {
        Log.e(TAG, "initBackupService: called")
        val backupServiceIntent = Intent(this, BackupRestoreService::class.java)
        startService(backupServiceIntent)
        bindService(backupServiceIntent, mConnection, Context.BIND_AUTO_CREATE)
    }

    private fun requestSignIn(email : String) : Account?{

        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        return if(googleSignInAccount == null){
            val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .setAccountName(email)
                    //.requestEmail()
                    //.requestIdToken(getString(R.string.google_sign_in_server_client_id))
                    //.requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
                    .build()
            val client = GoogleSignIn.getClient(this, signInOptions)
            val googleAPIClient = client.asGoogleApiClient()
            startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN)

            /*client.silentSignIn().addOnSuccessListener {
                Log.e(TAG, "requestSignIn: SilentSignInSuccess")
            }.addOnFailureListener{
                Log.e(TAG, "requestSignIn: SilentSignInFailed : $it")
                it.printStackTrace()
            }*/
            null
        } else{
            googleSignInAccount.account
        }
    }

    private fun signOut()
    {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build()
        val client = GoogleSignIn.getClient(this, signInOptions)
        client.signOut().addOnSuccessListener {
            Log.e(TAG, "signOut: signOutSuccess")
        }.addOnFailureListener{
            Log.e(TAG, "signOut: signOutFailed :")
            it.printStackTrace()
        }
    }

    override fun onDestroy() {
        try{
            unbindService(mConnection)
        }catch (ex : Exception){
            ex.printStackTrace()
        }
        super.onDestroy()
    }

    private fun getGoogleAccount()
    {
        /*isContactPermissionGranted()
        val accounts: Array<Account> = AccountManager.get(this).accounts
        Log.e(TAG, "getGoogleAccount: ${accounts.size}")
        for (account in accounts)
        {
            val possibleEmail = account.name
            val type = account.type
            Log.e(TAG, "getGoogleAccount: $possibleEmail")
            if (type == "com.google")
            {
                val button = Button(this)
                button.text = possibleEmail
                button.setOnClickListener {  }
                emailButtonContainer.addView(button)
            }
        }*/
    }

    fun isContactPermissionGranted() : Boolean
    {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.GET_ACCOUNTS), 1)
            false
        } else true
    }
    lateinit var mGoogleApiClient : GoogleApiClient

    private fun createGoogleClient(email: String) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .setAccountName(email)
                .requestIdToken(getString(R.string.google_sign_in_server_client_id))
                .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener { connectionResult ->
                    Log.e(TAG,"onConnectionFailed  = $connectionResult")
                    onSilentLoginFinished(null)
                }
                .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                    override fun onConnected(bundle: Bundle?) {
                        Log.e(TAG,"onConnected bundle = $bundle")
                        onSilentLoginFinished(null)
                    }

                    override fun onConnectionSuspended(i: Int) {
                        Log.e(TAG, "onConnectionSuspended i = $i")
                        onSilentLoginFinished(null)
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    private fun silentLogin() {
        val pendingResult = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient)
        if (pendingResult != null) {
            if (pendingResult.isDone) {
                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                // and the GoogleSignInResult will be available instantly.
                Log.e(TAG, " ----------------  CACHED SIGN-IN ------------")
                Log.e(TAG,"pendingResult is done = ")
                val signInResult = pendingResult.get()
                onSilentLoginFinished(signInResult)
            } else {
                Log.e(TAG, "Setting result callback")
                // If the user has not previously signed in on this device or the sign-in has expired,
                // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                // single sign-on will occur in this branch.
                pendingResult.setResultCallback {
                    Log.e(TAG,"googleSignInResult = $it")
                    onSilentLoginFinished(it)
                }
            }
        } else {
            onSilentLoginFinished(null)
        }
    }

    private fun onSilentLoginFinished(signInResult: GoogleSignInResult?) {
        println("GoogleLoginIdToken.onSilentLoginFinished")
        if (signInResult != null) {
            val signInAccount = signInResult.signInAccount
            if (signInAccount != null) {
                val emailAddress = signInAccount.email
                val token = signInAccount.idToken
                println("token = $token")
                println("emailAddress = $emailAddress")
            }
        }
    }

    fun getFileSizeString(fileSize: Long): String? {
        val finalSize = ""
        //2786686
        Log.e(TAG, "getFileSizeString: FileSize : $fileSize")
        return if (fileSize < 1024) {
            String.format("%.2f", fileSize.toDouble()) + " B"
        } else {
            val fileSizeInKB = fileSize.toDouble() / 1024
            Log.e(TAG, "getFileSizeString: inKB : $fileSizeInKB")
            if (fileSizeInKB < 1024) {
                String.format("%.2f", fileSizeInKB.toDouble()) + " KB"
            } else {
                val fileSizeInMB = fileSizeInKB.toDouble() / 1024
                Log.e(TAG, "getFileSizeString: inMB : $fileSizeInKB")
                String.format("%.2f", fileSizeInMB.toDouble()) + " MB"
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }


}