/*
package com.example.mvi_compose.ui

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.material3.MaterialTheme as Material3

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ForwardMessageScreen(
    viewModel: ForwardMessageViewModel,
    forwardContent: ForwardContent,
    goToChatScreen: (chatId: Long, sharedMediaFile: SharedMediaFile?) -> Unit,
    upPress: () -> Unit = {}
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val clickedForwardItem = remember {
        mutableStateOf<ForwardItem?>(null)
    }

    val context = LocalContext.current
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, event.toastLength).show()
                }
                is ForwardMessageScreenUiEvent.OnNavigateToChat -> {
                    goToChatScreen(event.chatId, forwardContent.sharedMediaFile)
                }
            }
        }
    }

    if (state.loading) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            CustomCircularProgressIndicator(
//                modifier = Modifier
//                    .align(Alignment.Center)
//                    .padding(16.dp)
//            )
//        }
        SunbirdLoadingDialog(
            onDismiss = {}
        )
    }

    ModalBottomSheetLayout(
        sheetContent = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(colorResource(id = R.color.backgroundGrey))
            ) {
                when (val forwardItem = clickedForwardItem.value) {
                    is ForwardItem.Chat -> {
                        if (forwardItem.participantsPhonesOrEmails.size > 1) {
                            ForwardItemGroupChatSheetContent(phonesAndEmails = forwardItem.participantsPhonesOrEmails)
                        }
                    }
                    is ForwardItem.User -> {
                        ForwardItemUserSheetContent(
                            activatedTransferModes = viewModel.activatedTransferModes,
                            onSelectedTransferMode = { transferMode ->
                                viewModel.onEvent(ForwardMessageScreenEvent.OnSelectedTransferModeOfAUser(
                                    transferMode = transferMode,
                                    userPhoneOrEmail = forwardItem.phoneOrEmail,
                                    forwardContent = forwardContent

                                ))
                            },
                        )
                    }
                    null -> {}
                }
            }
        },
        sheetState = modalBottomSheetState,
        sheetBackgroundColor = Color.Transparent,
        sheetContentColor = Color.Transparent,
        sheetElevation = 0.dp,
        modifier = Modifier
            .fillMaxSize()
            .background(Material3.colorScheme.surface)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { upPress() }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = null,
                    )
                }
                Text(
                    text = stringResource(id = R.string.forward_messages),
                    style = Material3.typography.titleSmall,
                    color = Material3.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }

            val localFocusManager = LocalFocusManager.current
            ForwardMessageSearchTextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                initialValue = "",
                localFocusManager = localFocusManager,
                onTextFieldValueChange = { userText ->
                    viewModel.onEvent(ForwardMessageScreenEvent.OnSearchFilterChange(userText))
                },
                isMessageFilterEnabled = rememberSaveable { mutableStateOf(false) }
            )

            SunbirdDivider()

            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(state.forwardItems) { forwardItem: ForwardItem ->
                    when (forwardItem) {
                        is ForwardItem.Chat -> {
                            ForwardItemChatLayout(
                                forwardItem = forwardItem,
                                imageLoader = imageLoader,
                                onForwardItemClick = { chatId ->
                                    viewModel.onEvent(ForwardMessageScreenEvent.OnSelectedChat(
                                        chatId = chatId,
                                        forwardContent = forwardContent,
                                    ))
                                },
                                onGroupChatParticipantsInfoClick = {
                                    clickedForwardItem.value = forwardItem
                                    coroutineScope.launch {
                                        modalBottomSheetState.show()
                                    }
                                }
                            )
                        }
                        is ForwardItem.User -> {
                            ForwardItemUserLayout(
                                forwardItem = forwardItem,
                                imageLoader = imageLoader,
                                onForwardItemClick = {
                                    clickedForwardItem.value = forwardItem
                                    coroutineScope.launch {
                                        modalBottomSheetState.show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ForwardItemChatLayout(
    forwardItem: ForwardItem.Chat,
    imageLoader: ImageLoader,
    onForwardItemClick: (chatId: Long) -> Unit,
    onGroupChatParticipantsInfoClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .background(Material3.colorScheme.surface)
            .padding(vertical = 8.dp)
            .clickable {
                onForwardItemClick(forwardItem.chatId)
            }
    ) {
        Box(
            modifier = Modifier
                .weight(0.2f)
                .align(Alignment.CenterVertically)
        ) {
            when {
                forwardItem.participantsAvatars.size == 1 -> {
                    val participantAvatar = forwardItem.participantsAvatars[0]
                    if (participantAvatar.isNotEmpty()) {
                        AsyncImage(
                            model = participantAvatar,
                            imageLoader = imageLoader,
                            placeholder = painterResource(R.drawable.avatar_user_default_m3),
                            error = painterResource(R.drawable.avatar_user_default_m3),
                            fallback = painterResource(R.drawable.avatar_user_default_m3),
                            contentDescription = stringResource(R.string.btn_continue),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .size(48.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Material3.colorScheme.surfaceVariant,
                                    shape = CircleShape
                                )
                                .size(45.dp)
                                .clip(CircleShape)
                                .align(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.avatar_user_default_m3),
                                contentDescription = "",
                                tint = Material3.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
                forwardItem.participantsAvatars.size > 1 -> {
                    // TODO
                }
                forwardItem.participantsPhonesOrEmails.isNotEmpty() -> {
                    val sunbirdBackgroundColor = Tertiary60
                    val androidBackgroundColor = Neutral80
                    val whatsAppBackgroundColor = Green
                    val messengerBackgroundColor = Purple
                    val googleMessagesBackgroundColor = RcsBlue
                    val appleBackgroundColor = Blue

                    val transferModeColor = when (forwardItem.transferMode) {
                        TransferMode.IMESSAGE -> appleBackgroundColor
                        TransferMode.WHATSAPP -> whatsAppBackgroundColor
                        TransferMode.FACEBOOK_MESSENGER -> messengerBackgroundColor
                        TransferMode.GOOGLE_MESSAGES -> googleMessagesBackgroundColor
                        TransferMode.SMS_MMS -> androidBackgroundColor
                        else -> sunbirdBackgroundColor
                    }

                    var initialsName = ""
                    forwardItem.participantsPhonesOrEmails.forEach {
                        initialsName += it
                    }
                    Box(
                        modifier = Modifier
                            .size(47.dp)
                            .border(
                                border = BorderStroke(
                                    width = if (forwardItem.transferMode == TransferMode.SMS_MMS) 2.dp else 0.dp,
                                    color = if (forwardItem.transferMode == TransferMode.SMS_MMS) CinnamonTheme.colors.smsMms
                                    else Material3.colorScheme.surface
                                ), shape = CircleShape
                            )
                            .clip(CircleShape)
                            .align(Alignment.Center)
                            .background(transferModeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initialsName.uppercase(),
                            style = Material3.typography.labelLarge,
                            color = if (forwardItem.transferMode == TransferMode.SMS_MMS) CinnamonTheme.colors.smsMms
                            else Color.White
//                        color = Material3.colorScheme.outline
                        )
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Material3.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                            .size(45.dp)
                            .clip(CircleShape)
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.avatar_user_default_m3),
                            contentDescription = "",
                            tint = Material3.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            val transferModeImage = when (forwardItem.transferMode) {
                TransferMode.SMS_MMS -> painterResource(R.drawable.ic_m3_avatar_logo_android)
                TransferMode.IMESSAGE -> painterResource(R.drawable.ic_m3_avatar_logo_apple)
                TransferMode.WHATSAPP -> painterResource(R.drawable.ic_m3_avatar_logo_whatsapp)
                TransferMode.GOOGLE_MESSAGES -> painterResource(R.drawable.google_messages)
                TransferMode.FACEBOOK_MESSENGER -> painterResource(R.drawable.ic_messenger_with_background)
                else -> painterResource(R.drawable.ic_sunbird_round)
            }
            Image(
                painter = transferModeImage,
                contentScale = ContentScale.Crop,
                contentDescription = "",
                modifier =
                Modifier
                    .offset(x = (-9).dp, y = 5.dp)
                    .background(color = Material3.colorScheme.surface, shape = CircleShape)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .size(28.dp)
            )
        }

        Row(
            modifier = Modifier
                .padding(end = 24.dp)
                .height(55.dp)
                .weight(0.8f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(2.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(2.dp) ,
                    style = Material3.typography.labelLarge,
                    fontSize = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    text = forwardItem.chatName.ifEmpty { forwardItem.participantsNamesForUi },
                    color = Material3.colorScheme.onSurface
                )
                if (forwardItem.participantsPhonesOrEmails.size == 1) { // forwardItem.chatType == ChatType.INDIVIDUAL
                    Text(
                        text = forwardItem.participantsPhonesOrEmails[0],
                        style = Material3.typography.bodyMedium,
                        color = Material3.colorScheme.onSurface,
                        modifier = Modifier
                            .semantics {
                                contentDescription = "contactName"
                            }
                    )
                }
            }

            if (forwardItem.participantsPhonesOrEmails.size > 1) {
                Spacer(modifier = Modifier.weight(1f))
                Card(
                    modifier = Modifier.height(35.dp),
                    elevation = 0.dp,
                    onClick = {
                        onGroupChatParticipantsInfoClick()
                    },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(width = 1.dp, color = Material3.colorScheme.outline,)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Material3.colorScheme.surface)
                            .padding(horizontal = 6.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.avatar_user_default_m3),
                            contentDescription = stringResource(id = R.string.group_chat_users_amount),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        RegularText(
                            text = "${forwardItem.participantsPhonesOrEmails.size}", // .chat.userIds.size}",
                            fontSize = 15.sp,
                            lineHeight = 13.sp,
                            textAlign = TextAlign.Center,
                            color = Material3.colorScheme.onSurface,
                            fontWeight = FontWeight(400),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ForwardItemUserLayout(
    forwardItem: ForwardItem.User,
    imageLoader: ImageLoader,
    onForwardItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(Material3.colorScheme.surface)
            .padding(vertical = 8.dp)
            .clickable {
                onForwardItemClick()
            }
    ) {
        Box(
            modifier = Modifier
                .weight(0.2f)
                .align(Alignment.CenterVertically)
        ) {
            AsyncImage(
                model = forwardItem.avatar,
                imageLoader = imageLoader,
                placeholder = painterResource(R.drawable.avatar_user_default_m3),
                error = painterResource(R.drawable.avatar_user_default_m3),
                fallback = painterResource(R.drawable.avatar_user_default_m3),
                contentDescription = stringResource(R.string.btn_continue),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .size(48.dp)
            )
        }

        Row(
            modifier = Modifier
                .padding(end = 24.dp)
                .height(55.dp)
                .weight(0.8f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(2.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(2.dp) ,
                    style = Material3.typography.labelLarge,
                    fontSize = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    text = forwardItem.name.ifEmpty { forwardItem.phoneOrEmail },
                    color = Material3.colorScheme.onSurface
                )
                Text(
                    text = forwardItem.phoneOrEmail,
                    style = Material3.typography.bodyMedium,
                    color = Material3.colorScheme.onSurface,
                    modifier = Modifier
                        .semantics {
                            contentDescription = "contactName"
                        }
                )
            }
        }
    }
}

@Composable
fun ForwardItemGroupChatSheetContent(
    modifier: Modifier = Modifier,
    phonesAndEmails: List<String>
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp),
            style = Material3.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            text = stringResource(id = R.string.users_inside_group_chat),
            color = colorResource(id = R.color.colorBlack), // color = CinnamonTheme.colors.textPrimary,
            fontSize = 15.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))

        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(phonesAndEmails) {
                RegularText(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 10.dp),
                    style = MaterialTheme.typography.body1,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    text = it,
                    fontSize = 13.sp,
                    color = colorResource(id = R.color.colorBlack), // color = CinnamonTheme.colors.textPrimary,
                )
            }
        }
    }
}

@Composable
fun ForwardItemUserSheetContent(
    activatedTransferModes: List<TransferMode>,
    onSelectedTransferMode: (TransferMode) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(colorResource(id = R.color.backgroundGrey))
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 20.dp)
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.imessage),
                contentScale = ContentScale.Crop,
                contentDescription = "",
                alpha = if (activatedTransferModes.contains(TransferMode.IMESSAGE)) 1.0f else 0.3F,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (activatedTransferModes.contains(TransferMode.IMESSAGE)) {
                            onSelectedTransferMode(TransferMode.IMESSAGE)
                        }
                    }
            )
            Image(
                painter = painterResource(R.drawable.google_messages),
                contentScale = ContentScale.Crop,
                contentDescription = "",
                alpha = if (activatedTransferModes.contains(TransferMode.GOOGLE_MESSAGES)) 1.0f else 0.3F,
                modifier =
                Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (activatedTransferModes.contains(TransferMode.GOOGLE_MESSAGES)) {
                            onSelectedTransferMode(TransferMode.GOOGLE_MESSAGES)
                        }
                    },
            )
            Image(
                painter = painterResource(R.drawable.ic_m3_avatar_logo_android),
                contentScale = ContentScale.Crop,
                contentDescription = "",
                alpha = if (activatedTransferModes.contains(TransferMode.SMS_MMS)) 1.0f else 0.3F,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (activatedTransferModes.contains(TransferMode.SMS_MMS)) {
                            onSelectedTransferMode(TransferMode.SMS_MMS)
                        }
                    }
            )
            Image(
                painter = painterResource(R.drawable.ic_m3_avatar_logo_whatsapp),
                contentScale = ContentScale.Crop,
                contentDescription = "",
                alpha = if (activatedTransferModes.contains(TransferMode.WHATSAPP)) 1.0f else 0.3F,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (activatedTransferModes.contains(TransferMode.WHATSAPP)) {
                            onSelectedTransferMode(TransferMode.WHATSAPP)
                        }
                    }
            )
        }
    }
}
*/
