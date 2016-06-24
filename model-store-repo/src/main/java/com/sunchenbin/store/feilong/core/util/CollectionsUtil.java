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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunchenbin.store.feilong.core.bean.BeanUtilException;
import com.sunchenbin.store.feilong.core.bean.ConvertUtil;
import com.sunchenbin.store.feilong.core.bean.PropertyUtil;
import com.sunchenbin.store.feilong.core.lang.NumberUtil;
import com.sunchenbin.store.feilong.core.tools.jsonlib.JsonUtil;
import com.sunchenbin.store.feilong.core.util.predicate.ArrayContainsPredicate;
import com.sunchenbin.store.feilong.core.util.predicate.ObjectPropertyEqualsPredicate;

/**
 * {@link Collection} 工具类,是 {@link Collections} 的扩展和补充.<br>
 * 
 * <h3>{@link <a href="http://stamen.iteye.com/blog/2003458">SET-MAP现代诗一首</a>}</h3>
 * 
 * <blockquote>
 * <ul>
 * <li>天下人都知道Set,Map不能重复</li>
 * <li>80%人知道hashCode,equals是判断重复的法则 </li>
 * <li>40%人知道Set添加重复元素时,旧元素不会被覆盖</li>
 * <li>20%人知道Map添加重复键时,旧键不会被覆盖,而值会覆盖</li>
 * </ul>
 * </blockquote>
 * 
 * @author feilong
 * @version 1.0 Sep 2, 2010 8:08:40 PM
 * @since 1.0.0
 * @since jdk1.5
 */
public final class CollectionsUtil{

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionsUtil.class);

    /** Don't let anyone instantiate this class. */
    private CollectionsUtil(){
        //AssertionError不是必须的. 但它可以避免不小心在类的内部调用构造器. 保证该类在任何情况下都不会被实例化.
        //see 《Effective Java》 2nd
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }

    /**
     * iterator是否包含某个值.
     * 
     * <p style="color:red">
     * 注意,比较的是 {@link java.lang.Object#toString()}
     * </p>
     * 
     * @param iterator
     *            iterator
     * @param value
     *            value
     * @return iterator是否包含某个值,如果iterator为null/empty,则返回false
     * @see Iterator#hasNext()
     * @see Iterator#next()
     */
    public static boolean contains(Iterator<?> iterator,Object value){
        if (Validator.isNullOrEmpty(iterator)){
            return false;
        }
        while (iterator.hasNext()){
            Object object = iterator.next();
            if (object.toString().equals(value.toString())){
                return true;
            }
        }
        return false;
    }

    /**
     * 从 <code>collection</code>中 删除 所有的 <code>remove</code>. 返回剩余的集合,返回剩余的集合(原集合对象不变).
     * 
     * <p>
     * The cardinality of an element <code>e</code> in the returned collection is the same as the cardinality of <code>e</code> in
     * <code>collection</code> unless <code>remove</code> contains <code>e</code>, in which case the cardinality is zero.
     * </p>
     * 
     * <p>
     * 这个方法非常有用,如果你不想修改 <code>collection</code>的话,不能调用 <code>collection.removeAll(remove);</code>.
     * </p>
     * 
     * <p>
     * 底层实现是调用的 {@link ListUtils#removeAll(Collection, Collection)},将不是<code>removeElement</code> 的元素加入到新的list返回.
     * </p>
     * 
     * @param <T>
     *            the generic type
     * @param collection
     *            the collection from which items are removed (in the returned collection)
     * @param remove
     *            the items to be removed from the returned <code>collection</code>
     * @return a <code>List</code> containing all the elements of <code>c</code> except
     *         any elements that also occur in <code>remove</code>.
     * @see ListUtils#removeAll(Collection, Collection)
     * @since Commons Collections 3.2
     * @since 1.0.8
     */
    public static <T> List<T> removeAll(Collection<T> collection,Collection<T> remove){
        return ListUtils.removeAll(collection, remove);
    }

    /**
     * 从 <code>collection</code>中 删除 <code>removeElement</code>,返回剩余的集合(原集合对象不变).
     * 
     * <p>
     * 这个方法非常有用,如果你不想修改 <code>collection</code>的话,不能调用 <code>collection.remove(removeElement);</code>.
     * </p>
     * 
     * <p>
     * 底层实现是调用的 {@link ListUtils#removeAll(Collection, Collection)},将不是<code>removeElement</code> 的元素加入到新的list返回.
     * </p>
     * 
     * @param <T>
     *            the generic type
     * @param collection
     *            the collection from which items are removed (in the returned collection)
     * @param removeElement
     *            the remove element
     * @return a <code>List</code> containing all the elements of <code>c</code> except
     *         any elements that also occur in <code>remove</code>.
     * @see ListUtils#removeAll(Collection, Collection)
     * @since Commons Collections 3.2
     * @since 1.0.8
     */
    public static <T> List<T> remove(Collection<T> collection,T removeElement){
        Collection<T> remove = new ArrayList<T>();
        remove.add(removeElement);
        return removeAll(collection, remove);
    }

    /**
     * 去重.
     * 
     * <p>
     * 如果原 <code>collection</code> 是有序的,那么会保留原 <code>collection</code>元素顺序
     * </p>
     * 
     * <h3>效率问题？contains的本质就是遍历.</h3>
     * 
     * <blockquote>
     * <p>
     * 在100W的list当中执行0.546秒,而contains,我则没耐心去等了.顺便贴一下在10W下2段代码的运行时间.<br>
     * [foo1] 100000 -> 50487 : 48610 ms.<br>
     * [foo2] 100000 -> 50487 : 47 ms.<br>
     * </p>
     * </blockquote>
     * 
     * @param <T>
     *            the generic type
     * @param collection
     *            the item src list
     * @return if Validator.isNullOrEmpty(collection) 返回null<br>
     *         else 返回的是 {@link ArrayList}
     * @see ArrayList#ArrayList(java.util.Collection)
     * @see LinkedHashSet#LinkedHashSet(Collection)
     * @see <a
     *      href="http://www.oschina.net/code/snippet_117714_2991?p=2#comments">http://www.oschina.net/code/snippet_117714_2991?p=2#comments</a>
     */
    public static <T> List<T> removeDuplicate(Collection<T> collection){
        if (Validator.isNullOrEmpty(collection)){
            return Collections.emptyList();
        }
        return new ArrayList<T>(new LinkedHashSet<T>(collection));
    }

    /**
     * 解析对象集合,使用 {@link PropertyUtil#getProperty(Object, String)}取到对象特殊属性,拼成List(ArrayList).
     * 
     * <p>
     * 支持属性级联获取,支付获取数组,集合,map,自定义bean等属性
     * </p>
     * 
     * <h3>使用示例:</h3>
     * 
     * <blockquote>
     * 
     * <pre>
     * List&lt;User&gt; testList = new ArrayList&lt;User&gt;();
     * 
     * User user;
     * UserInfo userInfo;
     * 
     * //*******************************************************
     * List&lt;UserAddress&gt; userAddresseList = new ArrayList&lt;UserAddress&gt;();
     * UserAddress userAddress = new UserAddress();
     * userAddress.setAddress(&quot;中南海&quot;);
     * userAddresseList.add(userAddress);
     * 
     * //*******************************************************
     * Map&lt;String, String&gt; attrMap = new HashMap&lt;String, String&gt;();
     * attrMap.put(&quot;蜀国&quot;, &quot;赵子龙&quot;);
     * attrMap.put(&quot;魏国&quot;, &quot;张文远&quot;);
     * attrMap.put(&quot;吴国&quot;, &quot;甘兴霸&quot;);
     * 
     * //*******************************************************
     * String[] lovesStrings1 = { &quot;sanguo1&quot;, &quot;xiaoshuo1&quot; };
     * userInfo = new UserInfo();
     * userInfo.setAge(28);
     * 
     * user = new User(2L);
     * user.setLoves(lovesStrings1);
     * user.setUserInfo(userInfo);
     * user.setUserAddresseList(userAddresseList);
     * 
     * user.setAttrMap(attrMap);
     * testList.add(user);
     * 
     * //*****************************************************
     * String[] lovesStrings2 = { &quot;sanguo2&quot;, &quot;xiaoshuo2&quot; };
     * userInfo = new UserInfo();
     * userInfo.setAge(30);
     * 
     * user = new User(3L);
     * user.setLoves(lovesStrings2);
     * user.setUserInfo(userInfo);
     * user.setUserAddresseList(userAddresseList);
     * user.setAttrMap(attrMap);
     * testList.add(user);
     * 
     * //数组
     * List&lt;String&gt; fieldValueList1 = ListUtil.getFieldValueList(testList, &quot;loves[1]&quot;);
     * LOGGER.info(JsonUtil.format(fieldValueList1));
     * 
     * //级联对象
     * List&lt;Integer&gt; fieldValueList2 = ListUtil.getFieldValueList(testList, &quot;userInfo.age&quot;);
     * LOGGER.info(JsonUtil.format(fieldValueList2));
     * 
     * //Map
     * List&lt;Integer&gt; attrList = ListUtil.getFieldValueList(testList, &quot;attrMap(蜀国)&quot;);
     * LOGGER.info(JsonUtil.format(attrList));
     * 
     * //集合
     * List&lt;String&gt; addressList = ListUtil.getFieldValueList(testList, &quot;userAddresseList[0]&quot;);
     * LOGGER.info(JsonUtil.format(addressList));
     * </pre>
     * 
     * </blockquote>
     *
     * @param <T>
     *            返回集合类型 generic type
     * @param <O>
     *            可迭代对象类型 generic type
     * @param objectCollection
     *            任何可以迭代的对象
     * @param propertyName
     *            迭代泛型对象的属性名称,Possibly indexed and/or nested name of the property to be extracted
     * @return 解析迭代集合,取到对象特殊属性,拼成List(ArrayList)
     * @see com.sunchenbin.store.feilong.core.bean.BeanUtil#getProperty(Object, String)
     * @see org.apache.commons.beanutils.PropertyUtils#getProperty(Object, String)
     * @see #getPropertyValueCollection(Collection, String, Collection)
     * @since jdk1.5
     */
    public static <T, O> List<T> getPropertyValueList(Collection<O> objectCollection,String propertyName){
        List<T> list = new ArrayList<T>();
        return getPropertyValueCollection(objectCollection, propertyName, list);
    }

    /**
     * 获得 property value set.
     *
     * @param <T>
     *            the generic type
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object collection
     * @param propertyName
     *            the property name
     * @return the property value set
     * @see #getPropertyValueCollection(Collection, String, Collection)
     * @since 1.0.8
     */
    public static <T, O> Set<T> getPropertyValueSet(Collection<O> objectCollection,String propertyName){
        Set<T> set = new LinkedHashSet<T>();
        return getPropertyValueCollection(objectCollection, propertyName, set);
    }

    /**
     * 循环objectCollection,调用 {@link PropertyUtil#getProperty(Object, String)} 获得 propertyName的值,塞到 <code>returnCollection</code> 中返回.
     *
     * @param <T>
     *            the generic type
     * @param <O>
     *            the generic type
     * @param <K>
     *            the key type
     * @param objectCollection
     *            the object collection
     * @param propertyName
     *            the property name
     * @param returnCollection
     *            the return collection
     * @return the property value collection
     * @see com.sunchenbin.store.feilong.core.bean.PropertyUtil#getProperty(Object, String)
     * @see org.apache.commons.beanutils.BeanToPropertyValueTransformer
     * @since 1.0.8
     */
    private static <T, O, K extends Collection<T>> K getPropertyValueCollection(
                    Collection<O> objectCollection,
                    String propertyName,
                    K returnCollection){
        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection can't be null/empty!");
        }

        if (Validator.isNullOrEmpty(propertyName)){
            throw new NullPointerException("propertyName is null or empty!");
        }

        if (null == returnCollection){
            throw new NullPointerException("returnCollection is null!");
        }

        try{
            for (O bean : objectCollection){
                @SuppressWarnings("unchecked")
                T property = (T) PropertyUtil.getProperty(bean, propertyName);
                returnCollection.add(property);
            }
        }catch (BeanUtilException e){
            LOGGER.error(e.getClass().getName(), e);
        }
        return returnCollection;
    }

    /**
     * Finds the first element in the given collection which matches the given predicate.
     * 
     * <p>
     * If the input collection or predicate is null, or no element of the collection matches the predicate, null is returned.
     * </p>
     *
     * @param <O>
     *            the generic type
     * @param <V>
     *            the value type
     * @param objectCollection
     *            the object collection
     * @param propertyName
     *            the property name
     * @param value
     *            the value
     * @return the first element of the collection which matches the predicate or null if none could be found
     * @see CollectionUtils#find(Iterable, Predicate)
     */
    public static <O, V> O find(Collection<O> objectCollection,String propertyName,V value){
        Predicate<O> predicate = new ObjectPropertyEqualsPredicate<O>(propertyName, value);
        return CollectionUtils.find(objectCollection, predicate);
    }

    /**
     * 循环遍历 <code>objectCollection</code>,返回 当bean propertyName 属性值 equals 特定value 时候的list.
     *
     * @param <O>
     *            the generic type
     * @param <V>
     *            the value type
     * @param objectCollection
     *            the object list
     * @param propertyName
     *            the property name
     * @param value
     *            the value
     * @return the property value list
     * @see CollectionUtils#select(Iterable, Predicate)
     */
    public static <O, V> List<O> select(Collection<O> objectCollection,String propertyName,V value){
        Object[] values = { value };
        return select(objectCollection, propertyName, values);
    }

    /**
     * 调用 {@link ArrayContainsPredicate},获得 <code>propertyName</code>的值,判断是否 在<code>values</code>数组中;如果在,将该对象存入list中返回.
     * 
     * <p>
     * 具体参见 {@link ArrayContainsPredicate}的使用
     * </p>
     *
     * @param <O>
     *            the generic type
     * @param <V>
     *            the value type
     * @param objectCollection
     *            the object collection
     * @param propertyName
     *            the property name
     * @param values
     *            the values
     * @return the list< o>
     * @see com.sunchenbin.store.feilong.core.util.predicate.ArrayContainsPredicate#ArrayContainsPredicate(String, Object...)
     */
    public static <O, V> List<O> select(Collection<O> objectCollection,String propertyName,V...values){
        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection can't be null/empty!");
        }

        if (Validator.isNullOrEmpty(propertyName)){
            throw new NullPointerException("propertyName is null or empty!");
        }

        Predicate<O> predicate = new ArrayContainsPredicate<O>(propertyName, values);
        return (List<O>) CollectionUtils.select(objectCollection, predicate);
    }

    /**
     * Select.
     *
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object collection
     * @param predicate
     *            the predicate
     * @return the list< o>
     */
    public static <O> List<O> select(Collection<O> objectCollection,Predicate<O> predicate){
        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection can't be null/empty!");
        }
        return (List<O>) CollectionUtils.select(objectCollection, predicate);
    }

    /**
     * Select rejected.
     *
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object collection
     * @param predicate
     *            the predicate
     * @return the list< o>
     * @since 1.4.0
     */
    public static <O> List<O> selectRejected(Collection<O> objectCollection,Predicate<O> predicate){
        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection can't be null/empty!");
        }
        return (List<O>) CollectionUtils.selectRejected(objectCollection, predicate);
    }

    /**
     * 循环遍历 <code>objectCollection</code> ,返回 当bean propertyName 属性值不 equals 特定value 时候的list.
     *
     * @param <O>
     *            the generic type
     * @param <V>
     *            the value type
     * @param objectCollection
     *            the object list
     * @param propertyName
     *            the property name
     * @param value
     *            the value
     * @return the property value list
     * @see CollectionUtils#selectRejected(Iterable, Predicate)
     */
    public static <O, V> List<O> selectRejected(Collection<O> objectCollection,String propertyName,V value){
        Object[] values = { value };
        return selectRejected(objectCollection, propertyName, values);
    }

    /**
     * 循环遍历 <code>objectCollection</code> ,返回 当bean propertyName 属性值 都不在values 时候的list.
     *
     * @param <O>
     *            the generic type
     * @param <V>
     *            the value type
     * @param objectCollection
     *            the object collection
     * @param propertyName
     *            the property name
     * @param values
     *            the values
     * @return the list< o>
     */
    public static <O, V> List<O> selectRejected(Collection<O> objectCollection,String propertyName,V...values){
        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection can't be null/empty!");
        }

        if (Validator.isNullOrEmpty(propertyName)){
            throw new NullPointerException("propertyName is null or empty!");
        }
        Predicate<O> predicate = new ArrayContainsPredicate<O>(propertyName, values);
        return (List<O>) CollectionUtils.selectRejected(objectCollection, predicate);
    }

    /**
     * 解析对象集合,以 <code>keyPropertyName</code>属性值为key, <code>valuePropertyName</code>属性值为值,组成map返回.
     * 
     * <p>
     * 注意:返回的是 {@link LinkedHashMap}
     * </p>
     * <br>
     * 使用 {@link PropertyUtil#getProperty(Object, String)}取到对象特殊属性. <br>
     * 支持属性级联获取,支付获取数组,集合,map,自定义bean等属性
     * 
     * <h3>使用示例:</h3>
     * 
     * <blockquote>
     * 
     * <pre>
     * List&lt;User&gt; testList = new ArrayList&lt;User&gt;();
     * testList.add(new User(&quot;张飞&quot;, 23));
     * testList.add(new User(&quot;关羽&quot;, 24));
     * testList.add(new User(&quot;刘备&quot;, 25));
     * 
     * Map&lt;String, Integer&gt; map = CollectionsUtil.getFieldValueMap(testList, &quot;name&quot;, &quot;age&quot;);
     * 
     * 返回 :
     * 
     * "关羽": 24,
     * "张飞": 23,
     * "刘备": 25
     * </pre>
     * 
     * </blockquote>
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param <O>
     *            可迭代对象类型 generic type
     * @param objectCollection
     *            任何可以迭代的对象
     * @param keyPropertyName
     *            the key property name
     * @param valuePropertyName
     *            the value property name
     * @return 解析对象集合,以 <code>keyPropertyName</code>属性值为key, <code>valuePropertyName</code>属性值为值,组成map返回
     * @see com.sunchenbin.store.feilong.core.bean.BeanUtil#getProperty(Object, String)
     * @see org.apache.commons.beanutils.PropertyUtils#getProperty(Object, String)
     */
    public static <K, V, O> Map<K, V> getPropertyValueMap(Collection<O> objectCollection,String keyPropertyName,String valuePropertyName){
        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection can't be null/empty!");
        }

        if (Validator.isNullOrEmpty(keyPropertyName)){
            throw new NullPointerException("keyPropertyName is null or empty!");
        }

        if (Validator.isNullOrEmpty(valuePropertyName)){
            throw new NullPointerException("valuePropertyName is null or empty!");
        }

        Map<K, V> map = new LinkedHashMap<K, V>();

        for (O bean : objectCollection){
            @SuppressWarnings("unchecked")
            K key = (K) PropertyUtil.getProperty(bean, keyPropertyName);
            @SuppressWarnings("unchecked")
            V value = (V) PropertyUtil.getProperty(bean, valuePropertyName);

            map.put(key, value);
        }
        return map;
    }

    /**
     * Group 对象(如果propertyName 存在相同的值,那么这些值,将会以list的形式 put到map中).
     *
     * @param <T>
     *            注意,此处的T其实是 Object 类型, 需要区别对待,比如从excel中读取的类型是String,那么就不能简简单单的使用Integer来接收, 因为不能强制转换
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object list
     * @param propertyName
     *            对面里面属性的名称
     * @return the map< t, list< o>>
     * @see com.sunchenbin.store.feilong.core.bean.PropertyUtil#getProperty(Object, String)
     * @see com.sunchenbin.store.feilong.core.lang.ArrayUtil#group(Object[], String)
     * @see #groupOne(Collection, String)
     * @since 1.0.8
     */
    public static <T, O> Map<T, List<O>> group(Collection<O> objectCollection,String propertyName){
        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection can't be null/empty!");
        }
        if (Validator.isNullOrEmpty(propertyName)){
            throw new NullPointerException("propertyName is null or empty!");
        }

        //视需求  可以换成 HashMap 或者TreeMap
        Map<T, List<O>> map = new LinkedHashMap<T, List<O>>(objectCollection.size());

        for (O o : objectCollection){
            T t = PropertyUtil.getProperty(o, propertyName);
            List<O> valueList = map.get(t);
            if (null == valueList){
                valueList = new ArrayList<O>();
            }
            valueList.add(o);
            map.put(t, valueList);
        }
        return map;
    }

    /**
     * 循环 <code>objectCollection</code>,统计 {@code propertyName}出现的次数.
     *
     * @param <T>
     *            the generic type
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object collection
     * @param propertyName
     *            the property name
     * @return 返回的是 {@link LinkedHashMap}
     */
    public static <T, O> Map<T, Integer> groupCount(Collection<O> objectCollection,String propertyName){
        return groupCount(objectCollection, null, propertyName);
    }

    /**
     * 循环 <code>objectCollection</code>,只选择 符合 <code>includePredicate</code>的对象,统计 {@code propertyName}出现的次数.
     *
     * @param <T>
     *            the generic type
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object collection
     * @param includePredicate
     *            只选择 符合 <code>includePredicate</code>的对象,如果是null 则统计集合中全部的Object
     * @param propertyName
     *            the property name
     * @return the map< t, integer>
     * @since 1.2.0
     */
    public static <T, O> Map<T, Integer> groupCount(Collection<O> objectCollection,Predicate<O> includePredicate,String propertyName){
        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection can't be null/empty!");
        }

        if (Validator.isNullOrEmpty(propertyName)){
            throw new NullPointerException("propertyName is null or empty!");
        }

        Map<T, Integer> map = new LinkedHashMap<T, Integer>();

        for (O o : objectCollection){
            if (null != includePredicate){
                //跳出循环标记
                boolean continueFlag = !includePredicate.evaluate(o);

                if (continueFlag){
                    continue;
                }
            }

            T t = PropertyUtil.getProperty(o, propertyName);
            Integer count = map.get(t);
            if (null == count){
                count = 0;
            }
            count = count + 1;

            map.put(t, count);
        }
        return map;
    }

    /**
     * Group one(map只put第一个匹配的元素).
     *
     * @param <T>
     *            the generic type
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object collection
     * @param propertyName
     *            the property name
     * @return the map< t, o>
     * @see #group(Collection, String)
     * @since 1.0.8
     */
    public static <T, O> Map<T, O> groupOne(Collection<O> objectCollection,String propertyName){

        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection can't be null/empty!");
        }

        if (Validator.isNullOrEmpty(propertyName)){
            throw new NullPointerException("propertyName is null or empty!");
        }

        //视需求  可以换成 HashMap 或者TreeMap
        Map<T, O> map = new LinkedHashMap<T, O>(objectCollection.size());

        for (O o : objectCollection){
            T key = PropertyUtil.getProperty(o, propertyName);

            if (!map.containsKey(key)){
                map.put(key, o);
            }else{
                if (LOGGER.isDebugEnabled()){
                    LOGGER.debug(
                                    "Abandoned except the first value outside,map:{},containsKey key:[{}],",
                                    JsonUtil.format(map.keySet()),
                                    key);
                }
            }
        }
        return map;
    }

    /**
     * 算术平均值.
     *
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object collection
     * @param scale
     *            平均数值的精度
     * @param propertyNames
     *            需要计算平均值的对象属性名称
     * @return the map< string, list< o>>
     * @see #sum(Collection, String...)
     */
    public static <O> Map<String, Number> avg(Collection<O> objectCollection,int scale,String...propertyNames){

        //总分
        Map<String, Number> sumMap = sum(objectCollection, propertyNames);

        int size = objectCollection.size();

        //视需求  可以换成 HashMap 或者TreeMap
        Map<String, Number> map = new LinkedHashMap<String, Number>(size);

        for (Map.Entry<String, Number> entry : sumMap.entrySet()){
            String key = entry.getKey();
            Number value = entry.getValue();

            map.put(key, NumberUtil.getDivideValue(ConvertUtil.toBigDecimal(value), size, scale));
        }

        return map;
    }

    /**
     * 总和,计算集合对象内指定的属性的总和.
     *
     * @param <O>
     *            the generic type
     * @param objectCollection
     *            the object collection
     * @param propertyNames
     *            the property names
     * @return the map< string, list< o>>
     */
    public static <O> Map<String, Number> sum(Collection<O> objectCollection,String...propertyNames){

        if (Validator.isNullOrEmpty(objectCollection)){
            throw new NullPointerException("objectCollection can't be null/empty!");
        }

        if (Validator.isNullOrEmpty(propertyNames)){
            throw new NullPointerException("propertyNames is null or empty!");
        }

        int size = objectCollection.size();

        //总分
        Map<String, Number> sumMap = new LinkedHashMap<String, Number>(size);

        for (O o : objectCollection){
            for (String propertyName : propertyNames){
                Number propertyValue = PropertyUtil.getProperty(o, propertyName);

                Number mapPropertyNameValue = sumMap.get(propertyName);
                if (null == mapPropertyNameValue){
                    mapPropertyNameValue = 0;
                }
                sumMap.put(propertyName, NumberUtil.getAddValue(mapPropertyNameValue, propertyValue));
            }
        }
        return sumMap;
    }
}