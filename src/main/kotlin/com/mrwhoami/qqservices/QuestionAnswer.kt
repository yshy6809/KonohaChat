package com.mrwhoami.qqservices

import com.google.gson.JsonParser
import com.mrwhoami.qqservices.function.BotHelper
import com.mrwhoami.qqservices.function.Homeru
import com.mrwhoami.qqservices.util.TuringApiUtil
import kotlinx.coroutines.delay
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.recallIn
import net.mamoe.mirai.message.sendImage
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.regex.Pattern
import kotlin.random.Random


class QuestionAnswer {

    init {
        BotHelper.registerFunctions("人工智障聊天模式", listOf("香织陪我聊天", "香织来聊天", "结束人工智障模式：/退下吧"))


        val indexF = File(indexFilePath)
        if(!indexF.exists())
        {
            val writer: FileWriter?
            try {
                indexF.createNewFile()
                writer = FileWriter(indexFilePath)
                writer.write("1")
                writer?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        else
        {
            try {
                val reader = FileReader(indexFilePath)
                starFrom = reader.read().toInt()
                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val InGroup = listOf(0L)//特定的功能可以只给特定的群用，以免社会性死亡，

    private var turingOpen = false//图灵机器人开关
    private var repeat = hashSetOf<String>()//复读限制器，利用hashSet的元素互异性，使得相同的句子不被复读多次
    private var msgContent_Copy: String?  =  ""//句子副本，保存该复读的句子

    //静态变量，等价于static
    companion object {
        var starFrom  = 1
        const val indexFilePath = "D:\\IDM Download\\SDDDBotServices-master\\src\\main\\resources\\Himage\\index.txt"
        const val hImagePath = "D:\\IDM Download\\SDDDBotServices-master\\src\\main\\resources\\Himage"
    }

    fun starFrom_Write2File()
    {

        val indexF = File(indexFilePath)
        if(!indexF.exists())
        {
            try {
                indexF.createNewFile()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val writer = FileWriter(indexFilePath)
        writer.write(starFrom)
        writer?.close()
        println(">>>>写入完成")
    }



    //设定bot名字，不同的叫法都可以触发
    private fun containsBotName(msgContent: String) : Boolean{
        return msgContent.contains("香织")|| msgContent.contains("香织铃") ||
                msgContent.contains("kaori")|| msgContent.contains("kaorin") ||
                msgContent.contains("かおり")|| msgContent.contains("かおりん")
    }

    private fun getPlainText(messageChain: MessageChain) : String? {
        var buffer = ""
        for (msg in messageChain) {
            if (msg.isContentEmpty()) continue
            else if (msg.isPlain()) {
                buffer += msg.content
            } else continue
        }
        if (buffer.isEmpty()) return null
        return buffer
    }


    suspend fun onGrpMsg(event: GroupMessageEvent) {
        if (!InGroup.contains(event.group.id)) return

        val msg = event.message
        val msgContent = getPlainText(msg) ?: return//这就是消息主体，是String类型的文本了
        val grp = event.group


        //第一优先级是图灵机器人，如果在开启状态，那么就会进入
        if(turingOpen == true)
        {//如果在开启状态

            // 读取群消息，传递给Turing机器人
            var content = msgContent

            //取消聊天
            if(content.contentEquals("/退下吧"))
            {
                turingOpen = false
                grp.sendMessage("好的，你们继续聊，我就先退下了(❁´◡`❁)")
            }
            else{
                //接收返回的结果
                val result: String = TuringApiUtil.getResult(content)
                //把json格式的字符串转化为json对象
                val json = JsonParser().parse(result).asJsonObject
                //获得text键的内容，并转化为string
                val back = json["text"].toString().replace("\"", "")
                //println(back)
                //传送结果
                grp.sendMessage(back)
            }
            //如果开始了图灵机器人，不想执行接下来的代码，那么加个return即可
            //return

        }



        //以下一大段，直到结尾，都在这个when结构里面
        when {
            //开启聊天
            containsBotName(msgContent)
                    && msgContent.endsWith("陪我聊天") ->{
                turingOpen = true
                grp.sendMessage("我来啦~")
            }

            //包含关键字contains，完全相等contentEquals，前缀startsWith，后缀endsWith
            msgContent.contains("zaima") -> grp.sendMessage("buzai, GeiWoShuoZhongWen (　^ω^)")
            msgContent.contentEquals("老婆") -> {
//                val picture = this::class.java.getResource("/QuestionAnswer/？.jpg")
//                grp.sendImage(picture)
            }

//            msgContent.contains("测试") -> {
            //发图
//                val picture = this::class.java.getResource("/QuestionAnswer/XieXiao.jpg")
//                grp.sendImage(picture)
            //引用+艾特+信息
                //grp.sendMessage(msg.quote() + event.sender.at() + "来了来了")

                //msg.quote()
                //event.sender.at()
                //grp.sendMessage(Face(Face.taiyang))//发送qq原生表情，采用这样的形式增加可读性
//            }


            msgContent.startsWith("/百合")
            -> {
                var picR15Size = 1
                try {
                    val files = File(hImagePath).listFiles()
                    picR15Size = files.size-2
                    //多的两个是rename.cmd 和 indexF.txt
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                var IndexStr:String?

                var howMany = 1//涩图数量
                when{
                    msgContent.contains("10") || msgContent.contains("十") -> grp.sendMessage("10连不好，欲速则不达（图库撑不住）")
                    msgContent.contains("3") || msgContent.contains("三") -> howMany = 3
                    msgContent.contains("6") || msgContent.contains("半打") -> howMany = 6
                }
                for(index in starFrom..(starFrom+howMany)) {
                    //println(indexNum)
                    IndexStr = "/Himage/"+index+".jpg"
                    //println("/Himage/"+indexNum+".jpg")
                    val pictureR15 = this::class.java.getResource(IndexStr)
                    try {
                        grp.sendImage(pictureR15)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println(IndexStr)
                    }
                }
                starFrom = (starFrom+howMany)%picR15Size
                //图片可能发得慢，让它睡一会，再发文字消息
                //delay(1 * 1000L)//单位：毫秒
                //grp.sendMessage("ご注文いただいたセットはおとどいいたしました、ごゆっくり")
                starFrom_Write2File()
            }

            msgContent.contains("配叫涩图")->{
                grp.sendMessage(msg.quote() + event.sender.at() + "那你发给给我康康啊")
                grp.sendMessage(Face(Face.heng))
            }
            msgContent.contains("叫涩图")->{
                grp.sendMessage(msg.quote() + event.sender.at() + "那你发！！")
                grp.sendMessage(Face(Face.aoman))//傲慢
            }



            //这是天气模块，问题在于读取API接口的信息时乱码了，加之图灵机器人有天气功能，所以这功能就搁置了
//            msgContent.contains("天气")
//                    || msgContent.contains("How's the weather")
//                    || msgContent.contains("好使的威德")->
//            {
//                try {
//                    val weatherMap = getTodayWeather("101060101")//长春城市代码
//                    grp.sendMessage("为您播报今日天气"+
//                        weatherMap.get("city").toString() +"天气"+
//                                "，  当前气温" + weatherMap.get("temp")
//                    +"，  体感温度"+weatherMap.get("fl"))
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//            }

            msgContent.contains("早上好") ||
                    msgContent.contains("おはよう") ||
                    msgContent.contains("早啊")-> {
                //val picture = this::class.java.getResource("/QuestionAnswer/morning.jpg")
                //grp.sendImage(picture)

                if (BotHelper.memberIsAdmin(event.sender))
                    grp.sendMessage("ごきげんよう、今日も一日がんばってね～")

                else{
                    grp.sendMessage("早上好，今天也要元气满满哦！")
                    grp.sendMessage(Face(Face.taiyang))
                }

            }

//            //正则表达式测试
//            Pattern.matches(".*123.*", msgContent)->
//            {
//
//            }

            //白衣组专属复读
            (msgContent.endsWith("。。")  && !msgContent.endsWith("。。。"))//被哥哥复读，只能两个。。
                    ||msgContent.contains("喵喵喵")//
                    ||msgContent.contains("群里只剩")//群大佬睁眼说瞎话复读
                    ||msgContent.contains("生日快乐")//
                    ||msgContent.contains("yyds") || msgContent.contains("永远滴神") //永远滴神复读
                    ||msgContent.startsWith("不愧是")//不愧是复读
                    ||msgContent.startsWith("是，是")//是，是大佬复读
                    //||msgContent.contentEquals("？")//？你有问题复读
                    ||msgContent.contentEquals("草")//草复读
                    ||msgContent.contentEquals("惹")//eva大佬专属复读
                    ||msgContent.contentEquals("好")//好好怪复读
                    ||msgContent.contentEquals("我好了")//好好怪复读
                    ||msgContent.endsWith("！！！")//猫村专属复读
                    ||(msgContent.startsWith("“")  && msgContent.endsWith("”"))//都教授引用式复读
                    ||Pattern.matches(". . . .", msgContent)//阴 阳 怪 气
            -> {

                //下面的代码是解决：短时间内多人发送了关键词，或者大家在复读关键词时，如何保证bot只复读一次
                //利用hashSet的元素互异性，使得相同的句子不被读入多次

                if(repeat.add(msgContent) == true)//如果你成功录入，说明是新句子，可以复读，并更新句子副本
                {
                    //群里可能出现，一句话能被断断续续的复读多次，其中时间间隔特别长的情况，于是不能用时间作为标准
                    //应该还是以互异性为准，保证过了这波复读之后再纳入新的句子
                    if(repeat.size>=2  && msgContent_Copy != "")//如果纳入了新的 含关键字的句子，说明上一句已经没用了，可以将其去掉
                        repeat.remove(msgContent_Copy)

                    //复读，备份
                    grp.sendMessage(msgContent)//复读，将句子原封不动的发送回去
                    msgContent_Copy = msgContent

                }
            }

            msgContent.contains("狒狒")-> {
                if(Random.nextInt(10) >= 3)
                    grp.sendMessage("没有狒狒")
                else
                    grp.sendMessage("ff14，yyds")
            }
            msgContent.contentEquals("娇娇姐")-> {
                grp.sendMessage("别看管人了")
            }

            msgContent.endsWith("+1")-> {
                grp.sendMessage(msgContent.replace("+1","+2"))
            }


            msgContent.contains("傻逼")
                    ||msgContent.contains("你妈")
                    ||msgContent.contains("智障")
                    ||msgContent.contains("sb")
            -> {
                if (grp.botPermission.isOperator())
                {
                    msg.recall()
                    grp.sendMessage("不许素质用语").recallIn(3000) // 3 秒后自动撤回这条消息
                }

            }

            msgContent.contains("香织叫叫他")->{
                val targetId = event.message[At]!!.target
                val target = event.group[targetId]
                grp.sendMessage(target.at() + "大人叫你")

            }
            msgContent.contains("解禁")->{
                if (!BotHelper.memberIsAdmin(event.sender))//非特权人员无法解禁
                    return
                val targetId = event.message[At]!!.target
                val target = event.group[targetId]
                if (grp.botPermission.isOperator()){
                    target.unmute()
                    grp.sendMessage("赐予你爱与温柔")
                }
                else
                    grp.sendMessage("我还不是管理员。。")


            }
            containsBotName(msgContent) &&
                    (msgContent.contains("夸我")
                    || msgContent.contains("夸奖")) ->{
                grp.sendMessage(Homeru.chp)
            }


            //主动被禁言
            msgContent == "给我精致睡眠" -> {
                if(BotHelper.memberIsAdmin(event.sender))
                    grp.sendMessage("ゆめの中でお会いしましょう(❁´◡`❁)")
                else
                {
                    if (grp.botPermission.isOperator())
                    {
                        event.sender.mute(5 * 60 * 60)
                        grp.sendMessage("祝您好梦(❁´◡`❁)")
                    }

                }
            }


            containsBotName(msgContent) && msgContent.contains("おやすみ")
                    || msgContent.contains("晚安")
                    || msgContent.contains("睡了")
            -> {
//                val num = Random.nextInt(1, 3 + 1)//左开右闭区间
//                val PicNmae = "/QuestionAnswer/goodNight"+num+".jpg"
//                val picture = this::class.java.getResource(PicNmae)
//                grp.sendImage(picture)
                if (BotHelper.memberIsAdmin(event.sender))
                    grp.sendMessage("お休みなさい～、いいゆめを")

                else
                    grp.sendMessage("晚安，祝你好梦" + Face(5))

                //无论是谁都会被禁言
                if (grp.botPermission.isOperator())
                    event.sender.mute(5 * 60 * 60)

            }

            containsBotName(msgContent) && msgContent.contains("爬") -> {
                if (BotHelper.memberIsAdmin(event.sender)) {
                    //val num = Random.nextInt(1, 2 + 1)//左开右闭区间
//                    val PicNmae = "/QuestionAnswer/naku"+num+".jpg"
//                    val picture = this::class.java.getResource(PicNmae)
//                    grp.sendImage(picture)
                    grp.sendMessage("呜呜呜，不要欺负我( TдT)")
                } else {
                    if (grp.botPermission.isOperator()) {
                        event.sender.mute(Random.nextInt(1, 120) * 60)
                    }
//                    val picture = this::class.java.getResource("/QuestionAnswer/zhazha.jpg")
//                    grp.sendImage(picture)
                    grp.sendMessage("谁爬还不一定呢")//你爬( `д´)
                }
            }
            containsBotName(msgContent) && msgContent.contains("傻") -> {
                if (BotHelper.memberIsAdmin(event.sender)) {
//                    val num = Random.nextInt(1, 2 + 1)//左开右闭区间
//                    val PicNmae = "/QuestionAnswer/naku"+num+".jpg"
//                    val picture = this::class.java.getResource(PicNmae)
//                    grp.sendImage(picture)
                    grp.sendMessage("人家才不傻！(>д<)")
                } else {
                    if (grp.botPermission.isOperator()) {
                        event.sender.mute(Random.nextInt(1, 120) * 60)
                    }
                    //val picture = this::class.java.getResource("/QuestionAnswer/gun.jpg")
                    //grp.sendImage(picture)
                    grp.sendMessage("一边凉去( `д´)")
                }


            }

            containsBotName(msgContent) && msgContent.contains("萌")  -> {
//                val picture = this::class.java.getResource("/QuestionAnswer/nya.jpg")
//                grp.sendImage(picture)
            }

            containsBotName(msgContent) &&
                    (msgContent.contains("可爱")
                    || msgContent.contains("乖")
                    || msgContent.contains("好看")
                            || msgContent.contains("天使"))  -> {

//                val num = Random.nextInt(1, 5 + 1)//左开右闭区间
//                val PicNmae = "/QuestionAnswer/happy"+num+".jpg"

                //val picture = this::class.java.getResource(PicNmae)
                //grp.sendImage(picture)
                grp.sendMessage("欸嘿~(*ﾟ∀ﾟ*)")
            }


            containsBotName(msgContent) && msgContent.contains("出来")  -> {
//                try {
//
//                    var num = Random.nextInt(1, 11 + 1)
//                    var IndexStr = ""
//
//                    IndexStr = "/QuestionAnswer/deru$num.jpg"
//
//                    val picture = this::class.java.getResource(IndexStr)
//                    try {
//                        grp.sendImage(picture)
//                    } catch (e: Exception) {
//                        System.err.println(num)
//                        System.err.println(IndexStr)
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }

                grp.sendMessage("我～来～了")
            }

            containsBotName(msgContent) && (msgContent.contains("亲亲") ||
                                            msgContent.contains("啾啾") ||
                                            msgContent.contains("mua")) -> {
                if (BotHelper.memberIsAdmin(event.sender)) {
//                    val picture = this::class.java.getResource("/QuestionAnswer/love1.jpg")
//                    grp.sendImage(picture)
                    grp.sendMessage("愛してるよ")
                }
                else {
//                    val picture = this::class.java.getResource("/QuestionAnswer/love1.jpg")
//                    grp.sendImage(picture)
                    grp.sendMessage("人家害羞嘛")//不要啾啾我⊂彡☆))∀`)
                }
            }
            containsBotName(msgContent) && (msgContent.contains("日我") ||
                                            msgContent.contains("上我") ||
                                            msgContent.contains("曰我")) -> {
//                val picture = this::class.java.getResource("/QuestionAnswer/？.jpg")
//                grp.sendImage(picture)
                grp.sendMessage("你不对劲，你有问题，你快点爬(`ヮ´ )")
                if (grp.botPermission.isOperator()) {
                    event.sender.mute(Random.nextInt(1, 120) * 60)
                }
            }

            containsBotName(msgContent) && msgContent.contains("活着") -> {
                val answers = listOf(
                    "应该算活着吧",
                    "还能死了不成",
                    "大概？"
                )
                grp.sendMessage(answers[Random.nextInt(answers.size)])
            }

            msgContent.contentEquals("自我介绍") -> {

                grp.sendMessage(
                    "我是由心葉大人基于Mirai开发的白衣群御用人工智障群聊bot，不会做饭，只会水群，无情复读姬。" +
                            "发送给功能列表可以查看我能干什么，现在还很弱鸡，但是未来可期。（只要有大佬一起码代码的话:" +
                            "https://github.com/KonohaVio/KonohaChat"
                )
            }
            msgContent.contentEquals("功能列表") -> {
                grp.sendMessage(BotHelper.functionsToString(event.group.id))
            }
            containsBotName(msgContent) -> {
                val answers = listOf(
                    "お呼びでしょうか",
                    "您是在叫我么？",
                    "是在说我可爱嘛～",
                    "我来了～～",
                    "呼んだ？",
                    "我听到有坏银在偷偷议论我"
                )

                val idx = Random.nextInt(0, answers.size)
                grp.sendMessage(answers[idx])
            }
        }
    }
}
