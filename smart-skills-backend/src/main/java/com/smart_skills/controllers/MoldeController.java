package com.smart_skills.controllers;

import com.smart_skills.dto.ImportResultDTO;
import com.smart_skills.enums.Company;
import com.smart_skills.models.Molde;
import com.smart_skills.repository.MoldeRepository;
import com.smart_skills.service.MoldeImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

    @RestController
    @RequestMapping("/api/moldes")
    public class MoldeController {

        private final MoldeRepository moldeRepository;
        private final MoldeImportService moldeImportService;


        public MoldeController(MoldeRepository moldeRepository, MoldeImportService moldeImportService) {
            this.moldeRepository = moldeRepository;
            this.moldeImportService = moldeImportService;
        }


        @GetMapping
        public List<Molde> getAllMoldes() {
            return moldeRepository.findAll();
        }

        @GetMapping("/{wkzBauerNr}")
        public Molde getMoldeByWKZ(@PathVariable Long wkzBauerNr) {
            return moldeRepository.findByWkzBauerNr(wkzBauerNr)
                    .orElseThrow(() -> new RuntimeException("Molde not found with WKZ: " + wkzBauerNr));
        }

        @PostMapping(value = "/upload", consumes = "multipart/form-data")
        public ResponseEntity<List<ImportResultDTO>> upload(
                @RequestParam("files") List<MultipartFile> files,
                @RequestParam("companies") List<String> companies
        ) {
            System.out.println("FILES:");
            files.forEach(f -> System.out.println(f.getOriginalFilename()));

            System.out.println("COMPANIES:");
            companies.forEach(System.out::println);

            if (files == null || files.isEmpty()) {
                throw new RuntimeException("No files were sent.");
            }

            if (companies == null || companies.isEmpty()) {
                throw new RuntimeException("No companies were sent.");
            }

            if (files.size() != companies.size()) {
                throw new RuntimeException("Each file must have a matching company.");
            }

            List<ImportResultDTO> results = new ArrayList<>();

            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String companyRaw = companies.get(i);

                if (companyRaw == null || companyRaw.isBlank()) {
                    throw new RuntimeException("Company is missing for file: " + file.getOriginalFilename());
                }

                Company company;
                try {
                    company = Company.valueOf(companyRaw.trim().toUpperCase());
                } catch (Exception e) {
                    throw new RuntimeException("Invalid company value: " + companyRaw);
                }

                ImportResultDTO result = moldeImportService.importExcel(file, company);
                results.add(result);
            }

            return ResponseEntity.ok(results);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleException(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

