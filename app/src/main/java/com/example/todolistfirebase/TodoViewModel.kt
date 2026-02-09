package com.example.todolistfirebase

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TodoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Esta é a lista "mestre" que vem do Firebase
    val todoItems = mutableStateListOf<TodoItem>()

    /**
     * Lógica de Abas (Passo 1):
     * Criamos listas filtradas que a interface vai usar para mostrar cada aba.
     */

    // Pega apenas as tarefas onde isDone é falso
    val pendingTasks get() = todoItems.filter { !it.isDone }

    // Pega apenas as tarefas onde isDone é verdadeiro
    val completedTasks get() = todoItems.filter { it.isDone }

    /**
     * Carrega as tarefas em tempo real com ordenação
     */
    fun loadTasks() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("tasks")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("DEBUG_TODO", "Erro ao carregar: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    todoItems.clear()
                    val items = snapshot.documents.mapNotNull { doc ->
                        val item = doc.toObject(TodoItem::class.java)
                        // Vincula o ID do documento ao objeto TodoItem
                        item?.copy(id = doc.id)
                    }
                    todoItems.addAll(items)
                    Log.d("DEBUG_TODO", "Total de tarefas carregadas: ${items.size}")
                }
            }
    }

    /**
     * Adiciona uma nova tarefa
     */
    fun addTask(title: String, colorIndex: Int) {
        val userId = auth.currentUser?.uid ?: return
        val newTask = hashMapOf(
            "userId" to userId,
            "title" to title,
            "isDone" to false,
            "createdAt" to System.currentTimeMillis(),
            "colorTag" to colorIndex // Salva a cor escolhida no Firebase
        )
        db.collection("tasks").add(newTask)
    }

    /**
     * Flegar tarefa: Altera o status no Firebase
     */
    fun toggleTask(item: TodoItem) {
        if (item.id.isEmpty()) return

        val novoStatus = !item.isDone

        db.collection("tasks").document(item.id)
            .update("isDone", novoStatus)
            .addOnSuccessListener {
                Log.d("DEBUG_TODO", "Status alterado para: $novoStatus")
            }
    }

    /**
     * Exclui a tarefa permanentemente
     */
    fun deleteTask(item: TodoItem) {
        if (item.id.isEmpty()) return
        db.collection("tasks").document(item.id).delete()
            .addOnSuccessListener { Log.d("DEBUG_TODO", "Tarefa apagada.") }
    }
}