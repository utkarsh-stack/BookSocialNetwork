package com.utkarsh.stack.book.file;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileUploadService {

    @Value("${application.file.upload.book-cover-output-path}")
    private String fileUploadDir;

    public String saveFile(
            @NotNull MultipartFile file,
            @NotNull Integer userId){

        final String finalUploadFolder = fileUploadDir + File.separator + "users" + File.separator + userId;

        // Create folder to store file, every user will have their folder
        File targetFolder = new File(finalUploadFolder);
        if(!targetFolder.exists()){
            boolean folderCreated = targetFolder.mkdirs();
            if(!folderCreated) {
                log.warn("Failed to create parent folder to create file: " + finalUploadFolder);
                return null;
            }
        }
        // Now create file at this folder
        final String fileExtension = getExtension(file.getOriginalFilename());
        String targetFilePath = finalUploadFolder + File.separator + System.currentTimeMillis() +fileExtension;

        Path targetPath = Paths.get(finalUploadFolder);
        try{
            Files.write(targetPath, file.getBytes());
            log.info("Saved file successfully");
            return targetFilePath;

        }catch (Exception e){
            log.error("Failed to upload file: "+ targetFilePath);
            return null;
        }
    }
    public String getExtension(String fileName){
        if(fileName==null || fileName.isEmpty())
            return ".";
        String[] fileNameParts =  fileName.split(".");
        if(fileNameParts.length==0)
            return ".";
        return fileNameParts[fileNameParts.length-1];
    }
}
