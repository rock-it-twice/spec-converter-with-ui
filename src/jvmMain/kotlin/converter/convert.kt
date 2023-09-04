package converter


fun convert(pathIn: String, pathOut: String){
    // Открываем файл
    val currentSpec = ReadExcelFile(pathIn)
    // Извлекаем листы
    val unformattedSheets = currentSpec.workSheets
    // Получаем отформатированный словать (Map) со всеми деталями ЛСТК
    val sheets = GetData(unformattedSheets).formatted
    // Создаем эксель файл
    val generalSpec = CreateExcelFile(pathOut, sheets)
    // Генерируем общую спецификацию
    generalSpec.writeDataToFile()
    println("готово")
}