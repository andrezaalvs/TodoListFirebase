package com.example.todolistfirebase

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Cores das Categorias (UFU, TI, RH, Foco)
val categoryColors = listOf(
    Color(0xFF81D4FA), // Azul
    Color(0xFFA5D6A7), // Verde
    Color(0xFFFFCC80), // Laranja
    Color(0xFFEF9A9A)  // Vermelho
)

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val todoViewModel: TodoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }

            MaterialTheme(colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()) {
                val navController = rememberNavController()
                val authState by authViewModel.authState.observeAsState()

                LaunchedEffect(authState) {
                    when (authState) {
                        is AuthState.Authenticated -> {
                            navController.navigate("todo") { popUpTo("login") { inclusive = true } }
                        }
                        is AuthState.Unauthenticated -> navController.navigate("login")
                        else -> {}
                    }
                }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(authViewModel, onNavigateToSignup = { navController.navigate("signup") })
                        }
                        composable("signup") {
                            SignupScreen(authViewModel, onNavigateToLogin = { navController.navigate("login") })
                        }
                        composable("todo") {
                            TodoListScreen(todoViewModel, authViewModel, isDarkMode, onThemeToggle = { isDarkMode = !isDarkMode })
                        }
                    }
                }
            }
        }
    }
}

// --- TELA DE LOGIN COM MOSTRAR/ESCONDER SENHA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: AuthViewModel, onNavigateToSignup: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authState by viewModel.authState.observeAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("ToDo Firebase", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6750A4))
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = "Mostrar Senha")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (authState is AuthState.Loading) CircularProgressIndicator()
        else Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Entrar") }

        TextButton(onClick = onNavigateToSignup) { Text("Não tem conta? Cadastre-se") }
    }
}

// --- TELA DE CADASTRO ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(viewModel: AuthViewModel, onNavigateToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.observeAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Criar Conta", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(24.dp))

        if (authState is AuthState.Loading) CircularProgressIndicator()
        else Button(onClick = { viewModel.signup(email, password) }, modifier = Modifier.fillMaxWidth()) { Text("Cadastrar") }

        TextButton(onClick = onNavigateToLogin) { Text("Já tem conta? Faça Login") }
    }
}

// --- TELA DE TAREFAS ESTILO "MEU DIA" ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(todoViewModel: TodoViewModel, authViewModel: AuthViewModel, isDarkMode: Boolean, onThemeToggle: () -> Unit) {
    var taskTitle by remember { mutableStateOf("") }
    var selectedColorIndex by remember { mutableIntStateOf(0) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val categoryNames = listOf(
        "🏠 Rotina Pessoal",   // Índice 0 (Azul)
        "💼 Trabalho",         // Índice 1 (Verde)
        "🧘 Lazer",            // Índice 2 (Laranja)
        "🚨 Urgente"           // Índice 3 (Vermelho)
    )

    LaunchedEffect(Unit) { todoViewModel.loadTasks() }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Meu Dia", fontSize = 32.sp, fontWeight = FontWeight.Black)
                Text("Organize sua rotina hoje", color = Color.Gray, fontSize = 14.sp)
            }
            IconButton(onClick = onThemeToggle) {
                Icon(imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.NightsStay,
                    contentDescription = "Tema", tint = if (isDarkMode) Color.Yellow else Color(0xFF6750A4))
            }
            IconButton(onClick = { authViewModel.signout() }) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Sair", tint = Color.Red)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Seletor de Categorias
        Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            categoryColors.forEachIndexed { index, color ->
                Box(modifier = Modifier.size(34.dp).background(color, CircleShape)
                    .border(width = if (selectedColorIndex == index) 3.dp else 0.dp,
                        color = if (isDarkMode) Color.White else Color.Black, shape = CircleShape)
                    .clickable { selectedColorIndex = index })
            }
            Text(categoryNames[selectedColorIndex], fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterVertically))
        }

        OutlinedTextField(
            value = taskTitle,
            onValueChange = { taskTitle = it },
            placeholder = { Text("Adicionar tarefa...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = categoryColors[selectedColorIndex]),
            trailingIcon = {
                IconButton(onClick = {
                    if (taskTitle.isNotBlank()) {
                        todoViewModel.addTask(taskTitle, selectedColorIndex)
                        taskTitle = ""
                    }
                }) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = categoryColors[selectedColorIndex], modifier = Modifier.size(32.dp))
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.Transparent) {
            listOf("Fazer", "Feito").forEachIndexed { index, title ->
                Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index },
                    text = { Text(title, fontWeight = FontWeight.Bold) })
            }
        }

        val tasks = if (selectedTabIndex == 0) todoViewModel.pendingTasks else todoViewModel.completedTasks

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(tasks) { item -> TodoCard(item, todoViewModel, isDarkMode) }
        }
    }
}

@Composable
fun TodoCard(item: TodoItem, viewModel: TodoViewModel, isDarkMode: Boolean) {
    val baseColor = categoryColors[item.colorTag % categoryColors.size]
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDarkMode) baseColor.copy(alpha = 0.2f) else baseColor.copy(alpha = 0.35f))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = item.isDone, onCheckedChange = { viewModel.toggleTask(item) })
            Text(text = item.title, modifier = Modifier.weight(1f).padding(start = 12.dp),
                style = TextStyle(textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None,
                    fontSize = 18.sp, fontWeight = FontWeight.Medium, color = if (isDarkMode) Color.White else Color.Black))
            IconButton(onClick = { viewModel.deleteTask(item) }) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar")
            }
        }
    }
}