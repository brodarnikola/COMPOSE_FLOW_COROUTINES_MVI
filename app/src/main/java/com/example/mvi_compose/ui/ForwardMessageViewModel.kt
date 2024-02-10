/*
package com.example.mvi_compose.ui

import androidx.lifecycle.viewModelScope
import com.sunbird.core.data.model.TransferMode
import com.sunbird.peristance.room.entity.*
import com.sunbird.peristance.sharedPref.SharedPrefsStorage
import com.sunbird.repository.*
import com.sunbird.ui.base.BaseViewModel
import com.sunbird.ui.base.UiEvent
import com.sunbird.ui.forward_message.model.ForwardItem
import com.sunbird.ui.model.ForwardContent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ForwardMessageViewModel
@Inject
constructor(
    private val chatRepository: ChatRepository,
    private val userRepo: UserRepoImpl,
    private val forwardRepo: ForwardRepoImpl,
    private val sharedPrefsStorage: SharedPrefsStorage,
): BaseViewModel<ForwardMessageScreenUiState, ForwardMessageScreenEvent>() { //: ViewModel() {

    override fun initialState(): ForwardMessageScreenUiState = ForwardMessageScreenUiState()

    override fun onEvent(event: ForwardMessageScreenEvent) {
        when (event) {
            is ForwardMessageScreenEvent.OnSearchFilterChange -> onSearchFilterChange(
                searchQuery = event.textQuery
            )
            is ForwardMessageScreenEvent.OnSelectedChat -> sendForwardContentToChat(
                forwardContent = event.forwardContent,
                chatId = event.chatId
            )
            is ForwardMessageScreenEvent.OnSelectedTransferModeOfAUser -> sendForwardContentToUser(
                transferMode = event.transferMode,
                phoneOrEmail = event.userPhoneOrEmail,
                forwardContent = event.forwardContent
            )
        }
    }

    val activatedTransferModes = mutableListOf<TransferMode>()

    // holder of forward items to avoid fetching from DB more than once
    private val forwardItemsHolder = mutableListOf<ForwardItem>()

    private var searchFilterJob: Job? = null

    init {
        activatedTransferModes.addAll(sharedPrefsStorage.getConnectedTransferModes())
        getForwardItems()
    }

    private fun getForwardItems() {
        viewModelScope.launch {
            */
/** Sort order: existing chats, distinct users **//*

            // get all activated transfer mode existing chats (with users)
            // TODO: or get chat ids from UserChatCrossRef table? - its safer for existing chats
            chatRepository.getAllChatsByTransferModes(activatedTransferModes).forEach { chat ->
                val chatWithUsers = chatRepository.getUsersOfAChat(chatId = chat.chatId)
                if (chatWithUsers != null) {
                    val participantsNames = chatWithUsers.users.map { user ->
                        user.name.ifEmpty { user.phoneOrEmail }
                    }
                    var participantsNamesFormatted = participantsNames.toString().replace("[", "").replace("]", "")
                    val lastCommaIndex = participantsNamesFormatted.lastIndexOf(",")
                    if (lastCommaIndex > -1) participantsNamesFormatted = participantsNamesFormatted.substring(0, lastCommaIndex)

                    forwardItemsHolder.add(ForwardItem.Chat(
                        chatId = chatWithUsers.chat.chatId,
                        chatType = chatWithUsers.chat.chatType,
                        chatName = chatWithUsers.chat.chatName,
                        transferMode = chatWithUsers.chat.transferMode,
                        participantsPhonesOrEmails = chatWithUsers.users.map { it.phoneOrEmail },
                        participantsAvatars = chatWithUsers.users.map { it.avatar },
                        participantsNamesForUi = participantsNamesFormatted,
                    ))
                }
            }
            // get all distinct users
            val distinctUsers = userRepo.getAllUsersDistinct().sortedWith(
                // TODO: improve sorting algorithm
                compareBy(
                    { user ->
                        when {
                            user.phoneOrEmail.matches(Regex("^\\d+$")) -> 1 // Number
                            user.phoneOrEmail.startsWith("+") -> 2 // Number with '+' prefix
                            else -> 0 // Name
                        }
                    },
                    { user -> user.name.lowercase(Locale.ROOT) },
                    { user -> user.phoneOrEmail.toLongOrNull() ?: 0 }
                )
            )
            distinctUsers.forEach { user ->
                forwardItemsHolder.add(ForwardItem.User(
                    phoneOrEmail = user.phoneOrEmail,
                    name = user.name.ifEmpty { user.phoneOrEmail },
                    avatar = user.avatar
                ))
            }

            _state.update { it.copy(loading = false, forwardItems = forwardItemsHolder) }
        }
    }

    private fun onSearchFilterChange(searchQuery: String) {
        searchFilterJob?.cancel()
        searchFilterJob = viewModelScope.launch {
            delay(500L)
            // if it doesn't get canceled in half a second by keystroke, get queried contacts
            val searchQueryResult = if (searchQuery.isNotEmpty()) {
                forwardItemsHolder.filter { forwardItem: ForwardItem ->
                    when (forwardItem) {
                        is ForwardItem.Chat -> {
                            forwardItem.chatName.contains(searchQuery, ignoreCase = true)
                                    || forwardItem.participantsNamesForUi.contains(searchQuery, ignoreCase = true)
                                    || forwardItem.participantsPhonesOrEmails.any { it.contains(searchQuery) }
                        }
                        is ForwardItem.User -> {
                            forwardItem.name.contains(searchQuery)
                                    || forwardItem.phoneOrEmail.contains(searchQuery)
                        }
                    }
                }
            } else {
                forwardItemsHolder
            }
            _state.update { it.copy(forwardItems = searchQueryResult) }
        }
    }

    private fun sendForwardContentToChat(forwardContent: ForwardContent, chatId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }

            val chat = chatRepository.getChatById(chatId)
            if (chat != null) {
                handleForwardContent(forwardContent, chat)
                sendUiEvent(ForwardMessageScreenUiEvent.OnNavigateToChat(chatId = chat.chatId))
            }

            _state.update { it.copy(loading = false) }
        }
    }

    private fun sendForwardContentToUser(forwardContent: ForwardContent, transferMode: TransferMode, phoneOrEmail: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }

            val userOfSelectedTransferMode = userRepo.getUserByPhoneOrEmailAndTransferMode(phoneOrEmail = phoneOrEmail, transferMode = transferMode)
            if (userOfSelectedTransferMode != null) {
                // handle chat
                val chat = chatRepository.handleChatByUsersAndTransferMode(
                    users = listOf(userOfSelectedTransferMode),
                    transferMode = transferMode,
                )
                handleForwardContent(forwardContent, chat)
                sendUiEvent(ForwardMessageScreenUiEvent.OnNavigateToChat(chatId = chat.chatId))
            }

            _state.update { it.copy(loading = false) }
        }
    }

    private suspend fun handleForwardContent(forwardContent: ForwardContent, chat: Chat) {
        when {
            forwardContent.messageIdsToForward.isNotEmpty() -> {
                forwardRepo.handleMessagesToForward(
                    messageIds = forwardContent.messageIdsToForward,
                    chatId = chat.chatId,
                    transferMode = chat.transferMode
                )
            }
            forwardContent.mediaDataIdsToForward.isNotEmpty() -> {
                forwardRepo.handleMediaDataToForward(
                    mediaDataIds = forwardContent.mediaDataIdsToForward,
                    chatId = chat.chatId,
                    transferMode = chat.transferMode
                )
            }
            forwardContent.sharedMediaFile != null -> {
                sendUiEvent(ForwardMessageScreenUiEvent.OnNavigateToChat(chatId = chat.chatId))
            }
        }
    }
}

data class ForwardMessageScreenUiState(
    val forwardItems: List<ForwardItem> = listOf(),
    val loading: Boolean = true,
)

sealed interface ForwardMessageScreenEvent {
    data class OnSearchFilterChange(val textQuery: String): ForwardMessageScreenEvent
    data class OnSelectedChat(val chatId: Long, val forwardContent: ForwardContent): ForwardMessageScreenEvent
    data class OnSelectedTransferModeOfAUser(val transferMode: TransferMode, val userPhoneOrEmail: String, val forwardContent: ForwardContent): ForwardMessageScreenEvent
}

sealed class ForwardMessageScreenUiEvent : UiEvent {
    class OnNavigateToChat(val chatId: Long) : ForwardMessageScreenUiEvent()
}


*/
