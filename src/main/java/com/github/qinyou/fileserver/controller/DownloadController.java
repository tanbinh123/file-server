package com.github.qinyou.fileserver.controller;

import com.jfinal.core.ActionKey;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * 文件下载服务
 */
public class DownloadController extends BaseController {
    private static final Res res = I18n.use(lang);

    /**
     * 文件下载
     */
    @ActionKey("/common/download")
    public void download() {
        String path = getPara("path");
        String name = getPara("name");
        if (StrKit.isBlank(path)) {
            renderFail(res.get("PATH_EMPTY"));
            return;
        }
        path = path.replace("$", "/");
        path = PathKit.getWebRootPath() + "/" + path;
        File downloadFile = new File(path);
        if (!downloadFile.exists()) {
            renderFail(res.get("FILE_NOT_EXIST"));
            return;
        }
        if (StrKit.isBlank(name)) {
            renderFile(downloadFile);
        } else {
            String fileName = name.contains(".") ? name : name + "." + FilenameUtils.getExtension(downloadFile.getName());
            renderFile(downloadFile, fileName);
        }
    }
}
