package com.mostafatamer.chatwithme.viewModels.friendship_chat

import com.mostafatamer.chatwithme.AppDependencies
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.network.entity.dto.MessageDto
import com.mostafatamer.chatwithme.viewModels.friendship_chat.FriendshipChatViewModel.ChatCard

class LoadChatManager(
    private val friendChatViewModel: FriendshipChatViewModel,
    private val appDependencies: AppDependencies,
) {

    private val loadedChats: MutableSet<ChatDto> = mutableSetOf()

    fun addChat(chatDto: ChatDto) {
        if (!loadedChats.contains(chatDto)) {
            val missingMessages = getMissingMessages(chatDto)
            val chatCard = ChatCard(chatDto, missingMessages)

            friendChatViewModel.chats.add(chatCard)
            loadedChats.add(chatDto)
        }
    }

    private fun getMissingMessages(chatDto: ChatDto): Long? {
        val lastMessageNumber = chatDto.lastMessage?.messageNumber
        val lastReadMessageNumber = getLastReadMessageNumber(chatDto.tag)
        println(lastMessageNumber)
        println(lastReadMessageNumber)
        println("+++++++++++++++++++++++")


        if (lastMessageNumber == null) {
            resetLastMessageNumber(chatDto.tag)
            return null
        }

        return if (lastReadMessageNumber == null) lastMessageNumber
        else lastMessageNumber - lastReadMessageNumber
    }

    private fun resetLastMessageNumber(chatTag: String) {
        friendChatViewModel.chatSharedPreferences.setValue(
            SharedPreferences.FriendChat.lastMessageNumberWithChatTagAndUsername(
                chatTag, appDependencies.user.username
            ),
            null
        )
    }

    private fun getLastReadMessageNumber(chatTag: String): Long? {
        return friendChatViewModel.chatSharedPreferences.getLong(
            SharedPreferences.FriendChat.lastMessageNumberWithChatTagAndUsername(
                chatTag, appDependencies.user.username
            )
        )
    }

    fun addChats(chatResponse: List<ChatDto>) {
        val newChats = chatResponse.map { chatDto ->
            val missingMessages = getMissingMessages(chatDto)
            ChatCard(chatDto, missingMessages)
        }
        friendChatViewModel.chats.clear()
        friendChatViewModel.chats.addAll(newChats)
    }

    fun onMessageReceived(messageDto: MessageDto) {
        val chatCard = friendChatViewModel.chats.find { it.chat.tag == messageDto.chatTag }!!
        val index = friendChatViewModel.chats.indexOf(chatCard)
        val newChat = chatCard.chat.copy(lastMessage = messageDto)
        friendChatViewModel.chats[index] = chatCard.copy(
            chat = newChat,
            missingMessages = getMissingMessages(newChat)
        )
    }
}