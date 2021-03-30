package com.lingyun.lib.jstruct

import java.nio.ByteBuffer
import java.nio.ByteOrder

/*
* Created by mc_luo on 2021/3/23 .
* Copyright (c) 2021 The LingYun Authors. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
object JStrcut {

    val ALLOW_BASIC_TYPE = arrayOf('b', 'B', 'c', 'h', 'H', 'i', 'I', 'l', 'f', 'd')

    var byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN

    fun newPackContext(struct: String, elements: List<Any>): PackContext {
        var bo = byteOrder

        var s = struct
        when (struct[0]) {
            '>' -> {
                bo = ByteOrder.BIG_ENDIAN
                s = struct.substring(1)
            }
            '<' -> {
                bo = ByteOrder.LITTLE_ENDIAN
                s = struct.substring(1)
            }
        }
        val byteBuffer = ByteBuffer.allocate(1024)
        byteBuffer.order(bo)

        val context = PackContext(struct, byteBuffer, elements)
        return context
    }

    fun newUnPackContext(struct: String, data: ByteArray): UnpackContext {

        var bo = byteOrder

        var s = struct
        when (struct[0]) {
            '>' -> {
                bo = ByteOrder.BIG_ENDIAN
                s = struct.substring(1)
            }
            '<' -> {
                bo = ByteOrder.LITTLE_ENDIAN
                s = struct.substring(1)
            }
        }

        val byteBuffer = ByteBuffer.wrap(data)
        byteBuffer.order(bo)

        val context = UnpackContext(struct, byteBuffer)
        return context
    }
}