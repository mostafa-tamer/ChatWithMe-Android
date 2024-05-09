package com.mostafatamer.chatwithme.network.entity

class ApiResponse<T>   {
    var data: T? = null
    var apiError: ApiError? = null
}
