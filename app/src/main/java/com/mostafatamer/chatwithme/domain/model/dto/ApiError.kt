package com.mostafatamer.chatwithme.domain.model.dto

import lombok.Builder

@Builder
class ApiError {
    var message: String? = null
    var validationMessages: List<String>? = null
}