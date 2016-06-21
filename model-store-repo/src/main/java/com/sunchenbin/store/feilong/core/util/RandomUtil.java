/*
 * Copyright (C) 2008 feilong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sunchenbin.store.feilong.core.util;

import java.util.Random;

import com.sunchenbin.store.feilong.core.tools.slf4j.Slf4jUtil;

/**
 * 
 * 随机数工具类.
 * 
 * <ul>
 * <li>Math.random() 底层也是调用的 new Random(),值＝Random nextDouble()</li>
 * <li>把Random对象作为一个全局实例（static）来使用. Java中Random是线程安全的（内部进行了加锁处理）；</li>
 * <li>伪随机数</li>
 * <li>生成随机数的算法有很多种,最简单也是最常用的就是 "线性同余法"：  第n+1个数=(第n个数*29+37) % 1000,其中%是"求余数"运算符.</li>
 * </ul>
 * 
 * @author feilong
 * @version 1.0.0 2010-4-5 下午10:55:19
 * @version 1.0.7 2014年5月19日 下午6:45:01
 * @since 1.0.0
 * @see org.apache.commons.lang3.RandomStringUtils
 * @see org.apache.commons.lang3.RandomUtils
 */
public final class RandomUtil{

    /**
     * Random object used by random method.
     * 
     * <p>
     * This has to be not local to the random method so as to not return the same value in the same millisecond.<br>
     * 把Random对象作为一个全局实例（static）来使用. Java中Random是线程安全的（内部进行了加锁处理）；
     * </p>
     * 
     * @see org.apache.commons.lang.math.RandomUtils
     * @since 1.0.7
     */
    public static final Random JVM_RANDOM = new Random();

    /** Don't let anyone instantiate this class. */
    private RandomUtil(){
        //AssertionError不是必须的. 但它可以避免不小心在类的内部调用构造器. 保证该类在任何情况下都不会被实例化.
        //see 《Effective Java》 2nd
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }

    /**
     * 创建0-最大值之间的随机数.
     * 
     * <p>
     * 目前内部调用全局随机数{@link #JVM_RANDOM}
     * </p>
     * 
     * @param number
     *            随机数最大值
     * @return 创建0-最大值之间的随机数
     * @see java.lang.Math#random()
     */
    public static long createRandom(Number number){
        double random = JVM_RANDOM.nextDouble();
        return (long) Math.floor(random * number.longValue());
    }

    /**
     * 创建最小值和最大值之间的随机数.
     *
     * @param min
     *            最小值
     * @param max
     *            最大值
     * @return 创建最小值和最大值之间的随机数
     */
    public static long createRandom(Number min,Number max){
        long maxLong = max.longValue();
        long minLong = min.longValue();
        if (maxLong < minLong){
            String messagePattern = "maxLong:[{}] can not < minLong:[{}]";
            throw new IllegalArgumentException(Slf4jUtil.formatMessage(messagePattern, maxLong, minLong));
        }
        long cha = maxLong - minLong;
        return minLong + createRandom(cha);
    }

    // ********************************************************************

    /**
     * 生成一个指定长度大小的随机正整数.
     * 
     * <p>
     * 目前内部调用全局随机数{@link #JVM_RANDOM}
     * </p>
     *
     * @param length
     *            设定所取出随机数的长度.length小于11
     * @return 返回生成的随机数
     * @see #JVM_RANDOM
     */
    public static long createRandomWithLength(int length){
        // 该值大于等于 0.0 且小于 1.0 正号的 double 值
        double random = JVM_RANDOM.nextDouble();
        if (random < 0.1){// 可能出现 0.09346924349151808
            random = random + 0.1;
        }

        // ****************************************
        long num = 1;
        for (int i = 0; i < length; ++i){
            num = num * 10;
        }
        return (long) (random * num);
    }

    // ****************************************************************

    /**
     * 随机抽取字符串char,拼接成随机字符串.
     * 
     * <p>
     * 取一个字符串里面 随机长度字符
     * </p>
     *
     * @param str
     *            被抽取的字符串
     * @param minLength
     *            最小长度
     * @param maxLength
     *            最大长度
     * @return 得到随机字符串
     */
    public static String createRandomFromString(String str,int minLength,int maxLength){
        if (Validator.isNullOrEmpty(str)){
            throw new NullPointerException("the str is null or empty!");
        }

        if (maxLength <= 0){
            String messagePattern = "maxLength:[{}] can not zero";
            throw new IllegalArgumentException(Slf4jUtil.formatMessage(messagePattern, maxLength));
        }

        if (maxLength < minLength){
            String messagePattern = "maxLength:[{}] can not < minLength:[{}]";
            throw new IllegalArgumentException(Slf4jUtil.formatMessage(messagePattern, maxLength, minLength));
        }
        long length = createRandom(minLength, maxLength);
        return createRandomFromString(str, (int) length);
    }

    /**
     * 随机抽取字符串char,拼接成随机字符串.
     * 
     * <p>
     * 取一个字符串里面 指定长度字符
     * </p>
     *
     * @param str
     *            被抽取的字符串
     * @param length
     *            随机字符串长度
     * @return 得到随机字符串
     */
    public static String createRandomFromString(String str,int length){
        if (Validator.isNullOrEmpty(str)){
            throw new NullPointerException("the str is null or empty!");
        }
        if (length <= 0){
            String messagePattern = "length:[{}] can not <=0";
            throw new IllegalArgumentException(Slf4jUtil.formatMessage(messagePattern, length));
        }

        char[] ch = new char[length];
        int j = str.length();
        for (int i = 0; i < length; ++i){
            int index = JVM_RANDOM.nextInt(j);// 随机取个字符
            ch[i] = str.charAt(index);
        }
        return new String(ch);
    }
}