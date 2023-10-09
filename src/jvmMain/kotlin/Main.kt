import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.Desktop
import java.io.File



@Composable
@Preview
fun App() {

    // Делаем нормальный вид всплывающего окна
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    // Фильтр файлов
    val filter = FileNameExtensionFilter("Файлы эксель (*.xlsx)","xlsx")

    val importChooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
        fileFilter = filter
    }
    val exportChooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    }

    // Отслеживание состояния кнопки
    val isButtonEnabled = remember { mutableStateOf(false) }
    // Отслеживание состояния кнопки открытия проводника
    val isPathButtonEnabled = remember { mutableStateOf(false) }

    // Запоминаем путь импорта / экспорта
    val pathIn = remember { mutableStateOf("файл не выбран") }
    val pathOut = remember { mutableStateOf("") }

    val filename = remember { mutableStateOf("") }

    MaterialTheme {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            // Шапка
            Text(text = "Specification converter ver: 1.0",
                modifier = Modifier.align(Alignment.CenterHorizontally)
                                   .padding(top = 30.dp, bottom = 15.dp),
                fontWeight = FontWeight.Bold
            )
            // Выбор файла для конвертации
            Row(modifier = Modifier.fillMaxWidth().padding(start = 50.dp, end = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween){
                Text(text = "Исходный файл:",
                    modifier = Modifier.padding(top = 14.dp, end = 10.dp)
                )
                if (filename.value == ""){
                    Text(text = "файл не выбран",
                        modifier = Modifier.padding(top = 14.dp, end = 10.dp),
                        color = Color.LightGray,
                        overflow = TextOverflow.Clip
                    )
                } else {
                Text(text = filename.value,
                    modifier = Modifier.padding(top = 14.dp, end = 10.dp),
                    color = Color.Green,
                    overflow = TextOverflow.Clip
                )
                }
                Button(modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(backgroundColor = (Color.LightGray)),
                    onClick = {
                        val result = importChooser.showOpenDialog(null)
                        if (result == JFileChooser.APPROVE_OPTION){
                            pathIn.value = importChooser.selectedFile.absolutePath
                            filename.value = importChooser.selectedFile.name
                            isButtonEnabled.value = true
                            pathOut.value = importChooser.currentDirectory.path
                        }
                    }) {
                    Text(if (pathIn.value == "файл не выбран") "выбрать" else "изменить")
                }
            }

            // Выбор папки для сохранения общей спецификации
            Row(Modifier.fillMaxWidth().padding(start = 50.dp, end = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween){
                Text("Сохранить в папку:",
                    modifier = Modifier.padding(top = 14.dp, end = 10.dp),
                    textAlign = TextAlign.Left
                )
                if (pathOut.value == ""){
                    Text("папка не выбрана",
                        modifier = Modifier.padding(top = 14.dp, end = 10.dp),
                        textAlign = TextAlign.Center,
                        color = Color.LightGray,
                        overflow = TextOverflow.Clip
                    )
                } else {
                    Text(
                        pathOut.value,
                        modifier = Modifier.padding(top = 14.dp, end = 10.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Green,
                        overflow = TextOverflow.Clip
                    )
                }
                Button(modifier = Modifier.padding(bottom = 15.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = (Color.LightGray)),
                    enabled = isButtonEnabled.value,
                    onClick = {
                        val result = exportChooser.showOpenDialog(null)
                        if (result == JFileChooser.APPROVE_OPTION){
                            pathOut.value = exportChooser.selectedFile.path
                        }
                    }) {
                    Text("изменить")
                }
            }
            // Сформировать общую спецификацию
            Row(modifier = Modifier.fillMaxWidth().padding(start = 50.dp, end = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                var progress by remember { mutableStateOf(0.0f) }
                val scope = rememberCoroutineScope()
                Button(
                    modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(backgroundColor = (Color.LightGray)),
                    enabled = isButtonEnabled.value,
                    onClick = {
                        isPathButtonEnabled.value = false
                        scope.launch {
                            while (progress < 1f){
                                progress += 0.1f
                                delay(100L)
                            }
                        }
                        converter.convert(pathIn.value, pathOut.value)
                        // Меняем значение видимости кнопки перехода в папку
                        isPathButtonEnabled.value = true
                        progress = 0f
                    })
                {
                    Text("Сформировать общую спецификацию")
                }

                // Индикатор прогресса
                if (!isPathButtonEnabled.value){
                    LinearProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterVertically)
                            .padding(horizontal = 15.dp),
                        progress = progress
                    )
                } else {
                    AnimatedVisibility(
                        visible = true,
                        modifier = Modifier,
                        enter = fadeIn(animationSpec = tween(durationMillis = 300, easing = LinearEasing))
                    ){
                        Icon(Icons.Default.Check,
                            contentDescription = "Check mark",
                            tint = Color.Green
                        )
                    }
                }



            }


        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication,
        title = "SpecConverter",
        state = rememberWindowState(width = 960.dp, height = 360.dp)
    ) {
        App()
    }
}
