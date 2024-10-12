package com.back_end.forum.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FolderUtils {
    public static void createDirectories(String filepath) {
        Path path = Paths.get(filepath);

        if (!Files.notExists(path)) {
            return;
        }

        try {
            Files.createDirectories(path);
            log.info("Directory created: {}",  filepath);
        } catch (IOException e) {
            log.error("Failed to create directory: {}", e.getMessage());
        }
    }
}
