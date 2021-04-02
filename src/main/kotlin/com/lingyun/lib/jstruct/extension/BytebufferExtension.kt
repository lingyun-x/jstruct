package com.lingyun.lib.jstruct.extension

import java.nio.ByteBuffer
import java.nio.charset.Charset

/*
* Created by mc_luo on 2021/3/30 .
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
fun ByteBuffer.readString(byteNumber: Int, charset: Charset): String {
    val byteArray = readByteArray(byteNumber)
    return String(byteArray, charset)
}

fun ByteBuffer.readByteArray(size: Int): ByteArray {
    val bytes = ByteArray(size)
    get(bytes)
    return bytes
}

fun ByteBuffer.readUByteArray(size: Int): ShortArray {
    val bytes = ByteArray(size)
    get(bytes)
    return bytes.map { it.toUByte().toShort() }.toShortArray()
}

fun ByteBuffer.readCharArray(size: Int): CharArray {
    val chars = CharArray(size)
    for (i in 0 until size) {
        chars[i] = getChar()
    }
    return chars
}

fun ByteBuffer.readShortArray(size: Int): ShortArray {
    val shorts = ShortArray(size)

    for (i in 0 until size) {
        val h = getShort()
        shorts[i] = h
    }
    return shorts
}

fun ByteBuffer.readUShortArray(size: Int): IntArray {
    val shorts = IntArray(size)
    for (i in 0 until size) {
        val h = getShort()
        shorts[i] = h.toUShort().toInt()
    }
    return shorts
}

fun ByteBuffer.readIntArray(size: Int): IntArray {
    val ints = IntArray(size)
    for (i in 0 until size) {
        val values = getInt()
        ints[i] = values
    }
    return ints
}

fun ByteBuffer.readUIntArray(size: Int): LongArray {
    val longs = LongArray(size)
    for (i in 0 until size) {
        val l = getInt().toUInt().toLong()
        longs[i] = l
    }
    return longs
}

fun ByteBuffer.readLongArray(size: Int): LongArray {
    val longs = LongArray(size)
    for (i in 0 until size) {
        val l = getLong()
        longs[i] = l
    }
    return longs
}

fun ByteBuffer.readFloatArray(size: Int): FloatArray {
    val fs = FloatArray(size)
    for (i in 0 until size) {
        val f = getFloat()
        fs[i] = f
    }
    return fs
}

fun ByteBuffer.readDoubleArray(size: Int): DoubleArray {
    val ds = DoubleArray(size)
    for (i in 0 until size) {
        val d = getDouble()
        ds[i] = d
    }
    return ds
}

fun ByteBuffer.writeString(s: String, charset: Charset) {
    val bytes = s.toByteArray(charset)
    writeByteArray(bytes)
}

fun ByteBuffer.writeByteArray(value: Any) {
    val bytes = value as ByteArray
    put(bytes)
}

fun ByteBuffer.writeUByteArray(value: Any) {
    val bytes = (value as ShortArray).map { it.toByte() }.toByteArray()
    put(bytes)
}

fun ByteBuffer.writeCharArray(value: Any) {
    val chars = value as CharArray
    for (c in chars) {
        putChar(c)
    }
}

fun ByteBuffer.writeShortArray(value: Any) {
    val shorts = value as ShortArray
    for (s in shorts) {
        putShort(s)
    }
}

fun ByteBuffer.writeUnsignShortArray(value: Any) {
    val shorts = (value as IntArray).map { it.toShort() }
    for (s in shorts) {
        putShort(s)
    }
}

fun ByteBuffer.writeIntArray(value: Any) {
    val ints = value as IntArray
    for (i in ints) {
        putInt(i)
    }
}

fun ByteBuffer.writeUIntArray(value: Any) {
    val ints = (value as LongArray).map { it.toInt() }
    for (i in ints) {
        putInt(i)
    }
}

fun ByteBuffer.writeLongArray(value: Any) {
    val longs = value as LongArray
    for (l in longs) {
        putLong(l)
    }
}

fun ByteBuffer.writeFloatArray(value: Any) {
    val fs = value as FloatArray
    for (f in fs) {
        putFloat(f)
    }
}

fun ByteBuffer.writeDoubleArray(value: Any) {
    val ds = value as DoubleArray
    for (d in ds) {
        putDouble(d)
    }
}

