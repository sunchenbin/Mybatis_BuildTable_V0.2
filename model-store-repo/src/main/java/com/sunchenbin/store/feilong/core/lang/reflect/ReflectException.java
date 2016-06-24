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
package com.sunchenbin.store.feilong.core.lang.reflect;

import com.sunchenbin.store.feilong.core.tools.slf4j.Slf4jUtil;

//Exception又分为两类：一种是CheckedException,一种是UncheckedException.
//
//这两种Exception的区别主要是CheckedException需要用try...catch...显示的捕获,
//而UncheckedException不需要捕获. 通常UncheckedException又叫做RuntimeException.
//  
//《effective java》指出：
//  对于可恢复的条件使用被检查的异常（CheckedException）,
//  对于程序错误（言外之意不可恢复,大错已经酿成）使用运行时异常（RuntimeException）.

/**
 * 反射时候出现的异常
 * 
 * @author feilong
 * @version 1.0.7 2014-7-14 17:38
 * @since 1.0.7
 */
public final class ReflectException extends RuntimeException{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1699987643831455524L;

    /**
     * Instantiates a new reflect exception.
     */
    public ReflectException(){
        super();
    }

    /**
     * Instantiates a new reflect exception.
     * 
     * @param message
     *            the message
     */
    public ReflectException(String message){
        super(message);
    }

    /**
     * Instantiates a new reflect exception(支持slf4j格式写法).
     * 
     * @param messagePattern
     *            the message pattern
     * @param args
     *            the args
     */
    public ReflectException(String messagePattern, Object...args){
        super(Slf4jUtil.formatMessage(messagePattern, args));
    }

    /**
     * Instantiates a new reflect exception.
     * 
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public ReflectException(String message, Throwable cause){
        super(message, cause);
    }

    /**
     * Instantiates a new reflect exception.
     * 
     * @param cause
     *            the cause
     */
    public ReflectException(Throwable cause){
        super(cause);
    }
}