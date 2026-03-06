package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.dto.ClosureResponse;
import com.suresell.mscoreapp.application.usecase.ManageClosureUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/closures")
@RequiredArgsConstructor
@Tag(name = "Closures Management", description = "Operaciones para la administración de cierres de caja")
public class ClosureController {

    private final ManageClosureUseCase manageClosureUseCase;

    @GetMapping("/history")
    @Operation(summary = "Listar historial de cierres de caja")
    public List<ClosureResponse> getClosureHistory() {
        return manageClosureUseCase.getClosureHistory();
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Exportar historial de cierres a Excel")
    public ResponseEntity<byte[]> exportToExcel() throws IOException {
        byte[] excelContent = manageClosureUseCase.exportToExcel();
        
        if (excelContent == null || excelContent.length == 0) {
            return ResponseEntity.noContent().build();
        }

        String fileName = "historial_cierres_caja_" + LocalDate.now() + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }
}
