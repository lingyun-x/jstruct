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

    val ALLOW_BASIC_TYPE = arrayOf('b', 'B', 'h', 'H', 'i', 'I', 'l', 'f', 'd')

    var byteOrder = ByteOrder.BIG_ENDIAN

    fun unpack(data: ByteArray, struct: String): List<Any> {
        var bo = byteOrder

        var s  = struct
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

        val packContext = PackContext(s, byteBuffer)
        return packContext.unpack()
    }

    fun pack(struct: String, values: List<Any>): ByteArray {
        var bo = byteOrder

        var s  = struct
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

        val packContext = PackContext(s, byteBuffer, values.toMutableList())

        return packContext.pack()
    }
}