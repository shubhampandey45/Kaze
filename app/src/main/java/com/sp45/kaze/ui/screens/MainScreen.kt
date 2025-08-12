package com.sp45.kaze.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sp45.kaze.R
import com.sp45.kaze.ui.components.ChatSection
import com.sp45.kaze.ui.components.SurfaceViewRendererComposable
import com.sp45.kaze.ui.viewmodel.MainViewModel
import com.sp45.kaze.utils.ChatItem
import com.sp45.kaze.utils.MatchState
import compose.icons.FeatherIcons
import compose.icons.feathericons.Play
import compose.icons.feathericons.StopCircle

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = hiltViewModel()
    val matchState = viewModel.matchState.collectAsState()
    val chatState = viewModel.chatList.collectAsState()
    val chatText = remember { mutableStateOf("") }
    val context = LocalContext.current

    // Permission request launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (!permissions.all { it.value }) {
            Toast.makeText(
                context, "Camera and Microphone permissions are required", Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.permissionsGranted()
        }
    }

    // Launch permission request effect
    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEA)) // Background color

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2.5f)
        ) {
            SurfaceViewRendererComposable(
                modifier = Modifier.fillMaxSize(), onSurfaceReady = { renderer ->
                    viewModel.initRemoteSurfaceView(renderer)
                }, message = when (matchState.value) {
                    MatchState.LookingForMatchState -> "Looking For Match ..."
                    MatchState.IDLE -> "Not Looking For Match, Press Start"
                    else -> null
                }
            )
        }


        Row(
            Modifier
                .fillMaxWidth()
                .weight(2f)
                .padding(8.dp)
        ) {
            ChatSection(chatItems = chatState.value) // Displaying the chat list
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (matchState.value != MatchState.NewState) {
                Box(
                    modifier = Modifier
                        .weight(1f) // This will make the Box take up 1f of the available space
                        .padding(3.dp) // Padding around the Box (this ensures there’s space for the switch camera button)
                ) {
                    // Surface that fills the entire Box
                    SurfaceViewRendererComposable(
                        modifier = Modifier.fillMaxSize(), // This makes the SurfaceViewRenderer fill the available space within the Box
                        onSurfaceReady = { renderer ->
                            viewModel.startLocalStream(renderer) // Start the local stream when SurfaceViewRenderer is ready
                        })

                    // Switch Camera Button at the bottom left
                    IconButton(
                        onClick = {
                            viewModel.switchCamera()
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd) // Align it to the bottom-right of the Box
                            .padding(3.dp)
                            .size(30.dp)

                    ) {
                        Icon(
                            painterResource(R.drawable.ic_switch_camera),
                            contentDescription = "Switch Camera",
                            tint = Color(0xE4E5E5E5),
                            modifier = Modifier
                                .size(30.dp)
                                .background(
                                    color = Color(0x465B5B5B), shape = RoundedCornerShape(5.dp)
                                )
                                .padding(5.dp)

                        )
                    }

                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // OutlinedTextField for chat input
                    OutlinedTextField(
                        value = chatText.value,
                        onValueChange = { chatText.value = it },
                        label = { Text("Type your message") },
                        modifier = Modifier.weight(7f),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color(0xFFA4BAD1),
                            unfocusedTextColor = Color(0xFFA4BAD1),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    // Send button to add the chat item
                    IconButton(onClick = {
                        // Add the new chat item to the chat list in the ViewModel
                        if (chatText.value.isNotEmpty()) {
                            val newChatItem = ChatItem(text = chatText.value, isMine = true)
                            viewModel.sendChatItem(newChatItem)
                            chatText.value = "" // Clear the input field after sending
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color(0xFFA4BAD1),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = {
                            viewModel.stopLookingForMatch()
                        }, Modifier.weight(5f), colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(
                                0xFFC294A4
                            )
                        )
                    ) {
                        Icon(
                            imageVector = FeatherIcons.StopCircle,
                            contentDescription = "Stop",
                            tint = Color.White,
                            modifier = Modifier.size(25.dp)

                        )
                    }

                    Spacer(modifier = Modifier.weight(0.25f))

                    IconButton(
                        onClick = {
                            viewModel.findNextMatch()
                        }, Modifier.weight(5f), colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFFA4BAD1)
                        )
                    ) {
                        Icon(
                            imageVector = FeatherIcons.Play,
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }
        }
    }
}









@Composable
fun MainScreen1() {

    val viewModel: MainViewModel = hiltViewModel()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (!permissions.all { it.value }) {
//            Toast.makeText(
//                context, "Permission denied", Toast.LENGTH_SHORT
//            ).show()
            showDialog = true
        } else {
            // Permission granted
            viewModel.permissionsGranted()

        }
    }
    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO
            )
        )
    }

    if (showDialog) {
        PermissionDeniedDialog(
            onSettingsClick = {
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                )
                showDialog = false
            },
            onDismiss = {
                showDialog = false
            }
        )
    }
}

@Composable
fun PermissionDeniedDialog(
    onSettingsClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission Required") },
        text = { Text("App needs camera and mic access. Please grant them in settings to use app.") },
        confirmButton = {
            TextButton(onClick = onSettingsClick) { Text("Go to Settings") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel ") }
        }
    )
}