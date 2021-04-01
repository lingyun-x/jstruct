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
    fun testComplexType() {
        val struct ="1i2[1i10s]"
        val s = "0123456789"
        val slen :Int = s.toByteArray(JStruct.charset).size
        val complex = listOf<Any>(slen,s)
        val complexArray :List<Any> = listOf(complex,complex)

        val clen :Int= 2
        val elements:List<Any> = listOf(clen,complexArray)
        val bytes = JStruct.pack(struct,elements)
        println("bytes:${HexUtil.bytesToHexSpace(bytes)}")

        val unpackElements  = JStruct.unpack(struct,bytes)

        assertEquals(clen,unpackElements[0])

        val unpackComplexArray = unpackElements[1] as List<Any>
        val unpackComplex = unpackComplexArray[0] as List<Any>

        for(i in complex.indices){
            assertEquals(complex[i],unpackComplex[i])
        }

    }


}