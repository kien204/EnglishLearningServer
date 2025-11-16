package com.example.english_learning.controller.grammar;

import com.example.english_learning.dto.request.grammar.GrammarItemRequest;
import com.example.english_learning.service.grammar.GrammarItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/grammarItem")
public class GrammarItemController {

    @Autowired
    private GrammarItemService grammarItemService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody GrammarItemRequest request) {
        return grammarItemService.create(request);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody GrammarItemRequest request
    ) {
        return grammarItemService.update(id, request);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        return grammarItemService.getAlḷ();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return grammarItemService.delete(id);
    }

    @PostMapping(
            value = "/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File trống"));
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không xác định được tên file"));
        }

        try {
            if (fileName.endsWith(".csv") || fileName.endsWith(".txt")) {
                return grammarItemService.importCsv(file);

            } else if (fileName.endsWith(".xlsx")) {
                return grammarItemService.importXlsx(file);

            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Định dạng không hỗ trợ. Chỉ CSV, TXT, XLSX."));
            }

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Lỗi khi đọc file: " + e.getMessage()));
        }
    }
}

