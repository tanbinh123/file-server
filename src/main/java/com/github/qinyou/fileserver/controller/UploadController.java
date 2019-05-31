package com.github.qinyou.fileserver.controller;


import com.github.qinyou.fileserver.bean.ComRet;
import com.github.qinyou.fileserver.interceptor.SecurityInterceptor;
import com.github.qinyou.fileserver.service.FileService;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 * 文件上传下载
 *
 * @author chuang
 */
@Slf4j
public class UploadController extends BaseController {
    private static final Res res = I18n.use(lang);

    /**
     * 单文件上传
     */
    @ActionKey("/common/upload")
    @Before(SecurityInterceptor.class)
    public void upload() throws IOException {
        UploadFile uploadFile = getFile("file");

        if (uploadFile == null) {
            renderFail(res.get("PARAM_FILE_EMPTY"));
            return;
        }

        String originalFileName = uploadFile.getOriginalFileName();
        String extension = FilenameUtils.getExtension(originalFileName);

        // 文件类型非法
        if (!FileService.checkFileType(extension)) {
            FileService.deleteFile(uploadFile.getFile());
            renderFail(res.format("FILE_TYPE_NOT_LIMIT", extension));
            return;
        }

        // 文件保存
        // basePath 一般是应用标识，多应用文件分目录
        String relativePath = FileService.fileRelativeSavePath(extension, get("basePath"));
        File saveFile = new File(PathKit.getWebRootPath() + "/" + relativePath);
        if (saveFile.exists()) {
            // 正常情况 不会发生
            FileService.deleteFile(uploadFile.getFile());
            renderFail(res.format("FILE_EXIST", originalFileName));
            return;
        }

        FileUtils.copyFile(uploadFile.getFile(), saveFile);
        FileService.deleteFile(uploadFile.getFile());

        ComRet singleRet = new ComRet();
        singleRet.setName(originalFileName);
        singleRet.setPath(relativePath);
        long sizeL = saveFile.length();
        singleRet.setSizeL(sizeL);
        singleRet.setSize(FileUtils.byteCountToDisplaySize(sizeL));
        StringBuffer url = getRequest().getRequestURL();
        String uri = url.delete(url.length() - getRequest().getRequestURI().length(), url.length())
                .append(getRequest().getServletContext().getContextPath()).append("/").toString();
        singleRet.setUri(uri + relativePath);

        renderOk(singleRet);
    }
}
