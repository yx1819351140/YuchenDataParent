package com.yuchen.common.utils;

import java.io.Serializable;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import com.yuchen.common.utils.JedisClusterUtil;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

public class SimHashUtil implements Serializable {
    private int hashbits = 64;

    public String getSimHashStr(String content) {
        // 定义特征向量/数组
        int[] v = new int[this.hashbits];
        // 一、将文本去掉格式后, 分词.
        StringTokenizer stringTokens = new StringTokenizer(content);
        while (stringTokens.hasMoreTokens()) {
            String temp = stringTokens.nextToken();
            // 二、将每个分词hash为一组固定长度的数列.好比 64bit 的一个整数.
            BigInteger t = this.hash(temp);
            for (int i = 0; i < this.hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                // 三、创建一个长度为64的整数数组(假设要生成64位的数字指纹,也能够是其它数字),
                // 对每个分词hash后的数列进行判断,若是是1000...1,那么数组的第一位和末尾一位加1,
                // 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把全部的分词hash数列所有判断完毕.
                if (t.and(bitmask).signum() != 0) {
                    // 这里是计算整个文档的全部特征的向量和
                    // 这里实际使用中须要 +- 权重，而不是简单的 +1/-1，
                    v[i] += 1;
                } else {
                    v[i] -= 1;
                }
            }
        }
        BigInteger fingerprint = new BigInteger("0");
        StringBuffer simHashBuffer = new StringBuffer();
        for (int i = 0; i < this.hashbits; i++) {
            // 四、最后对数组进行判断,大于0的记为1,小于等于0的记为0,获得一个 64bit 的数字指纹/签名.
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
                simHashBuffer.append("1");
            } else {
                simHashBuffer.append("0");
            }
        }
        return simHashBuffer.toString();
    }

    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    public static int hammingDistance(String a, String b) {
        if (a == null || b == null) {
            return 0;
        }
        if (a.length() != b.length()) {
            return 0;
        }
        int disCount = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                disCount++;
            }
        }
        return disCount;
    }

    public static int getOneNumFromSimHash(String simhash) {
        try {
            if (simhash.length() != 64) {
                return -1;
            }
            int score = 0;
            for (int i = 0; i < simhash.length(); i++) {
                if ((Integer.parseInt(String.valueOf(simhash.charAt(i)))) == 1) {
                    score++;
                }
            }
            return score;
        } catch (Exception e) {
            return -1;
        }
    }

    public String isDataRepeat(String content, String titleId) {
        String initTitleId = null;
        String simHashStr = getSimHashStr(content);
        Jedis jedis = JedisClusterUtil.getNewsConnection();
        if (StringUtils.isNotBlank(simHashStr) && simHashStr.length() == 64) {
            String strKey = "";
            String strKeyPrefix = "SIMHASH_";
            for (int i = 0; i<4; i++) {
                strKey = strKeyPrefix + simHashStr.substring(i * 16, (i + 1) * 16);
                long currentTimeMillis = System.currentTimeMillis() / 1000 * 1000;
                Boolean hashKey = jedis.exists(strKey);
                // 计算simhash中1的个数
                int scoreBySimHash = getOneNumFromSimHash(simHashStr);
                // +100避免 1的个数少于3 减去3 后变成负数
                long base = scoreBySimHash + 100L;
                int maxDistance = 3;
                // 查询7天内的数据
                long begin =  currentTimeMillis - 7 * 24 * 60 * 60 * 1000L;
                long end = base + maxDistance + currentTimeMillis;
                // 删除七天前的数据
                jedis.zremrangeByScore(strKey, 0, begin);
                if (hashKey) {
                    Set<Tuple> values = jedis.zrangeByScoreWithScores(strKey, begin, end);
                    for (Tuple tuple: values) {
                        String str = tuple.getElement();
                        String oldsimHashStr = str.split("_")[0];
                        String oldTitleId = str.split("_")[1];
                        long score = new Double(tuple.getScore()).longValue();
                        // 取余获取需要比较的1的个数
                        long remainder = score % 1000;
                        // 判断1个数的范围
                        // 海明距离是位数值不一样的个数，如果海明距离小于等于3，那么两个simhash中1的个数最多相差3，进一步删选掉一部分数据，然后计算海明距离
                        if (remainder >= (base - maxDistance) && remainder <= (base + maxDistance)) {
                            int hammingDistance = hammingDistance(oldsimHashStr, simHashStr);
                            if (hammingDistance <= maxDistance) {
                                return oldTitleId;
                            }
                        }
                    }
                }
                jedis.zadd(strKey, currentTimeMillis + base, simHashStr + "_" + titleId);
                jedis.expire(strKey, 7 * 24 * 60 * 60);
            }
        }
        return initTitleId;
    }

    public String getSimHashCode(String content) {
        String simHashStr = getSimHashStr(content);
        Jedis jedis = JedisClusterUtil.getNewsConnection();
        if (StringUtils.isNotBlank(simHashStr) && simHashStr.length() == 64) {
            String strKey = "";
            String strKeyPrefix = "SIMHASH_";
            for (int i = 0; i<4; i++) {
                strKey = strKeyPrefix + simHashStr.substring(i * 16, (i + 1) * 16);
                long currentTimeMillis = System.currentTimeMillis() / 1000 * 1000;
                Boolean hashKey = jedis.exists(strKey);
                // 计算simhash中1的个数
                int scoreBySimHash = getOneNumFromSimHash(simHashStr);
                // +100避免 1的个数少于3 减去3 后变成负数
                long base = scoreBySimHash + 100L;
                int maxDistance = 3;
                // 查询7天内的数据
                long begin =  currentTimeMillis - 7 * 24 * 60 * 60 * 1000L;
                long end = base + maxDistance + currentTimeMillis;
                // 删除七天前的数据
                jedis.zremrangeByScore(strKey, 0, begin);
                if (hashKey) {
                    Set<Tuple> values = jedis.zrangeByScoreWithScores(strKey, begin, end);
                    for (Tuple tuple: values) {
                        String str = tuple.getElement();
                        long score = new Double(tuple.getScore()).longValue();
                        // 取余获取需要比较的1的个数
                        long remainder = score % 1000;
                        // 判断1个数的范围
                        // 海明距离是位数值不一样的个数，如果海明距离小于等于3，那么两个simhash中1的个数最多相差3，进一步删选掉一部分数据，然后计算海明距离
                        if (remainder >= (base - maxDistance) && remainder <= (base + maxDistance)) {
                            int hammingDistance = hammingDistance(str, simHashStr);
                            if (hammingDistance <= maxDistance) {
                                return str;
                            }
                        }
                    }
                }
            }
        }
        return simHashStr;
    }
}
