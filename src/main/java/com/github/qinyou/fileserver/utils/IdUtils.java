package com.github.qinyou.fileserver.utils;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

/**
 * 生成唯一性ID算法的工具类.
 */
class IdUtils {
    private final static Prop prop = PropKit.use("config.txt");
    private final static SnowflakeIdWorker idWorker;

    static {
        idWorker = new SnowflakeIdWorker(prop.getInt("workerId"), prop.getInt("dataCenterId"));
    }

    /**
     * 生成 18 位数字型字符串
     *
     * @return
     */
    public static String id() {
        return String.valueOf(idWorker.nextId());
    }
}
