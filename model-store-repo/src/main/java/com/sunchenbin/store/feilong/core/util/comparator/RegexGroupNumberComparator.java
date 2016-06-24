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
package com.sunchenbin.store.feilong.core.util.comparator;

import java.util.Comparator;

import com.sunchenbin.store.feilong.core.util.RegexUtil;

/**
 * 正则表达式分组 number group 比较器.
 * 
 * <p style="color:red">
 * 默认会提取正则表达式中的第一个group 转成int 类型进行比较
 * </p>
 * 
 * <p>
 * 比如 要比较 "ppt-coreContent2","ppt-coreContent13","ppt-coreContent12",默认直接使用字符串比较的话, ppt-coreContent13会排在 ppt-coreContent2前面
 * </p>
 * 
 * <p>
 * 针对这种情况, 你可以使用
 * </p>
 * 
 * <pre>
 * {@code
 * Collections.sort(includedFileUrlList, new RegexGroupNumberComparator(".*ppt-coreContent(\\d*).png"));
 * }
 * </pre>
 * 
 * @author feilong
 * @see org.apache.commons.io.comparator.NameFileComparator
 * @see org.apache.commons.io.comparator.DirectoryFileComparator
 * @see org.apache.commons.io.comparator.DefaultFileComparator
 * @see org.apache.commons.io.comparator.ExtensionFileComparator
 * @see org.apache.commons.io.comparator.PathFileComparator
 * @see org.apache.commons.io.comparator.SizeFileComparator
 * @version 1.4.0 2015年8月22日 下午11:36:52
 * @since 1.4.0
 */
public class RegexGroupNumberComparator implements Comparator<String>{

    /** 文件名称的正则表达式,主要方便提取数字,比如 ".*ppt-coreContent(\\d*).png". */
    private final String regexPattern;

    /**
     * The Constructor.
     * <p style="color:red">
     * 默认会提取正则表达式中的第一个group 转成int 类型进行比较
     * </p>
     * 
     * <p>
     * 比如 要比较 "ppt-coreContent2","ppt-coreContent13","ppt-coreContent12",默认直接使用字符串比较的话, ppt-coreContent13会排在 ppt-coreContent2前面
     * </p>
     * 
     * <p>
     * 针对这种情况, 你可以使用
     * </p>
     * 
     * <pre>
     * {@code
     * Collections.sort(includedFileUrlList, new RegexGroupNumberComparator(".*ppt-coreContent(\\d*).png"));
     * }
     * </pre>
     * 
     * @param regexPattern
     *            文件名称的正则表达式,主要方便提取数字,比如 ".*ppt-coreContent(\\d*).png"
     */
    public RegexGroupNumberComparator(String regexPattern){
        this.regexPattern = regexPattern;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(String s1,String s2){
        String group1 = RegexUtil.group(regexPattern, s1, 1);
        String group2 = RegexUtil.group(regexPattern, s2, 1);

        Integer parseInt = Integer.parseInt(group1);
        Integer parseInt2 = Integer.parseInt(group2);
        return parseInt.compareTo(parseInt2);
    }
}
