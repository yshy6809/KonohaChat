package com.mrwhoami.qqservices.util


import kotlinx.coroutines.*
import com.mrwhoami.qqservices.function.BotHelper
import com.mrwhoami.qqservices.util.plugin.PluginScheduler
import me.lovesasuna.lanzou.util.NetWorkUtil
import java.io.*
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


object BasicUtil {
    fun extractInt(string: String, defaultValue: Int = 0): Int {
        val pattern = Pattern.compile("\\d+")
        val buffer = StringBuffer()
        val matcher = pattern.matcher(string)
        while (matcher.find()) {
            buffer.append(matcher.group())
        }
        return if (buffer.toString().isEmpty()) defaultValue else buffer.toString().toInt()
    }

    fun <T> replace(message: String, `var`: String, replace: T): String {
        return message.replace(`var`, replace.toString())
    }

    /**
     * @param command 任务
     * @param delay 延迟(单位:秒)
     */
    fun scheduleWithFixedDelay(command: Runnable, initialDelay: Long, delay: Long, unit: TimeUnit): Pair<Job, PluginScheduler.RepeatTaskReceipt> {
        val receipt = PluginScheduler.RepeatTaskReceipt()
        val job = GlobalScope.launch {
            delay(unit.toMillis(initialDelay))
            while (!receipt.cancelled && this.isActive) {
                withContext(Dispatchers.IO) {
                    command.run()
                }
                delay(unit.toMillis(delay))
            }
        }
        return Pair(job, receipt)
    }

    fun debug(message: String): String {
        val reader = me.lovesasuna.lanzou.util.NetWorkUtil.post("https://paste.ubuntu.com/",
            "poster=Bot&syntax=text&expiration=day&content=$message".toByteArray(Charsets.UTF_8),
            arrayOf("Content-Type", "application/x-www-form-urlencoded"),
            arrayOf("host", "paste.ubuntu.com")
        )!!.second.bufferedReader()
        val builder = StringBuilder()
        reader.lines().skip(25).parallel().forEach {
            builder.append(it)
        }
        val s = Regex("<a class=\"pturl\" href=\"/p/([0-9]|[a-z]|[A-Z])+/plain/\">Download as text</a>").find(builder.toString())!!.value
        return "https://paste.ubuntu.com" + s.substringAfter("href=\"").substringBefore("/plain")
    }

    fun getLocation(c: Class<*>): File {
        return if (System.getProperty("os.name").contains("windows", true)) {
            Paths.get(c.protectionDomain.codeSource.location.path.replaceFirst("/", "")).parent.toFile()
        } else {
            Paths.get(c.protectionDomain.codeSource.location.path).parent.toFile()
        }
    }

    fun getLocation(fileName: String): File {
        return File("${getLocation(BotHelper::class.java).path}${File.separator}$fileName")
    }

}