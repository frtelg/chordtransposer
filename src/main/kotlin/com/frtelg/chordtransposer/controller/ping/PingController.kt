package com.frtelg.chordtransposer.controller.ping

import com.frtelg.chordtransposer.dto.response.PingResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class PingController {
    @GetMapping("/ping")
    fun getPing(): Mono<PingResponse> = Mono.fromCallable { PingResponse.create() }
}