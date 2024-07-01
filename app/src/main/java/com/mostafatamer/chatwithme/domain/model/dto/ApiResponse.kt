package com.mostafatamer.chatwithme.domain.model.dto

class ApiResponse<T>   {
    var data: T? = null
    var apiError: ApiError? = null
}
