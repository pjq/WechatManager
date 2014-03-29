package me.pjq.util;

public class Log {
    /**
     * 提示“调试”信息，可以覆盖这个方法使用log4j等第三方包
     * @author jianqing.cai@qq.com, https://github.com/caijianqing/weixinmp4java/, 2013-10-22 上午9:42:32
     * @param msg
     */
    public static void d(String tag, String msg) {
        System.out.println("debug-->" + msg);
    }

    /**
     * 提示“一般”信息，可以覆盖这个方法使用log4j等第三方包
     * @author jianqing.cai@qq.com, https://github.com/caijianqing/weixinmp4java/, 2013-10-22 上午9:42:32
     * @param msg
     */
    public static void i(String tag, String msg) {
        System.out.println("信息-->" + msg);
    }

    /**
     * 提示“警告”信息
     * @author jianqing.cai@qq.com, https://github.com/caijianqing/weixinmp4java/, 2013-10-21 下午7:35:18
     * @param msg
     */
    public static void w(String tag, String msg) {
        System.err.println("警告-->" + msg);
    }

    /**
     * 提示“错误”信息
     * @author jianqing.cai@qq.com, https://github.com/caijianqing/weixinmp4java/, 2013-10-21 下午7:35:18
     * @param msg
     */
    public static void e(String tag, String msg) {
        System.err.println("错误-->" + msg);
    }
}
