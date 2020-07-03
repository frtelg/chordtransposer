package com.frtelg.chordtransposer.dto.response

import com.frtelg.chordtransposer.dto.enum.MajorChord

data class TransposeSingleChordResponse(val transposedChord: String) {
    companion object {
        fun fromMajorChord(transposedChord: MajorChord) = TransposeSingleChordResponse(transposedChord.stringValue)
    }
}