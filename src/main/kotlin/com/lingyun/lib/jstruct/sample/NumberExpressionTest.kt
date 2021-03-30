package com.lingyun.lib.jstruct.sample

import com.lingyun.lib.jstruct.NumberEnvironmentExpressionParser

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
object NumberExpressionTest {

    @JvmStatic
    fun main(args: Array<String>) {
//        var expression =
//            "((1+2.23*3*4/@5)*@2+5*4/2-312+((12+23*34)*45+23*46))*((1+@2*@3+4/5))-5*4/2-1-@2/(312+((12+23*34)*45+23*46))-@(@2-1)"
//        val ctx = NumberEnvironmentExpressionParser.ExpressionContext(expression, 0, expression.length, ArrayList())
//        ctx.data.addAll(0..10)
//        ctx.data.add(1.0)

//        println("expression:@expression calculation result:@{NumberEnvironmentExpressionParser.parse(expression, ctx)}")

        val result =
            ((1 + 2.23 * 3 * 4 / 5.0) * 2 + 5 * 4 / 2.0 - 312 + ((12 + 23 * 34) * 45 + 23 * 46)) * ((1 + 2 * 3 + 4 / 5.0)) - 5.0 * 4 / 2.0 - 1 - 2.0 / (312 + ((12 + 23 * 34) * 45 + 23 * 46)) - (2 - 1)

        println("result:$result")

        val expression = "@-(@2*3)"
        val elmements = ArrayList<Any>()
        elmements.addAll(0..10)
        val ctx = NumberEnvironmentExpressionParser.ExpressionContext(
            expression,
            0,
            expression.length,
            elmements,
            0,
            currentElementIndex = elmements.size
        )
//        ctx.data.add(1.0)
        println("expression:$expression calculation result:${NumberEnvironmentExpressionParser.parse(expression, ctx)}")
    }
}