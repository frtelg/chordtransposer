package com.frtelg.chordtransposer.service

import com.frtelg.chordtransposer.dto.enums.MajorChord
import com.frtelg.chordtransposer.dto.request.TransposeChordsInFileRequest
import com.frtelg.chordtransposer.dto.response.TransposeChordsInFileResponse
import com.frtelg.chordtransposer.dto.response.TransposeSingleChordResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@Service
class TransposeChordService {
    val log = LoggerFactory.getLogger(this.javaClass)
    val chordRegex: Regex = "([A-G][#b]?(maj|m)?[2-9]?(sus|add|dim)?([1-9][0-9]?)?(/[A-G])?)".toRegex()

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

    private fun determineTargetFolder(request: TransposeChordsInFileRequest): Path {
        val requestPath = request.targetFolder?.path ?: System.getProperty("user.home")

        return Files.createFile(Path.of(requestPath, "transposed_file" + UUID.randomUUID() + ".txt"))
    }

    private fun transposeLine(line: String, steps: Int, writer: BufferedWriter) {
        if ("^(\\W+)?$chordRegex(\\W|\$)".toRegex().containsMatchIn(line)) {
            writer.append(transposeChordsFromString(line, steps))
        } else {
            writer.append(line)
        }

        writer.newLine()
    }

    private fun transposeChordsFromString(string: String, transposeSteps: Int): String =
            string.replace(chordRegex) { r ->
                val matchingChord = r.value
                matchingChord.replace("[A-G][b#]?".toRegex()) {
                    transposeChord(it.value, transposeSteps).stringValue
                }
            }

    private fun transposeChord(chord: String, transposeSteps: Int): MajorChord {
        val chordEnum = MajorChord.findByName(chord) ?: throw IllegalArgumentException("Non-existing chord")
        val steps = chordEnum.ordinal + transposeSteps
        val newChordOrdinal = Math.floorMod(steps, enumValues<MajorChord>().size)

        return MajorChord.findById(newChordOrdinal)
                ?: throw IllegalArgumentException("The chord transposer calculated a non-existing new chord. This should never occur, notify the developer!")
    }

}