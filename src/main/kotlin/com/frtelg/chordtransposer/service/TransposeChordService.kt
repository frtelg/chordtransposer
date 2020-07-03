package com.frtelg.chordtransposer.service

import com.frtelg.chordtransposer.dto.enum.MajorChord
import com.frtelg.chordtransposer.dto.request.TransposeChordsInFileRequest
import com.frtelg.chordtransposer.dto.response.TransposeChordsInFileResponse
import com.frtelg.chordtransposer.dto.response.TransposeSingleChordResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@Service
class TransposeChordService {
    val log = LoggerFactory.getLogger(this.javaClass)
    val chordRegex: Regex = "([A-G][#b]?[2-9]?(sus|add|maj|dim|m)?([1-9][0-9])?(/[A-G])?)(\\W|\$)".toRegex()

    fun transposeSingleChord(string: String, transposeSteps: Int): TransposeSingleChordResponse {
        if (string.contains("\\s".toRegex())) {
            throw IllegalArgumentException("Only one chord is allowed here!")
        }

        val transposedChord = transposeChordsFromString(string, transposeSteps)
        log.info("Successfully transposed chord {} by {} tones: {}", string, transposeSteps, transposedChord)

        return TransposeSingleChordResponse(transposeChordsFromString(string, transposeSteps))
    }

    fun transposeFile(request: TransposeChordsInFileRequest): TransposeChordsInFileResponse {
        val newFile = Files.createFile(Path.of(System.getProperty("user.home"), "file" + UUID.randomUUID() + ".txt"))
        val writer = newFile.toFile().bufferedWriter()

        File(request.file).bufferedReader().readLines().forEach {s ->
            writer.append(transposeChordsFromString(s, request.steps))
            writer.newLine()
        }

        writer.close()

        return TransposeChordsInFileResponse(newFile.toUri())
    }

    private fun transposeChordsFromString(string: String, transposeSteps: Int): String =
            string.replace(chordRegex) { r ->
                val matchingChord = r.value.replace("[^/#)\\w]".toRegex(), "")
                val newChord = matchingChord.replace("[A-G][b#]?".toRegex()) { r ->
                    transposeChord(r.value, transposeSteps).stringValue
                }

                if (r.value.contains(" ")) "$newChord "
                else newChord
            }

    private fun transposeChord(chord: String, transposeSteps: Int): MajorChord {
        val chordEnum = MajorChord.findByName(chord) ?: throw IllegalArgumentException("Non-existing chord")
        val steps = chordEnum.ordinal + transposeSteps
        val newChordOrdinal = Math.floorMod(steps, enumValues<MajorChord>().size)

        return MajorChord.findById(newChordOrdinal)
                ?: throw IllegalArgumentException("The chord transposer calculated a non-existing new chord. This should never occur, notify the developer!")
    }

}