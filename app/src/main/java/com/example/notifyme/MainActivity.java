package com.example.notifyme;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {

    private Button notifyButton;
    private Button updateButton;
    private Button cancelButton;

    // Constant to hold the notification channel id
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    // Constant to hold the notification id
    private static final int NOTIFICATION_ID = 0;
    // Constant member variable to hold the update notification action
    // For my broadcast
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.example.notifyme.ACTION_UPDATE_NOTIFICATION";

    // Member variable to store NotificationManager obj
    private NotificationManager notifyManager;
    // Member variable for the receiver
    private NotificatonReceiver receiver = new NotificatonReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // notify_button setup
        notifyButton = findViewById(R.id.notify_button);
        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send the notification
                sendNotification();
            }
        });

        // update_button setup
        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the notification
                updateNotification();
            }
        });

        // cancel_button setup
        cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel the notification
                cancelNotification();
            }
        });

        // Register the broadcast receiver
        registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));

        // Must call the createNotificationChannel()
        // If this step is missed the app will crash!
        createNotificationChannel();

        // Set the state of the buttons
        setNotificationButtonState(true, false, false);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    // Methods for my buttons
    // Send notification
    public void sendNotification() {
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                updateIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification",
                updatePendingIntent);
        notifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        // Set the state of the buttons after method is executed
        setNotificationButtonState(false, true, true);
    }

    // Update notification
    public void updateNotification() {
        Bitmap androidImage = BitmapFactory
                .decodeResource(getResources(), R.drawable.mascot_1);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated!"));
        notifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        // Set the state of the buttons after method is executed
        setNotificationButtonState(false, false, true);
    }

    // Cancel notification
    public void cancelNotification() {
        notifyManager.cancel(NOTIFICATION_ID);
        // Set the state of the buttons after method is executed
        setNotificationButtonState(true, false, false);
    }

    // Method to create the notification channel
    public void createNotificationChannel() {
        notifyManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create notification channel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            notifyManager.createNotificationChannel(notificationChannel);
        }
    }

    // Helper methods
    private NotificationCompat.Builder getNotificationBuilder() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(this,
                PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text.")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
    }

    // Utility methods
    void setNotificationButtonState(Boolean isNotifyEnabled,
                                    Boolean isUpdateEnabled,
                                    Boolean isCancelEnabled){
        notifyButton.setEnabled(isNotifyEnabled);
        updateButton.setEnabled(isUpdateEnabled);
        cancelButton.setEnabled(isCancelEnabled);
    }

    // Add a notification action button
    public class NotificatonReceiver extends BroadcastReceiver {

        public NotificatonReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            // Update the notification
            updateNotification();

        }
    }

}
