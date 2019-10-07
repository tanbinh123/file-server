package com.github.qinyou.fileserver.service;

import com.github.qinyou.fileserver.bean.ComUploadRet;
import com.github.qinyou.fileserver.bean.ImgUploadRet;
import com.github.qinyou.fileserver.controller.BaseController;
import com.github.qinyou.fileserver.utils.FileKit;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * upload service
 */
@Slf4j
public class UploadService {
    private static final Res res = I18n.use(BaseController.lang);

    /**
     * 文件删除
     *
     * @param uploadFile     上传的文件
     * @param secondBasePath 存盘基础路径
     * @return
     */
    public static Ret upload(UploadFile uploadFile, String secondBasePath) {
        Ret ret = Ret.create();

        if (uploadFile == null) {
            ret.setFail().set("msg", res.get("PARAM_FILE_EMPTY"));
            return ret;
        }
        String originalFileName = uploadFile.getOriginalFileName();
        String extension = FilenameUtils.getExtension(originalFileName);
        if (!FileKit.checkFileType(extension)) {
            FileKit.deleteFile(uploadFile.getFile());
            ret.setFail().set("msg", res.format("FILE_TYPE_NOT_LIMIT", extension));
            return ret;
        }
        // secondBasePath 一般是应用标识，多应用文件分目录
        String relativePath = FileKit.fileRelativeSavePath(extension, secondBasePath);
        File saveFile = new File(PathKit.getWebRootPath() + "/" + relativePath);
        if (saveFile.exists()) {
            FileKit.deleteFile(uploadFile.getFile());
            ret.setFail().set("msg", res.format("FILE_EXIST", originalFileName));
            return ret;
        }

        // 文件拷贝
        try {
            FileKit.copyFile(uploadFile.getFile(), saveFile);
            FileKit.deleteFile(uploadFile.getFile());
            ComUploadRet ComUploadRet = new ComUploadRet();
            ComUploadRet.setName(originalFileName);
            ComUploadRet.setPath(relativePath);
            long sizeL = saveFile.length();
            ComUploadRet.setSizeL(sizeL);
            ComUploadRet.setSize(FileKit.byteCountToDisplaySize(sizeL));
            ret.setOk().set("data", ComUploadRet);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            FileKit.deleteFile(uploadFile.getFile());
            ret.setFail().set("msg", e.getMessage());
        }

        return ret;
    }


    /**
     * 图片上传 并生成缩略图
     *
     * @param uploadFile     上传文件
     * @param secondBasePath 存盘基础路径
     * @param width          缩放后宽度
     * @return
     */
    public static Ret imgUploadResize(UploadFile uploadFile, String secondBasePath, Integer width) {
        Ret ret = Ret.create();
        Ret retTemp = UploadService.upload(uploadFile, secondBasePath);
        if (retTemp.isOk()) {
            ComUploadRet uploadRet = (ComUploadRet) retTemp.get("data");
            String oriPath = uploadRet.getPath();
            String extension = FilenameUtils.getExtension(oriPath);
            String newPath = oriPath.replaceAll("." + extension, "_w" + width + "." + extension);
            String webRootPath = PathKit.getWebRootPath() + "/";
            try {
                Thumbnails.of(webRootPath + oriPath).size(width, width).toFile(webRootPath + newPath);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                ret.setFail().set("msg", e.getMessage());
                return ret;
            }
            ImgUploadRet imgUploadRet = new ImgUploadRet();
            imgUploadRet.setName(uploadRet.getName());
            imgUploadRet.setNewPath(newPath);
            imgUploadRet.setOriPath(oriPath);
            imgUploadRet.setOpe("image resize");
            ret.set("data", imgUploadRet);
        } else {
            ret.setFail().set("msg", res.get("UPLOAD_FAIL"));
        }

        return ret;
    }


    /**
     * 图片上传并 添加水印
     *
     * @param uploadFile     上传的文件
     * @param secondBasePath 基础路径
     * @param watermark      水印加水印后 文件名
     * @param waterImgPath   水印图片路径
     * @param positions      水印位置
     * @param opacity        水印图片透明度
     * @return
     */
    public static Ret imgUploadWatermark(UploadFile uploadFile, String secondBasePath,
                                         String watermark, String waterImgPath,
                                         Positions positions, Float opacity) {
        Ret ret = Ret.create();
        Ret retTemp = UploadService.upload(uploadFile, secondBasePath);
        if (retTemp.isOk()) {
            ComUploadRet uploadRet = (ComUploadRet) retTemp.get("data");
            String oriPath = uploadRet.getPath();
            String extension = FilenameUtils.getExtension(oriPath);
            String newPath = oriPath.replaceAll("." + extension, "_" + watermark + "." + extension);
            String webRootPath = PathKit.getWebRootPath() + "/";

            try {
                Thumbnails.of(webRootPath + oriPath).scale(1.0f)
                        .watermark(positions, ImageIO.read(new File(waterImgPath)), opacity)
                        .toFile(webRootPath + newPath);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                ret.setFail().set("msg", e.getMessage());
                return ret;
            }

            ImgUploadRet imgUploadRet = new ImgUploadRet();
            imgUploadRet.setName(uploadRet.getName());
            imgUploadRet.setNewPath(newPath);
            imgUploadRet.setOriPath(oriPath);
            imgUploadRet.setOpe("image watermark");
            ret.set("data", imgUploadRet);
        } else {
            ret.setFail().set("msg", res.get("UPLOAD_FAIL"));
        }

        return ret;
    }
}
