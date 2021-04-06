package com.lingyun.lib.jstruct

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
interface IStrcutDataType

class BasicDataType(val type: Char) : IStrcutDataType

class ArrayDataType(val componentType: Char) : IStrcutDataType

class ArrayComplexDataType(val typeExpression: String, val structStartIndex: Int, val structEndIndex: Int) :
    IStrcutDataType

class ComplexDataType(val typeExpression: String, val structStartIndex: Int, val structEndIndex: Int) : IStrcutDataType

class EmbedComplexDataType(val typeExpression: String, val structStartIndex: Int, val structEndIndex: Int) :
    IStrcutDataType

class StringDataType() : IStrcutDataType