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
package com.sunchenbin.store.feilong.core.bean;

import java.beans.PropertyDescriptor;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunchenbin.store.feilong.core.util.Validator;

/**
 * 封装了 org.apache.commons.beanutils包下面的类.
 * 
 * <h3>关于类型转换:</h3>
 * 
 * <blockquote>
 * <p>
 * 这里使用偷懒的做法,调用了 {@link org.apache.commons.beanutils.ConvertUtilsBean#register(boolean, boolean, int) ConvertUtilsBean.register(boolean,
 * boolean, int)}方法<br>
 * 但是有后遗症,这是beanUtils核心公共的方法,可能会影响其他框架或者其他作者开发的代码<br>
 * 最正确的做法,自定义的类,自己单独写 {@link org.apache.commons.beanutils.Converter},<br>
 * 而 公共的类 比如 下面方法里面的类型:
 * </p>
 * 
 * <ul>
 * <li>{@link ConvertUtilsBean#registerPrimitives(boolean) registerPrimitives(boolean throwException)}</li>
 * <li>{@link ConvertUtilsBean#registerStandard(boolean,boolean) registerStandard(boolean throwException, boolean defaultNull);}</li>
 * <li>{@link ConvertUtilsBean#registerOther(boolean) registerOther(boolean throwException);}</li>
 * <li>{@link ConvertUtilsBean#registerArrays(boolean,int) registerArrays(boolean throwException, int defaultArraySize);}</li>
 * </ul>
 * 
 * 最好在用的时候 自行register,{@link org.apache.commons.beanutils.ConvertUtilsBean#deregister(Class) ConvertUtilsBean.deregister(Class)}
 * 
 * <p>
 * Example 1:
 * </p>
 * 
 * <pre>
 * 
 * MyObject myObject = new MyObject();
 * myObject.setId(3l);
 * myObject.setName(&quot;My Name&quot;);
 * 
 * ConvertUtilsBean cub = new ConvertUtilsBean();
 * cub.deregister(Long.class);
 * cub.register(new MyLongConverter(), Long.class);
 * 
 * LOGGER.debug(cub.lookup(Long.class));
 * 
 * BeanUtilsBean bub = new BeanUtilsBean(cub, new PropertyUtilsBean());
 * 
 * LOGGER.debug(bub.getProperty(myObject, &quot;name&quot;));
 * LOGGER.debug(bub.getProperty(myObject, &quot;id&quot;));
 * </pre>
 * 
 * </blockquote>
 * 
 * 
 * <h3>{@link PropertyUtils}与 {@link BeanUtils}区别:</h3>
 * 
 * <blockquote>
 * 
 * <pre>
 * BeanUtils.setProperty(pt1, &quot;x&quot;, &quot;9&quot;); // 这里的9是String类型
 * PropertyUtils.setProperty(pt1, &quot;x&quot;, 9); // 这里的是int类型
 * // 这两个类BeanUtils和PropertyUtils,前者能自动将int类型转化,后者不能
 * </pre>
 * 
 * <p>
 * {@link PropertyUtils}类和 {@link BeanUtils}类很多的方法在参数上都是相同的,但返回值不同。 <br>
 * BeanUtils着重于"Bean",返回值通常是String,<br>
 * 而PropertyUtils着重于属性,它的返回值通常是Object。 
 * </p>
 * </blockquote>
 * 
 * <h3>关于 {@link BeanUtils#copyProperty(Object, String, Object) copyProperty} 和 {@link BeanUtils#setProperty(Object, String, Object)
 * setProperty}的区别:</h3>
 * 
 * <blockquote>
 * 
 * <pre>
 * 两者功能相似
 * 
 * 两者的区别点在于:
 * copyProperty 不支持目标bean是索引类型,但是支持bean有索引类型的setter方法
 * copyProperty 不支持目标bean是Map类型,但是支持bean有Map类型的setter方法
 * 
 * 
 * 如果我们只是为bean的属性赋值的话,使用{@link BeanUtils#copyProperty(Object, String, Object)}就可以了;
 * 而{@link BeanUtils#setProperty(Object, String, Object)}方法是实现  {@link BeanUtils#populate(Object,Map)}机制的基础,也就是说如果我们需要自定义实现populate()方法,那么我们可以override {@link BeanUtils#setProperty(Object, String, Object)}方法.
 * 所以,做为一般的日常使用,{@link BeanUtils#setProperty(Object, String, Object)}方法是不推荐使用的.
 * </pre>
 * 
 * </blockquote>
 * 
 * 
 * <h3>关于propertyName:</h3>
 * 
 * <blockquote>
 * 
 * <pre>
 * {@code
 * getProperty和setProperty,它们都只有2个参数,第一个是JavaBean对象,第二个是要操作的属性名.
 * Company c = new Company();
 * c.setName("Simple");
 *  
 * 对于Simple类型,参数二直接是属性名即可
 * //Simple
 * LOGGER.debug(BeanUtils.getProperty(c, "name"));
 *  
 * 对于Map类型,则需要以“属性名（key值）”的形式
 * //Map
 *     LOGGER.debug(BeanUtils.getProperty(c, "address (A2)"));
 *     HashMap am = new HashMap();
 *     am.put("1","234-222-1222211");
 *     am.put("2","021-086-1232323");
 *     BeanUtils.setProperty(c,"telephone",am);
 * LOGGER.debug(BeanUtils.getProperty(c, "telephone (2)"));
 *  
 * 对于Indexed,则为“属性名[索引值]”,注意这里对于ArrayList和数组都可以用一样的方式进行操作.
 * //index
 *     LOGGER.debug(BeanUtils.getProperty(c, "otherInfo[2]"));
 *     BeanUtils.setProperty(c, "product[1]", "NOTES SERVER");
 *     LOGGER.debug(BeanUtils.getProperty(c, "product[1]"));
 *  
 * 当然这3种类也可以组合使用啦！
 * //nest
 *     LOGGER.debug(BeanUtils.getProperty(c, "employee[1].name"));
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @author feilong
 * @version 1.0.0 2010-7-9 下午02:44:36
 * @version 1.0.2 2012-5-15 15:07
 * @version 1.0.7 2014年5月21日 下午12:24:53 move to om.feilong.commons.core.bean package
 * @version 1.0.8 2014-7-22 12:37 将异常转成 BeanUtilException 抛出
 * 
 * @see com.sunchenbin.store.feilong.core.bean.PropertyUtil
 * 
 * @see java.beans.BeanInfo
 * @see java.beans.PropertyDescriptor
 * @see java.beans.MethodDescriptor
 * 
 * @see org.apache.commons.beanutils.BeanUtils
 * @see org.apache.commons.beanutils.Converter
 * @see org.apache.commons.beanutils.converters.DateConverter
 * @see org.apache.commons.beanutils.converters.DateTimeConverter
 * @see org.apache.commons.beanutils.converters.AbstractConverter
 * 
 * @see org.apache.commons.beanutils.ConvertUtils#register(org.apache.commons.beanutils.Converter, Class)
 * 
 * @see org.apache.commons.beanutils.ConvertUtilsBean#registerPrimitives(boolean)
 * @see org.apache.commons.beanutils.ConvertUtilsBean#registerStandard(boolean, boolean)
 * @see org.apache.commons.beanutils.ConvertUtilsBean#registerOther(boolean)
 * @see org.apache.commons.beanutils.ConvertUtilsBean#registerArrays(boolean, int)
 * 
 * @see "org.springframework.beans.BeanUtils"
 * 
 * @since 1.0.0
 */
public final class BeanUtil{

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtil.class);

    /** Don't let anyone instantiate this class. */
    private BeanUtil(){
        //AssertionError不是必须的. 但它可以避免不小心在类的内部调用构造器. 保证该类在任何情况下都不会被实例化.
        //see 《Effective Java》 2nd
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }

    static{
        boolean throwException = false;
        boolean defaultNull = true;
        int defaultArraySize = 10;

        //XXX 
        BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
        ConvertUtilsBean convertUtils = beanUtilsBean.getConvertUtils();

        convertUtils.register(throwException, defaultNull, defaultArraySize);
    }

    // [start] copyProperties

    /**
     * 对象值的复制 {@code fromObj-->toObj}.
     * 
     * <h3>注意:</h3> <blockquote>
     * <ol>
     * <li>这种copy都是浅拷贝,复制后的2个Bean的同一个属性可能拥有同一个对象的ref, 这个在使用时要小心,特别是对于属性为自定义类的情况 .</li>
     * <li>此方法调用了 {@link BeanUtils#copyProperties(Object, Object)} ,会自动进行Object--->String--->Object类型转换,<br>
     * 如果需要copy的两个对象属性之间的类型是一样的话,那么调用这个方法会有性能消耗,此时建议调用 {@link PropertyUtil#copyProperties(Object, Object, String...)}</li>
     * </ol>
     * </blockquote>
     * 
     * 
     * <h3>注意点:</h3>
     * 
     * <blockquote>
     * 
     * <ol>
     * <li>如果传入的includePropertyNames,含有 <code>fromObj</code>没有的属性名字,将会抛出异常</li>
     * <li>如果传入的includePropertyNames,含有 <code>fromObj</code>有,但是 <code>toObj</code>没有的属性名字,可以正常运行,see
     * {@link org.apache.commons.beanutils.BeanUtilsBean#copyProperty(Object, String, Object)} Line391</li>
     * <li>
     * 
     * <p>
     * 如果有 {@link java.util.Date} 类型的 需要copy,那么 需要先这么着:
     * </p>
     * 
     * <p>
     * DateConverter converter = new DateConverter(DatePattern.forToString, Locale.US);<br>
     * ConvertUtils.register(converter, Date.class);
     * </p>
     * 
     * 或者使用内置的:
     * <p>
     * ConvertUtils.register(new DateLocaleConverter(Locale.US, DatePattern.forToString), Date.class); <br>
     * BeanUtil.copyProperty(b, a, &quot;date&quot;);
     * </p>
     * 
     * </li>
     * </ol>
     * </blockquote>
     * 
     * 
     * <h3>使用示例:</h3>
     * 
     * <blockquote>
     * 
     * <pre>
     * 例如两个pojo:enterpriseSales和enterpriseSales_form 都含有字段&quot;enterpriseName&quot;,&quot;linkMan&quot;,&quot;phone&quot;
     * 
     * 通常写法
     * enterpriseSales.setEnterpriseName(enterpriseSales_form.getEnterpriseName());
     * enterpriseSales.setLinkMan(enterpriseSales_form.getLinkMan());
     * enterpriseSales.setPhone(enterpriseSales_form.getPhone());
     * 
     * 此时,可以使用
     * BeanUtil.copyProperties(enterpriseSales,enterpriseSales_form,new String[]{&quot;enterpriseName&quot;,&quot;linkMan&quot;,&quot;phone&quot;});
     * </pre>
     * 
     * </blockquote>
     * 
     * 
     * <h3>{@link BeanUtils#copyProperties(Object, Object)}与 {@link PropertyUtils#copyProperties(Object, Object)}区别</h3>
     * 
     * <blockquote>
     * <ul>
     * <li>{@link BeanUtils#copyProperties(Object, Object)}能给不同的两个成员变量相同的,但类名不同的两个类之间相互赋值</li>
     * <li>{@link PropertyUtils#copyProperties(Object, Object)} 提供类型转换功能,即发现两个JavaBean的同名属性为不同类型时,在支持的数据类型范围内进行转换,而前者不支持这个功能,但是速度会更快一些.</li>
     * <li>commons-beanutils v1.9.0以前的版本 BeanUtils不允许对象的属性值为 null,PropertyUtils可以拷贝属性值 null的对象.<br>
     * (<b>注:</b>commons-beanutils v1.9.0+修复了这个情况,BeanUtilsBean.copyProperties() no longer throws a ConversionException for null properties
     * of certain data types),具体信息,可以参阅commons-beanutils的
     * {@link <a href="http://commons.apache.org/proper/commons-beanutils/javadocs/v1.9.2/RELEASE-NOTES.txt">RELEASE-NOTES.txt</a>}</li>
     * </ul>
     * </blockquote>
     *
     * @param toObj
     *            目标对象
     * @param fromObj
     *            原始对象
     * @param includePropertyNames
     *            包含的属性数组名字数组,(can be nested/indexed/mapped/combo)<br>
     *            如果 是null or empty ,将会调用 {@link BeanUtils#copyProperties(Object, Object)}
     * @see #setProperty(Object, String, Object)
     * @see org.apache.commons.beanutils.BeanUtilsBean#copyProperties(Object, Object)
     * @see <a href="http://www.cnblogs.com/kaka/archive/2013/03/06/2945514.html">Bean复制的几种框架性能比较(Apache BeanUtils、PropertyUtils,Spring
     *      BeanUtils,Cglib BeanCopier)</a>
     */
    //XXX add excludePropertyNames support
    public static void copyProperties(Object toObj,Object fromObj,String...includePropertyNames){
        if (null == toObj){
            throw new NullPointerException("No destination bean/toObj specified");
        }
        if (null == fromObj){
            throw new NullPointerException("No origin bean/fromObj specified");
        }

        if (Validator.isNullOrEmpty(includePropertyNames)){
            try{
                BeanUtils.copyProperties(toObj, fromObj);
                return;
            }catch (Exception e){
                LOGGER.error(e.getClass().getName(), e);
                throw new BeanUtilException(e);
            }
        }
        for (String propertyName : includePropertyNames){
            String value = getProperty(fromObj, propertyName);
            setProperty(toObj, propertyName, value);
        }
    }

    // [end]

    // [start] setProperty

    /**
     * 使用 {@link BeanUtils#setProperty(Object, String, Object)} 来设置属性值(<b>会进行类型转换</b>).
     * 
     * @param bean
     *            Bean on which setting is to be performed
     * @param propertyName
     *            Property name (can be nested/indexed/mapped/combo)
     * @param value
     *            Value to be set
     * @see org.apache.commons.beanutils.BeanUtils#setProperty(Object, String, Object)
     * 
     * @see org.apache.commons.beanutils.BeanUtilsBean#setProperty(Object, String, Object)
     * 
     * @see org.apache.commons.beanutils.PropertyUtils#setProperty(Object, String, Object)
     * @see com.sunchenbin.store.feilong.core.bean.PropertyUtil#setProperty(Object, String, Object)
     */
    public static void setProperty(Object bean,String propertyName,Object value){
        try{
            // BeanUtils支持把所有类型的属性都作为字符串处理,在后台自动进行类型转换(字符串和真实类型的转换)
            BeanUtils.setProperty(bean, propertyName, value);
        }catch (Exception e){
            LOGGER.error(e.getClass().getName(), e);
            throw new BeanUtilException(e);
        }
    }

    // [end]

    // [start] getProperty

    /**
     * 使用 {@link BeanUtils#getProperty(Object, String)} 类从对象中取得属性值.
     *
     * @param bean
     *            bean
     * @param propertyName
     *            属性名称
     * @return 使用BeanUtils类从对象中取得属性值
     * @see org.apache.commons.beanutils.BeanUtils#getProperty(Object, String)
     * @see org.apache.commons.beanutils.PropertyUtils#getProperty(Object, String)
     * @see com.sunchenbin.store.feilong.core.bean.PropertyUtil#getProperty(Object, String)
     */
    public static String getProperty(Object bean,String propertyName){
        // Return the value of the specified property of the specified bean,
        // no matter which property reference format is used, as a String.
        try{
            return org.apache.commons.beanutils.BeanUtils.getProperty(bean, propertyName);
        }catch (Exception e){
            LOGGER.error(e.getClass().getName(), e);
            throw new BeanUtilException(e);
        }
    }

    // [end]

    // [start] cloneBean

    /**
     * 调用{@link BeanUtils#cloneBean(Object)}.
     * 
     * <p>
     * 这个方法通过默认构造函数建立一个bean的新实例,然后拷贝每一个属性到这个新的bean中
     * <p>
     * 
     * <p>
     * {@link BeanUtils#cloneBean(Object)}在源码上看是调用了getPropertyUtils().copyProperties(newBean, bean);<br>
     * 最后实际上还是<b>复制的引用 ,无法实现深clone</b>
     * </p>
     * 
     * <p>
     * 但还是可以帮助我们减少工作量的,假如类的属性不是基础类型的话（即自定义类）,可以先clone出那个自定义类,在把他付给新的类,覆盖原来类的引用,<br>
     * 是为那些本身没有实现clone方法的类准备的 
     * </p>
     *
     * @param <T>
     *            the generic type
     * @param bean
     *            Bean to be cloned
     * @return the cloned bean
     *         (复制的引用 ,无法实现深clone)
     * @see org.apache.commons.beanutils.BeanUtils#cloneBean(Object)
     * @see org.apache.commons.beanutils.PropertyUtilsBean#copyProperties(Object, Object)
     */
    @SuppressWarnings("unchecked")
    public static <T> T cloneBean(T bean){
        try{
            //Clone a bean based on the available property getters and setters, even if the bean class itself does not implement Cloneable.
            return (T) BeanUtils.cloneBean(bean);
        }catch (Exception e){
            LOGGER.error(e.getClass().getName(), e);
            throw new BeanUtilException(e);
        }
    }

    // [end]

    // [start] describe 把Bean的属性值放入到一个Map里面

    /**
     * 返回一个<code>bean</code>中所有的可读属性(read method),并将属性名/属性值放入一个Map中.
     * 
     * <p>
     * 另外还有一个名为class的属性,属性值是Object的类名,事实上class是java.lang.Object的一个属性.
     * </p>
     * <p>
     * <span style="color:red">缺陷:<br>
     * 自己手工注册的ConvertUtils.register(dateTimeConverter, java.util.Date.class)不会生效</span><br>
     * 
     * 在赋值的时候,虽然调用了 {@link BeanUtilsBean#getNestedProperty(Object, String)}, 虽然也调用了 ConvertUtilsBean来转换 <br>
     * 但是 {@link ConvertUtilsBean#ConvertUtilsBean()} 默认的构造函数 是使用标准的转换
     * </p>
     *
     * @param bean
     *            Bean whose properties are to be extracted
     * @return Map of property descriptors
     * @see org.apache.commons.beanutils.BeanUtils#describe(Object)
     * @see org.apache.commons.beanutils.PropertyUtils#describe(Object)
     * @see PropertyUtil#describe(Object)
     * @see PropertyDescriptor
     * @see #populate(Object, Map)
     */
    public static Map<String, String> describe(Object bean){
        try{
            //Return the entire set of properties for which the specified bean provides a read method.
            return BeanUtils.describe(bean);
        }catch (Exception e){
            LOGGER.error(e.getClass().getName(), e);
            throw new BeanUtilException(e);
        }
    }

    // [end]

    // [start] populate(填充) 把properties/map里面的值放入bean中

    /**
     * 把properties/map里面的值populate(填充) 到bean中.
     *
     * @param bean
     *            JavaBean whose properties are being populated
     * @param properties
     *            Map keyed by property name,with the corresponding (String or String[]) value(s) to be set
     * @see org.apache.commons.beanutils.BeanUtils#populate(Object, Map)
     */
    public static void populate(Object bean,Map<String, ?> properties){
        try{
            BeanUtils.populate(bean, properties);
        }catch (Exception e){
            LOGGER.error(e.getClass().getName(), e);
            throw new BeanUtilException(e);
        }
    }

    // [end]
}