package com.mostafatamer.chatwithme.domain.usecase

import com.mostafatamer.chatwithme.domain.model.dto.dto.Chat
import com.mostafatamer.chatwithme.data.repository.restful.ChatRepository

class GroupManagementUseCase(
    private val chatRepository: ChatRepository,
) {
    lateinit var chat: Chat

    fun addMember(userName: String, onMemberAdded: (Boolean) -> Unit) {
        chatRepository.addMember(chat.tag, userName)
            .setOnSuccess {
                onMemberAdded.invoke(it.apiError == null)
            }.execute()
    }

    fun leaveGroup(onGroupLeft: (Boolean) -> Unit) {
        chatRepository.leaveGroup(chat.tag)
            .setOnSuccess {
                onGroupLeft.invoke(it.apiError == null)
            }.execute()
    }

    fun createGroup(groupName: String, onCreateGroup: (Boolean) -> Unit) {
        chatRepository.createGroup(groupName)
            .setOnSuccess {
                onCreateGroup.invoke(it.apiError == null)
            }.execute()
    }
}
