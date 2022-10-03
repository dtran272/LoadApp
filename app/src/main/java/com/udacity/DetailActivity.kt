package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.cancelNotifications()

        val bundle = intent.extras

        bundle?.let {
            val fileNameText = it.getString("fileName", "")
            val isSuccess = it.getBoolean("isSuccess")

            setContent(fileNameText, isSuccess)
        }

        okButton.setOnClickListener {
            finish()
        }
    }

    private fun setContent(fileNameText: String, isSuccess: Boolean) {
        fileName.text = fileNameText

        if (isSuccess) {
            status.setTextColor(getColor(R.color.green))
            status.text = getString(R.string.success_text)
        } else {
            status.setTextColor(getColor(R.color.red))
            status.text = getString(R.string.error_text)
        }
    }
}
