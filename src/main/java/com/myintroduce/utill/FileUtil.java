package com.myintroduce.utill;

import com.myintroduce.domain.FileInfo;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileUtil {

    // 파일 이름이 기준 length 보다 길경우 잘라서 리턴
    public static String cutFileName (String fileName, int length) {
        if(fileName.length() <= length) return fileName;

        String extension = fileName.substring(fileName.lastIndexOf("."));
        return fileName.substring(0, length - extension.length()) + extension;
    }

    // 랜덤 파일 이름 리턴
    public static String getRandomFileName(String fileName) {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String extension = fileName.substring(fileName.lastIndexOf("."));
        return RandomStringUtils.randomAlphabetic(5)+ "_" + RandomStringUtils.randomNumeric(5) + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + extension;
    }
    
    // dirPath 에 디렉토리가 없는경우 생성 메서드
    public static void createDir(String dirPath) {
        File file = new File(dirPath);
        if(!file.exists()) file.mkdir();
    }
    
    // 파일이 존재하는 경우 삭제 메서드
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if(file.exists()) file.delete();
    }

    public static FileInfo getFileInfo(String originalName, String domain,
                                       String dirType, String fileUploadPath, String subFileUploadPath) {

        // file parameter setting
        String saveName = FileUtil.getRandomFileName(originalName);
        String fileUrl = domain + dirType + subFileUploadPath + saveName;
        String saveDir = fileUploadPath + subFileUploadPath;
        String filePath =  saveDir + saveName;

        // file 디렉토리 생성
        FileUtil.createDir(saveDir);

        return new FileInfo(filePath, originalName, fileUrl);
    }
}
