package converter

import org.apache.poi.ss.usermodel.*
import java.io.FileInputStream

class ReadExcelFile(private val path: String) {

    private val workbook = readFile()
    val workSheets = getSheets()

    private fun readFile(): Workbook {
            // Open the Excel file
            val inputStream = FileInputStream(path)
            return WorkbookFactory.create(inputStream)
    }

    // Получение всех листов файла
    private fun getSheets(): List<Sheet>{
            var cnt: Int = workbook.numberOfSheets
            val workSheets = mutableListOf<Sheet>()

            while (cnt > 0) {
                val currentSheet = workbook.getSheetAt(cnt-1)
                workSheets.add(currentSheet)
                cnt --
            }
            return workSheets
    }
}
