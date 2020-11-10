package com.harnet.devnewsradar.model

data class SmsInfo(
    var to: String,
    var text: String,
    var articleUrl: String,
    var imageURL: String
)