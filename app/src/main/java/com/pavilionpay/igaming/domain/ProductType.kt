package com.pavilionpay.igaming.domain

enum class ProductType(private val productTypeString: String) {
    Preferred("Preferred"),
    Online("Online");

    override fun toString() = productTypeString

    companion object {
        fun fromString(productType: String): ProductType {
            return when (productType) {
                Preferred.toString() -> Preferred
                Online.toString() -> Online
                else -> throw IllegalArgumentException("Invalid product type: $productType")
            }
        }
    }
}
