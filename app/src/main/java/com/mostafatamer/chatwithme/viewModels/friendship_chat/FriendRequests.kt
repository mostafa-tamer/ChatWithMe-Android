package com.mostafatamer.chatwithme.viewModels.friendship_chat

import com.mostafatamer.chatwithme.network.firebase.FriendRequest

class FriendRequests(private val friendChatViewModel: FriendshipChatHubViewModel) {
    private val newFriendRequests = mutableSetOf<FriendRequest>()

    fun newFriendRequest(friendRequest: FriendRequest) {
        if (!newFriendRequests.contains(friendRequest)) {
            friendChatViewModel.numberOfFriendRequests++
        }
    }
}