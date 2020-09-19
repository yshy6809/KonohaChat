package com.mrwhoami.qqservices.function



import com.mrwhoami.qqservices.util.BasicUtil
import com.mrwhoami.qqservices.util.pictureSearchUtil.Ascii2d
import com.mrwhoami.qqservices.util.interfaces.FunctionListener
import com.mrwhoami.qqservices.util.network.NetWorkUtil
import com.mrwhoami.qqservices.util.pictureSearchUtil.Saucenao
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.*
import java.lang.StringBuilder

class PictureSearch : FunctionListener {
    init{
        BotHelper.registerFunctions("P站搜图", listOf("/搜图+空格"))
    }
    private val map = HashMap<Long, Int>()

    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        val senderID = event.sender.id
        if (message.startsWith("/搜图 ") && !map.contains(senderID)) {
            map[senderID] = BasicUtil.ExtraceInt(message.split(" ")[1], 1)
            event.reply(At(event.sender as Member) + "请发送图片")
        }

        if (map[senderID] != null && image != null) {
            val source = when (map[senderID]) {
                1 -> {
                    event.reply(At(event.sender as Member) + "Saucenao查找中!")
                    Saucenao
                }
                2 -> {
                    event.reply(At(event.sender as Member) + "Ascii2d查找中!")
                    Ascii2d
                }
                else -> Saucenao
            }
            val results = source.search(image.queryUrl())
            if (results.isEmpty()) {
                event.reply("未查找到结果!")
                map.remove(senderID)
                return true
            }
            map.remove(senderID)
            results.forEach {
                val builder = StringBuilder()
                it.extUrls.forEach {
                    builder.append(it).append("\n")
                }
                event.reply(event.uploadImage(NetWorkUtil.get(it.thumbnail)!!.first) as Message + PlainText("\n相似度: ${it.similarity} \n画师名: ${it.memberName} \n相关链接: \n${builder.toString().replace(Regex("\n$"), "")}"))
            }
        }
        return true
    }


}