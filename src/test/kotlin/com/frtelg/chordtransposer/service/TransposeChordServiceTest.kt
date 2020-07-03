package com.frtelg.chordtransposer.service

import com.frtelg.chordtransposer.dto.request.TransposeChordsInFileRequest
import com.frtelg.chordtransposer.dto.response.TransposeSingleChordResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URI

class TransposeChordServiceTest {
    val dir = System.getProperty("user.dir") + "/src/test/resources"

    private val transposeChordService: TransposeChordService = TransposeChordService()

    @Test
    fun testFile() {
        val request = TransposeChordsInFileRequest(URI("file://$dir/test.txt"), 3)

        val response = transposeChordService.transposeFile(request)

        File(response.resultFile).readLines().forEach {s ->
            println(s)
        }
    }

    @Test
    fun testSingleChord() {
        val response = transposeChordService.transposeSingleChord("F#", 3)

        assertEquals(TransposeSingleChordResponse("A"), response)
    }
}