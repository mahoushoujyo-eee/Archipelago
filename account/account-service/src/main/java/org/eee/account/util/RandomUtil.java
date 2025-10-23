package org.eee.account.util;
import java.util.Random;

public class RandomUtil
{
    public static String generateSixDigitCode() {
        Random random = new Random();
        int code = random.nextInt(999999); // 生成 0 ~ 999999 之间的随机数
        return String.format("%06d", code); // 格式化为 6 位，不足补零
    }
}
