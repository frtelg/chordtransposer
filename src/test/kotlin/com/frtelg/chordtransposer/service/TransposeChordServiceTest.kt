package com.frtelg.chordtransposer.service

import com.frtelg.chordtransposer.dto.request.TransposeChordsInFileRequest
import com.frtelg.chordtransposer.dto.request.TransposeChordsInTextRequest
import com.frtelg.chordtransposer.dto.response.TransposeSingleChordResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URI

class TransposeChordServiceTest {
    val dir = System.getProperty("user.dir") + "/src/test/resources"

    private val transposeChordService: TransposeChordService = TransposeChordService()

    @Test
    fun testTransposeText() {
        val inputText = File(URI("file://$dir/test.txt")).readText()
        val request = TransposeChordsInTextRequest(inputText)

        val response = transposeChordService.transposeText(request, 3)

        val responseContent = response.transposedText.split("\n").iterator()
        val expectedFileContent = File(URI("file://$dir/expected_result.txt")).readLines().iterator()

        responseContent.forEachRemaining{ assertTrue(expectedFileContent.next().trimEnd() == it.trimEnd()) }
    }

    @Test
    fun testFileNoTargetFolder() {
        val request = TransposeChordsInFileRequest(URI("file://$dir/test.txt"), 3, null)

        val response = transposeChordService.transposeFile(request)
        val responseFile = File(response.resultFile)
        assertTrue(responseFile.absolutePath.contains("${System.getProperty("user.home")}/transposed_file"))

        val responseFileContent = responseFile.readLines().iterator()
        val expectedFileContent = File(URI("file://$dir/expected_result.txt")).readLines().iterator()

        responseFileContent.forEachRemaining{ assertTrue(expectedFileContent.next().trimEnd() == it.trimEnd()) }

        responseFile.delete()
    }

    @Test
    fun testFileWithTargetFolder() {
        val request = TransposeChordsInFileRequest(URI("file://$dir/test.txt"), 3, URI("file://$dir"))

        val response = transposeChordService.transposeFile(request)
        val responseFile = File(response.resultFile)
        assertTrue(responseFile.absolutePath.contains(dir))

        val responseFileContent = responseFile.readLines().iterator()
        val expectedFileContent = File(URI("file://$dir/expected_result.txt")).readLines().iterator()

        responseFileContent.forEachRemaining{ assertTrue(expectedFileContent.next().trimEnd() == it.trimEnd()) }

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