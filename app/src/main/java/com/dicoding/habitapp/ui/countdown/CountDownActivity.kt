package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIF_UNIQUE_WORK
import java.util.concurrent.TimeUnit

class CountDownActivity : AppCompatActivity() {
    private val workManager = WorkManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        val habit = intent.getParcelableExtra<Habit>(HABIT) as Habit
        val paddedMinute = DateUtils.formatElapsedTime(habit.minutesFocus * 60)

        findViewById<TextView>(R.id.tv_count_down_title).text = habit.title
        val tvCountDown: TextView = findViewById<TextView?>(R.id.tv_count_down).apply {
            text = paddedMinute
        }

        val viewModel = ViewModelProvider(this)[CountDownViewModel::class.java]

        //TODO 10 : Set initial time and observe current time. Update button state when countdown is finished
        viewModel.apply {
            setInitialTime(habit.minutesFocus)
            currentTimeString.observe(this@CountDownActivity) {
                tvCountDown.text = it
            }
            eventCountDownFinish.observe(this@CountDownActivity) { isFinish ->
                Log.d("TAG-finish", "onCreate: $isFinish")
                updateButtonState(!isFinish)
            }
        }

        //TODO 13 : Start and cancel One Time Request WorkManager to notify when time is up.
        findViewById<Button>(R.id.btn_start).setOnClickListener {

            val data = workDataOf(HABIT_ID to habit.id, HABIT_TITLE to habit.title)
            val notificationRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(habit.minutesFocus, TimeUnit.MINUTES)
                .setInputData(data)
                .addTag(habit.id.toString())
                .build()
            workManager.enqueueUniqueWork(
                NOTIF_UNIQUE_WORK,
                ExistingWorkPolicy.REPLACE,
                notificationRequest
            )
            viewModel.startTimer()
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            viewModel.resetTimer()
            workManager.cancelAllWorkByTag(habit.id.toString())
        }
    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }
}