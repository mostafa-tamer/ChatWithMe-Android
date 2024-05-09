package com.mostafatamer.chatwithme.network.entity

import lombok.Builder

@Builder
class ApiError {
    var message: String? = null
    var validationMessages: List<String>? = null
}