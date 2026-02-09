package com.example.todolistfirebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init { checkAuthStatus() }

    fun checkAuthStatus() {
        if (auth.currentUser == null) _authState.value = AuthState.Unauthenticated
        else _authState.value = AuthState.Authenticated
    }

    fun login(email: String, pass: String) {
        val cleanEmail = email.trim()
        val cleanPass = pass.trim()
        if (cleanEmail.isEmpty() || cleanPass.isEmpty()) {
            _authState.value = AuthState.Error("E-mail e senha são obrigatórios")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(cleanEmail, cleanPass).addOnCompleteListener { task ->
            if (task.isSuccessful) _authState.value = AuthState.Authenticated
            else _authState.value = AuthState.Error(task.exception?.message ?: "Erro ao entrar")
        }
    }

    fun signup(email: String, pass: String) {
        val cleanEmail = email.trim()
        val cleanPass = pass.trim()
        if (cleanPass.length < 6) {
            _authState.value = AuthState.Error("A senha precisa de 6 caracteres")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(cleanEmail, cleanPass).addOnCompleteListener { task ->
            if (task.isSuccessful) _authState.value = AuthState.Authenticated
            else _authState.value = AuthState.Error(task.exception?.message ?: "Erro ao criar conta")
        }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}