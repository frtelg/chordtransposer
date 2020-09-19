package com.frtelg.chordtransposer.controller.version

import com.frtelg.chordtransposer.dto.response.PingResponse
import com.frtelg.chordtransposer.dto.response.VersionResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class VersionController @Autowired constructor(
        @Value("\${frtelg.chordtransposer.version}") private val version: String
) {

    @GetMapping("/version")
    fun getVersion(): Mono<VersionResponse> = Mono.fromCallable { VersionResponse(version) }
}