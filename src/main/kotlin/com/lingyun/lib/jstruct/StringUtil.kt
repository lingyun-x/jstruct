package com.lingyun.lib.jstruct

import com.lingyun.lib.jstruct.exception.ExpressionException

/*
* Created by mc_luo on 2021/3/25 .
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
object StringUtil {

    fun getNumberEndIndex(expression: String, startIndex: Int, endIndex: Int): Int {
        if (startIndex >= endIndex) return -1
        if (endIndex > expression.length) return -1
        if (expression[startIndex] !in '0'..'9') {
            throw ExpressionException("index:$startIndex number can only start with 0-9")
        }
        for (i in startIndex until endIndex) {
            when (expression[i]) {
                in '0'..'9', '.' -> {

                }
                else -> {
                    return i - 1
                }
            }
        }
        return endIndex - 1
    }

    fun getNextNumberExpressionEnd(expression: String, startIndex: Int, endIndex: Int): Int {
        if (endIndex > expression.length) return -1
        when (expression[startIndex]) {
            '(' -> {
                val expressionEndIndex = findClosingParenthesis(expression, startIndex + 1, endIndex)
                if (expressionEndIndex < endIndex) {
                    return expressionEndIndex
                }
                return -1
            }
            '-' -> {
                return getNextNumberExpressionEnd(expression, startIndex + 1, endIndex)
            }
            in '0'..'9' -> {
                return getNumberEndIndex(expression, startIndex, endIndex)
            }
            '$' -> {
                if (endIndex > startIndex + 1) {
                    when (expression[startIndex + 1]) {
                        '(' -> {
                            val expressionEndIndex = findClosingParenthesis(expression, startIndex + 2, endIndex)
                            return expressionEndIndex
                        }
                        in '0'..'9' -> {
                            return getNumberEndIndex(expression, startIndex + 1, endIndex)
                        }
                    }
                } else {
                    throw ExpressionException("index:$startIndex '$' not have any param")
                }
            }
            else -> {
                throw ExpressionException("index:$startIndex char:${expression[startIndex]} is not a numer symbol")
            }
        }

        return endIndex - 1
    }

    fun findClosingParenthesis(expression: String, startIndex: Int, endIndex: Int): Int {
        if (endIndex > expression.length) return -1
        var count = 1
        for (i in startIndex until endIndex) {
            when (expression[i]) {
                ')' -> {
                    count--
                    if (count == 0) {
                        return i
                    }
                }
                '(' -> {
                    count++
                }
            }
        }
        return -1
    }

    fun isNumber(expression: String, startIndex: Int, endIndex: Int): Boolean {
        var index = startIndex
        if (expression[startIndex] == '-') {
            index++
        }

        if (index >= expression.length) {
            return false
        }

        if (expression[index] == '.') {
            return false
        }

        for (i in index..endIndex) {
            when (expression[i]) {
                in '0'..'9', '.' -> {

                }
                else -> {
                    return false
                }
            }
        }

        return true
    }

    fun firstFirstCharIndex(expression: String, startIndex: Int, endIndex: Int, expectChar: Char): Int {
        for (i in startIndex until endIndex) {
            if (expression[i] == expectChar){
                return i
            }
        }

        return -1
    }

}