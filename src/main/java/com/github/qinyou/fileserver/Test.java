package com.github.qinyou.fileserver;

import com.jfinal.kit.HashKit;
import com.jfinal.kit.PropKit;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        Long timestamp = System.currentTimeMillis();
        String authPwd = PropKit.use("config.txt").get("authPwd");

        // 生成 token
        System.out.println("timestamp: " + timestamp);
        System.out.println("authToken: " + HashKit.md5(HashKit.md5(authPwd + timestamp) + timestamp));

    }
}
