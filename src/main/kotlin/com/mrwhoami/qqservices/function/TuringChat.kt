package com.mrwhoami.qqservices.function

import com.mrwhoami.qqservices.util.TuringApiUtil
import com.google.gson.JsonParser
import java.util.Scanner



object TuringChat {
    init {
        BotHelper.registerFunctions("图灵", listOf("蕾姆陪我聊天"))
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val sc = Scanner(System.`in`)
        while (true) {
            var content: String? = ""
            //控制台输入信息
            content = sc.nextLine()

            //接收返回的结果
            val result: String = TuringApiUtil.getResult(content)

            //把json格式的字符串转化为json对象
            val json = JsonParser().parse(result).asJsonObject
            //获得text键的内容，并转化为string
            val back = json["text"].toString()

            //打印结果
            println(back)
        }

    }
}