import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.Desktop
import java.awt.Dimension
import java.io.File



@Composable
@Preview
fun App() {

    // Делаем нормальный вид всплывающего окна выбора файла
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

    // Отслеживание состояния кнопки выбора файла
    val isButtonEnabled = remember { mutableStateOf(false) }

    // Переменные для хранения путей к файлам импорта / экспорта
    val pathIn = remember { mutableStateOf("файл не выбран") }
    val pathOut = remember { mutableStateOf("") }

    val filename = remember { mutableStateOf("") }

    MaterialTheme {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            // Шапка
            Row(modifier = Modifier.fillMaxWidth().padding(start = 50.dp, end = 50.dp, top = 30.dp, bottom = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom){

                Text(text = "Specification converter",
                    modifier = Modifier,
                    fontWeight = FontWeight.Bold,
                    fontSize = 3.em
                )
                Text(text = "ver: 1.1",
                    modifier = Modifier,
                    fontWeight = FontWeight.Thin
                )

            }
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {

                var animationProgress by remember { mutableStateOf(0.0f) }
                val scope = rememberCoroutineScope()
                // Отслеживание состояния существования пути к папке с общей спецификацией
                val isPathEnabled = remember { mutableStateOf(false) }

                Button(
                    modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(backgroundColor = (Color.LightGray)),
                    enabled = isButtonEnabled.value,
                    onClick = {
                        isPathEnabled.value = false
                        animationProgress = 0f
                        scope.launch {
                            while (animationProgress < 1f){
                                animationProgress += 0.1f
                                delay(100L)
                            }
                        }
                        converter.convert(pathIn.value, pathOut.value)
                        // Меняем значение доступа пути в папку
                        isPathEnabled.value = !isPathEnabled.value
                    })
                {
                    Text("Сформировать общую спецификацию")
                }

                // Индикатор прогресса конвертации
                ProgressionIndicator(isPathEnabled, animationProgress)

                // Кнопка перехода в папку с общей спецификацией
                Button(
                    modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(backgroundColor = (Color.LightGray)),
                    enabled = isPathEnabled.value,
                    onClick = {
                        val file = File(pathOut.value)
                        if (Desktop.isDesktopSupported()){
                            val desktop = Desktop.getDesktop()
                            if (desktop.isSupported(Desktop.Action.OPEN)){
                                desktop.open(file)
                            }
                        }
                    }
                ){
                    Text("Открыть папку")
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
        // Задаем минимальный размер окна
        window.minimumSize = Dimension(960,360)
        App()
    }
}
