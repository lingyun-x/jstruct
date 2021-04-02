package com.lingyun.lib.jstruct

import com.lingyun.lib.jstruct.exception.ExpressionException

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
open class JStructContext(
    val struct: String,
    val ctx: NumberEnvironmentExpressionParser.ExpressionContext,
) {

    fun getNextNumber(): Int {
        val expressionEndIndex = ctx.expressionEndIndex
        //skip white space
        var firstChar = ctx.expression[ctx.expressionStartIndex]

        while (firstChar == ' ') {
            ctx.expressionStartIndex++
            firstChar = ctx.expression[ctx.expressionStartIndex]
        }

        if (firstChar in JStruct.ALLOW_BASIC_TYPE) {
            return 1
        }

        if (firstChar == '[' || firstChar == '{') {
            return 1
        }

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
//        println("[${ctx.expressionStartIndex}-${ctx.expressionEndIndex}] ${ctx.expression.substring(ctx.expressionStartIndex,ctx.expressionEndIndex)}")
        val result = ctx.getNumber().toInt()
//        println("getNextNumber:$result")

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
                val type = ArrayComplexDataType(typeExpression, ctx.expressionStartIndex, endIndex)
                ctx.expressionStartIndex = endIndex + 1
                return type
            }
            '{' -> {
                ctx.expressionStartIndex++
                val endIndex = StringUtil.findClosingCharIndex(
                    ctx.expression,
                    ctx.expressionStartIndex,
                    ctx.expressionEndIndex,
                    '{',
                    '}'
                )
                if (endIndex == -1) {
                    throw ExpressionException("index:${ctx.expressionStartIndex - 1} char [ not find closing char ]")
                }

                val typeExpression = ctx.expression.substring(ctx.expressionStartIndex, endIndex).trim()
                if (typeExpression.isEmpty()) {
                    throw ExpressionException("index:${ctx.expressionStartIndex} array must have a type")
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

}