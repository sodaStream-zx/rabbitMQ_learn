package rabbitcore.rabbit.utils;

import java.util.concurrent.TimeUnit;

/**
 * @author Twilight
 * @desc
 * @createTime 2019-05-18-17:04
 */
public class PauseUtil {
    public static void pause(Integer seconds, Integer mills) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
            TimeUnit.MILLISECONDS.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
