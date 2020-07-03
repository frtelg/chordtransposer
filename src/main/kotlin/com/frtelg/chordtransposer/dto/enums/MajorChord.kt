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
        fun findByName(name: String): MajorChord? = enumValues<MajorChord>().firstOrNull { s ->
            s.stringValue == name
        }

        fun findById(id: Int): MajorChord? = enumValues<MajorChord>().firstOrNull { e ->
            e.ordinal == id
        }


    }
}