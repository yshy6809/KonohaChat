package com.mrwhoami.qqservices.function

import com.mrwhoami.qqservices.util.BasicUtil
import mu.KotlinLogging
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.isAdministrator
import net.mamoe.mirai.contact.isOwner
import com.mrwhoami.qqservices.util.plugin.PluginScheduler
import net.mamoe.mirai.utils.BotConfiguration
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class BotHelper {
    companion object {

        lateinit var botConfig: BotConfiguration
        val scheduler = PluginScheduler()
        val dataFolder = File("${BasicUtil.getLocation(BotHelper.javaClass).path}${File.separator}Bot")
            .also { if (!it.exists()) Files.createDirectories(Paths.get(it.toURI())) }


        private val logger = KotlinLogging.logger {}
        // Set up a bot owner using res/owner.txt
        private var botOwner : Long? = null
        private var moduleFunctionList : MutableMap<String, List<String>> = mutableMapOf()
        private var groupDisabledModules : MutableMap<Long, List<String>> = mutableMapOf()

        fun loadConfig() {
//            val ownerConfigFile = File("res/owner.txt")
//            if (!ownerConfigFile.exists()) return
//            botOwner = ownerConfigFile.readText().trim().toLong()
            val botInit = BotInitInfo()
            botOwner = botInit.botOwnerQQId
            logger.info { "Bot admin set to $botOwner" }
        }

        //是否为群主，管理员，bot主人
        fun memberIsAdmin(member : Member) : Boolean {
            return member.isAdministrator() || member.isOwner() || member.id == botOwner
        }

        fun memberIsBotOwner(member : Member) : Boolean {
            return member.id == botOwner
        }

        fun registerFunctions (moduleName : String, functionNames : List<String>) {
            moduleFunctionList[moduleName] = functionNames
        }

        fun functionsToString (grpId : Long) : String {
            var functionsString = "<当前功能列表>\n"
            val disabledModules : List<String> = if (groupDisabledModules.containsKey(grpId)) {
                groupDisabledModules[grpId]!!
            } else {
                emptyList()
            }
            for ((moduleName, functionList) in moduleFunctionList) {
                functionsString += "[模块：$moduleName]"
                functionsString += if (disabledModules.contains(moduleName)) "(模块已禁用)\n"  else "\n"
                for (function in functionList) {
                    functionsString += "$function\n"
                }
            }
            return functionsString
        }
    }
}