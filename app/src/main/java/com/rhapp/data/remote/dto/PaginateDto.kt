package com.rhapp.data.remote.dto

data class PaginatedDto<T>(
    val count:    Int,
    val next:     String?,
    val previous: String?,
    val results:  List<T>,
)