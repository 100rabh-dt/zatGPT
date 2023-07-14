package com.zattech.zatgpt.models.imageresponse

data class GenerateImageModel(
    val created: Int,
    val `data`: List<DataX>
)