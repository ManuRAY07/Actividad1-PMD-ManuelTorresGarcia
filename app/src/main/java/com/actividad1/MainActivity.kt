package com.actividad1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.actividad1.ui.theme.Actividad1Theme

//Guardamos los datos del formulario.
class Formulario {
    var salario by mutableStateOf("")
    var edad by mutableStateOf("")
    var grupo by mutableStateOf(1)
    var pagas by mutableStateOf(12)
    var situacion by mutableStateOf(0)
    var hijos by mutableStateOf("")
    var indefinido by mutableStateOf(true)
    var discapacidad by mutableStateOf(false)

    //Se usa al hacer una petición nueva.
    fun limpiar() {
        salario = ""
        edad = ""
        grupo = 1
        pagas = 12
        situacion = 0
        hijos = ""
        indefinido = true
        discapacidad = false
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Actividad1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF000040),
                    contentColor = Color.White
                ) {
                    PantallaPrincipal(onSalir = { finish() })
                }
            }
        }
    }
}

//Las pantallas son->0 Inicio, 1->Formulario, 2->Confirmación y 3->Resultado
@Composable
fun PantallaPrincipal(onSalir: () -> Unit) {
    val formulario = remember { Formulario() }
    var pantalla by remember { mutableStateOf(0) }
    var resultado by remember { mutableStateOf<Resultado?>(null) }

    when (pantalla) {
        0 -> PantallaInicio(onComenzar = { pantalla = 1 }, onSalir = onSalir)

        1 -> PantallaDatos(
            formulario = formulario,
            onRevisar = { pantalla = 2 }
        )

        2 -> PantallaConfirmacion(
            formulario = formulario,
            onContinuar = {
                val bruto = formulario.salario.toDoubleOrNull() ?: 0.0
                resultado = calcularSalario(
                    bruto,
                    formulario.pagas,
                    formulario.situacion,
                    formulario.hijos.toIntOrNull() ?: 0,
                    formulario.indefinido,
                    formulario.discapacidad
                )
                pantalla = 3
            },
            onRectificar = { pantalla = 1 }
        )

        else -> {
            val actual = resultado
            if (actual != null) {
                PantallaResultados(
                    resultado = actual,
                    onVolver = {
                        formulario.limpiar()
                        pantalla = 1
                    },
                    onSalir = onSalir
                )
            }
        }
    }
}

//Aquí he hecho un pequeño menú muy simple donde el usuario decide si continuar o salir
@Composable
fun PantallaInicio(onComenzar: () -> Unit, onSalir: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Calcula tu salario neto",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(12.dp))

        Button(onClick = onComenzar, modifier = Modifier.fillMaxWidth()) {
            Text("Comenzar")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(onClick = onSalir, modifier = Modifier.fillMaxWidth()) {
            Text("Salir")
        }
    }
}

//En esta sección es donde se recogen los datos para después
@Composable
fun PantallaDatos(formulario: Formulario, onRevisar: () -> Unit) {
    var menuAbierto by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    //Colores de las cajas de texto, el fondo transparente, los bordes y letras blancas
    val colorCampos = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.LightGray,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White,
        cursorColor = Color.White
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //Aquí se alinea el texto con los iconos de radios y casillas
        Text(
            "Calculadora de salario neto",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 12.dp)
        )

        //El campo del salario
        OutlinedTextField(
            value = formatMiles(formulario.salario),
            onValueChange = { texto ->
                //Solo se guardan los dígitos, el formato con puntos se aplica al mostrarlo
                formulario.salario = texto.filter { c -> c.isDigit() }
            },
            label = { Text("Salario bruto anual (€)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = colorCampos,
            modifier = Modifier
                .padding(start = 12.dp)
                .fillMaxWidth()
        )

        //El campo de la edad
        OutlinedTextField(
            value = formulario.edad,
            onValueChange = { texto ->
                formulario.edad = texto.filter { c -> c.isDigit() }
            },
            label = { Text("Edad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = colorCampos,
            modifier = Modifier
                .padding(start = 12.dp)
                .fillMaxWidth()
        )

        Text(
            "Grupo profesional",
            modifier = Modifier.padding(start = 12.dp)
        )
        //El campo del grupo profesional, al pulsarlo se abre el menú
        Box(
            modifier = Modifier
                .padding(start = 12.dp)
                .fillMaxWidth()
        ) {
            OutlinedButton(onClick = { menuAbierto = true }) {
                Text("Grupo ${formulario.grupo}")
            }
            DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                (1..11).forEach { n ->
                    DropdownMenuItem(
                        text = { Text("Grupo $n") },
                        onClick = {
                            formulario.grupo = n
                            menuAbierto = false
                        }
                    )
                }
            }
        }

        Text(
            "Número de pagas",
            modifier = Modifier.padding(start = 12.dp)
        )
        //Aquí se elige el número de pagas
        Row {
            RadioButton(selected = formulario.pagas == 12, onClick = { formulario.pagas = 12 })
            Text("12 pagas")
            RadioButton(selected = formulario.pagas == 14, onClick = { formulario.pagas = 14 })
            Text("14 pagas")
        }

        //Aquí lo mismo pero para la situación familiar
        Text(
            "Situación familiar",
            modifier = Modifier.padding(start = 12.dp)
        )
        Row {
            RadioButton(selected = formulario.situacion == 0, onClick = { formulario.situacion = 0 })
            Text("Soltero/a")
        }
        Row {
            RadioButton(selected = formulario.situacion == 1, onClick = { formulario.situacion = 1 })
            Text("Casado/a o pareja de hecho")
        }
        Row {
            RadioButton(selected = formulario.situacion == 2, onClick = { formulario.situacion = 2 })
            Text("Familia monoparental")
        }

        //Aquí va el dato de los hijos
        OutlinedTextField(
            value = formulario.hijos,
            onValueChange = { texto ->
                formulario.hijos = texto.filter { c -> c.isDigit() }
            },
            label = { Text("Hijos a cargo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = colorCampos,
            modifier = Modifier
                .padding(start = 12.dp)
                .fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .padding(start = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Contrato indefinido", modifier = Modifier.weight(1f))
            //Se pone On/Off el contrato indefinido
            Switch(
                checked = formulario.indefinido,
                onCheckedChange = { formulario.indefinido = it }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            //Se marca o no la discapacidad
            Checkbox(
                checked = formulario.discapacidad,
                onCheckedChange = { formulario.discapacidad = it }
            )
            Text("Discapacidad reconocida (33 % o más)")
        }

        Text(
            error,
            color = Color(0xFFFF8A80),
            modifier = Modifier.padding(start = 12.dp)
        )

        //Aquí se comprueban los datos y se muestra el error si algo no está bien
        Button(
            onClick = {
                val bruto = formulario.salario.toDoubleOrNull()
                val edadNum = formulario.edad.toIntOrNull()

                error = when {
                    formulario.salario.isEmpty() -> "No has indicado el salario bruto anual"
                    bruto == null || bruto <= 0 -> "El salario bruto no es válido"
                    formulario.edad.isEmpty() -> "No has indicado la edad"
                    edadNum == null || edadNum < 16 || edadNum > 100 -> "La edad debe estar entre 16 y 100 años"
                    else -> ""
                }

                if (error.isEmpty()) {
                    onRevisar()
                }
            },
            modifier = Modifier
                .padding(start = 12.dp)
                .fillMaxWidth()
        ) {
            Text("Calcular")
        }
    }
}

//Aquí se le muestra al usuario un resumen de sus datos y se le pregunta si están correctos o los quiere rectificar
@Composable
fun PantallaConfirmacion(
    formulario: Formulario,
    onContinuar: () -> Unit,
    onRectificar: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Estos datos son correctos", style = MaterialTheme.typography.headlineSmall)
        //Se muestran los datos para que se revisen
        Text("Salario bruto anual: " + formatMiles(formulario.salario) + " €")
        Text("Edad: " + formulario.edad.ifEmpty { "sin indicar" })
        Text("Grupo: " + formulario.grupo)
        Text("Número de pagas: " + formulario.pagas)
        Text("Situación familiar: " + etiquetaSituacion(formulario.situacion))
        Text("Hijos: " + formulario.hijos.ifEmpty { "0" })
        Text("Contrato: " + if (formulario.indefinido) "indefinido" else "temporal")
        Text("Discapacidad reconocida: " + if (formulario.discapacidad) "sí" else "no")

        Spacer(Modifier.height(16.dp))

        Button(onClick = onContinuar, modifier = Modifier.fillMaxWidth()) {
            Text("Sí, continuar")
        }

        OutlinedButton(onClick = onRectificar, modifier = Modifier.fillMaxWidth()) {
            Text("No, rectificar")
        }
    }
}

//Aquí se muestra el resultado del cálculo, la pantalla más importante
@Composable
fun PantallaResultados(resultado: Resultado, onVolver: () -> Unit, onSalir: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Titulo("Resultado del cálculo")

        FilaDato("Salario bruto anual", formato(resultado.brutoAnual) + " €")
        FilaDato("Salario bruto mensual", formato(resultado.brutoMensual) + " €")
        FilaDato("Tipo de retención", formato(resultado.tipoIrpf) + " %")
        FilaDato("Retención de IRPF anual", formato(resultado.retencionIrpf) + " €")
        FilaDato("Salario neto anual", formato(resultado.netoAnual) + " €")
        FilaDato("Salario neto mensual", formato(resultado.netoMensual) + " €")
        HorizontalDivider(thickness = 1.dp, color = Color.White)

        if (resultado.brutoAnual <= resultado.minimoExento) {
            Text(
                "Está por debajo del mínimo exento, así que no hay retención.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }

        Titulo("Posibles deducciones")

        FilaDato("Seguridad Social", formato(resultado.seguridadSocial) + " €")
        FilaDato("Mínimo exento", formato(resultado.minimoExento) + " €")
        HorizontalDivider(thickness = 1.dp, color = Color.White)

        Spacer(Modifier.height(24.dp))

        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Nuevo cálculo")
        }
        Spacer(Modifier.height(12.dp))

        OutlinedButton(onClick = onSalir, modifier = Modifier.fillMaxWidth()) {
            Text("Salir")
        }
    }
}

//Así los títulos se ven en grande y centrados
@Composable
fun Titulo(texto: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
    }
}

//Así las líneas de datos siguen la misma estructura, altura fija, línea divisoria arriba, nombre a la izquierda y valor a la derecha
@Composable
fun FilaDato(etiqueta: String, valor: String) {
    HorizontalDivider(thickness = 1.dp, color = Color.White)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, style = MaterialTheme.typography.bodyLarge)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}

//Esto añade un punto cada tres cifras
fun formatMiles(digitos: String): String {
    if (digitos.isEmpty()) return ""
    return digitos.reversed().chunked(3).joinToString(".").reversed()
}

//Se encarga de transformar los 0,1,2 de las opciones en los textos de las respuestas
fun etiquetaSituacion(situacion: Int): String {
    return when (situacion) {
        0 -> "Soltero/a"
        1 -> "Casado/a o pareja de hecho"
        else -> "Familia monoparental"
    }
}

//Muestra un número con 2 decimales
fun formato(valor: Double): String {
    return String.format("%.2f", valor)
}