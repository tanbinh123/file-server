package com.github.qinyou.fileserver.service;

import com.github.qinyou.fileserver.utils.IdUtils;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.io.File;
import java.util.*;

@Slf4j
public class FileService {

    private static Set<String> limitTypes = new HashSet<>();

    private static List<String> imageTypelimit;
    private static String imagePath;

    private static List<String> mediaTypeLimit;
    private static String mediaPath;

    private static List<String> officeTypeLimit;
    private static String officePath;

    private static List<String> fileTypeLimit;
    private static String filePath;

    static {
        Prop prop = PropKit.use("config.txt");

        imagePath = prop.get("imagePath");
        imageTypelimit = Arrays.asList(prop.get("imageType").split(","));
        limitTypes.addAll(imageTypelimit);

        mediaPath = prop.get("mediaPath");
        mediaTypeLimit = Arrays.asList(prop.get("mediaType").split(","));
        limitTypes.addAll(mediaTypeLimit);

        officePath = prop.get("officePath");
        officeTypeLimit = Arrays.asList(prop.get("officeType").split(","));
        limitTypes.addAll(officeTypeLimit);

        filePath = prop.get("filePath");
        fileTypeLimit = Arrays.asList(prop.get("fileType").split(","));
        limitTypes.addAll(fileTypeLimit);
    }

    /**
     * 检查文件类型是否合法
     *
     * @param extension 文件后缀
     * @return true 合法，false 非法
     */
    public static boolean checkFileType(String extension) {
        boolean flag = false;
        return limitTypes.contains(extension);
    }

    ;


    /**
     * 获得文件存盘 相对 路径
     *
     * @param extension 文件后缀
     * @return
     */
    public static String fileRelativeSavePath(String extension) {
        String path = "/" + new DateTime(new Date()).toString("yyyy_MM_dd");

        if (imageTypelimit.contains(extension)) {
            path = imagePath + path;
        } else if (mediaPath.contains(extension)) {
            path = mediaPath + path;
        } else if (officeTypeLimit.contains(extension)) {
            path = officePath + path;
        } else if (fileTypeLimit.contains(extension)) {
            path = filePath + path;
        } else {
            throw new RuntimeException(extension + " 未找到存盘相对路径");
        }

        // 时分秒毫秒+随机数
        path = path + "/" + IdUtils.id() + "." + extension;
        return path;
    }

    ;


    public static void deleteFile(File file) {
        if (!file.delete()) {
            log.error("文件{} 未删除成功.", file.getAbsolutePath());
        }
        ;
    }

    public static void main(String[] args) {
        System.out.println(fileRelativeSavePath("jpg"));
    }
}
