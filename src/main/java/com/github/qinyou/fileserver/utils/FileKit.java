package com.github.qinyou.fileserver.utils;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.util.*;

@Slf4j
public class FileKit extends FileUtils {

    private static final Set<String> limitTypes = new HashSet<>();

    private static final String basePath;
    private static final List<String> imageTypelimit;
    private static final String imagePath;

    private static final List<String> mediaTypeLimit;
    private static final String mediaPath;

    private static final List<String> officeTypeLimit;
    private static final String officePath;

    private static final List<String> fileTypeLimit;
    private static final String filePath;

    static {
        Prop prop = PropKit.use("config.txt");

        basePath = prop.get("basePath");

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
        return limitTypes.contains(extension);
    }


    /**
     * 获得文件存盘 相对路径
     *
     * @param extension      文件后缀
     * @param secondBasePath 二级基础路径 (自定义存盘)
     * @return
     */
    public static String fileRelativeSavePath(String extension, String secondBasePath) {
        String path = "/" + new DateTime(new Date()).toString("yyyy_MM_dd");

        if (imageTypelimit.contains(extension)) {
            path = imagePath + path;
        } else if (mediaTypeLimit.contains(extension)) {
            path = mediaPath + path;
        } else if (officeTypeLimit.contains(extension)) {
            path = officePath + path;
        } else if (fileTypeLimit.contains(extension)) {
            path = filePath + path;
        } else {
            throw new RuntimeException(extension + " 未找到存盘相对路径");
        }

        // 时分秒毫秒+随机数
        if (StrKit.notBlank(secondBasePath)) {
            path = basePath + "/" + secondBasePath + path + "/" + IdUtils.id() + "." + extension;
        } else {
            path = basePath + path + "/" + IdUtils.id() + "." + extension;
        }
        return path;
    }


    /**
     * 文件删除
     *
     * @param file 文件对象
     */
    public static void deleteFile(File file) {
        if (!file.delete()) {
            log.error("文件{} 未删除成功.", file.getAbsolutePath());
        }
    }
}
