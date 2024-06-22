package com.mostafatamer.chatwithme.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mostafatamer.chatwithme.main_activity.MainActivity
import com.mostafatamer.chatwithme.R
import com.mostafatamer.chatwithme.enumeration.Screens
import com.mostafatamer.chatwithme.network.entity.MessageType
import com.mostafatamer.chatwithme.network.firebase.AcceptFriendRequest
import com.mostafatamer.chatwithme.network.firebase.Chat
import com.mostafatamer.chatwithme.network.firebase.FriendRequest
import com.mostafatamer.chatwithme.Singleton.CurrentScreen
import com.mostafatamer.chatwithme.utils.JsonConverter
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val payload = remoteMessage.data["cloud_message"]!!
        val jsonObject = JSONObject(payload)

        val messageType = jsonObject.getString("messageType")
        val jsonData = jsonObject.getString("data")

        when (messageType) {
            MessageType.FRIEND_CHAT_MESSAGE.value -> {
                chatMessage(jsonData)
            }

            MessageType.FRIEND_REQUEST.value -> {
                friendRequest(jsonData)
            }

            MessageType.FRIEND_REQUEST_ACCEPTED.value -> {
                acceptFriendRequest(jsonData)
            }
        }
    }

    private fun acceptFriendRequest(jsonData: String) {
        val friendRequest = JsonConverter
            .fromJson(jsonData, AcceptFriendRequest::class.java)

        val appName = baseContext.getString(R.string.app_name)

        sendNotification(
            appName,
            "${friendRequest.receiver.nickname} accepted friend request"
        )
    }

    private fun friendRequest(jsonData: String) {
        val friendRequest = JsonConverter
            .fromJson(jsonData, FriendRequest::class.java)

        sendNotification(
            "${friendRequest.sender.nickname} send friend request",
            friendRequest.message
        )
    }

    private fun chatMessage(jsonData: String) {
        val chat = JsonConverter
            .fromJson(jsonData, Chat::class.java)

        val currentScreen = CurrentScreen.screen as? Screens.ChatsScreen

        val isLegalToPushNotification =
            currentScreen?.chatTag != chat.chatDto.tag

        if (isLegalToPushNotification) {
            sendNotification(chat.title, chat.message)
        }
    }

    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }


    private fun sendRegistrationToServer(token: String?) {
    }

    private fun sendNotification(title: String, messageBody: String) {
        val requestCode = 0
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = getString(R.string.app_name)//TODO change
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Channel human readable title",
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.setShowBadge(true)
        notificationManager.createNotificationChannel(channel)

        val notificationId =
            System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}