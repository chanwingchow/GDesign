package com.chanwingchow

import kotlinx.serialization.Serializable

/**
 * 响应。
 *
 * @param data 数据
 */
@Serializable
data class Response<T>(val data: T)