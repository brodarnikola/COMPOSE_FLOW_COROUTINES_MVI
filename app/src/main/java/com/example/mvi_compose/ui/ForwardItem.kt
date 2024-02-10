package com.example.mvi_compose.ui


sealed class ForwardItem {
    data class Chat(
        val chatId: Long,
        val chatType: String,
        val chatName: String,
        val transferMode: String,
        val participantsPhonesOrEmails: List<String>,
        val participantsAvatars: List<String>,
        val participantsNamesForUi: String // names or phoneOrEmail-s of users
    ): ForwardItem()

    data class User(
        val phoneOrEmail: String,
        val name: String,
        val avatar: String,
    ): ForwardItem()
}
