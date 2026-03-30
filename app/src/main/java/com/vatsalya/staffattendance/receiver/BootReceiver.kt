package com.vatsalya.staffattendance.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vatsalya.staffattendance.util.ReminderWorker

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule the daily reminder after device reboots
            ReminderWorker.scheduleDailyReminder(context, 8, 30)
        }
    }
}
