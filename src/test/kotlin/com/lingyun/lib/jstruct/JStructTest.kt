package com.lingyun.lib.jstruct

import com.lingyun.lib.jstruct.extension.readString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

/*
* Created by mc_luo on 2021/4/1 .
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
internal class JStructTest {


    @Test
    fun testWhiteSpace() {
        val struct = "1i i 1i"
        val aint0 = 0
        val aint1 = 1
        val aint2 = 2

        val elements = listOf<Any>(aint0, aint1, aint2)
        val bytes = JStruct.pack(struct, elements)
        println("bytes:${HexUtil.bytesToHexSpace(bytes)}")
        val values = JStruct.unpack(struct, bytes)
        assertEquals(aint0, values[0])
        assertEquals(aint1, values[1])
        assertEquals(aint2, values[2])
    }

    @Test
    fun testCharType() {
        val struct = "(1+2+3)c"
        val chars: List<Char> = listOf('0', '1', '2', '3', '4', '5')
        val bytes = JStruct.pack(struct, chars)
        println("bytes:${HexUtil.bytesToHexSpace(bytes)}")
        val values = JStruct.unpack(struct, bytes)

        values.indices.forEach {
            assertEquals(chars[it], values[it])
        }
    }

    @Test
    fun testCharArray() {
        val struct = "1i@0[c]"
        val aint = 3
        val charArray: CharArray = charArrayOf('a', 'b', 'c')
        val bytes = JStruct.pack(struct, listOf(aint, charArray))
        println("bytes:${HexUtil.bytesToHexSpace(bytes)}")
        val values = JStruct.unpack(struct, bytes)

        assertEquals(aint, values[0])
        assert(charArray.contentEquals(values[1] as CharArray))
    }

    @Test
    fun testStringType() {
        val struct = "1i@-1s"
        val s = "0123456789"
        val aint = s.toByteArray(JStruct.charset).size

        val bytes = JStruct.pack(struct, listOf(aint, s))
        println("bytes:${HexUtil.bytesToHexSpace(bytes)}")
        val byteBuffer = ByteBuffer.wrap(bytes)
        val aint0 = byteBuffer.getInt()
        val s0 = byteBuffer.readString(aint0, JStruct.charset)

        assertEquals(s, s0)
        assertEquals(aint, aint0)

        val elements = JStruct.unpack(struct, bytes)

        assertEquals(aint, elements[0])
        assertEquals(s, elements[1])
    }

    @Test
    fun testComplexArrayType() {
        val struct = "1i2[1i10s]"
        val s = "0123456789"
        val slen: Int = s.toByteArray(JStruct.charset).size
        val complex = listOf<Any>(slen, s)
        val complexArray: List<Any> = listOf(complex, complex)

        val clen: Int = 2
        val elements: List<Any> = listOf(clen, complexArray)
        val bytes = JStruct.pack(struct, elements)
        println("bytes:${HexUtil.bytesToHexSpace(bytes)}")

        val unpackElements = JStruct.unpack(struct, bytes)

        assertEquals(clen, unpackElements[0])

        val unpackComplexArray = unpackElements[1] as List<Any>
        val unpackComplex = unpackComplexArray[0] as List<Any>

        for (i in complex.indices) {
            assertEquals(complex[i], unpackComplex[i])
        }

    }

    @Test
    fun testComplexType() {
        val struct = "1i2{1i10s}"
        val s = "0123456789"
        val slen: Int = s.toByteArray(JStruct.charset).size
        val complex = listOf<Any>(slen, s)

        val clen: Int = 2
        val elements: List<Any> = listOf(clen, complex, complex)
        val bytes = JStruct.pack(struct, elements)
        println("bytes:${HexUtil.bytesToHexSpace(bytes)}")

        val unpackElements = JStruct.unpack(struct, bytes)

        assertEquals(clen, unpackElements[0])

        val unpackComplex = unpackElements[1] as List<Any>
        val unpackComplex2 = unpackElements[2] as List<Any>

        for (i in complex.indices) {
            assertEquals(complex[i], unpackComplex[i])
            assertEquals(complex[i], unpackComplex2[i])
        }

    }

    @Test
    fun testComplex() {
        val struct = "1{4i}1b1B1h1H1i1I1l1f1d1i@-1[b]1i@-1[h]1i@-1[i]"
        val head = listOf<Any>(0x01, 0x02, 0x03, 0x04)

        val aByte: Byte = 0
        val aUByte: Short = 0

        val aShort: Short = 0
        val aUShort: Int = 0
        val aInt: Int = 0
        val aUInt: Long = 0L

        val aLong: Long = 0L
        val aFloat: Float = 0.0f
        val aDouble: Double = 0.0

        val byteArrayLen: Int = 2

        val byteArray: ByteArray = byteArrayOf(0x01, 0x02)

        val shortArrayLen: Int = 2

        val shortArray: ShortArray = shortArrayOf(0x7f01, 0x7f02)

        val intArrayLen: Int = 2

        val intArray: IntArray = intArrayOf(0x01020304, 0x07060504)

        val elements = listOf<Any>(
            head, aByte, aUByte, aShort, aUShort, aInt, aUInt, aLong, aFloat, aDouble,
            byteArrayLen, byteArray, shortArrayLen, shortArray, intArrayLen, intArray
        )

        val bytes = JStruct.pack(struct, elements)

        println("bytes:${HexUtil.bytesToHexSpace(bytes)}")

        val unpackElements = JStruct.unpack(struct, bytes)

        val unpackBytes = JStruct.pack(struct, unpackElements)

        assert(unpackBytes.contentEquals(bytes))

    }

    @Test
    fun testComplex2() {
        val struct = "1{1b1b1i1i}1i@-1[1b1B1h1H1i1I1l1f1d]1b1{1b1B1h1H1i1I1l1f1d}"

        val head = listOf<Any>(0x01.toByte(), 0x02.toByte(), 0x01020304, 0x05060708)

        val complexLen: Int = 2
        val complex = listOf<Any>(
            0x01.toByte(), 0xff.toByte().toShort(),
            0x40ff.toShort(), 0xff40,
            0x1f2f3f4f, 0xf1f2f3f4L,
            0x1a2a3a4a5a6a7a8aL, 0.1f, 0.2
        )

        val aByte: Byte = 0x18
        val elements = listOf<Any>(head, complexLen, listOf<Any>(complex, complex), aByte, complex)

        val bytes = JStruct.pack(struct,elements)
        println("bytes:${HexUtil.bytesToHexSpace(bytes)}")

        val unpackElements = JStruct.unpack(struct,bytes)
        val unpackBytes = JStruct.pack(struct,unpackElements)

        assert(bytes.contentEquals(unpackBytes))
    }


}