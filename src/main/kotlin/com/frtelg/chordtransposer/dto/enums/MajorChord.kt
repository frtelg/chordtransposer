package com.frtelg.chordtransposer.dto.enums

import java.lang.IllegalArgumentException

enum class MajorChord(val stringValue: String) {
    A("A"),
    Bflat("Bb"),
    B("B"),
    C("C"),
    Csharp("C#"),
    D("D"),
    Eflat("Eb"),
    E("E"),
    F("F"),
    Fsharp("F#"),
    G("G"),
    Gsharp("G#");

    companion object {
        private val sharpOrFlatRegex = "(?i)(sharp|flat)".toRegex()
        private val flat = "FLAT"
        private val sharp = "SHARP"

        fun findByName(name: String): MajorChord? {
            val refactoredName = if (name.contains(sharpOrFlatRegex)) decorateSharpOrFlat(name)
                                 else name

            return enumValues<MajorChord>().firstOrNull { s ->
                s.stringValue == refactoredName
            }
        }

        fun findById(id: Int): MajorChord? = enumValues<MajorChord>().firstOrNull { e ->
            e.ordinal == id
        }

        private fun decorateSharpOrFlat(string: String): String {
            val upperCaseString = string.toUpperCase()

            if (upperCaseString == flat) return string.replace(flat, "b")
            else if (upperCaseString == sharp) return string.replace(sharp, "#")

            throw IllegalArgumentException("Only $flat or $sharp is expected to be decorated")
        }
    }
}