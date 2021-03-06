package com.frtelg.chordtransposer.service

import com.frtelg.chordtransposer.dto.request.TransposeChordsInFileRequest
import com.frtelg.chordtransposer.dto.request.TransposeChordsInTextRequest
import com.frtelg.chordtransposer.dto.response.TransposeSingleChordResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.File
import java.net.URI

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TransposeChordService::class])
@TestPropertySource(locations = ["classpath:application.properties"])
class TransposeChordServiceTest {
    private val dir = System.getProperty("user.dir") + "/src/test/resources"

    @Autowired
    lateinit var transposeChordService: TransposeChordService

    @Test
    fun testTransposeText() {
        val inputText = File(URI("file://$dir/test.txt")).readText()
        val request = TransposeChordsInTextRequest(inputText)

        val response = transposeChordService.transposeText(request, 3)

        println(response.transposedText)

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
    fun singleBChord_isNotConfusedWithFlatSign() {
        val response = transposeChordService.transposeSingleChord("B", 3)

        assertEquals(TransposeSingleChordResponse("D"), response)
    }

    @Test
    fun flatSymbolIsAccepted() {
        val response = transposeChordService.transposeSingleChord("B♭", 1)

        assertEquals(TransposeSingleChordResponse("B"), response)
    }

    @Test
    fun testSingleNegative() {
        val response = transposeChordService.transposeSingleChord("A", -2)

        assertEquals(TransposeSingleChordResponse("G"), response)
    }

    @Test
    fun testSingleComplexChord() {
        val response = transposeChordService.transposeSingleChord("Am7add13/G", 1)

        assertEquals(TransposeSingleChordResponse("Bbm7add13/G#"), response)
    }

    @Test
    fun testAlternativeChordNameDb() {
        val response = transposeChordService.transposeSingleChord("Db", 1)

        assertEquals(TransposeSingleChordResponse("D"), response)
    }

    @Test
    fun testAlternativeChordNameAb() {
        val response = transposeChordService.transposeSingleChord("Ab", 1)

        assertEquals(TransposeSingleChordResponse("A"), response)
    }

    @Test
    fun testBug() {
        val response = transposeChordService.transposeSingleChord("Eb/Bb", 1)

        assertEquals(TransposeSingleChordResponse("E/B"), response)
    }

    @Test
    fun testSingleChordUpperCase() {
        val response = transposeChordService.transposeSingleChord("AM7ADD13/G", 1)

        assertEquals(TransposeSingleChordResponse("BbM7ADD13/G#"), response)
    }
}