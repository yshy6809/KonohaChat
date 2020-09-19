package com.mrwhoami.qqservices


import com.mrwhoami.qqservices.function.*
import com.mrwhoami.qqservices.function.colorphoto.ColorPhoto
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.*
import net.mamoe.mirai.message.*
import net.mamoe.mirai.message.data.*


fun Bot.messageDSL(){
    val baike_ = Baike()
    val Himage_net_ = ColorPhoto()
    val picSearch_ = PictureSearch()
    val hitokoto_ = Hitokoto()
    val bilibili_ = Bilibili()
    //val dynamic_ = Dynamic()
    val nbnhhsh_ = Nbnhhsh()
    this.subscribeGroupMessages{
        always {
            baike_.execute(this@always, this@always.message.contentToString(), this@always.message[Image], this@always.message[Face])
            Himage_net_.execute(this@always, this@always.message.contentToString(), this@always.message[Image], this@always.message[Face])
            picSearch_.execute(this@always, this@always.message.contentToString(), this@always.message[Image], this@always.message[Face])
            hitokoto_.execute(this@always, this@always.message.contentToString(), this@always.message[Image], this@always.message[Face])
            bilibili_.execute(this@always, this@always.message.contentToString(), this@always.message[Image], this@always.message[Face])
            //dynamic_.execute(this@always, this@always.message.contentToString(), this@always.message[Image], this@always.message[Face])
            nbnhhsh_.execute(this@always, this@always.message.contentToString(), this@always.message[Image], this@always.message[Face])
        }
    }




//这里提供了和QuestionAnswer稍微不一样的回复方式，具体请ctrl+左键点击那些函数的定义查看细节
//为什么会这么多种方式混杂，因为这个bot就是四处找文档，东一段西一段学出来的（我太菜了
    this.subscribeMessages{

        var RePicFlag = false//复读图片的开关

        //"a" reply "b" 这是个模式，收到a，回复b
        "atall" reply AtAll
        "给我哭" reply Face(Face.liulei)//发qq原生表情，具体有哪些可以点入Face的定义
        //"语音" reply Voice("vioce.m4a")//具体得看
        "戳一戳" reply PokeMessage.Poke
        "比心" reply PokeMessage.ShowLove
        atBot() reply "主人，是你叫我么"


        case("repic") {
            RePicFlag = true
            reply("成功开启图片复读"+RePicFlag)
        }
        case("unrepic") {
            RePicFlag = false
            reply("成功取消图片复读"+RePicFlag)
        }


        //如果消息包含以下类型，如Image
        has<Image> {
            val ID= "${message[Image]}" //获取第一个 Image 类型的消息

            //图片id如何获得？因为bot收到信息会在控制台打印一份给你，所以可以复制下来
            if(ID == "[mirai:image:{F08A4F0C-E431-C279-D21A-991FFFC06E0E}.mirai]"){//不够色
                reply("你发一张给我康康啊")
                //如果ta真发图片了
                val value = nextMessage { message.any(Image)}//下一条信息
                //反过来嘲讽ta：这也叫涩图？！
                val picture = this::class.java.getResource("/QuestionAnswer/Hdame2.jpg")
                this.sendImage(picture)
            }

            //猫村大佬可了不得复读
            if(ID == "[mirai:image:{61A9570E-F327-802C-A72E-D88194756FA6}.mirai]")
                reply(message)


            if(RePicFlag == true)//无情的图片复读，实用性不高
            {
                reply(message)
            }
        }

        //包含关键词
//        contains("蕾姆的涩图") {
//            reply("雷姆才没有涩图呢，蕾姆可清纯了")
//        }

    }
}