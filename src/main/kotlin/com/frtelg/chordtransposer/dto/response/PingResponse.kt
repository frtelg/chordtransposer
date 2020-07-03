package com.frtelg.chordtransposer.dto.response

import java.time.Instant

data class PingResponse(val dateTime: Instant) {
    companion object {
        fun create() = PingResponse(Instant.now())
    }
}