package com.mrwhoami.qqservices.function

import com.mrwhoami.qqservices.util.interfaces.FunctionListener
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.net.URLEncoder
import kotlin.random.Random

//class Baike : FunctionListener {
//    init{
//        BotHelper.registerFunctions("百度百科", listOf("/百科+空格+查询词"))
//    }
//    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
//        if (message.startsWith("/百科 ")) {
//            val string = message.split(" ")[1]
//            val url = "https://baike.baidu.com/item/${URLEncoder.encode(string, "UTF-8")}"
//            val reader = NetWorkUtil.get(url)!!.first.bufferedReader()
//            for (i in 0 until 10) reader.readLine()
//            val desc = reader.readLine()//返回文字
//            val args = desc.split("\"")//切割，只要第四个内容
//            if (args.size > 1) {
//
//                //下面这个操作是因为qq不支持超过一定尺寸的长消息，所以得拆段发
//                val Mainmessage = args[3].split("。")
//                var partMessage = ""
//                var counter__ = 0
//                for(Mainmessage2 in Mainmessage){
//                    for (i in Mainmessage2.split("，"))
//                    {
//                        println(i)
//                        if (i.endsWith("...")) {
//                            partMessage = partMessage+i.replace(Regex("...$"), "")+"。"
//                        }
//                        else{
//                            partMessage = partMessage+i+"，"
//                        }
//                        counter__++
//                        //这是按12个,分隔，更好的应该用String.size判断
//                        if(counter__ == 12)
//                        {
//                            event.reply(partMessage)
//                            partMessage = ""
//                            counter__ = 0
//                        }
//                    }
//                }
//                //把剩下的不足12句的也发了
//                if(partMessage != "")
//                    event.reply(partMessage)
//                //这是原来的操作，是一口气全发了，但是往往不行（是我太菜了，想要真正解决这个问题，可能要试一试一次性发生，看看具体报什么错
//                //event.reply(args[3].replace(Regex("...$"), ""))
//            } else {
//                event.reply("肥肠抱歉，百度百科未收录此词条")
//            }
//            return true
//        }
//        return false
//
//    }
//
//}

class Baike : FunctionListener {
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        if (message.startsWith("/百科 ")) {
            val string = message.split(" ")[1]
            val url = "https://baike.baidu.com/item/${URLEncoder.encode(string, "UTF-8")}"
            val reader = NetWorkUtil.get(url)!!.second.bufferedReader()
            for (i in 0 until 10) reader.readLine()
            val desc = reader.readLine()
            val args = desc.split("\"")
            if (args.size > 1) {
                event.reply(args[3].replace(Regex("...$"), ""))
            } else {
                if(Random.nextInt(2)>=1)
                    event.reply("肥肠抱歉，百科百科它……没有收录这个伟大的词条")
                else
                    event.reply("肥肠抱歉，百科百科说这个词条……等待着您的撰写")
            }
            reader.close()
            return true
        }
        return false

    }

}