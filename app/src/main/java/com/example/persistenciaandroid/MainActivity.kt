package com.example.persistenciaandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.persistenciaandroid.data.AppDatabase
import com.example.persistenciaandroid.data.TareaRepository
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.persistenciaandroid.data.Tarea
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

class MainActivity : ComponentActivity() {

    private lateinit var tareaRepository: TareaRepository

    private var nombreGuardado = ""
    private var correoGuardado = ""
    private var descripcionGuardada = ""

    private var tiempoRestante = 25 * 60
    private var temporizadorActivo = false

    private var campoConFoco = "nombre"

    private var borradorGuardado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        tareaRepository = TareaRepository(database.tareaDao())

        // Recuperación explícita del estado guardado
        if (savedInstanceState != null) {
            nombreGuardado =
                savedInstanceState.getString("nombre") ?: ""

            correoGuardado =
                savedInstanceState.getString("correo") ?: ""

            descripcionGuardada =
                savedInstanceState.getString("descripcion") ?: ""

            tiempoRestante =
                savedInstanceState.getInt("tiempoRestante", 25 * 60)

            temporizadorActivo =
                savedInstanceState.getBoolean("temporizadorActivo", false)

            campoConFoco =
                savedInstanceState.getString("campoConFoco") ?: "nombre"
        }

        setContent {
            val tareas by tareaRepository.todasLasTareas.collectAsState(initial = emptyList())

            PersistenciaScreen(
                tareas = tareas,
                onAgregarTarea = { tarea ->
                    lifecycleScope.launch {
                        tareaRepository.insertar(tarea)
                    }
                },

                onActualizarTarea = { tarea ->
                    lifecycleScope.launch {
                        tareaRepository.actualizar(tarea)
                    }
                },

                onEliminarTarea = { tarea ->
                    lifecycleScope.launch {
                        tareaRepository.eliminar(tarea)
                    }
                },

                nombreInicial = nombreGuardado,
                correoInicial = correoGuardado,
                descripcionInicial = descripcionGuardada,
                tiempoInicial = tiempoRestante,
                temporizadorInicial = temporizadorActivo,
                campoConFocoInicial = campoConFoco,

                onNombreChange = {
                    nombreGuardado = it
                },

                onCorreoChange = {
                    correoGuardado = it
                },

                onDescripcionChange = {
                    descripcionGuardada = it
                },

                onTiempoChange = {
                    tiempoRestante = it
                },

                onTemporizadorChange = {
                    temporizadorActivo = it
                },

                onCampoFocoChange = {
                    campoConFoco = it
                }
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Campos del formulario
        outState.putString("nombre", nombreGuardado)
        outState.putString("correo", correoGuardado)
        outState.putString("descripcion", descripcionGuardada)

        // Estado del temporizador
        outState.putInt("tiempoRestante", tiempoRestante)
        outState.putBoolean("temporizadorActivo", temporizadorActivo)

        // Campo que tenía el foco
        outState.putString("campoConFoco", campoConFoco)
    }

    override fun onPause() {
        super.onPause()

        borradorGuardado = true
    }

    override fun onStop() {
        super.onStop()

        borradorGuardado = true
    }
}

@Composable
fun PersistenciaScreen(
    tareas: List<Tarea>,
    onAgregarTarea: suspend (Tarea) -> Unit,
    onActualizarTarea: suspend (Tarea) -> Unit,
    onEliminarTarea: suspend (Tarea) -> Unit,
    nombreInicial: String,
    correoInicial: String,
    descripcionInicial: String,
    tiempoInicial: Int,
    temporizadorInicial: Boolean,
    campoConFocoInicial: String,

    onNombreChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onTiempoChange: (Int) -> Unit,
    onTemporizadorChange: (Boolean) -> Unit,
    onCampoFocoChange: (String) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    var tituloTarea by remember {
        mutableStateOf("")
    }

    var descripcionTarea by remember {
        mutableStateOf("")
    }

    var nombre = remember {
        mutableStateOf(nombreInicial)
    }

    var correo = remember {
        mutableStateOf(correoInicial)
    }

    var descripcion = remember {
        mutableStateOf(descripcionInicial)
    }

    var tiempo = remember {
        mutableStateOf(tiempoInicial)
    }

    var temporizadorActivo = remember {
        mutableStateOf(temporizadorInicial)
    }

    val nombreFocusRequester = remember {
        FocusRequester()
    }

    val correoFocusRequester = remember {
        FocusRequester()
    }

    val descripcionFocusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(campoConFocoInicial) {
        when (campoConFocoInicial) {
            "nombre" -> nombreFocusRequester.requestFocus()
            "correo" -> correoFocusRequester.requestFocus()
            "descripcion" -> descripcionFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(temporizadorActivo.value) {
        while (temporizadorActivo.value && tiempo.value > 0) {
            delay(1000)

            tiempo.value -= 1
            onTiempoChange(tiempo.value)

            if (tiempo.value == 0) {
                temporizadorActivo.value = false
                onTemporizadorChange(false)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Mis tareas",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nueva tarea",
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = tituloTarea,
            onValueChange = {
                tituloTarea = it
            },
            label = {
                Text("Título")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = descripcionTarea,
            onValueChange = {
                descripcionTarea = it
            },
            label = {
                Text("Descripción")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (tituloTarea.isNotBlank()) {
                    coroutineScope.launch {
                        onAgregarTarea(
                            Tarea(
                                titulo = tituloTarea,
                                descripcion = descripcionTarea
                            )
                        )

                        tituloTarea = ""
                        descripcionTarea = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar tarea")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (tareas.isEmpty()) {
            Text("No hay tareas guardadas.")
        } else {

            tareas.forEach { tarea ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = tarea.titulo,
                            fontSize = 20.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = tarea.descripcion,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Creada: ${
                                SimpleDateFormat(
                                    "dd/MM/yyyy HH:mm",
                                    Locale.getDefault()
                                ).format(Date(tarea.fechaCreacion))
                            }",
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (tarea.estadoCompletado) {
                                "Estado: Completada"
                            } else {
                                "Estado: Pendiente"
                            },
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (tarea.sincronizada) {
                                "Sincronización: Sincronizada"
                            } else {
                                "Sincronización: Pendiente"
                            },
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        onActualizarTarea(
                                            tarea.copy(
                                                estadoCompletado =
                                                    !tarea.estadoCompletado
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    if (tarea.estadoCompletado) {
                                        "Pendiente"
                                    } else {
                                        "Completar"
                                    }
                                )
                            }

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        onActualizarTarea(
                                            tarea.copy(
                                                sincronizada = true
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !tarea.sincronizada
                            ) {
                                Text(
                                    if (tarea.sincronizada) {
                                        "Sincronizada"
                                    } else {
                                        "Sincronizar"
                                    }
                                )
                            }

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        onEliminarTarea(tarea)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Persistencia Android",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre.value,
            onValueChange = {
                nombre.value = it
                onNombreChange(it)
            },
            label = {
                Text("Nombre")
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(nombreFocusRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        onCampoFocoChange("nombre")
                    }
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo.value,
            onValueChange = {
                correo.value = it
                onCorreoChange(it)
            },
            label = {
                Text("Correo")
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(correoFocusRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        onCampoFocoChange("correo")
                    }
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = descripcion.value,
            onValueChange = {
                descripcion.value = it
                onDescripcionChange(it)
            },
            label = {
                Text("Descripción")
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(descripcionFocusRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        onCampoFocoChange("descripcion")
                    }
                }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Temporizador",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        val minutos = tiempo.value / 60
        val segundos = tiempo.value % 60

        Text(
            text = String.format("%02d:%02d", minutos, segundos),
            fontSize = 36.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                temporizadorActivo.value = true
                onTemporizadorChange(true)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar temporizador")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                temporizadorActivo.value = false
                onTemporizadorChange(false)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pausar temporizador")
        }
    }
}