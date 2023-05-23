package com.example.kotlinapp.data.models

import androidx.room.*

@Dao
interface UserDao {


    @Query("SELECT * FROM users WHERE phoneNumber = :first LIMIT 1")
    suspend fun findByNumber(first: String): UserModel

    @Query("SELECT * FROM users WHERE username = :first LIMIT 1")
    suspend fun findByUsername(first: String): UserModel

    @Insert
    suspend fun insertAll(vararg users: UserModel)

    @Delete
    suspend fun delete(user: UserModel)

    @Query("UPDATE users SET username = :newUsername WHERE uid = :userId")
    suspend fun setUsername(userId: Int, newUsername: String)

    @Query("UPDATE users SET password = :newPassword WHERE uid = :userId")
    suspend fun setPassword(userId: Int, newPassword: String)

    @Query("SELECT * FROM Users")
    suspend fun getAllUsers(): List<UserModel>

    @Query("UPDATE users SET role = :role WHERE uid = :userId")
    suspend fun updateUserRole(userId: Int, role: String)
}

