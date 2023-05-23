package com.example.kotlinapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.material.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.kotlinapp.data.AppDatabase
import com.example.kotlinapp.data.models.UserModel
import com.example.kotlinapp.ui.theme.KotlinAppTheme
import kotlinx.coroutines.launch

class UserEditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    UserScreen()
                }
            }
        }
    }
}

@Composable
fun UserScreen() {
    var selectedUserIds by remember { mutableStateOf(emptySet<Int>()) }
    val rememberCoroutineScope = rememberCoroutineScope()
    val applicationContext = LocalContext.current
    val db = AppDatabase.getInstance(applicationContext)
    var users by remember {
        mutableStateOf(emptyList<UserModel>())
    }
    var currentUser by remember {
        mutableStateOf(UserModel(0, "", "", "", ""))
    }
    val sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)


    LaunchedEffect(key1 = true) {
        users = db.userDao().getAllUsers()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            itemsIndexed(users) { _, user ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = user.role == "admin",
                        onCheckedChange = { isChecked ->
                            users = users.map { if (it.uid == user.uid) it.copy(role = if (isChecked) "admin" else "user") else it }
                            if (isChecked) {
                                selectedUserIds = selectedUserIds + user.uid
                            } else {
                                selectedUserIds = selectedUserIds - user.uid
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(text = user.username ?: "")
                }
            }
        }

        Button(
            onClick = {
                val adminUserIds = selectedUserIds.toList()
                val userRole = "user"
                val adminRole = "admin"

                rememberCoroutineScope.launch {
                    // Обновление ролей пользователей
                    users.map { it.uid }.forEach { userId ->
                        if (adminUserIds.contains(userId)) {
                            db.userDao().updateUserRole(userId, adminRole)
                        } else {
                            db.userDao().updateUserRole(userId, userRole)
                        }
                    }

                    currentUser =
                        sharedPreferences.getString("username", "")
                            ?.let { db.userDao().findByUsername(it) }!!
                    db.userDao().updateUserRole(currentUser.uid, "admin")
                }

                selectedUserIds = emptySet()

                val intent = Intent(applicationContext, MainActivity::class.java)


                /*intent.putExtra("username", currentUser.username)
                intent.putExtra("phoneNumber", currentUser.phoneNumber)
                intent.putExtra("password", currentUser.password)
                intent.putExtra("role", currentUser.role)*/

                applicationContext.startActivity(intent)
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = "Сохранить")
        }
    }
}