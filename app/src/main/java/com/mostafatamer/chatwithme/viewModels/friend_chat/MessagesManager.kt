package com.mostafatamer.chatwithme.viewModels.friend_chat

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mostafatamer.chatwithme.Singleton.UserSingleton
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.network.entity.dto.MessageDto

class MessagesManager(private val friendChatViewModel: FriendChatViewModel) {

    private val loadedMessages = mutableSetOf<MessageDto>()

    fun addMessage(messageDto: MessageDto, onNewMessage: () -> Unit) {
        if (!loadedMessages.contains(messageDto)) {
            loadedMessages.add(messageDto)
            friendChatViewModel.messages.addSorted(messageDto)
            saveLastMessageNumberOfTheChat(messageDto)
            onNewMessage.invoke()
        }
    }

    fun addMessages(messagesApi: List<MessageDto>, onNewMessage: () -> Unit) {
        messagesApi.forEach { friendChatViewModel.messages.addSorted(it) }

        val lastMessage = messagesApi.maxByOrNull { it.messageNumber!! }

        lastMessage?.let {
            val lastMessageNumberKey = getLastMessageNumberKey()

            friendChatViewModel.sharedPreferencesHelper.setValue(
                lastMessageNumberKey, it.messageNumber!!
            )
        }

        onNewMessage.invoke()
    }

    private fun saveLastMessageNumberOfTheChat(messageDto: MessageDto) {
        friendChatViewModel.sharedPreferencesHelper.setValue(
            SharedPreferences.FriendChat.lastMessageNumberWithChatTagAndUsername(
                friendChatViewModel.chatDto.tag,
                UserSingleton.getInstance().username
            ),
            messageDto.messageNumber!!
        )
    }

    //TODO Optimize
    private fun SnapshotStateList<MessageDto>.addSorted(item: MessageDto) {
        if (this.isEmpty()) {
            add(item)
            return
        }

        val iterator = this.listIterator()

        while (iterator.hasNext()) {
            if (item.timeStamp < iterator.next().messageNumber!!)
                break
        }
        add(iterator.nextIndex(), item)
    }

    private fun getLastMessageNumberKey(): String {
        val lastMessageNumberKey =
            SharedPreferences.FriendChat.lastMessageNumberWithChatTagAndUsername(
                friendChatViewModel.chatDto.tag,
                UserSingleton.getInstance().username
            )
        return lastMessageNumberKey
    }
}