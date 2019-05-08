package com.github.qinyou.fileserver.interceptor;

import com.github.qinyou.fileserver.Constant;
import com.github.qinyou.fileserver.service.FileService;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.*;
import com.jfinal.upload.UploadFile;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * 服务授权 验证
 * 加密方式 MD5(MD5(authPwd)+timestamp)
 *
 * @author chuang
 */
@Slf4j
public class UploadInterceptor implements Interceptor {

    private static String authPwd; // 认证密文
    private static Long authIdle;   // 超时最大数(毫秒)

    static {
        Prop prop = PropKit.use("config.txt");
        authPwd = prop.get("authPwd");
        authIdle = prop.getInt("authIdle") * 3600 * 1000L;
    }


    @Override
    public void intercept(Invocation invocation) {
        UploadFile uploadFile = invocation.getController().getFile();
        HttpServletRequest req = invocation.getController().getRequest();

        // 认证参数不为空
        String authToken = req.getParameter("authToken");
        if (StrKit.isBlank(authToken)) {
            FileService.deleteFile(uploadFile.getFile());
            invocation.getController().renderJson(buildFail(Constant.AUTH_TOKEN_EMPTY));
            return;
        }

        // 时间戳不可为空
        String timestampStr = req.getParameter("timestamp");
        if (StrKit.isBlank(timestampStr)) {
            FileService.deleteFile(uploadFile.getFile());
            invocation.getController().renderJson(buildFail(Constant.TIMESTAMP_EMPTY));
            return;
        }

        // 时间戳格式错误
        Long timestamp = 0L;
        try {
            timestamp = Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            FileService.deleteFile(uploadFile.getFile());
            invocation.getController().renderJson(buildFail(Constant.TIMESTAMP_NOTLONG));
            return;
        }

        // 服务器时间戳 - 客户端时间戳 > authIdle 认为 authToken 超时
        Long serverTimeStamp = System.currentTimeMillis();
        if (serverTimeStamp - timestamp > authIdle) {
            FileService.deleteFile(uploadFile.getFile());
            invocation.getController().renderJson(buildFail(Constant.AUTH_TOKEN_TIMEOUT));
            return;
        }

        String realAuthToken = HashKit.md5(HashKit.md5(authPwd + timestamp) + timestamp);
        if (!authToken.equals(realAuthToken)) {
            FileService.deleteFile(uploadFile.getFile());
            invocation.getController().renderJson(buildFail(Constant.AUTH_TOKEN_ERROR));
            return;
        }

        // 访问 controller
        try {
            invocation.invoke();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            FileService.deleteFile(uploadFile.getFile());
            invocation.getController().renderJson(buildFail(e.toString()));
        }
    }

    /**
     * 生成 错误提示信息
     *
     * @param msg
     * @return
     */
    private Ret buildFail(String msg) {
        return Ret.create().setFail().set("msg", msg).set("timestamp", System.currentTimeMillis());
    }
}
