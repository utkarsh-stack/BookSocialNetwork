package com.utkarsh.stack.book.file;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileUtils {
    public static byte[] readFileFromLocation(String bookCover) {
        if(StringUtils.isBlank(bookCover)){
            return null;
        }
        try{
            Path filePath = new File(bookCover).toPath();
            return Files.readAllBytes(filePath);

        }catch (IOException e){
            log.warn("File not found or unable to read at: "+ bookCover);
            return null;
        }
    }
}
