package com.lingyun.lib.jstruct

import com.lingyun.lib.jstruct.exception.ExpressionException
import java.nio.ByteBuffer

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
class PackContext(
    val struct: String,
    val byteBuffer: ByteBuffer,
    val elements: MutableList<Any> = ArrayList(),
    var currentElementIndex: Int = 0
) {
    val ctx: NumberEnvironmentExpressionParser.ExpressionContext

    init {
        ctx = NumberEnvironmentExpressionParser.ExpressionContext(struct, 0, struct.length, elements)
    }

    fun unpack(): List<Any> {
        val result = ArrayList<Any>()
        while (ctx.expressionStartIndex < ctx.expressionEndIndex) {
            val number = getNextNumber()
            val type = getNextType()
            val values = readData(type, number)
            result.addAll(values)
        }

        return result
    }

    fun pack(): ByteArray {
        while (ctx.expressionStartIndex < ctx.expressionEndIndex) {
            val number = getNextNumber()
            val type = getNextType()

            write(type, number)
        }
        return byteBuffer.array().sliceArray(0 until byteBuffer.position())
    }

    fun getNextNumber(): Int {
        ctx.currentElementIndex = currentElementIndex
        val expressionEndIndex = ctx.expressionEndIndex

        //find number expression
        var eei = ctx.expressionStartIndex
        while (eei < ctx.expressionEndIndex) {
            when (ctx.expression[eei]) {
                in '0'..'9', '(', ')', '$', '+', '-', '*', '/' -> {
                    eei++
                }
                else -> {
                    break
                }
            }
        }

        ctx.expressionEndIndex = eei
        val result = ctx.getNumber().toInt()

        ctx.expressionEndIndex = expressionEndIndex
        return result
    }

    fun getNextType(): IStrcutDataType {
        val c = ctx.expression[ctx.expressionStartIndex]
        return when (c) {
            'b', 'B', 'h', 'H', 'i', 'I', 'l', 'f', 'd' -> {
                ctx.expressionStartIndex++
                BasicDataType(c)
            }
            '[' -> {
                ctx.expressionStartIndex++
                val endIndex = StringUtil.findClosingCharIndex(
                    ctx.expression,
                    ctx.expressionStartIndex,
                    ctx.expressionEndIndex,
                    '[',
                    ']'
                )
                println("expression [${ctx.expressionStartIndex}-${ctx.expressionEndIndex}]")
                println("[${ctx.expressionStartIndex}-$endIndex]")
                if (endIndex == -1){
                    throw ExpressionException("index:${ctx.expressionStartIndex-1} char [ not find closing char ]")
                }

                val typeExpression = ctx.expression.substring(ctx.expressionStartIndex, endIndex).trim()
                println("typeExpression:${typeExpression}")
                if (typeExpression.isEmpty()) {
                    throw ExpressionException("index:${ctx.expressionStartIndex} array must have a type")
                }

                if (typeExpression.length == 1) {
                    ctx.expressionStartIndex = endIndex + 1
                    return ArrayDataType(typeExpression[0])
                }
                val type = ComplexDataType(typeExpression, ctx.expressionStartIndex, endIndex)
                ctx.expressionStartIndex = endIndex + 1
                return type
            }
            else -> {
                throw ExpressionException("index:${ctx.expressionStartIndex} not supprt this typs:${c}")
            }
        }
    }

    fun addElement(element: Any) {
        elements.add(element)
        currentElementIndex++
    }


    fun readData(dataType: IStrcutDataType, number: Int): List<Any> {
        val result = ArrayList<Any>()
        when (dataType) {
            is BasicDataType -> {
                for (i in 0 until number) {
                    val value = readBasicData(dataType.type)
                    addElement(value)
                    result.add(value)
                }
            }
            is ArrayDataType -> {
                when (dataType.itemType) {
                    'b' -> {
                        val value = readByteArray(number)
                        addElement(value)
                        result.add(value)
                    }
                    'B' -> {
                        val value = readUByteArray(number)
                        addElement(value)
                        result.add(value)
                    }
                    'h' -> {
                        val value = readShortArray(number)
                        addElement(value)
                        result.add(value)
                    }
                    'H' -> {
                        val value = readUnsignShortArray(number)
                        addElement(value)
                        result.add(value)
                    }
                    'i' -> {
                        val value = readIntArray(number)
                        addElement(value)
                        result.add(value)
                    }
                    'I' -> {
                        val value = readUIntArray(number)
                        addElement(value)
                        result.add(value)
                    }
                    'l' -> {
                        val value = readLongArray(number)
                        addElement(value)
                        result.add(value)
                    }
                    'f' -> {
                        val value = readFloatArray(number)
                        addElement(value)
                        result.add(value)
                    }
                    'd' -> {
                        val value = readDoubleArray(number)
                        addElement(value)
                        result.add(value)
                    }
                    else -> {
                        throw IllegalArgumentException("not support this type:${dataType.itemType}")
                    }
                }
            }
            is ComplexDataType -> {
                val endIndex = ctx.expressionEndIndex
                val startIndex = ctx.expressionStartIndex

                val elementStartIndex = ctx.elementStartIndex


                for (i in 0 until number) {
                    ctx.expressionStartIndex = dataType.structStartIndex
                    ctx.expressionEndIndex = dataType.structEndIndex

                    ctx.elementStartIndex = currentElementIndex
                    val values = unpack()
                    result.addAll(values)
                }

                ctx.elementStartIndex = elementStartIndex
                ctx.expressionStartIndex = startIndex
                ctx.expressionEndIndex = endIndex
            }
            else -> {
                throw IllegalArgumentException("not support this type:${dataType::class}")
            }
        }

        return result
    }

    fun readBasicData(type: Char): Any {
        return when (type) {
            //byte
            'b' -> {
                byteBuffer.get()
            }
            //
            'B' -> {
                byteBuffer.get().toUByte().toShort()
            }
            'h' -> {
                byteBuffer.getShort()
            }
            'H' -> {
                byteBuffer.getShort().toUShort().toInt()
            }
            'i' -> {
                byteBuffer.getInt()
            }
            'I' -> {
                byteBuffer.getInt().toUInt().toLong()
            }
            'l' -> {
                byteBuffer.getLong()
            }
            'f' -> {
                byteBuffer.getFloat()
            }
            'd' -> {
                byteBuffer.getDouble()
            }
            else -> {
                throw ExpressionException("not support this type:$type")
            }
        }
    }

    fun readByteArray(size: Int): ByteArray {
        val bytes = ByteArray(size)
        byteBuffer.get(bytes)
        return bytes
    }

    fun readUByteArray(size: Int): ShortArray {
        val bytes = ByteArray(size)
        byteBuffer.get(bytes)
        return bytes.map { it.toUByte().toShort() }.toShortArray()
    }

    fun readShortArray(size: Int): ShortArray {
        val shorts = ShortArray(size)

        for (i in 0 until size) {
            val h = byteBuffer.getShort()
            shorts[i] = h
        }
        return shorts
    }

    fun readUnsignShortArray(size: Int): IntArray {
        val shorts = IntArray(size)
        for (i in 0 until size) {
            val h = byteBuffer.getShort()
            shorts[i] = h.toUShort().toInt()
        }
        return shorts
    }

    fun readIntArray(size: Int): IntArray {
        val ints = IntArray(size)
        for (i in 0 until size) {
            val i = byteBuffer.getInt()
            ints[i] = i
        }
        return ints
    }

    fun readUIntArray(size: Int): LongArray {
        val ints = LongArray(size)
        for (i in 0 until size) {
            val l = byteBuffer.getInt().toUInt().toLong()
            ints[i] = l
        }
        return ints
    }

    fun readLongArray(size: Int): LongArray {
        val longs = LongArray(size)
        for (i in 0 until size) {
            val l = byteBuffer.getLong()
            longs[i] = l
        }
        return longs
    }

    fun readFloatArray(size: Int): FloatArray {
        val fs = FloatArray(size)
        for (i in 0 until size) {
            val f = byteBuffer.getFloat()
            fs[i] = f
        }
        return fs
    }

    fun readDoubleArray(size: Int): DoubleArray {
        val ds = DoubleArray(size)
        for (i in 0 until size) {
            val d = byteBuffer.getDouble()
            ds[i] = d
        }
        return ds
    }

    fun write(type: IStrcutDataType, number: Int) {
        when (type) {
            is BasicDataType -> {
                for (i in 0 until number) {
                    writeBasicData(type.type, elements[currentElementIndex++])
                }
            }
            is ArrayDataType -> {
                when (type.itemType) {
                    //byte
                    'b' -> {
                        writeByteArray(elements[currentElementIndex++])
                    }
                    //
                    'B' -> {
                        writeUByteArray(elements[currentElementIndex++])
                    }
                    'h' -> {
                        writeShortArray(elements[currentElementIndex++])
                    }
                    'H' -> {
                        writeUnsignShortArray(elements[currentElementIndex++])
                    }
                    'i' -> {
                        writeIntArray(elements[currentElementIndex++])
                    }
                    'I' -> {
                        writeUIntArray(elements[currentElementIndex++])
                    }
                    'l' -> {
                        writeLongArray(elements[currentElementIndex++])
                    }
                    'f' -> {
                        writeFloatArray(elements[currentElementIndex++])
                    }
                    'd' -> {
                        writeDoubleArray(elements[currentElementIndex++])
                    }
                    else -> {
                        throw ExpressionException("not support this type:$type")
                    }
                }
            }
            is ComplexDataType -> {
                val endIndex = ctx.expressionEndIndex
                val startIndex = ctx.expressionStartIndex
                val esi = ctx.elementStartIndex

                for (i in 0 until number) {
                    ctx.expressionEndIndex = type.structEndIndex
                    ctx.expressionStartIndex = type.structStartIndex
                    ctx.elementStartIndex = currentElementIndex
                    pack()
                }

                ctx.elementStartIndex = esi
                ctx.expressionStartIndex = startIndex
                ctx.expressionEndIndex = endIndex
            }
        }
    }

    fun writeBasicData(type: Char, value: Any) {
        when (type) {
            //byte
            'b' -> {
                byteBuffer.put(value as Byte)
            }
            //
            'B' -> {
                byteBuffer.put((value as Short).toByte())
            }
            'h' -> {
                byteBuffer.putShort((value as Short))
            }
            'H' -> {
                byteBuffer.putShort((value as Int).toShort())
            }
            'i' -> {
                byteBuffer.putInt(value as Int)
            }
            'I' -> {
                byteBuffer.putInt((value as Long).toInt())
            }
            'l' -> {
                byteBuffer.putLong(value as Long)
            }
            'f' -> {
                byteBuffer.putFloat(value as Float)
            }
            'd' -> {
                byteBuffer.putDouble(value as Double)
            }
            else -> {
                throw ExpressionException("not support this type:$type")
            }
        }
    }

    fun writeByteArray(value: Any) {
        val bytes = value as ByteArray
        byteBuffer.put(bytes)
    }

    fun writeUByteArray(value: Any) {
        val bytes = (value as ShortArray).map { it.toByte() }.toByteArray()
        byteBuffer.put(bytes)
    }

    fun writeShortArray(value: Any) {
        val shorts = value as ShortArray
        for (s in shorts) {
            byteBuffer.putShort(s)
        }
    }

    fun writeUnsignShortArray(value: Any) {
        val shorts = (value as IntArray).map { it.toShort() }
        for (s in shorts) {
            byteBuffer.putShort(s)
        }
    }

    fun writeIntArray(value: Any) {
        val ints = value as IntArray
        for (i in ints) {
            byteBuffer.putInt(i)
        }
    }

    fun writeUIntArray(value: Any) {
        val ints = (value as LongArray).map { it.toInt() }
        for (i in ints) {
            byteBuffer.putInt(i)
        }
    }

    fun writeLongArray(value: Any) {
        val longs = value as LongArray
        for (l in longs) {
            byteBuffer.putLong(l)
        }
    }

    fun writeFloatArray(value: Any) {
        val fs = value as FloatArray
        for (f in fs) {
            byteBuffer.putFloat(f)
        }
    }

    fun writeDoubleArray(value: Any) {
        val ds = value as DoubleArray
        for (d in ds) {
            byteBuffer.putDouble(d)
        }
    }
}