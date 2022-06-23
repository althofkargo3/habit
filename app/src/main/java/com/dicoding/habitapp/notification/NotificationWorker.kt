package com.dicoding.habitapp.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.data.HabitRepository
import com.dicoding.habitapp.ui.countdown.CountDownActivity
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID

@RequiresApi(Build.VERSION_CODES.S)
class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val habitId = inputData.getInt(HABIT_ID, 0)
    private val habitTitle = inputData.getString(HABIT_TITLE) ?: ""
    val context = ctx


    private fun getPendingIntent(habit: Habit): PendingIntent? {
        val intent = Intent(applicationContext, CountDownActivity::class.java).apply {
            putExtra(HABIT, habit)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_MUTABLE)
        }
    }

    override fun doWork(): Result {

        val prefManager =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val shouldNotify =
            prefManager.getBoolean(applicationContext.getString(R.string.pref_key_notify), false)

        //TODO 12 : If notification preference on, show notification with pending intent

        if (shouldNotify) {
            val repository = HabitRepository.getInstance(context)
            val habit = repository.getById(habitId) as Habit?

            return if (habit != null) {
                doOneTimeNotification(habit)
                Result.success()
            } else {
                Result.failure()
            }
        }
        return Result.failure()
    }

    private fun doOneTimeNotification(habit: Habit) {

        val intent = getPendingIntent(habit)

        showNotification(
            id = habit.id,
            title = habitTitle,
            text = context.getString(R.string.notify_content),
            contentIntent = intent
        )
    }

    private fun showNotification(
        id: Int,
        title: String,
        text: String,
        contentIntent: PendingIntent?
    ) {
        val builder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID).apply {
                priority = NotificationCompat.PRIORITY_DEFAULT
                setSmallIcon(R.drawable.ic_notifications)
                setContentTitle(title)
                setContentText(text)
                setContentIntent(contentIntent)
                setAutoCancel(true)
            }

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(id, builder.build())
        }
    }


}
