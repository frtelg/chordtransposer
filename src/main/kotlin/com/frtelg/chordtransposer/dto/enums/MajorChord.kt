package com.frtelg.chordtransposer.dto.enums

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
        private val flat = "(?i)(flat|♭|(?-i)b)".toRegex()
        private val sharp = "(?i)(sharp|#)".toRegex()

        fun findByName(name: String): MajorChord? {
            val isFlat = name.contains(flat)
            val isSharp = name.contains(sharp)

            val closestPureChordIndex = enumValueOf<MajorChord>(name[0].toString()).ordinal

            return when {
                isFlat -> findByIndex(closestPureChordIndex - 1)
                isSharp -> findByIndex(closestPureChordIndex + 1)
                else -> findByIndex(closestPureChordIndex)
            }
        }

        fun findByIndex(id: Int): MajorChord? = enumValues<MajorChord>().firstOrNull { e ->
            e.ordinal == Math.floorMod(id, values().size) // use Math.floorMod in order to enable negative modulo as well)
        }

    }
}