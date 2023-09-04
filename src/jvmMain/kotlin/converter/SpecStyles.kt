package converter

import org.apache.poi.ss.usermodel.*

// Класс определения стилей ячеек
class SpecStyles(private val cellStyle: CellStyle) {

    fun borderAllSides(){
        cellStyle.borderTop = BorderStyle.THIN
        cellStyle.borderBottom = BorderStyle.THIN
        cellStyle.borderLeft = BorderStyle.THIN
        cellStyle.borderRight = BorderStyle.THIN
        cellStyle.topBorderColor = IndexedColors.BLACK.index
        cellStyle.bottomBorderColor = IndexedColors.BLACK.index
        cellStyle.leftBorderColor = IndexedColors.BLACK.index
        cellStyle.rightBorderColor = IndexedColors.BLACK.index
    }

    fun headerStyle(){
        cellStyle.fillForegroundColor = IndexedColors.YELLOW.index
        cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
        cellStyle.alignment = HorizontalAlignment.CENTER
        cellStyle.verticalAlignment = VerticalAlignment.CENTER
        borderAllSides()
    }

    fun headerByDetailStyle(){
        cellStyle.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
        cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
        cellStyle.alignment = HorizontalAlignment.LEFT
        cellStyle.indention = 3
        cellStyle.verticalAlignment = VerticalAlignment.CENTER
        borderAllSides()
    }

    fun colNameStyle(){
        cellStyle.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
        cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
        borderAllSides()
    }

    fun sumHeaderStyle(){
        cellStyle.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
        cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
        cellStyle.alignment = HorizontalAlignment.LEFT
        cellStyle.indention = 3
        cellStyle.verticalAlignment = VerticalAlignment.CENTER
        borderAllSides()
    }

    fun sumSumHeaderStyle(){
        cellStyle.fillForegroundColor = IndexedColors.AQUA.index
        cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
        cellStyle.alignment = HorizontalAlignment.RIGHT
        cellStyle.indention = 3
        cellStyle.verticalAlignment = VerticalAlignment.CENTER
        borderAllSides()
    }

    fun sumStyle(){
        cellStyle.alignment = HorizontalAlignment.CENTER
        cellStyle.verticalAlignment = VerticalAlignment.CENTER
        borderAllSides()
    }
}