package com.lingyun.lib.jstruct

import kotlin.experimental.and

/*
* Created by mc_luo on 2021/3/26 .
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
object HexUtil {

    private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()
    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v: Int = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = HEX_ARRAY[v ushr 4]
            hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
        }
        return String(hexChars)
    }

    const val SPACE_CHAR = ' '
    fun bytesToHexSpace(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 3)
        for (j in bytes.indices) {
            val v: Int = bytes[j].toInt() and 0xFF
            hexChars[j * 3] = HEX_ARRAY[v ushr 4]
            hexChars[j * 3 + 1] = HEX_ARRAY[v and 0x0F]
            hexChars[j * 3 + 2] = SPACE_CHAR
        }
        return String(hexChars)
    }
}