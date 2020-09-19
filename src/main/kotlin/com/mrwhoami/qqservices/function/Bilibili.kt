package com.mrwhoami.qqservices.function

import com.fasterxml.jackson.databind.ObjectMapper
import com.mrwhoami.qqservices.util.BasicUtil
import com.mrwhoami.qqservices.util.interfaces.FunctionListener
import com.mrwhoami.qqservices.util.network.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

class Bilibili : FunctionListener {
    init{
        BotHelper.registerFunctions("B站链接解析", emptyList())
    }
    private val pattern = Pattern.compile("BV(\\d|[a-z]|[A-Z]){10}")

    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {

        var av: String?
        var bv: String?
        var reader: BufferedReader?
        var inputStream: InputStream?
        if (message.toLowerCase().contains("av")) {
            av = BasicUtil.ExtraceInt(message).toString()
            inputStream = NetWorkUtil.get("https://api.bilibili.com/x/web-interface/view?aid=$av")?.first
        } else if (message.contains("BV")) {
            val matcher = pattern.matcher(message)
            bv = if (matcher.find()) {
                matcher.group()
            } else {
                return false
            }
            inputStream = NetWorkUtil.get("https://api.bilibili.com/x/web-interface/view?bvid=$bv")?.first
        } else {
            return false
        }
        if (inputStream == null) {
            return false
        }
        reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
        val line = reader.readLine()
        if (!line.startsWith("{\"code\":0")) {
            return false
        }
        val mapper = ObjectMapper()
        val jsonNode = mapper.readTree(line)
        val dataObject = jsonNode["data"]
        val pic = dataObject["pic"].asText();
        val title = dataObject["title"].asText()
        val UP = dataObject["owner"]["name"].asText()
        val uplink = dataObject["owner"]["mid"].asText()
        val zone = dataObject["tname"].asText()
        val statObject = dataObject["stat"]
        val view = statObject["view"].asText()
        val Barrage = statObject["danmaku"].asText()
        val reply = statObject["reply"].asText()
        val fav = statObject["favorite"].asText()
        val coin = statObject["coin"].asText()
        val share = statObject["share"].asText()
        val like = statObject["like"].asText()
        val desc = dataObject["desc"].asText()
        val builder = StringBuilder("\n" + title)
        builder.append("\nUP: ")
                .append(UP)
                .append("(https://space.bilibili.com/")
                .append(uplink)
                .append(")\n分区: ")
                .append(zone)
                .append("\n播放量: ")
                .append(view)
                .append(" 弹幕: ")
                .append(Barrage)
                .append(" 评论: ")
                .append(reply)
                .append("\n收藏: ")
                .append(fav)
                .append(" 投币: ")
                .append(coin)
                .append(" 分享: ")
                .append(share)
                .append(" 点赞: ")
                .append(like)
                .append("\n")
                //.append(desc)//这是视频简介，给注释了，以为有时候很长，会报“无法发送长消息”的错误

        event.reply(event.uploadImage(URL(pic))+builder.toString())
        /*
        * 如果要发送视频简介，有些特别长的视频简介，无法一次性发送那么多（qq的长消息限制），所以最好拆分。
        * */
//        val Mainmessage = desc.split("。")
//        println(Mainmessage)
//        var partMessage = ""
//        var counter__ = 0
//        for(Mainmessage2 in Mainmessage){
//            for (i in Mainmessage2.split("，"))
//            {
//                println(i)
//                partMessage = partMessage+i+"，"
//                counter__++
//                if(counter__ == 8)
//                {
//                    event.reply(partMessage)
//                    partMessage = ""
//                    counter__ = 0
//                }
//            }
//        }
//        //把剩下的不足8句的也发了
//        if(partMessage != "")
//            event.reply(partMessage+"<<<end")



        return true
    }

}