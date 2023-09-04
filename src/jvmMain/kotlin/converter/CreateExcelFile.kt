package converter

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import kotlin.io.path.Path
import kotlin.io.path.outputStream

class CreateExcelFile(path: String,
                      private val  sheets: Map<String, Map<String, List<FrameSteelDetail>>>) {

        private val workbook: Workbook = XSSFWorkbook()
        private val worksheet = workbook.createSheet("Общая спецификация")


    init {
        // задаем ширины столбцов
        worksheet.setColumnWidth(0, 18 * 267)
        for (i in 1..6){
            worksheet.setColumnWidth(i, 11 * 267)
        }
    }

    // создает заголовок раздела
    private fun createHeaderBySheetName(k: String, index: Int){
        // Создаем строку
        val currentRow = worksheet.createRow(index)

        // Задаем настройки шрифта
        val font = currentRow.sheet.workbook.createFont()
        font.bold = true
        font.fontHeightInPoints = 16

        // Задаем стиль ячеек для заголовка
        val headerStyle = workbook.createCellStyle()
        SpecStyles(headerStyle).headerStyle()
        headerStyle.setFont(font)

        // Создаем заголовок
        for (i in 0..6) {currentRow.createCell(i, CellType.STRING)}
        currentRow.height = 600

        // Заполняем данными
        currentRow.getCell(0).setCellValue(k)
        // Задаем стиль и объединяем ячейки
        for (i in 0..6){ worksheet.getRow(index).getCell(i).cellStyle = headerStyle }
        worksheet.addMergedRegion( CellRangeAddress(index, index, 0, 6) )
    }

    // задает имя таблицы и названия столбцов
    private fun createHeaderByMark(mark: String, index: Int){
        // Создаем строку
        val currentRow = worksheet.createRow(index)

        // Задаем настройки шрифта
        val font = currentRow.sheet.workbook.createFont()
        font.bold = true
        font.fontHeightInPoints = 14

        // Задаем стиль ячеек для заголовка
        val headerStyle = workbook.createCellStyle()
        SpecStyles(headerStyle).headerByDetailStyle()
        headerStyle.setFont(font)

        // создаем заголовок
        for (i in 0..6) {currentRow.createCell(i, CellType.STRING)}
        currentRow.height = 600

        // Заполняем данными
        currentRow.getCell(0).setCellValue(mark)

        // задаем стиль и объединяем ячейки
        for (i in 0..6){ worksheet.getRow(index).getCell(i).cellStyle = headerStyle }
        worksheet.addMergedRegion( CellRangeAddress(index, index, 0, 6) )
    }

    // Задает имена столбцов
    private fun createColumnNames(index: Int){

        // Создаем строку
        val currentRow = worksheet.createRow(index)

        // Задаем настройки шрифта
        val font = currentRow.sheet.workbook.createFont()
        font.bold = true

        // Задаем стиль ячеек для названия колонок
        val cellStyle = workbook.createCellStyle()
        SpecStyles(cellStyle).colNameStyle()
        cellStyle.setFont(font)

        currentRow.createCell(0).setCellValue("марка")
        currentRow.createCell(1).setCellValue("вес (кг/м)")
        currentRow.createCell(2).setCellValue("длина изд")
        currentRow.createCell(3).setCellValue("длина общ")
        currentRow.createCell(4).setCellValue("кол-во")
        currentRow.createCell(5).setCellValue("вес изд")
        currentRow.createCell(6).setCellValue("вес общ")

        // присваиваем стиль ячейкам
        for (i in 0..6){
            currentRow.getCell(i).cellStyle = cellStyle
        }


    }

    // Заполняет строки данными
    private fun createDataRow(detail: FrameSteelDetail, index: Int){

        val dataRow = worksheet.createRow(index)

        // определяем стиль ячеек
        val cellStyle = workbook.createCellStyle()
        SpecStyles(cellStyle).borderAllSides()

        // заполняем ячейки данными
        dataRow.createCell(0, CellType.STRING).setCellValue(detail.mark)
        dataRow.createCell(1, CellType.NUMERIC).setCellValue(detail.weight)
        dataRow.createCell(2, CellType.NUMERIC).setCellValue(detail.length)
        dataRow.createCell(3, CellType.FORMULA).cellFormula = "C${index+1}*E${index+1}"
        dataRow.createCell(4, CellType.NUMERIC).setCellValue(detail.quantity)
        dataRow.createCell(5, CellType.FORMULA).cellFormula = "B${index+1}*C${index+1}/1000"
        dataRow.createCell(6, CellType.FORMULA).cellFormula = "F${index+1}*E${index+1}"

        // присваиваем стиль ячейкам
        for (i in 0..6){
            dataRow.getCell(i).cellStyle = cellStyle
        }
    }

    // вычисляет итоговую сумму длины и веса изделий определенной марки
    private fun createSumRow(lastIndex: Int, rowIndex: Int){

        val currentRow = worksheet.createRow(rowIndex)

        // Задаем настройки шрифта
        val font = currentRow.sheet.workbook.createFont()
        font.bold = true

        val headerCellStyle = workbook.createCellStyle()
        SpecStyles(headerCellStyle).sumHeaderStyle()
        headerCellStyle.setFont(font)

        val dataCellStyle = workbook.createCellStyle()
        SpecStyles(dataCellStyle).sumStyle()

        currentRow.height = 600
        for (i in 0..6) { currentRow.createCell(i) }
        currentRow.getCell(0).setCellValue("Общая длина:")
        currentRow.getCell(3).cellFormula = "SUM(D${rowIndex-lastIndex}:D${rowIndex})"
        currentRow.getCell(4).setCellValue("Общий вес:")
        currentRow.getCell(6).cellFormula = "SUM(G${rowIndex-lastIndex}:G${rowIndex})"

        for (i in 0..2){ currentRow.getCell(i).cellStyle = headerCellStyle }
        worksheet.addMergedRegion(CellRangeAddress(rowIndex, rowIndex, 0, 2))
        currentRow.getCell(3).cellStyle = dataCellStyle

        for (i in 4..5){ currentRow.getCell(i).cellStyle = headerCellStyle }
        worksheet.addMergedRegion(CellRangeAddress(rowIndex, rowIndex, 4, 5))
        currentRow.getCell(6).cellStyle = dataCellStyle
    }

    // Подсчитывает итоговый вес всех деталей раздела
    private fun createSumRow(headerName: String, sumCoordinates: List<Int>, rowIndex: Int){

        val currentRow = worksheet.createRow(rowIndex)

        // Задаем настройки шрифта
        val font = currentRow.sheet.workbook.createFont()
        font.bold = true
        font.fontHeightInPoints = 14

        // задаем стиль заголовка
        val headerCellStyle = workbook.createCellStyle()
        SpecStyles(headerCellStyle).sumSumHeaderStyle()
        headerCellStyle.setFont(font)

        // вычисляем формулу
        var formula = ""
        sumCoordinates.forEachIndexed { index, it ->
            formula += if (index < sumCoordinates.lastIndex) "G${it+1}+" else "G${it+1}"
        }

        // задаем стиль ячейки данных
        val dataCellStyle = workbook.createCellStyle()
        SpecStyles(dataCellStyle).sumStyle()
        currentRow.height = 600

        // создаем заголовок
        for (i in 0..6) { currentRow.createCell(i) }
        for (i in 0..5) { currentRow.createCell(i).cellStyle = headerCellStyle }
        worksheet.addMergedRegion(CellRangeAddress(rowIndex, rowIndex, 0, 5))
        currentRow.getCell(0).setCellValue("Вес $headerName:")

        // создаем ячейку данных
        currentRow.getCell(6).cellFormula = formula
        currentRow.getCell(6).cellStyle = dataCellStyle
    }

    // Создает 2 пустых строки разделяющих группы данных
    private fun createEmptyRows(index: Int){
        worksheet.createRow(index)
        worksheet.createRow(index+1)
    }

    // Запись отформатированных данных в файл
    fun writeDataToFile(){
        var rowIndex = 0
        val sumSheetCoordinates = mutableListOf<Int>()
        sheets.forEach{ (sheetName, groups) ->
            val sumDetailCoordinates = mutableListOf<Int>()
            createHeaderBySheetName(sheetName, rowIndex)
            rowIndex ++
            createEmptyRows(rowIndex)
            rowIndex +=2
            groups.forEach{ (markName, details) ->
                createHeaderByMark(markName, rowIndex)
                rowIndex ++
                createColumnNames(rowIndex)
                rowIndex ++
                details.forEachIndexed{ i, it ->
                    if (i != details.lastIndex) {
                        createDataRow(it, rowIndex)
                        rowIndex ++
                    } else{
                        createDataRow(it, rowIndex)
                        rowIndex ++
                        createSumRow(details.lastIndex, rowIndex)
                        sumDetailCoordinates.add(rowIndex)
                        rowIndex ++
                        createEmptyRows(rowIndex)
                        rowIndex +=2
                    }
                }
            }
            createSumRow(sheetName, sumDetailCoordinates, rowIndex)
            sumSheetCoordinates.add(rowIndex)
            rowIndex ++
            createEmptyRows(rowIndex)
            rowIndex +=2
        }
        createEmptyRows(rowIndex)
        rowIndex +=2
        createSumRow("каркаса ИТОГО", sumSheetCoordinates, rowIndex)

        workbook.write(tempFile.outputStream())
        workbook.close()
    }
    private val tempFile = kotlin.io.path.createTempFile(Path(path), "ОС_", ".xlsx")
}