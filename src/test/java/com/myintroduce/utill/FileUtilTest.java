package com.myintroduce.utill;

import com.myintroduce.domain.FileInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class FileUtilTest {

    @Test
    public void cutFileName_test() {
        // given
        String fileName = "파일이름.txt";
        int length = 10;

        // when
        String cutFileName = FileUtil.cutFileName(fileName, length);

        // then
        assertThat(cutFileName).isEqualTo(fileName);
    }

    @Test
    public void cutFileName_over_test() {
        // given
        String fileName = "파일이름.txt";
        int length = 5;

        // when
        String cutFileName = FileUtil.cutFileName(fileName, length);

        // then
        assertThat(cutFileName).isEqualTo("파.txt");
    }
    
    @Test
    public void getRandomFileName_test() throws Exception {
        // given
        String fileName = "파일이름.txt";

        // when
        String randomFileName = FileUtil.getRandomFileName(fileName);

        // then
        String[] split = randomFileName.split("_");
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertThat(split[0].matches("^[a-zA-Z]{5}$")).isEqualTo(true);
        assertThat(split[1].matches("^[0-9]{5}$")).isEqualTo(true);
        assertThat(split[2]).isEqualTo(now+".txt");
    }

    @Test
    public void getFileInfo_test() {
        // given
        String originalName = "파일이름.txt";
        String domain = "http://localhost:8080/";
        String dirType = "images/";
        String fileUploadPath = "C:/";
        String subFileUploadPath = "member/";

        // when
        FileInfo fileInfo = FileUtil.getFileInfo(originalName, domain, dirType, fileUploadPath, subFileUploadPath);

        // then
        String fileUrl = domain + dirType + subFileUploadPath;
        String saveDir = fileUploadPath + subFileUploadPath;
        assertThat(fileInfo.getFileUrl()).contains(fileUrl);
        assertThat(fileInfo.getFilePath()).contains(saveDir);
        assertThat(fileInfo.getFileOriginName()).isEqualTo(originalName);
    }
}