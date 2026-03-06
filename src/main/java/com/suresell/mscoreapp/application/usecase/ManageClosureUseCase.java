package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.ClosureResponse;
import com.suresell.mscoreapp.domain.model.DailyClosureEntity;
import com.suresell.mscoreapp.domain.port.out.DailyClosureRepository;
import com.suresell.mscoreapp.shared.enums.ClosureStatus;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageClosureUseCase {

    private final DailyClosureRepository repository;
    private final DailyClosureMapper mapper;

    @Transactional(readOnly = true)
    public List<ClosureResponse> getClosureHistory() {
        return mapper.toResponseList(repository.findAllOrderByClosingTimeDesc());
    }

    @Transactional(readOnly = true)
    public byte[] exportToExcel() throws IOException {
        List<DailyClosureEntity> closures = repository.findAllOrderByClosingTimeDesc();
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Historial de Cierres");

            // --- ESTILOS ---
            
            // Estilo Encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Estilo Moneda
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("$ #,##0.00"));

            // Estilos de Estado (Semaforización)
            CellStyle balancedStyle = createStatusStyle(workbook, IndexedColors.LIGHT_GREEN);
            CellStyle positiveStyle = createStatusStyle(workbook, IndexedColors.LIGHT_YELLOW);
            CellStyle negativeStyle = createStatusStyle(workbook, IndexedColors.CORAL);

            // --- ENCABEZADOS ---
            String[] columns = {"Fecha Cierre", "Cajero", "Efectivo Esperado", "Efectivo Contado", "Diferencia", "Estado", "Base Sig. Día", "Notas"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- DATOS ---
            int rowIdx = 1;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            for (DailyClosureEntity closure : closures) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(closure.getClosingTime().format(dateFormatter));
                row.createCell(1).setCellValue(closure.getUserName());
                
                Cell expectedCell = row.createCell(2);
                expectedCell.setCellValue(closure.getTotalExpectedCash().doubleValue());
                expectedCell.setCellStyle(currencyStyle);

                Cell countedCell = row.createCell(3);
                countedCell.setCellValue(closure.getTotalCountedCash().doubleValue());
                countedCell.setCellStyle(currencyStyle);

                Cell diffCell = row.createCell(4);
                diffCell.setCellValue(closure.getDifferenceAmount().doubleValue());
                diffCell.setCellStyle(currencyStyle);

                Cell statusCell = row.createCell(5);
                statusCell.setCellValue(closure.getStatus().toString());
                
                // Aplicar semaforización
                if (closure.getStatus() == ClosureStatus.BALANCED) statusCell.setCellStyle(balancedStyle);
                else if (closure.getStatus() == ClosureStatus.POSITIVE_DIFF) statusCell.setCellStyle(positiveStyle);
                else if (closure.getStatus() == ClosureStatus.NEGATIVE_DIFF) statusCell.setCellStyle(negativeStyle);

                Cell baseCell = row.createCell(6);
                baseCell.setCellValue(closure.getBaseBalanceForNextDay().doubleValue());
                baseCell.setCellStyle(currencyStyle);

                row.createCell(7).setCellValue(closure.getNotes());
            }

            // Auto-ajustar columnas
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private CellStyle createStatusStyle(Workbook workbook, IndexedColors color) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}
