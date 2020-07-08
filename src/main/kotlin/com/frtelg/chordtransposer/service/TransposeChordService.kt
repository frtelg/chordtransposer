package com.frtelg.chordtransposer.service

import com.frtelg.chordtransposer.dto.enums.MajorChord
import com.frtelg.chordtransposer.dto.request.TransposeChordsInFileRequest
import com.frtelg.chordtransposer.dto.request.TransposeChordsInTextRequest
import com.frtelg.chordtransposer.dto.response.TransposeChordsInFileResponse
import com.frtelg.chordtransposer.dto.response.TransposeChordsInTextResponse
import com.frtelg.chordtransposer.dto.response.TransposeSingleChordResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@Service
class TransposeChordService constructor(@Value("\${frtelg.chordtransposer.outputfolder}") val outputDirectory: String) {
    val log = LoggerFactory.getLogger(this.javaClass)

    val chordRegex: Regex = "([A-G][#b]?(?i)(maj|m)?[2-9]?(sus|add|dim)?(?-i)([1-9][0-9]?)?(/[A-G])?)".toRegex()
    val majorChordRegex: String = "[A-G][b#]?"

    fun transposeSingleChord(string: String, transposeSteps: Int): TransposeSingleChordResponse {
        if (string.contains("\\s".toRegex())) {
            throw IllegalArgumentException("Only one chord is allowed here!")
        }

        val transposedChord = transposeChordsFromString(string, transposeSteps)
        log.info("Successfully transposed chord {} by {} tones: {}", string, transposeSteps, transposedChord)

        return TransposeSingleChordResponse(transposeChordsFromString(string, transposeSteps))
    }

    fun transposeFile(request: TransposeChordsInFileRequest): TransposeChordsInFileResponse {
        val newFile = determineTargetFolder(request)
        val writer = newFile.toFile().bufferedWriter()

        File(request.file).bufferedReader().readLines().forEach { transposeLine(it, request.steps, writer) }

        writer.close()

        return TransposeChordsInFileResponse(newFile.toUri())
    }

    fun transposeText(request: TransposeChordsInTextRequest, steps: Int): TransposeChordsInTextResponse {
        val transposedText = request.text.split("\n")
                .map { transposeLine(it, steps) }
                .joinToString("\n")

        return TransposeChordsInTextResponse(transposedText)
    }

    private fun determineTargetFolder(request: TransposeChordsInFileRequest): Path {
        val requestPath = request.targetFolder?.path ?: outputDirectory

        return Files.createFile(Path.of(requestPath, "transposed_file" + UUID.randomUUID() + ".txt"))
    }

    private fun transposeLine(line: String, steps: Int, writer: BufferedWriter) {
        writer.append(transposeLine(line, steps))

        writer.newLine()
    }

    private fun transposeLine(line:String, steps: Int): String =
        if ("^(\\W+)?$chordRegex(\\W|\$)".toRegex().containsMatchIn(line)) {
            transposeChordsFromString(line, steps)
        } else {
            line
        }

    private fun transposeChordsFromString(string: String, transposeSteps: Int): String =
            string.replace(chordRegex) { r ->
                val matchingChord = r.value
                // capture note in the base as well
                matchingChord.replace("(^$majorChordRegex|/$majorChordRegex)".toRegex()) {
                    val transposedChord = transposeChord(it.value.replace("/", ""), transposeSteps).stringValue

                    if (it.value.matches("^/.+".toRegex())) "/" + transposedChord
                    else transposedChord
                }
            }

    private fun transposeChord(chord: String, transposeSteps: Int): MajorChord {
        val chordEnum = MajorChord.findByName(chord) ?: throw IllegalArgumentException("Non-existing chord: $chord")
        val steps = chordEnum.ordinal + transposeSteps
        val newChordOrdinal = Math.floorMod(steps, enumValues<MajorChord>().size) // use Math.floorMod in order to enable negative modulo as well

        return MajorChord.findById(newChordOrdinal)
                ?: throw IllegalArgumentException("The chord transposer calculated a non-existing new chord. This should never occur, notify the developer!")
    }
}