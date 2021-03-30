package com.lingyun.lib.jstruct.sample

import com.lingyun.lib.jstruct.HexUtil
import com.lingyun.lib.jstruct.JStrcut
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.ByteBuffer


/*
* Created by mc_luo on 2021/3/29 .
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
internal class BasicTypeSampleTest {

    @Test
    fun testBasicType() {
        val struct = "1b1B1h1H1i1I1l1f1d"
        val aByte: Byte = 0x01
        val aUByte: Short = 0xff
        val aShort: Short = 0x0102.toShort()
        val aUShort: Int = 0xff01
        val aInt: Int = 0x01020304
        val aUInt: Long = 0xf0020304
        val along: Long = 0x01020304f1f2f3f4
        val afloat: Float = 2.1f
        val adouble: Double = 2.2

        val elements = listOf<Any>(aByte, aUByte, aShort, aUShort, aInt, aUInt, along, afloat, adouble)
        val bytes = JStrcut.newPackContext(struct, elements).pack()
        println("bytes:${HexUtil.bytesToHexSpace(bytes)}")


        val results = JStrcut.newUnPackContext(struct, bytes).unpack()

        for (i in 0 until elements.size - 1) {
            assertEquals(elements[i], results[i])
        }

        val adouble2 = results.get(results.size - 1) as Double
        assertEquals(adouble, adouble2, 0.00)
    }

    @Test
    fun testByteArray() {
        val struct = "1i10[b]"

        val len = 10
        val bs = (0..9).toList().map { it.toByte() }.toByteArray()
        val elements = listOf<Any>(len, bs)
        val result = JStrcut.newPackContext(struct, elements).pack()

        val byteBuffer = ByteBuffer.wrap(result)
        val i1 = byteBuffer.getInt()
        assertEquals(i1, len)

        for (i in 0..9) {
            val b = byteBuffer.get()
            assertEquals(b, i.toByte())
        }

        println("bytes:${HexUtil.bytesToHexSpace(result)}")

        val elements2 = JStrcut.newUnPackContext(struct, result).unpack()

        val aint = elements2.get(0) as Int
        val abytearray = elements2.get(1) as ByteArray

        assertEquals(aint, len)

        for (i in bs.indices) {
            assertEquals(abytearray[i], bs[i])
        }
    }

    @Test
    fun testShortArray() {
        val struct = "1i10[h]"

        val len = 10
        val hs = (0..9).toList().map { it.toShort() }.toShortArray()
        val elements = listOf<Any>(len, hs)
        val result = JStrcut.newPackContext(struct, elements).pack()

        val byteBuffer = ByteBuffer.wrap(result)
        val i1 = byteBuffer.getInt()
        assertEquals(i1, len)

        for (i in 0..9) {
            val b = byteBuffer.getShort()
            assertEquals(b, i.toShort())
        }

        println("bytes:${HexUtil.bytesToHexSpace(result)}")

        val elements2 = JStrcut.newUnPackContext(struct, result).unpack()

        val aint = elements2.get(0) as Int
        val ashortarray = elements2.get(1) as ShortArray

        assertEquals(aint, len)

        for (i in hs.indices) {
            assertEquals(ashortarray[i], hs[i])
        }
    }

    fun testIntArray() {

    }

    fun testLongArray() {

    }

    fun testFloatArray() {

    }

    fun testDoubleArray() {

    }

    fun testDynamicArray() {

    }


    @Test
    fun testComplexType() {
        val struct = "2[1b1i2[1h1f1d]]"

        val complex1 = ArrayList<Any>()
        val complex2 = ArrayList<Any>()

        val aShort: Short = 0x0102
        val aFloat: Float = 100.1f
        val aDouble: Double = 20.02

        val complex20 = ArrayList<Any>()
        complex20.add(aShort)
        complex20.add(aFloat)
        complex20.add(aDouble)

        complex2.add(complex20)
        complex2.add(complex20)

        val aByte: Byte = 0x0f
        val aint: Int = 0x01020304

        val complex10 = ArrayList<Any>()

        complex10.add(aByte)
        complex10.add(aint)
        complex10.add(complex2)

        complex1.add(complex10)
        complex1.add(complex10)

        val elements = ArrayList<Any>()
        elements.add(complex1)

        val bytes = JStrcut.newPackContext(struct, elements).pack()

        println("bytes:${HexUtil.bytesToHexSpace(bytes)}")

        val es = JStrcut.newUnPackContext(struct, bytes).unpack()

        val c1 = es.get(0) as List<Any>
        val c10 = c1.get(0) as List<Any>

        assertEquals(c10.get(0), aByte)
        assertEquals(c10.get(1), aint)

        val c2 = c10.get(2) as List<Any>
        val c20 = c2.get(0) as List<Any>
        val c21 = c2.get(1) as List<Any>
        assertEquals(c20.get(0), aShort)
        assertEquals(c20.get(1), aFloat)
        assertEquals(c20.get(2), aDouble)

        assertEquals(c21.get(0), aShort)
        assertEquals(c21.get(1), aFloat)
        assertEquals(c21.get(2) as Double, aDouble, 0.000000001)

//        val cc1 = c1.get(1)
//
//        assertEquals(cc1.get(0), aByte)
//        assertEquals(cc1.get(1), aint)
//
//        val c12 = cc1.get(2) as List<Any>
//        val c120 = c12.get(0) as List<Any>
//        val c121 = c12.get(1) as List<Any>
//        assertEquals(c120.get(0), aShort)
//        assertEquals(c120.get(1), aFloat)
//        assertEquals(c120.get(2), aDouble)
//
//        assertEquals(c121.get(0), aShort)
//        assertEquals(c121.get(1), aFloat)
//        assertEquals(c121.get(2) as Double, aDouble, 0.000000001)
    }


    @Test
    fun testDynamicComplexType() {
        val struct = "1b@0[1b1i@1[1h1f1d]]"

        val ab :Byte = 0x02

        val complex1 = ArrayList<Any>()
        val complex2 = ArrayList<Any>()

        val aShort: Short = 0x0102
        val aFloat: Float = 100.1f
        val aDouble: Double = 20.02

        val complex20 = ArrayList<Any>()
        complex20.add(aShort)
        complex20.add(aFloat)
        complex20.add(aDouble)

        complex2.add(complex20)
        complex2.add(complex20)

        val aByte: Byte = 0x0f
        val aint: Int = 0x02

        val complex10 = ArrayList<Any>()

        complex10.add(aByte)
        complex10.add(aint)
        complex10.add(complex2)

        complex1.add(complex10)
        complex1.add(complex10)

        val elements = ArrayList<Any>()
        elements.add(ab)
        elements.add(complex1)

        val bytes = JStrcut.newPackContext(struct, elements).pack()

        println("bytes:${HexUtil.bytesToHexSpace(bytes)}")

        val es = JStrcut.newUnPackContext(struct, bytes).unpack()

        val c1 = es.get(1) as List<Any>
        val c10 = c1.get(0) as List<Any>

        assertEquals(c10.get(0), aByte)
        assertEquals(c10.get(1), aint)

        val c2 = c10.get(2) as List<Any>
        val c20 = c2.get(0) as List<Any>
        val c21 = c2.get(1) as List<Any>
        assertEquals(c20.get(0), aShort)
        assertEquals(c20.get(1), aFloat)
        assertEquals(c20.get(2), aDouble)

        assertEquals(c21.get(0), aShort)
        assertEquals(c21.get(1), aFloat)
        assertEquals(c21.get(2) as Double, aDouble, 0.000000001)

//        val cc1 = c1.get(1)
//
//        assertEquals(cc1.get(0), aByte)
//        assertEquals(cc1.get(1), aint)
//
//        val c12 = cc1.get(2) as List<Any>
//        val c120 = c12.get(0) as List<Any>
//        val c121 = c12.get(1) as List<Any>
//        assertEquals(c120.get(0), aShort)
//        assertEquals(c120.get(1), aFloat)
//        assertEquals(c120.get(2), aDouble)
//
//        assertEquals(c121.get(0), aShort)
//        assertEquals(c121.get(1), aFloat)
//        assertEquals(c121.get(2) as Double, aDouble, 0.000000001)
    }

    @Test
    fun testDynamicComplexType2() {
        val struct = "1b@0[1b1i@-1[1h1f1d]]"

        val complex1 = ArrayList<Any>()
        val complex2 = ArrayList<Any>()

        val aShort: Short = 0x0102
        val aFloat: Float = 100.1f
        val aDouble: Double = 20.02

        complex2.add(aShort)
        complex2.add(aFloat)
        complex2.add(aDouble)

        val aByte: Byte = 0x01
        val aint: Int = 0x02

        complex1.add(aByte)
        complex1.add(aint)
        complex1.addAll(complex2)
        complex1.addAll(complex2)

        val elements = ArrayList<Any>()
        val c1Len: Byte = 0x02
        elements.add(c1Len)
        elements.addAll(complex1)
        elements.addAll(complex1)

        val bytes = JStrcut.newPackContext(struct, elements).pack()

        println("bytes:${HexUtil.bytesToHexSpace(bytes)}")

        val es = JStrcut.newUnPackContext(struct, bytes).unpack()

        val c1Len0 = es.get(0) as Byte

        //complex 1
        //loop0
        val aByte0 = es.get(1) as Byte
        val aInt1 = es.get(2) as Int
        //complex 2
        //loop 0
        val aShort2 = es.get(3) as Short
        val aFloat3 = es.get(4) as Float
        val aDouble4 = es.get(5) as Double
        //loop 1
        val aShort5 = es.get(6) as Short
        val aFloat6 = es.get(7) as Float
        val aDouble7 = es.get(8) as Double

        //complex 1
        //loop1
        val aByte10 = es.get(9) as Byte
        val aInt11 = es.get(10) as Int
        //complex 2
        //loop 0
        val aShort12 = es.get(11) as Short
        val aFloat13 = es.get(12) as Float
        val aDouble14 = es.get(13) as Double
        //loop 1
        val aShort15 = es.get(14) as Short
        val aFloat16 = es.get(15) as Float
        val aDouble17 = es.get(16) as Double

        assertEquals(c1Len, c1Len0)

        assertEquals(aByte, aByte0)
        assertEquals(aByte, aByte10)
        assertEquals(aint, aInt1)
        assertEquals(aint, aInt11)

        assertEquals(aShort, aShort2)
        assertEquals(aShort, aShort5)

        assertEquals(aShort, aShort12)
        assertEquals(aShort, aShort15)

        assertEquals(aFloat, aFloat3)
        assertEquals(aFloat, aFloat6)
        assertEquals(aFloat, aFloat13)
        assertEquals(aFloat, aFloat16)

        assertEquals(aDouble, aDouble4, 0.000000001)
        assertEquals(aDouble, aDouble7, 0.000000001)
        assertEquals(aDouble, aDouble14, 0.000000001)
        assertEquals(aDouble, aDouble17, 0.000000001)
    }
}