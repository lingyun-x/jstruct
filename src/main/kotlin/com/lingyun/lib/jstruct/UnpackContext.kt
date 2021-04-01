package com.lingyun.lib.jstruct

import com.lingyun.lib.jstruct.exception.ExpressionException
import com.lingyun.lib.jstruct.extension.*
import java.nio.ByteBuffer

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
class UnpackContext(
    struct: String,
    val byteBuffer: ByteBuffer,
    val elements: MutableList<Any> = ArrayList(),
    ctx: NumberEnvironmentExpressionParser.ExpressionContext = NumberEnvironmentExpressionParser.ExpressionContext(
        struct,
        0,
        struct.length,
        elements
    )
) : JStructContext(struct, ctx) {


    fun unpack(): List<Any> {
        while (ctx.expressionStartIndex < ctx.expressionEndIndex) {
            getNextElement()
        }
        return elements
    }

    fun getNextElement(): List<Any> {
        val number = getNextNumber()
        val type = getNextType()
        val values = readData(type, number)

        values.forEach {
            addElement(it)
        }
        return values
    }

    fun addElement(element: Any) {
        elements.add(element)
        ctx.currentElementIndex++
    }

    fun readData(dataType: IStrcutDataType, number: Int): List<Any> {
        val result = ArrayList<Any>()
        when (dataType) {
            is BasicDataType -> {
                for (i in 0 until number) {
                    val value = readBasicData(dataType.type)
                    result.add(value)
                }
            }
            is StringDataType -> {
                val value = readString(number)
                result.add(value)
            }
            is ArrayDataType -> {
                when (dataType.componentType) {
                    'b' -> {
                        val value = byteBuffer.readByteArray(number)
                        result.add(value)
                    }
                    'B' -> {
                        val value = byteBuffer.readUByteArray(number)
                        result.add(value)
                    }
                    'c' -> {
                        val value = byteBuffer.readCharArray(number)
                        result.add(value)
                    }
                    'h' -> {
                        val value = byteBuffer.readShortArray(number)
                        result.add(value)
                    }
                    'H' -> {
                        val value = byteBuffer.readUShortArray(number)
                        result.add(value)
                    }
                    'i' -> {
                        val value = byteBuffer.readIntArray(number)
                        result.add(value)
                    }
                    'I' -> {
                        val value = byteBuffer.readUIntArray(number)
                        result.add(value)
                    }
                    'l' -> {
                        val value = byteBuffer.readLongArray(number)
                        result.add(value)
                    }
                    'f' -> {
                        val value = byteBuffer.readFloatArray(number)
                        result.add(value)
                    }
                    'd' -> {
                        val value = byteBuffer.readDoubleArray(number)
                        result.add(value)
                    }
                    else -> {
                        throw IllegalArgumentException("not support this type:${dataType.componentType}")
                    }
                }
            }
            is ComplexDataType -> {
                val complexStruct = ctx.expression.substring(dataType.structStartIndex, dataType.structEndIndex)
                val values = ArrayList<Any>()

                for (i in 0 until number) {
                    val unpackContext = UnpackContext(complexStruct, byteBuffer)
                    val vs = unpackContext.unpack()
                    values.add(vs)
                }

                result.add(values)
            }
            else -> {
                throw IllegalArgumentException("not support this type:${dataType::class}")
            }
        }

        return result
    }

    fun readString(number: Int): String {
        val bytes = ByteArray(number)
        byteBuffer.get(bytes)
        return String(bytes, JStruct.charset)
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
            'c' -> {
                byteBuffer.getChar()
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


}