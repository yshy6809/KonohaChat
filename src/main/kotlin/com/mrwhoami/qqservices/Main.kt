package com.mrwhoami.qqservices

import com.mrwhoami.qqservices.function.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import mu.KotlinLogging
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.join
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.sendImage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


private val logger = KotlinLogging.logger {}

suspend fun main() {
    // Login QQ. Use another data class to avoid password tracking.
    val login = BotInitInfo()
    val miraiBot = Bot(login.botQQId, login.botQQPassword) {
        fileBasedDeviceInfo("device.json")
    }.alsoLogin()
    logger.info { "${login.botQQId} is logged in." }


    // Initialize helper
    BotHelper.loadConfig()
    // Initialize services
    val qAndA = QuestionAnswer()
    val repeater = Repeater()
    val voteBan = VoteBan()
    val muteMenu = MuteMenu()
    val welcome = Welcome(miraiBot.groups)
    //val groupDaily = GroupDaily()

    logger.info { "Initialization finished." }

    miraiBot.messageDSL()

    miraiBot.subscribeAlways<GroupMessageEvent> {
        // repeater behaviour
        qAndA.onGrpMsg(it)
        repeater.onGrpMsg(it)
        voteBan.onGrpMsg(it)
        muteMenu.onGrpMsg(it)
        welcome.onGrpMsg(it)
        //groupDaily.onGrpMsg(it)//群日志未启用
    }

    miraiBot.subscribeAlways<MemberJoinEvent> {
        welcome.onMemberJoin(it)
    }

    // Check per minute
    GlobalScope.launch {
        while (miraiBot.isActive) {
            logger.info { "5-min heart beat event." }
            delay(60 *5* 1000L)
        }
    }

    // Check per hour
    GlobalScope.launch {
        while (miraiBot.isActive) {

            logger.info { "1-hour heart beat event." }
            //groupDaily.onHourWake(miraiBot)
            delay(60 * 60 * 1000L)
        }
    }



    GlobalScope.launch{
        var dfHour = SimpleDateFormat("HH");
        var dfMimute = SimpleDateFormat("mm");
        var d =Date();
        var Hour_ : String
        var Minute_ :String


        while (miraiBot.isActive)
        {
            d =Date()
            Minute_ = dfMimute.format(d);
            Hour_ = dfHour.format(d);

            if(Hour_ == "21" && Integer.valueOf(Minute_) < Integer.valueOf("20"))
            {
                for (group in miraiBot.groups) {
                    if (group.id == 0L) {

                        group.sendMessage(AtAll+"到点了，生而打卡，我很抱歉   " +
                                "https://ehall.jlu.edu.cn/taskcenter/workflow/index")

                        val num = Random.nextInt(1,4+1)//左开右闭区间
                        val PicNmae = "/QuestionAnswer/sign"+num+".jpg"
                        val picture = this::class.java.getResource(PicNmae)
                        group.sendImage(picture)
                    }
                }



            }

            if(Hour_ == "23"  && Integer.valueOf(Minute_) > Integer.valueOf("30") )
            {
                //遍历bot加的群。
                for (group in miraiBot.groups) {
                    if (group.id == 0L) {//这里需要自己设置一下

                        group.sendMessage(AtAll+"主人主人，睡觉啦！早睡早起身体好～")
                        val num = Random.nextInt(1,4+1)//左开右闭区间
                        val PicNmae = "/QuestionAnswer/sign"+num+".jpg"
                        val picture = this::class.java.getResource(PicNmae)
                        group.sendImage(picture)

                    }
                }
            }

            delay(60 * 30 * 1000L)//半小时检测一次
        }

    }

    //这个很关键，千万不能删！
    miraiBot.join() // 等待 Bot 离线, 避免主线程退出
    

}