package com.mrwhoami.qqservices.function

//大体框架已经移到Config里了，这里应该已经没用了
data class BotInitInfo (
    val botQQId : Long = 0L,
    val botQQPassword : String = "",
    val botOwnerQQId : Long = 0L  //bot的所有者将会享有特权
)