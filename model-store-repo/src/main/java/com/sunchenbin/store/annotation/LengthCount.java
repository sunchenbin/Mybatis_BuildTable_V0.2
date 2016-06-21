package com.sunchenbin.store.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 针对数据库类型加注解，用来标记该类型是否需要设置长度，需要设置几个
 * @author sunchenbin
 *
 */
//该注解用于方法声明
@Target(ElementType.FIELD)
//VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Retention(RetentionPolicy.RUNTIME)
//将此注解包含在javadoc中
@Documented
//允许子类继承父类中的注解
@Inherited
public @interface LengthCount{

	/**
	 * 默认是1，0表示不需要设置，1表示需要设置一个，2表示需要设置两个
	 * @return
	 */
	public int LengthCount() default 1;
}
