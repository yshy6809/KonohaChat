package com.mrwhoami.qqservices.function

import com.mrwhoami.qqservices.util.interfaces.FunctionListener
import com.mrwhoami.qqservices.util.network.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.net.URLEncoder

class Baike : FunctionListener {
    init{
        BotHelper.registerFunctions("百度百科", listOf("/百科+空格+查询词"))
    }
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        if (message.startsWith("/百科 ")) {
            val string = message.split(" ")[1]
            val url = "https://baike.baidu.com/item/${URLEncoder.encode(string, "UTF-8")}"
            val reader = NetWorkUtil.get(url)!!.first.bufferedReader()
            for (i in 0 until 10) reader.readLine()
            val desc = reader.readLine()
            println(desc)
            val args = desc.split("\"")
            if (args.size > 1) {
            println(args[3])

                //下面这个操作是因为qq不支持超过一定尺寸的长消息，所以得拆段发
            val Mainmessage = args[3].split("。")
            var partMessage = ""
            var counter__ = 0
            for(Mainmessage2 in Mainmessage){
                for (i in Mainmessage2.split("，"))
                {
                    println(i)
                    if (i.endsWith("...")) {
                        partMessage = partMessage+i.replace(Regex("...$"), "")+"。"
                    }
                    else{
                        partMessage = partMessage+i+"，"
                    }
                    counter__++
                    //这是按12个,分隔，更好的应该用String.size判断
                    if(counter__ == 12)
                    {
                        event.reply(partMessage)
                        partMessage = ""
                        counter__ = 0
                    }
                }
            }
                //把剩下的不足12句的也发了
                if(partMessage != "")
                    event.reply(partMessage)




                //event.reply(args[3].replace(Regex("...$"), ""))
            } else {
                event.reply("百度百科未收录此词条!")
            }
            return true
        }
        return false

    }

}