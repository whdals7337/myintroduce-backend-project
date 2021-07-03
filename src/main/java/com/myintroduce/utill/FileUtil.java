package com.myintroduce.utill;

import com.myintroduce.error.exception.file.NoSupportedBrowserException;
import org.apache.commons.lang3.RandomStringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

public class FileUtil {

    private FileUtil() {
        throw new IllegalStateException("Utility class");
    }

    // 파일 이름이 기준 length 보다 길경우 잘라서 리턴 메서드
    public static String cutFileName (String fileName, int length) {
        if(fileName.length() <= length) {
            return fileName;
        }

        String extension = fileName.substring(fileName.lastIndexOf("."));
        return fileName.substring(0, length - extension.length()) + extension;
    }

    // 랜덤 파일 이름 리턴 메서드
    public static String getRandomFileName(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf("."));
        return RandomStringUtils.randomAlphabetic(5)+ "_" + RandomStringUtils.randomNumeric(5) + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + extension;
    }

    public static String getFileNameByBrowser(String fileName, HttpServletRequest request)
            throws UnsupportedEncodingException, NoSupportedBrowserException {
        String browser= "";
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if(headerName.equals("user-agent")) {
                browser = request.getHeader(headerName);
            }
        }

        String docName = "";
        if (browser.contains("Trident") || browser.contains("MSIE") || browser.contains("Edge")) {
            docName = FileUtil.mappingSpecialCharacter(URLEncoder.encode(fileName, "UTF-8"));

        } else if (browser.contains("Firefox")) {
            docName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        } else if (browser.contains("Opera")) {
            docName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        } else if (browser.contains("Chrome")) {
            docName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        } else if (browser.contains("Safari")) {
            docName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        } else {
            throw new NoSupportedBrowserException();
        }
        return docName;
    }

    public static String mappingSpecialCharacter(String name) {

        // 파일명에 사용되는 특수문자
        char[] shList = { '~', '!', '@', '#', '$', '%', '&', '(', ')', '=', ';', '[', ']', '{', '}', '^', '-' };
        try {
            for (char sh : shList) {
                String encodeStr = URLEncoder.encode(sh + "", "UTF-8");
                name = name.replaceAll(encodeStr, "\\" + sh);
            }

            // 띄워쓰기 -> + 치환
            name = name.replace("%2B", "+");
            // 콤마 -> _ 치환
            name = name.replace("%2C", "_");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return name;
    }
}
