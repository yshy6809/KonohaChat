package com.mrwhoami.qqservices.function

data class BotInitInfo (
    val botQQId : Long = 0L,
    val botQQPassword : String = "password",
    val botOwnerQQId : Long = 0L  //bot的所有者将会有更高权限
)