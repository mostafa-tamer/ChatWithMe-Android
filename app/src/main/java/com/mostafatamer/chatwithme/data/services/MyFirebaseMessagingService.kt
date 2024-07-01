package com.mostafatamer.chatwithme.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.mostafatamer.chatwithme.CurrentScreen
import com.mostafatamer.chatwithme.MainActivity
import com.mostafatamer.chatwithme.R
import com.mostafatamer.chatwithme.domain.model.dto.MessageType
import com.mostafatamer.chatwithme.domain.model.dto.firebase.AcceptFriendRequest
import com.mostafatamer.chatwithme.domain.model.dto.firebase.AddToGroup
import com.mostafatamer.chatwithme.domain.model.dto.firebase.FirebaseChat
import com.mostafatamer.chatwithme.domain.model.dto.firebase.FriendRequest
import com.mostafatamer.chatwithme.sealed.Screens
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService() : FirebaseMessagingService() {

    @Inject
    lateinit var jsonConverter: Gson

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

            MessageType.ADD_TO_GROUP.value -> {
                addToGroup(jsonData)
            }
        }
    }

    private fun addToGroup(jsonData: String) {
        val addToGroup = jsonConverter
            .fromJson(jsonData, AddToGroup::class.java)

        val appName = baseContext.getString(R.string.app_name)

        sendNotification(
            appName,
            "You are added to ${addToGroup.groupChat.groupName}"
        )
    }

    private fun acceptFriendRequest(jsonData: String) {
        val friendRequest = jsonConverter
            .fromJson(jsonData, AcceptFriendRequest::class.java)

        val appName = baseContext.getString(R.string.app_name)

        sendNotification(
            appName,
            "${friendRequest.receiver.nickname} accepted friend request"
        )
    }

    private fun friendRequest(jsonData: String) {
        val friendRequest = jsonConverter
            .fromJson(jsonData, FriendRequest::class.java)

        sendNotification(
            "${friendRequest.sender.nickname} send friend request",
            friendRequest.message
        )
    }

    private fun chatMessage(jsonData: String) {
        val chat = jsonConverter
            .fromJson(jsonData, FirebaseChat::class.java)

        val currentScreen = CurrentScreen.screen as? Screens.ChatsScreen

        val isLegalToPushNotification =
            currentScreen?.chatTag != chat.chat.tag

        if (isLegalToPushNotification) {
            val (title, message) = if (chat.chat.groupName != null) {
                chat.chat.groupName!! to "${chat.title}: ${chat.message}"
            } else {
                chat.title to chat.message
            }

            sendNotification(title, message)
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
            .setSmallIcon(R.drawable.chat_round_line_svgrepo_com__1_)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setTimeoutAfter(500)
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