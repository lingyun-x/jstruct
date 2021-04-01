package com.lingyun.lib.jstruct

import com.lingyun.lib.jstruct.exception.ExpressionException

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
object NumberEnvironmentExpressionParser {

    fun parse(expression: String, ctx: ExpressionContext? = null): Double {
        val context = ctx ?: ExpressionContext(expression, 0, expression.length)
        if (expression.isEmpty()) {
            throw ExpressionException("expression is empty")
        }

        var number = getNextNumber(context)

        while (context.expressionStartIndex < context.expressionEndIndex) {
            //find next symbol
            when (expression[context.expressionStartIndex]) {
                '+' -> {
                    context.expressionStartIndex++
                    number += parse(expression, context)
                    break
                }
                '-' -> {
                    number += parse(expression, context)
                    break
                }
                '*' -> {
                    context.expressionStartIndex++
                    val nextNumber = getNextNumber(context)
                    number *= nextNumber
                }
                '/' -> {
                    context.expressionStartIndex++
                    val nextNumber = getNextNumber(context)
                    number /= nextNumber
                }
                else -> {
                    throw ExpressionException("index:${context.expressionStartIndex} need a calculating symbol!!")
                }
            }
        }
        return number
    }

    fun getNextNumber(ctx: ExpressionContext): Double {
        val expression = ctx.expression
        val elements = ctx.elements

        val numberEndIndex =
            StringUtil.getNextNumberExpressionEnd(expression, ctx.expressionStartIndex, ctx.expressionEndIndex)
        if (numberEndIndex == -1) {
            throw ExpressionException("index:${ctx.expressionStartIndex} not a number :${expression[ctx.expressionStartIndex]}")
        }
        val result = when (expression[ctx.expressionStartIndex]) {
            '-' -> {
                ctx.expressionStartIndex++
                val endIndex = ctx.expressionEndIndex
                ctx.expressionEndIndex = numberEndIndex + 1
                val number = getNextNumber(ctx)

                ctx.expressionStartIndex = numberEndIndex + 1
                ctx.expressionEndIndex = endIndex
                -number
            }
            '(' -> {
                ctx.expressionStartIndex++
                val endIndex = ctx.expressionEndIndex
                ctx.expressionEndIndex = numberEndIndex
                val number = parse(expression, ctx)

                ctx.expressionStartIndex = numberEndIndex + 1
                ctx.expressionEndIndex = endIndex
                number
            }
            '@' -> {
                ctx.expressionStartIndex++
                when (expression[ctx.expressionStartIndex]) {
                    in '0'..'9' -> {
                        var envIndex =
                            expression.substring(ctx.expressionStartIndex, numberEndIndex + 1).toString().toInt()
                        envIndex += ctx.elementStartIndex

                        val number = elements[envIndex].toString().toDouble()
                        ctx.expressionStartIndex = numberEndIndex + 1
                        number
                    }
                    else -> {
                        val endIndex = ctx.expressionEndIndex
                        ctx.expressionEndIndex = numberEndIndex + 1

                        var envIndex = getNextNumber(ctx).toInt()

                        if (envIndex < 0) {
                            envIndex += ctx.currentElementIndex
                        } else {
                            envIndex += ctx.elementStartIndex
                        }

                        ctx.expressionStartIndex = numberEndIndex + 1
                        ctx.expressionEndIndex = endIndex
                        val number = elements[envIndex].toString().toDouble()
                        number
                    }
                }
            }
            in '0'..'9' -> {
                val number = expression.substring(ctx.expressionStartIndex, numberEndIndex + 1).toDouble()
                ctx.expressionStartIndex = numberEndIndex + 1
                number
            }
            else -> {
                throw ExpressionException("index:${ctx.expressionStartIndex} is need a number")
            }
        }
        return result
    }

    class ExpressionContext(
        val expression: String,
        var expressionStartIndex: Int,
        var expressionEndIndex: Int,
        val elements: List<Any> = ArrayList(),
        var elementStartIndex: Int = 0,
        var currentElementIndex: Int = 0
    ) {
        fun getNumber(): Double {
            return NumberEnvironmentExpressionParser.parse(expression, this)
        }
    }

}