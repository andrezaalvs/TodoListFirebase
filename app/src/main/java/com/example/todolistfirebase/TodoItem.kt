package com.example.todolistfirebase


import com.google.firebase.firestore.PropertyName

data class TodoItem(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    @get:PropertyName("isDone") @set:PropertyName("isDone") var isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val colorTag: Int = 0 // Adicionado para as cores!
)