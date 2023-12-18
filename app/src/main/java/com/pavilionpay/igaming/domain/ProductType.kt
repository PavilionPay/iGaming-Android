package com.pavilionpay.igaming.domain

sealed class ProductType(private val productType: String) {
    object Preferred : ProductType("Preferred")
    object Online : ProductType("Online") {
        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }
    }

    override fun toString() = productType

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
