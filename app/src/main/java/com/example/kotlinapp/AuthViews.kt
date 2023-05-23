package com.example.kotlinapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.room.Room
import com.example.kotlinapp.data.AppDatabase
import com.example.kotlinapp.data.models.UserModel

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun RegistrationView() {
    var phoneNumber by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val applicationContext = LocalContext.current

    val db = AppDatabase.getInstance(applicationContext)
    val rememberCoroutineScope = rememberCoroutineScope()


    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Номер телефона") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                )
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Имя пользователя") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Подтвердите пароль") },
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
                        if (phoneNumber == "" || username == "" || password == "" || confirmPassword == ""){
                            Toast.makeText(applicationContext, "Заполните все поля!", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        if (password != confirmPassword){
                            Toast.makeText(applicationContext, "Пароли не совпадают!", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        val user = db.userDao().findByNumber(phoneNumber)
                        if (user != null){
                            Toast.makeText(applicationContext, "Имя пользователя занято!", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        val temp = if(username == "admin"){
                            "admin";
                        } else
                            "user"

                        db.userDao().insertAll(
                            UserModel(
                                uid = 0,
                                phoneNumber = phoneNumber,
                                username = username,
                                password = password,
                                role = temp
                            )
                        )



                        Toast.makeText(applicationContext, "Теперь можете авторизоваться", Toast.LENGTH_SHORT).show()
                    }

                },
                modifier = Modifier
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Регистрация"
                )
            }
        }
    }
}

@SuppressLint("CommitPrefEdits")
@Composable
fun AuthorizationView() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val applicationContext = LocalContext.current

    val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "database-name"
    ).build()
    val rememberCoroutineScope = rememberCoroutineScope();

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Имя пользователя") },

            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )
            Button(
                onClick = {
                          rememberCoroutineScope.launch {
                              val user = db.userDao().findByUsername(username)

                              if (user == null){
                                  Toast.makeText(applicationContext, "Пользователь не существует!", Toast.LENGTH_SHORT).show()
                                  return@launch
                              }

                              if (user.password != password){
                                  Toast.makeText(applicationContext, "Неверный пароль!", Toast.LENGTH_SHORT).show()
                                  return@launch
                              }

                              val sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE);

                              Toast.makeText(applicationContext, "Добро пожаловать, $username", Toast.LENGTH_SHORT).show()

                              val savedPhoneNumber = db.userDao().findByUsername(username).phoneNumber
                              val savedPassword = db.userDao().findByUsername(username).password
                              val savedRole = db.userDao().findByUsername(username).role

                              val intent = Intent(applicationContext, AuthorizedPage::class.java)
                              intent.putExtra("username", username)
                              intent.putExtra("phoneNumber", savedPhoneNumber)
                              intent.putExtra("password", savedPassword)
                              intent.putExtra("role", savedRole)

                              val editor = sharedPreferences.edit()
                              editor.putString("username", username)
                              editor.putString("phoneNumber", savedPhoneNumber)
                              editor.putString("password", savedPassword)
                              editor.putString("role", savedRole)
                              editor.apply()

                              SessionManagerUtil.startUserSession(applicationContext, 5)

                              applicationContext.startActivity(intent)
                          }
                },
                modifier = Modifier
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Войти"
                )
            }
        }
    }
}

