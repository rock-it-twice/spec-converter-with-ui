package converter

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

class GetData(unformattedSheets: List<Sheet>) {

    private val workSheets = unformattedSheets
    val formatted = getFormattedData()

    // Получение словаря с отформатированными данными.
    // Ключ_1: Имя листа;
    // Значение_1: — ключ_2: Марка детали;
    //             — значение_2: отсортированный список деталей.

    private fun getFormattedData(): Map<String, Map<String, List<FrameSteelDetail>>> {
        val sheetMap: MutableMap<String, Map<String, List<FrameSteelDetail>>> = mutableMapOf()
        // Перебираем список листов
        workSheets.forEach {
            // Извлекаем имя текущего листа
            val sheetName = it.sheetName
            // Создаем пустой список для хранения деталей из нашей спецификации
            val details: MutableList<FrameSteelDetail> = mutableListOf()
            // Получаем количество используемых строк
            var rowNum = it.physicalNumberOfRows
            // Создаем переменную для хранения кол-ва деталей (кол-во сборочных панелей)
            var multiplier = 1.0
            var rowIndex = 0
            while (rowNum > 0) {
                // Проверяем, что строка не пустая
                if (it.getRow(rowIndex) != null) {
                    val currentRow = it.getRow(rowIndex)
                    when (true) {
                        // Проверяем, содержит ли текущая строка множитель (кол-во сборочных панелей)
                        findMultiplier(currentRow) -> {
                            multiplier = currentRow.getCell(7).numericCellValue
                        }
                        // Если строка является деталью ЛСТК, сохраняем её в списке деталей текущего листа
                        checkRow(currentRow)       -> {
                            details.add(createDetail(currentRow, multiplier))
                        }
                        else -> {}
                    }
                    rowIndex++
                    rowNum--
                }
                // Если строка оказалась пустой, переходим к следующей и сбрасываем множитель
                else {
                    rowIndex++
                    multiplier = 1.0
                }
            }
            // Пополняем Map (K = Имя текущего листа, V = отредактированный список деталей)
            sheetMap[sheetName] = groupByMark(unionDuplicates(sort(details)))
        }
        return sheetMap
    }

    // Сортирует список, сначала по марке, затем по длине изделия в порядке убывания
    private fun sort(unsorted: List<FrameSteelDetail>): List<FrameSteelDetail> {
        return unsorted.sortedWith(compareBy<FrameSteelDetail> { it.mark }
            .thenByDescending { it.length })
    }

    // Группирует отсортированный список по марке изделия
    private fun groupByMark(sorted: List<FrameSteelDetail>): Map<String, List<FrameSteelDetail>>{
        val groupedByMark = mutableMapOf<String, List<FrameSteelDetail>>()
        val uniqueNames = mutableSetOf<String>()

        sorted.forEach { uniqueNames.add(it.mark) }
        uniqueNames.forEach { name ->
            val list = mutableListOf<FrameSteelDetail>()
            sorted.forEach { if (it.mark == name) list.add(it) }
            groupedByMark[name] = list
        }

        return groupedByMark
    }

    // Объединение деталей одной марки и одной длины
    private fun unionDuplicates(sorted: List<FrameSteelDetail>): List<FrameSteelDetail>{
        val newSortedList = mutableListOf<FrameSteelDetail>()
        var cnt = 0
        sorted.forEach{
            when(true){
                (cnt == 0) -> {
                    newSortedList.add(it)
                    cnt++
                }
                (cnt > 0 ) -> {
                    if (isEquals(it, newSortedList[cnt-1])) {
                        newSortedList[cnt-1].addQuantity(it.quantity)
                    }
                    else {
                        newSortedList.add(it)
                        cnt++
                    }
                }
                else -> {}
            }
        }
        return newSortedList
    }

    // Сравнение двух изделий по марке, весу марки(кг/пог. м) и длине изделия
    private fun isEquals(a1:FrameSteelDetail, a2:FrameSteelDetail): Boolean{
        return (a1.mark == a2.mark && a1.weight == a2.weight && a1.length == a2.length)
    }

    // Поиск множителя по ключевому слову
    private fun findMultiplier(currentRow: Row): Boolean {
        val cell = currentRow.getCell(1)
        return if (cell != null) cell.toString().lowercase().contains("кол-во ") else false
    }

    // Создание экземпляра класса FrameSteelDetail
    private fun createDetail(row: Row, multiplier: Double): FrameSteelDetail {
        return FrameSteelDetail(
            mark = row.getCell(1).stringCellValue,
            weight = row.getCell(2).numericCellValue,
            length = row.getCell(3).numericCellValue,
            quantity = row.getCell(5).numericCellValue * multiplier
        )
    }

    // Проверка строки по второй ячейке,
    // содержит ли строка деталь ЛСТК, которую необходимо сохранить в отформатированном списке
    private fun checkRow(currentRow: Row): Boolean {
        val cell = currentRow.getCell(1)
        var isCheckSuccess = false
        if(cell != null) {
            // приводим ячейку к единому формату для удобства сравнения
            val formattedCell = cell.toString().lowercase()
            when (true) {
                formattedCell.contains("спецификация") -> {}
                formattedCell.contains("марка") -> {}
                formattedCell.contains("вес ") -> {}
                formattedCell.contains("примечание:") -> {}
                else -> {
                    isCheckSuccess = true
                }
            }
        } else{ return false }
        return isCheckSuccess
    }
}