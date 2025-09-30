package com.study.board.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {

    @Test
    void formatFileSize_1024바이트_1KB반환() {
        // When
        String result = FileUtil.formatFileSize(1024);

        // Then
        assertEquals("1 KB", result);
    }

    @Test
    void formatFileSize_1048576바이트_1MB반환() {
        // When
        String result = FileUtil.formatFileSize(1048576);

        // Then
        assertEquals("1 MB", result);
    }

    @Test
    void formatFileSize_0_0B반환() {
        // When
        String result = FileUtil.formatFileSize(0);

        // Then
        assertEquals("0 B", result);
    }

    @Test
    void getFileExtension_정상파일명_확장자반환() {
        // When
        String result = FileUtil.getFileExtension("test.txt");

        // Then
        assertEquals("txt", result);
    }

    @Test
    void getFileExtension_확장자없음_빈문자열반환() {
        // When
        String result = FileUtil.getFileExtension("test");

        // Then
        assertEquals("", result);
    }

    @Test
    void getFileExtension_null_빈문자열반환() {
        // When
        String result = FileUtil.getFileExtension(null);

        // Then
        assertEquals("", result);
    }

    @Test
    void getFileNameWithoutExtension_정상파일명_이름만반환() {
        // When
        String result = FileUtil.getFileNameWithoutExtension("test.txt");

        // Then
        assertEquals("test", result);
    }

    @Test
    void sanitizeFilename_특수문자포함_안전한파일명반환() {
        // When
        String result = FileUtil.sanitizeFilename("test<>file|name.txt");

        // Then
        assertEquals("test__file_name.txt", result);
    }

    @Test
    void isImageFile_이미지파일_true반환() {
        // When & Then
        assertTrue(FileUtil.isImageFile("image.jpg"));
        assertTrue(FileUtil.isImageFile("image.png"));
        assertTrue(FileUtil.isImageFile("image.gif"));
        assertFalse(FileUtil.isImageFile("document.pdf"));
    }

    @Test
    void isDocumentFile_문서파일_true반환() {
        // When & Then
        assertTrue(FileUtil.isDocumentFile("document.pdf"));
        assertTrue(FileUtil.isDocumentFile("document.docx"));
        assertTrue(FileUtil.isDocumentFile("document.txt"));
        assertFalse(FileUtil.isDocumentFile("image.jpg"));
    }
}
