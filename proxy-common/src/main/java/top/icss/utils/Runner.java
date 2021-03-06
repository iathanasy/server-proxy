package top.icss.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author cd.wang
 * @create 2022-04-02 10:31
 */
public class Runner {

    public static void wait(int second) {
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (Throwable t) {
            // ignore
        }
    }

    // 在规定时间内运行,非阻塞
    public static void runWithTimeout(Callable<Boolean> callable, int timeout) {
        long endTime = System.currentTimeMillis()+timeout;
        while (System.currentTimeMillis() < endTime) {
            try {
                final Boolean ret = callable.call();
                if (ret) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
