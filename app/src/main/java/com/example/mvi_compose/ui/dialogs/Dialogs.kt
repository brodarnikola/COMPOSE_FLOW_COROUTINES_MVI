package com.example.mvi_compose.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mvi_compose.R

@Composable
fun ConfirmOrCancelDialog(
    titleText: String? = null,
    descriptionText: String? = null,
    cancelText: String = "Cancel",
    confirmText: String = "Ok",
    onlyConfirmButton: Boolean = false,
    onConfirmOrCancel: (Boolean) -> Unit,
) {
    Dialog(
        onDismissRequest = {
            onConfirmOrCancel(onlyConfirmButton)
        },
        properties = DialogProperties(
            dismissOnClickOutside = false
        )
    ) {
        Surface(
             color = colorResource(id = R.color.purple_200),
//            onClick = { onConfirmOrCancel(onlyConfirmButton) }
        ) {
            Box(
                modifier =
                Modifier
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 24.dp)
                    .fillMaxSize()
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.Center)
                ) {
                    Column(
                        modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (titleText != null) {
                            Text(
                                text = titleText,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(top = 24.dp)
                                    .padding(horizontal = 24.dp)
                                    .fillMaxWidth()
                                    .semantics {
                                        contentDescription = "dialogTitle = $titleText"
                                    },
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (descriptionText != null) {
                            Text(
                                text = descriptionText,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .padding(horizontal = 24.dp)
                                    .fillMaxWidth()
                                    .semantics {
                                        contentDescription = "dialogDescription = $descriptionText"
                                    },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = MaterialTheme.colorScheme.background)
                                .padding(vertical = 24.dp, horizontal = 24.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!onlyConfirmButton) {
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            onConfirmOrCancel(false)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cancelText,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            Box(
                                modifier = Modifier
                                    .clickable {
                                        onConfirmOrCancel(true)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = confirmText,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier =
                                    Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(50.dp)
                                        )
                                        .padding(horizontal = 24.dp, vertical = 10.dp)
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}