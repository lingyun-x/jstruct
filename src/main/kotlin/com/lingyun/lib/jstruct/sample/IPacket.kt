package com.lingyun.lib.jstruct.sample

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
interface IPacket {
    val packetHead: PacketHead
    fun packetStruct(): String
}

data class PacketHead(
    val packetId: Int, val messageType: Int, val deviceId: Int,
    val time: Long
) {
    fun packetStruct(): String {
        return "3i1l"
    }

    fun elements(): List<Any> {
        return listOf(packetId, messageType, deviceId, time)
    }

    fun elementSize(): Int {
        return 4
    }

    companion object {

        fun newPacketHead(value: List<Any>, startIndex: Int): PacketHead {
            return PacketHead(
                value[startIndex] as Int,
                value[startIndex + 1] as Int,
                value[startIndex + 2] as Int,
                value[startIndex + 3] as Long
            )
        }
    }

}