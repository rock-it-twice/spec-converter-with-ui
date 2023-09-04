import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.apache.poi.sl.usermodel.VerticalAlignment
import org.apache.poi.xddf.usermodel.chart.Shape
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileNameExtensionFilter



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

    val isButtonEnabled = remember { mutableStateOf(false) }

    // Запоминаем путь импорта / экспорта
    val pathIn = remember { mutableStateOf("файл не выбран") }
    val pathOut = remember { mutableStateOf("") }

    val filename = remember { mutableStateOf("") }

    MaterialTheme {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Text(text = "Specification converter ver: 1.0",
                modifier = Modifier.align(Alignment.CenterHorizontally)
                                   .padding(top = 30.dp, bottom = 15.dp),
                fontWeight = FontWeight.Bold
            )
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

            Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(backgroundColor = (Color.LightGray)),
                enabled = isButtonEnabled.value,
                onClick = {
                    converter.convert(pathIn.value, pathOut.value)
                }){
                Text("Сформировать общую спецификацию")
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
