package com.forge.talentacquisitionengine.candidateService.resumeParser.controller;

import com.forge.talentacquisitionengine.candidateService.resumeParser.model.ParsedResumeDTO;
import com.forge.talentacquisitionengine.candidateService.resumeParser.service.ResumeParseService;
import com.forge.talentacquisitionengine.candidateService.resumeParser.service.ResumeStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("api/v1/aiengine")
public class AiEngineFileController {

    @Autowired
    ResumeParseService resumeParseService;

    @Autowired
    ResumeStoreService resumeStoreService;


    @PostMapping
    public ResponseEntity<String> saveResume(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        if (file.getContentType()==null||!file.getContentType().equals("application/pdf")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File type not supported");
        }

        String fileName = file.getOriginalFilename();
        if (fileName==null||fileName.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File name is empty");
        }
        if(file.getSize()>10*1024*1024){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String response = resumeStoreService.saveResume(file);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/parse")
    public ResponseEntity<ParsedResumeDTO> parseResume(@RequestParam("file") MultipartFile file) {
        ParsedResumeDTO parsedResumeDTO = resumeParseService.parseResume(file);
        return parsedResumeDTO == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).build() : ResponseEntity.status(HttpStatus.OK).body(parsedResumeDTO);
    }



}
