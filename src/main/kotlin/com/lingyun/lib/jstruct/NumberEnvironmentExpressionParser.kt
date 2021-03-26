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
        val context = ctx ?: ExpressionContext(expression, 0, expression.length, ArrayList())
        if (expression.isEmpty()) {
            throw ExpressionException("expression is empty")
        }

        val startIndex = context.startIndex
        val endIndex = context.endIndex
        var number = getNextNumber(context, expression)

        while (context.startIndex < context.endIndex) {
            //find next symbol
            when (expression[context.startIndex]) {
                '+' -> {
                    context.startIndex++
                    number += parse(expression, context)
                    break
                }
                '-' -> {
                    number += parse(expression, context)
                    break
                }
                '*' -> {
                    context.startIndex++
                    val nextNumber = getNextNumber(context, expression)
                    number *= nextNumber
                }
                '/' -> {
                    context.startIndex++
                    val nextNumber = getNextNumber(context, expression)
                    number /= nextNumber
                }
                else -> {
                    throw ExpressionException("index:${context.startIndex} need a calculating symbol!!")
                }
            }
        }

        println(
            "parse [${startIndex}-${endIndex}] ${
                expression.substring(
                    startIndex,
                    endIndex
                )
            } "
        )
        println("parse [${startIndex}-${endIndex}] result:$number")
        return number
    }

    fun getNextNumber(ctx: ExpressionContext, expression: String): Double {
        val numberEndIndex = StringUtil.getNextNumberExpressionEnd(expression, ctx.startIndex, ctx.endIndex)
        if (numberEndIndex == -1) {
            throw ExpressionException("index:${ctx.startIndex} not a number :${expression[ctx.startIndex]}")
        }
        val result = when (expression[ctx.startIndex]) {
            '-' -> {
                ctx.startIndex++
                val endIndex = ctx.endIndex
                ctx.endIndex = numberEndIndex + 1
                val number = getNextNumber(ctx, expression)

                ctx.startIndex = numberEndIndex + 1
                ctx.endIndex = endIndex
                -number
            }
            '(' -> {
                ctx.startIndex++
                val endIndex = ctx.endIndex
                ctx.endIndex = numberEndIndex
                val number = parse(expression, ctx)

                ctx.startIndex = numberEndIndex + 1
                ctx.endIndex = endIndex
                number
            }
            '$' -> {
                ctx.startIndex++
                when (expression[ctx.startIndex]) {
                    in '0'..'9' -> {
                        val envIndex = expression.substring(ctx.startIndex, numberEndIndex + 1).toInt()
                        val number = ctx.data[envIndex].toString().toDouble()
                        ctx.startIndex = numberEndIndex + 1
                        number
                    }
                    else -> {
                        val endIndex = ctx.endIndex
                        ctx.endIndex = numberEndIndex + 1

                        val envIndex = getNextNumber(ctx, expression).toInt()

                        ctx.startIndex = numberEndIndex + 1
                        ctx.endIndex = endIndex
                        val number = ctx.data[envIndex].toString().toDouble()
                        number
                    }
                }
            }
            in '0'..'9' -> {
                val number = expression.substring(ctx.startIndex, numberEndIndex + 1).toDouble()
                ctx.startIndex = numberEndIndex + 1
                number
            }
            else -> {
                throw ExpressionException("index:${ctx.startIndex} is need a number")
            }
        }
        return result
    }


    class ExpressionContext(
        val expression: String,
        var startIndex: Int,
        var endIndex: Int,
        val data: ArrayList<Any>
    ) {
        fun getNextNumber(): Double {
            return NumberEnvironmentExpressionParser.getNextNumber(this, expression)
        }
    }

}