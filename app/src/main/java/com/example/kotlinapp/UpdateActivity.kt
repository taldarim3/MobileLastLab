package com.example.kotlinapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.kotlinapp.data.AppDatabase
import com.example.kotlinapp.ui.theme.KotlinAppTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UpdateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val intent = intent

                    intent.getStringExtra("username")?.let {
                        UpdateScreen(usernameExtra = it,
                            phoneNumber = intent.getStringExtra("phoneNumber")!!,
                            passwordExtra = intent.getStringExtra("password")!!
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateScreen(usernameExtra: String, phoneNumber: String, passwordExtra: String) {
    var username by remember { mutableStateOf(usernameExtra) }
    var password by remember { mutableStateOf(passwordExtra) }
    var confirmPassword by remember { mutableStateOf("") }

    val applicationContext = LocalContext.current

    val db = AppDatabase.getInstance(applicationContext)

    val rememberCoroutineScope = rememberCoroutineScope();

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = "Your phone number: $phoneNumber")
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )
            var job: Job? by remember {
                mutableStateOf(null)
            }
            Button(
                onClick = {
                    job = rememberCoroutineScope.launch {
                        if (username == "" || password == "" || confirmPassword == ""){
                            Toast.makeText(applicationContext, "Заполните все поля?", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        if (password != confirmPassword){
                            Toast.makeText(applicationContext, "Пароли не совпадают!", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        val user = db.userDao().findByNumber(phoneNumber)
                        db.userDao().setPassword(
                            user.uid,
                            password
                        )

                        db.userDao().setUsername(
                            user.uid,
                            username
                        )


                        Toast.makeText(applicationContext, "Готово! Можете войти с новыми данными.", Toast.LENGTH_SHORT).show()

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        applicationContext.startActivity(intent)

                    }
                },
                modifier = Modifier
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Update"
                )
            }
        }
    }
}

