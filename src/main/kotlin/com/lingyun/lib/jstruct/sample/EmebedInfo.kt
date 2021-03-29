package com.lingyun.lib.jstruct.sample

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
data class EmebedInfo(
    val aByte: Byte, val aUByte: Short, val aShort: Short, val aUShort: Int, val aInt: Int,
    val aUInt: Long, val aLong: Long, val afloat: Float, val aDouble: Double,
    val aByteArrayLength: Int, val aByteArray: ByteArray,
    val aUByteArrayLength: Int, val aUByteArray: ShortArray,
    val aShortArrayLength: Int, val aShortArray: ShortArray,
    val aUShortArrayLength: Int, val aUShortArray: IntArray,
    val aIntArrayLength: Int, val aIntArray: IntArray,
    val aUIntArrayLength: Int, val aUIntArray: LongArray,
    val aLongArrayLength: Int, val aLongArray: LongArray,
    val aFloatArrayLength: Int, val aFloatArray: LongArray,
    val aDoubleArrayLength: Int, val aDoubleArray: LongArray,
) {
    fun packetStruct(): String {
        return "1b1B1h1H1i1I1l1f1d1i$-1[b]1i$-1[B]1i$-1[h]1i$-1[H]1i$-1[i]1i$-1[I]1i$-1[l]1i$-1[d]"
    }
}