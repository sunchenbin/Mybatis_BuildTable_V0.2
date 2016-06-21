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

import java.text.DecimalFormat;

/**
 * 数字格式,内置常用数字格式.
 * 
 * 
 * <h3>常用数字格式:</h3>
 * 
 * <blockquote>
 * <table border=1 cellspacing=0 cellpadding=4 summary="Chart showing symbol, * location, localized, and meaning.">
 * <tr style="background-color:#ccccff">
 * <th align="left">Symbol</th>
 * <th align="left">Location</th>
 * <th align="left">Localized?</th>
 * <th align="left">Meaning</th>
 * </tr>
 * <tr valign="top">
 * <td><code>0</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>代表阿拉伯数字,每一个0表示一位阿拉伯数字, 如果该位不存在则显示0<br>
 * 如果对应位置上没有数字,则用零代替</td>
 * </tr>
 * <tr valign="top" style="background-color:#eeeeff">
 * <td><code>#</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>代表阿拉伯数字,每一个#表示一位阿拉伯数字, 如果该位不存在则不显示<br>
 * 如果对应位置上没有数字,则保持原样（不用补）；如果最前、后为0,则保持为空.</td>
 * </tr>
 * <tr valign="top">
 * <td><code>.</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>小数点分隔符或货币的小数分隔符</td>
 * </tr>
 * <tr valign="top" style="background-color:#eeeeff">
 * <td><code>-</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Minus sign 代表负号</td>
 * </tr>
 * <tr valign="top">
 * <td><code>,</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Grouping separator分组分隔符</td>
 * </tr>
 * <tr valign="top" style="background-color:#eeeeff">
 * <td><code>E</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Separates mantissa and exponent in scientific notation. <em>Need not be quoted in prefix or suffix.</em> 分隔科学计数法中的尾数和指数</td>
 * </tr>
 * <tr valign="top">
 * <td><code>;</code></td>
 * <td>Subpattern boundary</td>
 * <td>Yes</td>
 * <td>Separates positive and negative subpatterns</td>
 * </tr>
 * <tr valign="top" style="background-color:#eeeeff">
 * <td><code>%</code></td>
 * <td>Prefix or suffix</td>
 * <td>Yes</td>
 * <td>Multiply by 100 and show as percentage数字乘以100并显示为百分数</td>
 * </tr>
 * <tr valign="top">
 * <td><code>&#92;u2030</code></td>
 * <td>Prefix or suffix</td>
 * <td>Yes</td>
 * <td>Multiply by 1000 and show as per mille value乘以1000并显示为千分数</td>
 * </tr>
 * <tr valign="top" style="background-color:#eeeeff">
 * <td><code>&#164;</code> (<code>&#92;u00A4</code>)</td>
 * <td>Prefix or suffix</td>
 * <td>No</td>
 * <td>Currency sign, replaced by currency symbol. If doubled, replaced by international currency symbol. If present in a pattern, the
 * monetary decimal separator is used instead of the decimal separator.<br>
 * 货币记号,由货币号替换.<br>
 * 如果两个同时出现,则用国际货币符号替换； 如果出现在某个模式中,则使用货币小数分隔符,而不使用小数分隔符</td>
 * </tr>
 * <tr valign="top">
 * <td><code>'</code></td>
 * <td>Prefix or suffix</td>
 * <td>No</td>
 * <td>Used to quote special characters in a prefix or suffix, for example, <code>"'#'#"</code> formats 123 to <code>"#123"</code>. To
 * create a single quote itself, use two in a row: <code>"# o''clock"</code>. <br>
 * 用于在前缀或后缀中为特殊字符加引号, 例如 "'#'#" 将 123 格式化为 "#123".<br>
 * 要创建单引号本身,则连续使用两个单引号,例如"# o''clock"</td>
 * </tr>
 * </table>
 * </blockquote>
 *
 * @author feilong
 * @version 1.4.0 2015年8月3日 上午3:06:20
 * @see DecimalFormat
 * @since 1.4.0
 */
public final class NumberPattern{

    /**
     * 整数,不含小数 <code>{@value}</code>.
     * 
     * @since 1.0.7
     * */
    public static final String NO_SCALE             = "#";

    /**
     * (2位小数点) <code>{@value}</code>.
     * 
     * @since 1.2.2
     * */
    public static final String TWO_DECIMAL_POINTS   = "#0.00";

    /** 百分数的表达式(不带小数) <code>{@value}</code>. */
    public static final String PERCENT_WITH_NOPOINT = "##%";

    /** 百分数的表达式(2位小数点) <code>{@value}</code>. */
    public static final String PERCENT_WITH_2POINT  = "#0.00%";

    /**
     * 百分数的表达式(1位小数点) <code>{@value}</code>.
     * 
     * @since 1.0.7
     */
    public static final String PERCENT_WITH_1POINT  = "#0.0%";

    /** Don't let anyone instantiate this class. */
    private NumberPattern(){
        //AssertionError不是必须的. 但它可以避免不小心在类的内部调用构造器. 保证该类在任何情况下都不会被实例化.
        //see 《Effective Java》 2nd
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }
}