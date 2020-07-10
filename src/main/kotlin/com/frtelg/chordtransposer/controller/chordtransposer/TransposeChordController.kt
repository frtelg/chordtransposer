package com.frtelg.chordtransposer.controller.chordtransposer

import com.frtelg.chordtransposer.dto.request.TransposeChordsInFileRequest
import com.frtelg.chordtransposer.dto.request.TransposeChordsInTextRequest
import com.frtelg.chordtransposer.dto.response.TransposeChordsInFileResponse
import com.frtelg.chordtransposer.dto.response.TransposeChordsInTextResponse
import com.frtelg.chordtransposer.dto.response.TransposeSingleChordResponse
import com.frtelg.chordtransposer.service.TransposeChordService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["*"])
class TransposeChordController(val transportChordService: TransposeChordService) {

    @GetMapping("/transpose/{chord}/{steps}")
    fun transposeSingleChord(@PathVariable("chord") chord: String,
                             @PathVariable("steps") steps: Int): TransposeSingleChordResponse =
            transportChordService.transposeSingleChord(chord, steps)

    @PostMapping("/transpose")
    fun transposeFile(@RequestBody request: TransposeChordsInFileRequest): TransposeChordsInFileResponse =
            transportChordService.transposeFile(request)

    @PostMapping("/transpose/{steps}")
    fun transposeText(@PathVariable steps: Int, @RequestBody request: TransposeChordsInTextRequest): TransposeChordsInTextResponse =
            transportChordService.transposeText(request, steps)
}