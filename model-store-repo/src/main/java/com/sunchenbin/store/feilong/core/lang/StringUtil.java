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
package com.sunchenbin.store.feilong.core.lang;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunchenbin.store.feilong.core.bean.ConvertUtil;
import com.sunchenbin.store.feilong.core.bean.IntrospectorUtil;
import com.sunchenbin.store.feilong.core.tools.slf4j.Slf4jUtil;
import com.sunchenbin.store.feilong.core.util.Validator;

/**
 * {@link String}工具类,可以 查询,截取,format,转成16进制码.
 * 
 * <h3>分隔(split)</h3>
 * 
 * <blockquote>
 * <ul>
 * <li>{@link #split(String, String)}</li>
 * </ul>
 * </blockquote>
 * 
 * <h3>分隔(tokenize)</h3> <blockquote>
 * <ul>
 * <li>{@link #tokenizeToStringArray(String, String)}</li>
 * <li>{@link #tokenizeToStringArray(String, String, boolean, boolean)}</li>
 * </ul>
 * 
 * <p>
 * 区别在于,split 使用的是 正则表达式 {@link Pattern#split(CharSequence)} 分隔(特别注意,一些特殊字符 $|()[{^?*+\\ 需要转义才能做分隔符),而 {@link StringTokenizer} 使用索引机制,在性能上
 * StringTokenizer更高<br>
 * 因此,在注重性能的场景,还是建议使用{@link StringTokenizer}
 * </p>
 * </blockquote>
 * 
 * <h3>{@link String#String(byte[] )} 和 {@link String#String(byte[], Charset)} 区别</h3>
 * 
 * <blockquote>
 * <p>
 * {@link String#String(byte[] )} 其实调用了{@link String#String(byte[], Charset)}; 先使用 {@link Charset#defaultCharset()},如果有异常 再用 ISO-8859-1,
 * 具体参见 {@link java.lang.StringCoding#decode(byte[], int, int)}
 * </p>
 * </blockquote>
 * 
 * <h3>{@link StringBuffer} && {@link StringBuilder} && {@link String} 对比</h3>
 * 
 * <blockquote>
 * <ul>
 * <li>{@link StringBuffer} 字符串变量（线程安全）</li>
 * <li>{@link StringBuilder} 字符串变量（非线程安全）</li>
 * <li>{@link String} 字符串常量</li>
 * <li>在大部分情况下 {@link StringBuffer} {@code >} {@link String}</li>
 * <li>在大部分情况下 {@link StringBuilder} {@code >} {@link StringBuffer}</li>
 * </ul>
 * </blockquote>
 *
 * @author feilong
 * @version 1.4.0 2015年8月3日 上午3:06:20
 * @see "org.springframework.util.StringUtils#tokenizeToStringArray(String, String)"
 * @see "org.springframework.beans.factory.xml.BeanDefinitionParserDelegate#MULTI_VALUE_ATTRIBUTE_DELIMITERS"
 * @see java.util.StringTokenizer
 * @see org.apache.commons.lang3.StringUtils
 * @since 1.4.0
 */
public final class StringUtil{

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);

    /** Don't let anyone instantiate this class. */
    private StringUtil(){
        //AssertionError不是必须的. 但它可以避免不小心在类的内部调用构造器. 保证该类在任何情况下都不会被实例化.
        //see 《Effective Java》 2nd
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }

    /**
     * Constructs a new <code>String</code> by decoding the specified array of bytes using the given charset.
     *
     * @param bytes
     *            The bytes to be decoded into characters, may be {@code null}
     * @param charsetType
     *            {@link CharsetType}
     * @return A new <code>String</code> decoded from the specified array of bytes using the given charset,
     *         or {@code null} if the input byte array was {@code null}.
     * @see String#String(byte[], String)
     * @see org.apache.commons.lang3.StringUtils#toString(byte[], String)
     * @see org.apache.commons.lang3.StringUtils#toEncodedString(byte[], Charset)
     * @see "org.apache.commons.codec.binary.StringUtils#newString(byte[], String)"
     * @since 1.3.0
     */
    public static String newString(byte[] bytes,String charsetType){
        return StringUtils.toEncodedString(bytes, Charset.forName(charsetType));
    }

    // [start] search

    /**
     * 查找子字符串 (<code>target</code>)在 字符串( <code>source</code>)中出现的次数.
     * 
     * <pre>
     *  StringUtil.searchTimes("xin", "xin")
     *  return  1
     *  
     * StringUtil.searchTimes("xiiiin", "ii")
     *  return  2
     * 
     * </pre>
     *
     * @param source
     *            查找的源字符串
     * @param target
     *            目标子串
     * @return count of target string in source
     * @see org.apache.commons.lang3.StringUtils#countMatches(CharSequence, CharSequence)
     * @since 1.0.2
     */
    public static int searchTimes(final CharSequence source,final CharSequence target){
        return org.apache.commons.lang3.StringUtils.countMatches(source, target);
    }

    // [end]
    /**
     * 给一串字符串前后增加两个引号.
     * 
     * <pre>
     * String text = &quot;jinxin.feilong&quot;;
     * LOGGER.info(StringUtil.addDoubleQuotes(text));
     * 
     * 结果:  "jinxin.feilong"
     * </pre>
     * 
     * @param text
     *            任意的字符串
     * @return "\"" + text + "\""
     */
    public static String addDoubleQuotes(String text){
        return "\"" + text + "\"";
    }

    /**
     * 单词首字母大写.
     * 
     * <p>
     * Example 1: jinxin ---> Jinxin
     * </p>
     * 
     * <pre>
     * StringUtils.capitalize(null)  = null
     * StringUtils.capitalize(&quot;&quot;)    = &quot;&quot;
     * StringUtils.capitalize(&quot;cat&quot;) = &quot;Cat&quot;
     * StringUtils.capitalize(&quot;cAt&quot;) = &quot;CAt&quot;
     * </pre>
     * 
     * @param word
     *            单词
     * @return 单词首字母大写
     * @see org.apache.commons.lang3.StringUtils#swapCase(String)
     * @see org.apache.commons.lang3.StringUtils#capitalize(String)
     */
    public static String firstCharToUpperCase(String word){
        return StringUtils.capitalize(word);
    }

    /**
     * 单词首字母小写.
     * 
     * <p>
     * Example 1: Jinxin ---> jinxin
     * </p>
     * 
     * <pre>
     * StringUtils.capitalize(null)  = null
     * StringUtils.capitalize(&quot;&quot;)    = &quot;&quot;
     * StringUtils.capitalize(&quot;Jinxin&quot;) = &quot;jinxin&quot;
     * StringUtils.capitalize(&quot;CAt&quot;) = &quot;cAt&quot;
     * </pre>
     * 
     * 
     * <h3>注意:</h3>
     * 
     * <blockquote>
     * <ol>
     * <li>和 {@link IntrospectorUtil#decapitalize(String)} 的区别.</li>
     * <li>如果要使用一段文字,每个单词首字母小写, 可以使用 {@link org.apache.commons.lang3.text.WordUtils#uncapitalize(String, char...)}</li>
     * </ol>
     * </blockquote>
     * 
     * @param word
     *            单词
     * @return 单词首字母小写
     * @see org.apache.commons.lang3.StringUtils#uncapitalize(String)
     */
    public static String firstCharToLowerCase(String word){
        return StringUtils.uncapitalize(word);
    }

    // [start]Contain

    /**
     * 判断 <code>seq</code> 内是否包含 指定的 <code>searchSeq</code>.
     * 
     * @param seq
     *            原始字符串 jinxin,the CharSequence to check, may be null
     * @param searchSeq
     *            被包含的字符串 in,the CharSequence to find, may be null
     * @return 包含返回true,如果text 为null 返回false
     * @see org.apache.commons.lang3.StringUtils#contains(CharSequence, CharSequence)
     */
    public static boolean contains(final CharSequence seq,final CharSequence searchSeq){
        return org.apache.commons.lang3.StringUtils.contains(seq, searchSeq);
    }

    /**
     * 忽略 大小写 是否包含.
     * 
     * <pre>
     * StringUtil.containsIgnoreCase(null, &quot;&quot;)  return false
     * StringUtil.containsIgnoreCase(text, null) return false
     * StringUtil.containsIgnoreCase(text, &quot;&quot;) return true
     * StringUtil.containsIgnoreCase(text, &quot;feilong&quot;) return true
     * StringUtil.containsIgnoreCase(text, &quot;feilong1&quot;)  return false
     * StringUtil.containsIgnoreCase(text, &quot;feiLong&quot;)  return true
     * 
     * </pre>
     * 
     * @param str
     *            the CharSequence to check, may be null
     * @param searchStr
     *            the CharSequence to find, may be null
     * @return true if the CharSequence contains the search CharSequence irrespective of
     *         case or false if not or {@code null} string input
     */
    public static boolean containsIgnoreCase(final CharSequence str,final CharSequence searchStr){
        return org.apache.commons.lang3.StringUtils.containsIgnoreCase(str, searchStr);
    }

    // [end] 

    // [start]replace

    // ********************************replace************************************************
    /**
     * 使用给定的 replacement 替换此字符串所有匹配给定的正则表达式的子字符串..
     * 
     * @param content
     *            需要被替换的字符串
     * @param regex
     *            用来匹配此字符串的正则表达式
     * @param replacement
     *            用来替换每个匹配项的字符串
     * @return 所得String,如果传过来的内容是空,则返回""
     */
    public static String replaceAll(Object content,String regex,String replacement){
        if (null == content){
            return StringUtils.EMPTY;
        }
        return content.toString().replaceAll(regex, replacement);
    }

    /**
     * 使用指定的字面值替换序列替换此字符串所有匹配字面值目标序列的子字符串.
     * 
     * <p>
     * 该替换从字符串的开头朝末尾执行,例如,用 "b" 替换字符串 "aaa" 中的 "aa" 将生成 "ba" 而不是 "ab".
     * </p>
     * 
     * <pre>
     * 处理了replacement为空的情况
     * </pre>
     * 
     * @param content
     *            内容
     * @param target
     *            要被替换的 char 值序列
     * @param replacement
     *            char 值的替换序列
     * @return 所有匹配字面值目标序列的子字符串
     */
    public static String replace(Object content,String target,Object replacement){
        if (null == content){
            return StringUtils.EMPTY;
        }
        // 替换序列是null
        String useReplacement = Validator.isNullOrEmpty(replacement) ? StringUtils.EMPTY : replacement.toString();
        return content.toString().replace(target, useReplacement);
    }

    /**
     * The following example demonstrates this:
     * 
     * <pre>
     * Map valuesMap = HashMap();
     * valuesMap.put(&quot;animal&quot;, &quot;quick brown fox&quot;);
     * valuesMap.put(&quot;target&quot;, &quot;lazy dog&quot;);
     * 
     * StrSubstitutor sub = new StrSubstitutor(valuesMap);
     * String templateString = &quot;The ${animal} jumped over the ${target}.&quot;;
     * String resolvedString = sub.replace(templateString);
     * </pre>
     * 
     * yielding:
     * 
     * <pre>
     *      The quick brown fox jumped over the lazy dog.
     * </pre>
     *
     * @param <V>
     *            the value type
     * @param templateString
     *            the template string
     * @param valuesMap
     *            the values map
     * @return the string
     * @see org.apache.commons.lang3.text.StrSubstitutor#replace(String)
     * @since 1.1.1
     */
    public static <V> String replace(String templateString,Map<String, V> valuesMap){
        StrSubstitutor strSubstitutor = new StrSubstitutor(valuesMap);
        return strSubstitutor.replace(templateString);
    }

    // [end]

    // [start]startsWith

    /**
     * 测试此字符串是否以指定的前缀开始..
     * 
     * @param value
     *            value
     * @param prefix
     *            前缀
     * @return 如果参数表示的字符序列是此字符串表示的字符序列的前缀,则返回 true；否则返回 false.还要注意,如果参数是空字符串,或者等于此 String 对象（用 equals(Object) 方法确定）,则返回 true.
     */
    public static boolean startsWith(Object value,String prefix){
        return ConvertUtil.toString(value).startsWith(prefix);
    }

    // [end]
    /**
     * 带有数字格式的数字字符串,与数字相加(一般生成流水号使用).
     * 
     * <pre>
     * {@code
     * stringAddInt("002",2); return 004
     * stringAddInt("000002",1200); return 001202
     * }
     * </pre>
     * 
     * @param str
     *            带有数字格式的数字字符串 比如 002
     * @param i
     *            数字
     * @return 带有数字格式的数字字符串,与数字相加(一般生成流水号使用).
     * @see NumberUtil#toString(Number, String)
     */
    public static String stringAddInt(String str,int i){
        int length = str.length();
        String pattern = "";
        for (int j = 0; j < length; ++j){
            pattern += "0";
        }
        return NumberUtil.toString(Integer.parseInt(str) + i, pattern);
    }

    // [start]substring

    // ********************************substring************************************************
    /**
     * [截取]从指定索引处(beginIndex)的字符开始,直到此字符串末尾.
     * 
     * <p>
     * Gets a substring from the specified String avoiding exceptions.
     * </p>
     *
     * <p>
     * 如果 beginIndex 是负数,那么表示倒过来截取,从结尾开始截取长度,此时等同于 {@link #substringLast(String, int)}
     * </p>
     *
     * <p>
     * A {@code null} String will return {@code null}. An empty ("") String will return "".
     * </p>
     *
     * <pre>
     * StringUtil.substring(null, *)   = null
     * StringUtil.substring("", *)     = ""
     * StringUtil.substring("abc", 0)  = "abc"
     * StringUtil.substring("abc", 2)  = "c"
     * StringUtil.substring("abc", 4)  = ""
     * StringUtil.substring("abc", -2) = "bc"
     * StringUtil.substring("abc", -4) = "abc"
     * </pre>
     * 
     * <pre>
     * substring("jinxin.feilong",6) 
     * return .feilong
     * </pre>
     * 
     * @param text
     *            内容 the String to get the substring from, may be null
     * @param beginIndex
     *            从指定索引处 the position to start from,negative means count back from the end of the String by this many characters
     * @return substring from start position, {@code null} if null String input
     * @see org.apache.commons.lang3.StringUtils#substring(String, int)
     * @see #substringLast(String, int)
     */
    public static String substring(final String text,final int beginIndex){
        return org.apache.commons.lang3.StringUtils.substring(text, beginIndex);
    }

    /**
     * [截取]从开始位置(startIndex),截取固定长度(length)字符串.
     * 
     * <pre>
     * StringUtil.substring("jinxin.feilong", 6, 2)
     * 
     * renturn .f
     * </pre>
     *
     * @param textString
     *            被截取文字
     * @param startIndex
     *            索引开始位置,0开始
     * @param length
     *            长度 {@code >=1} 1个 即本身 <br>
     *            正常情况下,即返回出来的字符串长度
     * @return the string
     * @see org.apache.commons.lang3.StringUtils#substring(String, int, int)
     */
    public static String substring(final String textString,int startIndex,int length){
        return org.apache.commons.lang3.StringUtils.substring(textString, startIndex, startIndex + length);
    }

    /**
     * [截取]:从第一次出现字符串位置开始(包含)截取到最后.
     * 
     * <p>
     * 调用{@link #substring(String, String, int)}, 默认 shift=0 包含当前 beginString.
     * </p>
     * 
     * <pre>
     * substring(&quot;jinxin.feilong&quot;,&quot;.&quot;)======&gt;&quot;.feilong&quot;
     * </pre>
     * 
     * @param text
     *            text
     * @param beginString
     *            beginString开始截取的字符串
     * @return 调用{@link #substring(String, String, int)}, 默认 shift=0 包含当前 beginString.
     * @see #substring(String, String, int)
     */
    public static String substring(final String text,String beginString){
        return substring(text, beginString, 0);
    }

    /**
     * [截取]:从第一次出现字符串位置开始(包含)截取到最后,shift表示向前或者向后挪动位数.
     * 
     * <h3>示例:</h3>
     * 
     * <blockquote>
     * <ul>
     * <li>substring(&quot;jinxin.feilong&quot;,&quot;.&quot;,0)======&gt;&quot;.feilong&quot;</li>
     * <li>substring(&quot;jinxin.feilong&quot;,&quot;.&quot;,1)======&gt;&quot;feilong&quot;</li>
     * </ul>
     * </blockquote>
     *
     * @param text
     *            text
     * @param beginString
     *            beginString
     * @param shift
     *            负数表示向前,整数表示向后,0表示依旧从自己的位置开始算起
     * @return <ul>
     *         <li>if isNullOrEmpty(text),return null</li>
     *         <li>if isNullOrEmpty(beginString),return null</li>
     *         <li>if text.indexOf(beginString)==-1,return null</li>
     *         <li>{@code  beginIndex + shift > text.length()},return null</li>
     *         <li>else,return text.substring(beginIndex + shift)</li>
     *         </ul>
     * @see org.apache.commons.lang3.StringUtils#substringAfter(String, String)
     */
    public static String substring(final String text,String beginString,int shift){
        if (Validator.isNullOrEmpty(text)){
            return StringUtils.EMPTY;
        }

        if (Validator.isNullOrEmpty(beginString)){
            return StringUtils.EMPTY;
        }
        //****************************************************
        int beginIndex = text.indexOf(beginString);
        if (beginIndex == -1){// 查不到指定的字符串
            return StringUtils.EMPTY;
        }
        //****************************************************
        int startIndex = beginIndex + shift;
        int textLength = text.length();

        if (startIndex < 0){
            String pattern = "beginIndex+shift<0,beginIndex:{},shift:{},text:{},text.length:{}";
            throw new IllegalArgumentException(Slf4jUtil.formatMessage(pattern, beginIndex, shift, text, textLength));
        }

        if (startIndex > textLength){
            LOGGER.warn("beginIndex+shift>text.length(),beginIndex:{},shift:{},text:{},text.length:{}", beginIndex, shift, text, textLength);
            return StringUtils.EMPTY;
        }
        // 索引从0开始
        return text.substring(startIndex);
    }

    /**
     * [截取]:从开始的字符串到结束的字符串中间的字符串.
     * 
     * <p>
     * 包含开始的字符串startString,不包含结束的endString.
     * </p>
     * 
     * @param text
     *            文字
     * @param startString
     *            开始的字符串,null表示从开头开始截取
     * @param endString
     *            结束的字符串
     * @return
     *         <ul>
     *         <li>Validator.isNullOrEmpty(text),return null;</li>
     *         <li>Validator.isNullOrEmpty(startString),return text.substring(0, text.indexOf(endString))</li>
     *         </ul>
     * @see org.apache.commons.lang3.StringUtils#substringBetween(String, String, String)
     */
    public static String substring(final String text,final String startString,final String endString){
        if (Validator.isNullOrEmpty(text)){
            return StringUtils.EMPTY;
        }
        if (Validator.isNullOrEmpty(startString)){
            return text.substring(0, text.indexOf(endString));
        }
        int beginIndex = text.indexOf(startString);
        int endIndex = text.indexOf(endString);
        return text.substring(beginIndex, endIndex);
    }

    /**
     * [截取]:获取文字最后位数的字符串.
     * 
     * <p>
     * 调用了 {@link String#substring(int)}
     * </p>
     * 
     * <pre>
     * Example 1:
     * {@code
     * StringUtil.substringLast("jinxin.feilong", 5)---->ilong
     * }
     * </pre>
     * 
     * @param text
     *            文字
     * @param lastLenth
     *            最后的位数
     * @return 截取文字最后几个字符串
     * @see java.lang.String#substring(int)
     */
    public static String substringLast(final String text,int lastLenth){
        return text.substring(text.length() - lastLenth);
    }

    /**
     * [截取]:去除最后几位.
     * 
     * <p>
     * 调用了 {@link java.lang.String#substring(int, int)}
     * </p>
     * 
     * <pre>
     * Example 1:
     * {@code
     * StringUtil.substringWithoutLast("jinxin.feilong", 5)---->jinxin.fe
     * }
     * </pre>
     * 
     * @param text
     *            文字
     * @param lastLenth
     *            最后的位数
     * @return 去除最后几位,如果text是空,则返回""
     * @see java.lang.String#substring(int, int)
     * @see org.apache.commons.lang3.StringUtils#left(String, int)
     */
    public static String substringWithoutLast(final String text,final int lastLenth){
        if (Validator.isNullOrEmpty(text)){
            return StringUtils.EMPTY;
        }
        return text.substring(0, text.length() - lastLenth);
    }

    /**
     * [截取]:去除最后的字符串(<code>text</code>必须以 <code>lastString</code>结尾).
     *
     * @param text
     *            the text
     * @param lastString
     *            the last string
     * @return the string
     * @since 1.4.0
     */
    public static String substringWithoutLast(final CharSequence text,final String lastString){
        if (Validator.isNullOrEmpty(text)){
            return StringUtils.EMPTY;
        }

        //由于上面的循环中,最后一个元素可能是null或者empty,判断加还是不加拼接符有点麻烦,因此,循环中统一拼接,但是循环之后做截取处理
        String returnValue = text.toString();
        if (Validator.isNullOrEmpty(lastString)){
            return returnValue;
        }

        if (returnValue.endsWith(lastString)){
            //去掉最后的拼接符
            return substringWithoutLast(returnValue, lastString.length());
        }
        return returnValue;
    }

    // [end]

    // [start]toBytes

    // ********************************************************************************
    /**
     * 字符串转换成byte数组.
     * 
     * @param value
     *            字符串
     * @return byte数组
     * @since 1.3.0
     */
    public static byte[] getBytes(String value){
        return value.getBytes();
    }

    /**
     * 字符串转换成byte数组.
     * 
     * @param value
     *            字符串
     * @param charsetName
     *            受支持的 charset 名称,比如 utf-8, {@link CharsetType}
     * @return 所得 byte 数组
     * @see String#getBytes(String)
     * @see CharsetType
     * @since 1.3.0
     */
    public static byte[] getBytes(String value,String charsetName){
        try{
            return value.getBytes(charsetName);
        }catch (UnsupportedEncodingException e){
            LOGGER.error(e.getClass().getName(), e);
        }
        return ArrayUtils.EMPTY_BYTE_ARRAY;
    }

    // [end]

    // [start]splitToT

    /**
     * 将字符串分隔成 字符串数组.
     * 
     * <h3>注意</h3>
     * 
     * <blockquote>
     * <p>
     * 注意此处不是简单的分隔符是正则表达式, .$|()[{^?*+\\ 在正则表达式中有特殊的含义,因此我们使用.的时候必须进行转义,<br>
     * <span style="color:red">"\"转义时要写成"\\\\"</span> <br>
     * 最终调用了 {@link java.util.regex.Pattern#split(CharSequence)}
     * </p>
     * </blockquote>
     * 
     * 建议使用 {@link #tokenizeToStringArray(String, String)} 或者 {@link StringUtils#split(String)}
     * 
     * @param value
     *            value
     * @param regexSpliter
     *            分隔符,注意此处不是简单的分隔符是正则表达式, .$|()[{^?*+\\ 在正则表达式中有特殊的含义,因此我们使用.的时候必须进行转义,<br>
     *            <span style="color:red">"\"转义时要写成"\\\\"</span> <br>
     *            最终调用了 {@link java.util.regex.Pattern#split(CharSequence)}
     * @return 如果value 是null,返回null
     * @see String#split(String)
     * @see String#split(String, int)
     * @see java.util.regex.Pattern#split(CharSequence)
     * @see StringUtils#split(String)
     * @see #tokenizeToStringArray(String, String)
     */
    public static String[] split(String value,String regexSpliter){
        if (Validator.isNullOrEmpty(value)){
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return value.split(regexSpliter);
    }

    // [end]

    // [start]tokenizeToStringArray

    /**
     * (此方法借鉴 {@link "org.springframework.util.StringUtils#tokenizeToStringArray"}).
     * 
     * <p>
     * Tokenize the given String into a String array via a StringTokenizer. <br>
     * Trims tokens and omits empty tokens.
     * </p>
     * 
     * <p>
     * The given delimiters string is supposed to consist of any number of delimiter characters. Each of those characters can be used to
     * separate tokens. A delimiter is always a single character; for multi-character delimiters, consider using
     * {@code delimitedListToStringArray}
     * 
     * @param str
     *            the String to tokenize
     * @param delimiters
     *            the delimiter characters, assembled as String<br>
     *            参数中的所有字符都是分隔标记的分隔符,比如这里可以设置成 ";, " ,spring就是使用这样的字符串来分隔数组/集合的
     * @return an array of the tokens
     * @see java.util.StringTokenizer
     * @see String#trim()
     * @see "org.springframework.util.StringUtils#delimitedListToStringArray"
     * @see "org.springframework.util.StringUtils#tokenizeToStringArray"
     * @since 1.0.7
     */
    public static String[] tokenizeToStringArray(String str,String delimiters){
        boolean trimTokens = true;
        boolean ignoreEmptyTokens = true;
        return tokenizeToStringArray(str, delimiters, trimTokens, ignoreEmptyTokens);
    }

    /**
     * (此方法借鉴 {@link "org.springframework.util.StringUtils#tokenizeToStringArray"}).
     * <p>
     * Tokenize the given String into a String array via a StringTokenizer.
     * </p>
     * <p>
     * The given delimiters string is supposed to consist of any number of delimiter characters. <br>
     * Each of those characters can be used to separate tokens. <br>
     * A delimiter is always a single character; <br>
     * for multi-character delimiters, consider using {@code delimitedListToStringArray}
     * 
     * @param str
     *            the String to tokenize
     * @param delimiters
     *            the delimiter characters, assembled as String<br>
     *            参数中的所有字符都是分隔标记的分隔符,比如这里可以设置成 ";, " ,spring就是使用这样的字符串来分隔数组/集合的
     * @param trimTokens
     *            是否使用 {@link String#trim()}操作token
     * @param ignoreEmptyTokens
     *            是否忽视空白的token,如果为true,那么token必须长度 {@code >} 0;如果为false会包含长度=0 空白的字符<br>
     *            omit empty tokens from the result array
     *            (only applies to tokens that are empty after trimming; StringTokenizer
     *            will not consider subsequent delimiters as token in the first place).
     * @return an array of the tokens ({@code null} if the input String
     *         was {@code null})
     * @see java.util.StringTokenizer
     * @see String#trim()
     * @see "org.springframework.util.StringUtils#delimitedListToStringArray"
     * @see "org.springframework.util.StringUtils#tokenizeToStringArray"
     * @since 1.0.7
     */
    public static String[] tokenizeToStringArray(String str,String delimiters,boolean trimTokens,boolean ignoreEmptyTokens){
        if (null == str){
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        //StringTokenizer implements Enumeration<Object>
        //其在 Enumeration接口的基础上,  定义了 hasMoreTokens nextToken两个方法
        //实现的Enumeration接口中的  hasMoreElements nextElement 调用了  hasMoreTokens nextToken
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()){
            String token = st.nextToken();
            if (trimTokens){
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0){
                tokens.add(token);
            }
        }
        return ConvertUtil.toArray(tokens, String.class);
    }

    // [end]
    // [start]format

    /**
     * 格式化字符串.
     * 
     * <ul>
     * <li>StringUtil.format("%03d", 1)不能写成 StringUtil.format("%03d", "1")</li>
     * </ul>
     * 
     * <p>
     * %index$开头,index从1开始取值,表示将第index个参数拿进来进行格式化.<br>
     * 对整数进行格式化:格式化字符串由4部分组成:%[index$][标识][最小宽度]转换方式<br>
     * 对浮点数进行格式化:%[index$][标识][最少宽度][.精度]转换方式<br>
     * 转换方式 转换符和标志的说明<br>
     * </p>
     * 
     * <h3>转换符</h3>
     * 
     * <blockquote>
     * <table border="1" cellspacing="0" cellpadding="4">
     * <tr style="background-color:#ccccff">
     * <th align="left">转换符</th>
     * <th align="left">说明</th>
     * <th align="left">示例</th>
     * </tr>
     * <tr valign="top">
     * <td>%s</td>
     * <td>字符串类型</td>
     * <td>"mingrisoft"</td>
     * </tr>
     * <tr valign="top" style="background-color:#eeeeff">
     * <td>%c</td>
     * <td>字符类型</td>
     * <td>'m'</td>
     * </tr>
     * <tr valign="top">
     * <td>%b</td>
     * <td>布尔类型</td>
     * <td>true</td>
     * </tr>
     * <tr valign="top" style="background-color:#eeeeff">
     * <td>%d</td>
     * <td>整数类型（十进制）</td>
     * <td>99</td>
     * </tr>
     * <tr valign="top">
     * <td>%x</td>
     * <td>整数类型（十六进制）</td>
     * <td>FF</td>
     * </tr>
     * <tr valign="top" style="background-color:#eeeeff">
     * <td>%o</td>
     * <td>整数类型（八进制）</td>
     * <td>77</td>
     * </tr>
     * <tr valign="top">
     * <td>%f</td>
     * <td>浮点类型</td>
     * <td>99.99</td>
     * </tr>
     * <tr valign="top" style="background-color:#eeeeff">
     * <td>%a</td>
     * <td>十六进制浮点类型</td>
     * <td>FF.35AE</td>
     * </tr>
     * <tr valign="top">
     * <td>%e</td>
     * <td>指数类型</td>
     * <td>9.38e+5</td>
     * </tr>
     * <tr valign="top" style="background-color:#eeeeff">
     * <td>%g</td>
     * <td>通用浮点类型（f和e类型中较短的）</td>
     * <td></td>
     * </tr>
     * <tr valign="top">
     * <td>%h</td>
     * <td>散列码</td>
     * <td></td>
     * </tr>
     * <tr valign="top" style="background-color:#eeeeff">
     * <td>%%</td>
     * <td>百分比类型</td>
     * <td>％</td>
     * </tr>
     * <tr valign="top">
     * <td>%n</td>
     * <td>换行符</td>
     * <td></td>
     * </tr>
     * <tr valign="top" style="background-color:#eeeeff">
     * <td>%tx</td>
     * <td>日期与时间类型（x代表不同的日期与时间转换符</td>
     * <td></td>
     * </tr>
     * </table>
     * </blockquote>
     * 
     * 
     * <h3>标志</h3>
     * 
     * <blockquote>
     * <table border="1" cellspacing="0" cellpadding="4">
     * <tr style="background-color:#ccccff">
     * <th align="left">标志</th>
     * <th align="left">说明</th>
     * <th align="left">示例</th>
     * <th align="left">结果</th>
     * </tr>
     * <tr valign="top">
     * <td>+</td>
     * <td>为正数或者负数添加符号</td>
     * <td>("%+d",15)</td>
     * <td>+15</td>
     * </tr>
     * <tr valign="top" style="background-color:#eeeeff">
     * <td>-</td>
     * <td>左对齐(不可以与“用0填充”同时使用)</td>
     * <td>("%-5d",15)</td>
     * <td>|15 |</td>
     * </tr>
     * <tr valign="top">
     * <td>0</td>
     * <td>数字前面补0</td>
     * <td>("%04d", 99)</td>
     * <td>0099</td>
     * </tr>
     * <tr valign="top" style="background-color:#eeeeff">
     * <td>空格</td>
     * <td>在整数之前添加指定数量的空格</td>
     * <td>("% 4d", 99)</td>
     * <td>| 99|</td>
     * </tr>
     * <tr valign="top">
     * <td>,</td>
     * <td>以“,”对数字分组</td>
     * <td>("%,f", 9999.99)</td>
     * <td>9,999.990000</td>
     * </tr>
     * <tr valign="top" style="background-color:#eeeeff">
     * <td>(</td>
     * <td>使用括号包含负数</td>
     * <td>("%(f", -99.99)</td>
     * <td>(99.990000)</td>
     * </tr>
     * <tr valign="top">
     * <td>#</td>
     * <td>如果是浮点数则包含小数点,如果是16进制或8进制则添加0x或0</td>
     * <td>("%#x", 99) <br>
     * ("%#o", 99)</td>
     * <td>0x63<br>
     * 0143</td>
     * </tr>
     * <tr valign="top" style="background-color:#eeeeff">
     * <td>{@code <}</td>
     * <td>格式化前一个转换符所描述的参数</td>
     * <td>("%f和%{@code <}3.2f", 99.45)</td>
     * <td>99.450000和99.45</td>
     * </tr>
     * <tr valign="top">
     * <td>$</td>
     * <td>被格式化的参数索引</td>
     * <td>("%1$d,%2$s", 99,"abc")</td>
     * <td>99,abc</td>
     * </tr>
     * </table>
     * </blockquote>
     * 
     * @param format
     *            the format
     * @param args
     *            the args
     * @return A formatted string
     * @see java.util.Formatter
     * @see String#format(String, Object...)
     * @see String#format(java.util.Locale, String, Object...)
     * @since JDK 1.5
     */
    public static String format(String format,Object...args){
        return String.format(format, args);
    }

    // [end]

}
