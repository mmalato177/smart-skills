package com.smart_skills.service;

import com.smart_skills.dto.ImportResultDTO;
import com.smart_skills.enums.Company;
import com.smart_skills.enums.Status;
import com.smart_skills.models.Molde;
import com.smart_skills.repository.MoldeRepository;
import org.apache.poi.ss.usermodel.*;
        import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;

@Service
public class MoldeImportService {

    private final MoldeRepository moldeRepository;

    public MoldeImportService(MoldeRepository moldeRepository) {
        this.moldeRepository = moldeRepository;
    }

    public ImportResultDTO importExcel(MultipartFile file, Company company){
        int created = 0;
        int updated = 0;
        int skipped = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet("Dashboard");
            if (sheet == null) {
                throw new RuntimeException("Sheet 'Dashboard' not found.");
            }

            Iterator<Row> rows = sheet.iterator();
            if (!rows.hasNext()) {
                throw new RuntimeException("Sheet 'Dashboard' is empty.");
            }

            Row headerRow = rows.next();
            for (Cell cell : headerRow) {
                System.out.println("HEADER [" + cell.getColumnIndex() + "]: '" + getStringCellValue(cell) + "'");
            }

            int orderCol = requireColumnIndex(headerRow, "Order");
            int wisCol = requireColumnIndex(headerRow, "WIS");
            int poDateCol = requireColumnIndex(headerRow, "PO date");
            int poDeliveryDateCol = requireColumnIndex(headerRow, "PO Delivery date");
            int anticipatedDeliveryDateCol = requireColumnIndex(headerRow, "Anticipated delivery date");
            int poQualificationEndDateCol = requireColumnIndex(headerRow, "PO Qualification end date");
            int anticipatedQualificationEndDateCol = requireColumnIndex(headerRow, "Anticipated qualification end date");
            int statusCol = requireColumnIndex(headerRow, "Status");
            int dateCol = requireColumnIndex(headerRow, "Date");
            int anticipatedEndDateCol = requireColumnIndex(headerRow, "Anticipated end date");
            int statusRemarksCol = requireColumnIndex(headerRow, "Status Remarks");
            int responsibleDeveloperCol = requireColumnIndex(headerRow, "Responsible developer");
            int wkzCol = findColumnIndex(headerRow, "WKZ");
            if (wkzCol == -1) {
                wkzCol = findColumnIndex(headerRow, "WKZ-Bauer Nr.");
            }
            if (wkzCol == -1) {
                throw new RuntimeException("Required column not found in Excel: WKZ / WKZ Bauer Nr.");
            }


            while (rows.hasNext()) {
                Row row = rows.next();

                Long wkzBauerNr = getLongCellValue(row.getCell(wkzCol));
                if (wkzBauerNr == null) {
                    skipped++;
                    continue;
                }

                Optional<Molde> existing = moldeRepository.findByWkzBauerNr(wkzBauerNr);

                Molde molde;
                if (existing.isPresent()) {
                    molde = existing.get();
                    updated++;
                } else {
                    molde = new Molde();
                    molde.setWkzBauerNr(wkzBauerNr);
                    created++;
                }

                molde.setOrder(getStringCellValue(row.getCell(orderCol)));
                molde.setWis(getStringCellValue(row.getCell(wisCol)));
                molde.setPoDate(getDateCellValue(row.getCell(poDateCol)));
                molde.setPoDeliveryDate(getDateCellValue(row.getCell(poDeliveryDateCol)));
                molde.setAnticipatedDeliveryDate(getDateCellValue(row.getCell(anticipatedDeliveryDateCol)));
                molde.setPoQualificationEndDate(getDateCellValue(row.getCell(poQualificationEndDateCol)));
                molde.setAnticipatedQualificationEndDate(getDateCellValue(row.getCell(anticipatedQualificationEndDateCol)));
                molde.setStatus(parseStatus(getStringCellValue(row.getCell(statusCol))));
                molde.setDate(getDateCellValue(row.getCell(dateCol)));
                molde.setAnticipatedEndDate(getDateCellValue(row.getCell(anticipatedEndDateCol)));
                molde.setStatusRemarks(getStringCellValue(row.getCell(statusRemarksCol)));
                molde.setResponsibleDeveloper(getStringCellValue(row.getCell(responsibleDeveloperCol)));
                molde.setCompany(company);

                moldeRepository.saveAndFlush(molde);
            }
            System.out.println("FINAL RESULT -> created=" + created + ", updated=" + updated + ", skipped=" + skipped);
            return new ImportResultDTO(created, updated, skipped);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao importar Excel: " + e.getMessage(), e);
        }
    }

    private int findColumnIndex(Row headerRow, String columnName) {
        String expected = normalizeHeader(columnName);

        for (Cell cell : headerRow) {
            String value = getStringCellValue(cell);
            if (normalizeHeader(value).equals(expected)) {
                return cell.getColumnIndex();
            }
        }
        return -1;
    }

    private String normalizeHeader(String value) {
        if (value == null) return "";
        return value.trim()
                .replace("\n", " ")
                .replace("\r", " ")
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    private int requireColumnIndex(Row headerRow, String columnName) {
        int index = findColumnIndex(headerRow, columnName);
        if (index < 0) {
            throw new RuntimeException("Required column not found in Excel: " + columnName);
        }
        return index;
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> {
                String value = cell.getStringCellValue();
                yield value == null || value.isBlank() ? null : value.trim();
            }
            case NUMERIC -> {
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num)) {
                    yield String.valueOf((long) num);
                }
                yield String.valueOf(num);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }

    private Long getLongCellValue(Cell cell) {
        if (cell == null) return null;

        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> (long) cell.getNumericCellValue();
                case STRING -> {
                    String value = cell.getStringCellValue();
                    yield (value == null || value.isBlank()) ? null : Long.parseLong(value.trim());
                }
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate getDateCellValue(Cell cell) {
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            }

            if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue();
                if (value == null || value.isBlank()) return null;
                return LocalDate.parse(value.trim());
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private Status parseStatus(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim()
                .replace(" ", "_")
                .replace("-", "_")
                .toUpperCase();

        try {
            return Status.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value in Excel: " + value);
        }
    }
}
