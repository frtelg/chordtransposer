package com.frtelg.chordtransposer.dto.request

import java.net.URI

data class TransposeChordsInFileRequest(val file: URI, val steps: Int)