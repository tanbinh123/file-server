package com.github.qinyou.fileserver;

import com.github.qinyou.fileserver.controller.FileController;
import com.github.qinyou.fileserver.utils.LogBackLogFactory;
import com.jfinal.config.*;
import com.jfinal.json.FastJsonFactory;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;


/**
 * Jfinal框架 配置
 *
 * @author zhangcuang
 */
public class AppConfig extends JFinalConfig {

    public static void main(String[] args) {
        UndertowServer.create(AppConfig.class)
                .configWeb(builder -> {
                    // cors 跨域 filter
                    builder.addFilter("CORS", "com.thetransactioncompany.cors.CORSFilter");
                    builder.addFilterUrlMapping("CORS", "/*");
                    builder.addFilterInitParam("CORS", "cors.allowOrigin", "*");
                    builder.addFilterInitParam("CORS", "cors.supportedMethods", "GET, POST, HEAD, PUT, DELETE");
                    builder.addFilterInitParam("CORS", "cors.supportedHeaders", "Accept, Origin, X-Requested-With, Content-Type, Last-Modified");
                    builder.addFilterInitParam("CORS", "cors.exposedHeaders", "Set-Cookie");
                    builder.addFilterInitParam("CORS", "cors.supportsCredentials", "true");
                }).start();
    }

    /**
     * 配置JFinal常量
     *
     * @param me 常量集合
     */
    @Override
    public void configConstant(Constants me) {
        Prop prop = PropKit.use("config.txt");
        me.setDevMode(prop.getBoolean("devMode"));
        me.setLogFactory(new LogBackLogFactory());
        me.setInjectDependency(true);

        // 上传下载
        me.setBaseUploadPath(prop.get("uploadPath"));
        me.setMaxPostSize(prop.getInt("maxPostSize"));
        me.setBaseDownloadPath(prop.get("uploadPath"));

        me.setJsonFactory(new FastJsonFactory());
        me.setJsonDatePattern("yyyy-MM-dd HH:mm:ss");
    }


    /**
     * 配置JFinal路由
     *
     * @param me 路由集合
     */
    @Override
    public void configRoute(Routes me) {
        me.add("/file", FileController.class);
    }


    @Override
    public void configPlugin(Plugins me) {
    }

    @Override
    public void configInterceptor(Interceptors me) {
    }

    @Override
    public void configHandler(Handlers me) {
    }

    @Override
    public void configEngine(Engine me) {
    }
}
