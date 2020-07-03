package com.frtelg.chordtransposer.controller.chordtransposer

import com.frtelg.chordtransposer.dto.request.TransposeChordsInFileRequest
import com.frtelg.chordtransposer.dto.response.TransposeChordsInFileResponse
import com.frtelg.chordtransposer.dto.response.TransposeSingleChordResponse
import com.frtelg.chordtransposer.service.TransposeChordService
import org.springframework.web.bind.annotation.*

@RestController
class TransposeChordController(val transportChordService: TransposeChordService) {

    @GetMapping("/transpose/{chord}/{steps}")
    fun transposeSingleChord(@PathVariable("chord") chord: String,
                             @PathVariable("steps") steps: Int): TransposeSingleChordResponse =
            transportChordService.transposeSingleChord(chord, steps)

    @PostMapping("/transpose")
    fun transposeFile(@RequestBody request: TransposeChordsInFileRequest): TransposeChordsInFileResponse =
            transportChordService.transposeFile(request)
}