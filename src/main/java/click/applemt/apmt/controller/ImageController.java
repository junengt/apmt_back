package click.applemt.apmt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ImageController {
    private final ResourceLoader resourceLoader;

    @GetMapping("/image")
    public ResponseEntity<Resource> showImage(@RequestParam Map<String, String> param) {
        File file = null;

        String absolPath = new File("").getAbsolutePath() + "\\";
        String testPath = "images/";
        String path = absolPath + param.get("path");


        Resource resource = new FileSystemResource(path);
        if(!resource.exists()) {
            System.out.println("FILE : NOT Found");
            return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        Path filePath = null;
        try {
            filePath = Paths.get(path);
            headers.add("Content-Type", Files.probeContentType(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }
}
