package com.study.board.util;

import java.text.DecimalFormat;

/**
 * 파일 관련 유틸리티 클래스
 */
public class FileUtil {

    private static final String[] SIZE_UNITS = {"B", "KB", "MB", "GB", "TB"};

    /**
     * 파일 크기를 읽기 쉬운 형태로 변환
     * 예: 1024 -> 1.0 KB, 1048576 -> 1.0 MB
     */
    public static String formatFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }

        int unitIndex = (int) (Math.log(size) / Math.log(1024));
        double fileSize = size / Math.pow(1024, unitIndex);

        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(fileSize) + " " + SIZE_UNITS[unitIndex];
    }

    /**
     * 파일 확장자 추출
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }

        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 파일명에서 확장자를 제외한 이름 추출
     */
    public static String getFileNameWithoutExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return filename;
        }

        return filename.substring(0, lastDotIndex);
    }

    /**
     * 안전한 파일명 생성 (특수문자 제거)
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "unnamed_file";
        }

        // 위험한 문자들을 언더스코어로 대체
        return filename.replaceAll("[^a-zA-Z0-9가-힣._-]", "_");
    }

    /**
     * 파일 타입이 이미지인지 확인
     */
    public static boolean isImageFile(String filename) {
        String extension = getFileExtension(filename);
        return extension.matches("^(jpg|jpeg|png|gif|bmp|webp)$");
    }

    /**
     * 파일 타입이 문서인지 확인
     */
    public static boolean isDocumentFile(String filename) {
        String extension = getFileExtension(filename);
        return extension.matches("^(pdf|doc|docx|xls|xlsx|ppt|pptx|txt|hwp)$");
    }
}
