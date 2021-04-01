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
class PackContext(
    val struct: String,
    val byteBuffer: ByteBuffer,
    val elements: List<Any> = ArrayList(),
) {
    val ctx: NumberEnvironmentExpressionParser.ExpressionContext

    init {
        ctx = NumberEnvironmentExpressionParser.ExpressionContext(struct, 0, struct.length, elements)
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
        val expressionEndIndex = ctx.expressionEndIndex

        //find number expression
        var eei = ctx.expressionStartIndex
        while (eei < ctx.expressionEndIndex) {
            when (ctx.expression[eei]) {
                in '0'..'9', '(', ')', '@', '+', '-', '*', '/' -> {
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
            'b', 'B', 'c', 'h', 'H', 'i', 'I', 'l', 'f', 'd' -> {
                ctx.expressionStartIndex++
                BasicDataType(c)
            }
            's' -> {
                ctx.expressionStartIndex++
                StringDataType()
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
                if (endIndex == -1) {
                    throw ExpressionException("index:${ctx.expressionStartIndex - 1} char [ not find closing char ]")
                }

                val typeExpression = ctx.expression.substring(ctx.expressionStartIndex, endIndex).trim()
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

    fun write(type: IStrcutDataType, number: Int) {
        when (type) {
            is BasicDataType -> {
                for (i in 0 until number) {
                    writeBasicData(type.type, elements[ctx.currentElementIndex++])
                }
            }
            is StringDataType -> {
                writeString(elements[ctx.currentElementIndex++] as String, number)
            }
            is ArrayDataType -> {
                when (type.componentType) {
                    //byte
                    'b' -> {
                        byteBuffer.writeByteArray(elements[ctx.currentElementIndex++])
                    }
                    //
                    'B' -> {
                        byteBuffer.writeUByteArray(elements[ctx.currentElementIndex++])
                    }
                    'c' -> {
                        byteBuffer.writeCharArray(elements[ctx.currentElementIndex++])
                    }
                    'h' -> {
                        byteBuffer.writeShortArray(elements[ctx.currentElementIndex++])
                    }
                    'H' -> {
                        byteBuffer.writeUnsignShortArray(elements[ctx.currentElementIndex++])
                    }
                    'i' -> {
                        byteBuffer.writeIntArray(elements[ctx.currentElementIndex++])
                    }
                    'I' -> {
                        byteBuffer.writeUIntArray(elements[ctx.currentElementIndex++])
                    }
                    'l' -> {
                        byteBuffer.writeLongArray(elements[ctx.currentElementIndex++])
                    }
                    'f' -> {
                        byteBuffer.writeFloatArray(elements[ctx.currentElementIndex++])
                    }
                    'd' -> {
                        byteBuffer.writeDoubleArray(elements[ctx.currentElementIndex++])
                    }
                    else -> {
                        throw ExpressionException("not support this type:$type")
                    }
                }
            }
            is ComplexDataType -> {
                val complexStruct = ctx.expression.substring(type.structStartIndex, type.structEndIndex)
                val complexElementList = elements[ctx.currentElementIndex++] as List<Any>

                for (i in 0 until number) {
                    val complexElements = complexElementList[i] as MutableList<Any>
                    val packContext2 = PackContext(complexStruct, byteBuffer, complexElements)
                    packContext2.pack()
                }
            }
        }
    }

    fun writeString(value: String, number: Int) {
        val bytes = (value as String).toByteArray(JStruct.charset)
        if (bytes.size != number) {
            throw ExpressionException("string bytes size != number")
        }
        byteBuffer.writeByteArray(bytes)
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
            'c' -> {
                byteBuffer.putChar(value as Char)
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

}