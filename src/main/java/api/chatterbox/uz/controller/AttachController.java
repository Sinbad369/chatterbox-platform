package api.chatterbox.uz.controller;

import api.chatterbox.uz.dto.AttachDTO;
import api.chatterbox.uz.service.AttachService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/attach")
@Tag(name = "AttachController", description = "API set for working with Attach")
public class AttachController {
    @Autowired
    private AttachService attachService;

    @PostMapping("/upload")
    @Operation(summary = "Upload attach", description = "Uploads a file and returns its metadata")
    public ResponseEntity<AttachDTO> create(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attachService.upload(file));
    }

    @GetMapping("/open/{fileName}")
    @Operation(summary = "Open attach", description = "Retrieves and returns the file by its name")
    public ResponseEntity<Resource> open(@PathVariable String fileName) {
        return attachService.open(fileName);
    }

}
