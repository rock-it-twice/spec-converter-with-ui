package converter


data class FrameSteelDetail(val mark: String,
                            val weight: Double,
                            val length: Double,
                            var quantity: Double){
    fun addQuantity(a: Double){
        quantity += a
    }
}
