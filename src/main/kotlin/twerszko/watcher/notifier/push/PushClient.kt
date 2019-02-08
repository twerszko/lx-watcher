package twerszko.watcher.notifier.push

import feign.Body
import feign.Headers
import feign.Param
import feign.RequestLine


interface PushClient {
    @RequestLine("POST")
    @Headers("Content-Type: text/plain")
    @Body("{body}")
    fun push(@Param("body") body: String)
}