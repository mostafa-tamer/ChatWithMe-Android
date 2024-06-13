package com.mostafatamer.chatwithme

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.mostafatamer.chatwithme.navigation.SetupNavGraph
import com.mostafatamer.chatwithme.ui.theme.ChatWithMeTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

class MainActivity : ComponentActivity() {

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()

        setContent {
            ChatWithMeTheme {

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {

//                    Indomi(stompClient = stompClient())
                    SetupNavGraph(rememberNavController())

                }

//                val stompClient = stompClient()
//                indomi(stompClient)
            }
        }
    }
}

fun helloWorld() {
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    coroutineScope.launch {

        coroutineScope {
            launch {
                for (i in 0..10) {
                    println("hello $i")
                    if (i == 5) {
                        throw CancellationException("error")
                    }
                }
            }

            for (i in 0..10) {
                println("world $i")
            }
        }
    }
}


private fun stompClient(): StompClient {
    val stompClient: StompClient =
        Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://192.168.1.14:9090/gs-guide-websocket")
    StompUtils.lifecycle(stompClient)
    stompClient.connect()
    return stompClient
}

@Composable
private fun Indomi(stompClient: StompClient) {
    var textMostafa by remember {
        mutableStateOf("text")
    }
    var textMahmoud by remember {
        mutableStateOf("text")
    }

    LaunchedEffect(key1 = Unit) {


        stompClient.topic("/send_friend_request/id_key_2").subscribe { stompMessage ->
            //            val jsonObject: JSONObject = JSONObject(stompMessage.getPayload())
            println(stompMessage.payload)
            textMostafa = stompMessage.payload.toString()
            //            println(jsonObject)
        }

    }
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                val jsonObject = JSONObject()
                jsonObject.put("message", "Mostafa Is Playing 2")

                stompClient
                    .send("/app/sendMessage", jsonObject.toString())
                    .subscribe()
            },
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Text(text = "Mostafa: $textMostafa\n\n")
            Text(text = "Mahmod: $textMahmoud")
        }
    }
}