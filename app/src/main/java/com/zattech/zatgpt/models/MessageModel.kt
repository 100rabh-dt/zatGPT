package com.zattech.zatgpt.models

data class MessageModel(
    var  isUser:Boolean,
    var isImage:Boolean,
    var message: String,
    val textResponse: String? = null  // Add the textResponse property

)
