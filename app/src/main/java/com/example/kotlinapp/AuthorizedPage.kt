package com.example.kotlinapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kotlinapp.ui.theme.KotlinAppTheme
import com.example.kotlinapp.weatherPage.WeatherAppUserScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class AuthorizedPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContent {
                KotlinAppTheme {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                        val intent = intent
                        intent.getStringExtra("username")?.let {
                            SecondActivity(name = it, phoneNumber = intent.getStringExtra("phoneNumber")!!,
                                password = intent.getStringExtra("password")!!, role = intent.getStringExtra("role")!!
                            )
                        }
                    }
                }
            }
    }
}

@Composable
fun SecondActivity(name: String, phoneNumber: String, password: String, role: String) {
    val isSessionActive = remember { mutableStateOf(true) }

    val currentTime = Date()
    val applicationContext = LocalContext.current

    LaunchedEffect(Unit) {
        val sessionActive = withContext(Dispatchers.IO) {
            SessionManagerUtil.isSessionActive(currentTime, applicationContext)
        }
        isSessionActive.value = sessionActive
    }

    if(isSessionActive.value){
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if(role == "admin"){
                    Text(text = "Вы админ")
                    Button(onClick = {
                        val intent = Intent(applicationContext, UserEditActivity::class.java)

                        applicationContext.startActivity(intent)
                    }) {
                        Text(text = "Обновить пользователей")
                    }
                }
                else{
                    WeatherAppUserScreen()
                }
                Text(text = "Привет $name!")
                Text(text = "Ваш номер телефона: $phoneNumber")
                Button(onClick = {
                    val intent = Intent(applicationContext, UpdateActivity::class.java)
                    intent.putExtra("username", name)
                    intent.putExtra("phoneNumber", phoneNumber)
                    intent.putExtra("password", password)

                    applicationContext.startActivity(intent)
                }) {
                    Text(text = "Изменить данные")
                }
                Button(onClick = {
                    
                    SessionManagerUtil.endUserSession(applicationContext)

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    applicationContext.startActivity(intent)
                }) {
                    Text(text = "Выйти")
                }
            }
        }


    }
    else{
        Toast.makeText(applicationContext, "Время сессии истекло", Toast.LENGTH_SHORT).show()
        fun printToken(context: Context) {
            val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
            val token = sharedPreferences.getLong("session_expiration", 0)
            println("Token: $token")
        }
        printToken(applicationContext)
        SessionManagerUtil.endUserSession(applicationContext)
        val intent = Intent(applicationContext, MainActivity::class.java)

        printToken(applicationContext)

        applicationContext.startActivity(intent)
    }
}