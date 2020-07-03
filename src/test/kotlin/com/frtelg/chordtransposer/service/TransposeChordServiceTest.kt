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
    fun testFileNoTargetFolder() {
        val request = TransposeChordsInFileRequest(URI("file://$dir/test.txt"), 3, null)

        val response = transposeChordService.transposeFile(request)
        val responseFile = File(response.resultFile).readLines()

        File(URI("file://$dir/expected_result.txt")).readLines().forEach {
            responseFile.contains(it)
        }
    }

    @Test
    fun testFileWithTargetFolder() {
        val request = TransposeChordsInFileRequest(URI("file://$dir/test.txt"), 3, URI("file://$dir"))

        val response = transposeChordService.transposeFile(request)
        val responseFile = File(response.resultFile)
        val responseFileLines = responseFile.readLines()

        File(URI("file://$dir/expected_result.txt")).readLines().forEach {
            responseFileLines.contains(it)
        }

        responseFile.delete()
    }

    @Test
    fun testSingleChord() {
        val response = transposeChordService.transposeSingleChord("F#", 3)

        assertEquals(TransposeSingleChordResponse("A"), response)
    }

    @Test
    fun testSingleNegative() {
        val response = transposeChordService.transposeSingleChord("A", -2)

        assertEquals(TransposeSingleChordResponse("G"), response)
    }

    @Test
    fun testSingleComplexChord() {
        val response = transposeChordService.transposeSingleChord("Am7add13", 1)

        assertEquals(TransposeSingleChordResponse("Bbm7add13"), response)
    }
}