package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        loadingButton.setOnClickListener {
            download()
        }

        // Create notification Download completed channel
        createChannel(
            getString(R.string.notification_download_complete_channel_id),
            getString(R.string.notification_download_complete_channel_name)
        )
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Configure notification channel
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.description = channelName

            // Create notification channel
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val bundle = Bundle()

            bundle.putBoolean("isSuccess", id != null)
            bundle.putString("fileName", getFileName())

            val notificationManager = getSystemService(context, NotificationManager::class.java) as NotificationManager
            notificationManager.sendNotification(getString(R.string.notification_description), applicationContext, bundle)

            loadingButton.onReset()
            toggleRadioButtonsLock(true)
        }
    }

    private fun download() {
        val url = getDownloadUrl()

        if (url == "") {
            Toast.makeText(this, R.string.toast_no_file_selected_message, Toast.LENGTH_SHORT).show()
        } else {
            loadingButton.onClicked()
            toggleRadioButtonsLock(false)

            val downloadUrl = Uri.parse(url)
            val fileName = downloadUrl.lastPathSegment

            val request =
                DownloadManager.Request(downloadUrl)
                    .setTitle(getString(R.string.download_file))
                    .setDescription(getString(R.string.app_description))
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.

            loadingButton.onStartDownload()
        }
    }

    private fun getDownloadUrl(): String {
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

        return when (radioGroup.checkedRadioButtonId) {
            R.id.bumpTechRadioButton -> "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
            R.id.udacityRadioButton -> "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
            R.id.squareRadioButton -> "https://github.com/square/retrofit/archive/refs/heads/master.zip"
            else -> ""
        }
    }

    private fun getFileName(): String {
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

        return when (radioGroup.checkedRadioButtonId) {
            R.id.bumpTechRadioButton -> getString(R.string.button_glide)
            R.id.udacityRadioButton -> getString(R.string.button_udacity)
            R.id.squareRadioButton -> getString(R.string.button_square)
            else -> "No content"
        }
    }

    private fun toggleRadioButtonsLock(enable: Boolean) {
        for (button in radioGroup.children) {
            button.isEnabled = enable
        }
    }
}
