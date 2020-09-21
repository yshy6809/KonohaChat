package com.mrwhoami.qqservices.data

data class ConfigData(var protocol: String = "ANDROID_PAD",
                      var account: Long = 0,
                      val botOwnerQQId : Long = 0,
                      var password: String = ""
//                      var pictureSearchAPI: String = "",
//                      var bilibiliCookie: String = "",
//                      var lanzouCookie: String = "",
//                      var disableFunction: List<String> = ArrayList()
                      )