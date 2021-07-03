package com.myintroduce.utill;

import com.myintroduce.domain.FileInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class FileUtilTest {

    @Test
    void cutFileName_test() {
        // given
        String fileName = "파일이름.txt";
        int length = 10;

        // when
        String cutFileName = FileUtil.cutFileName(fileName, length);

        // then
        assertThat(cutFileName).isEqualTo(fileName);
    }

    @Test
    void cutFileName_over_test() {
        // given
        String fileName = "파일이름.txt";
        int length = 5;

        // when
        String cutFileName = FileUtil.cutFileName(fileName, length);

        // then
        assertThat(cutFileName).isEqualTo("파.txt");
    }
    
    @Test
    void getRandomFileName_test() throws Exception {
        // given
        String fileName = "파일이름.txt";

        // when
        String randomFileName = FileUtil.getRandomFileName(fileName);

        // then
        String[] split = randomFileName.split("_");
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertAll(
                () -> assertThat(split[0]).matches("^[a-zA-Z]{5}$"),
                () -> assertThat(split[1]).matches("^[0-9]{5}$"),
                () -> assertThat(split[2]).isEqualTo(now+".txt")
        );
    }
}