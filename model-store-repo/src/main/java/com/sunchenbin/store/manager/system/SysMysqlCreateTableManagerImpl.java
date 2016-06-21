package com.sunchenbin.store.manager.system;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunchenbin.store.annotation.Column;
import com.sunchenbin.store.annotation.LengthCount;
import com.sunchenbin.store.annotation.Table;
import com.sunchenbin.store.command.CreateTableParam;
import com.sunchenbin.store.command.SysMysqlColumns;
import com.sunchenbin.store.constants.MySqlTypeConstant;
import com.sunchenbin.store.dao.system.CreateTablesMapper;
import com.sunchenbin.store.feilong.core.util.CollectionsUtil;
import com.sunchenbin.store.feilong.core.util.Validator;

@Transactional
@Service("sysMysqlCreateTableManager")
public class SysMysqlCreateTableManagerImpl implements SysMysqlCreateTableManager{
	private static final Logger	log	= LoggerFactory.getLogger(SysMysqlCreateTableManagerImpl.class);

	@Autowired
	private CreateTablesMapper createTablesMapper;

	/**
	 * 读取配置文件的三种状态（创建表、更新表、不做任何事情）
	 */
	@PostConstruct
	public void createTable(){
		String pack = "com.sunchenbin.store.model";
		Map<String, Object> buildTypeLengthMap = buildTypeLengthMap();

		Set<Class<?>> classes = getClasses(pack);

		// 用于存需要创建的表名+结构
		Map<String, List<Object>> newTableMap = new HashMap<String, List<Object>>();
		// 用于存需要更新字段类型等的表名+结构
		Map<String, List<Object>> modifyTableMap = new HashMap<String, List<Object>>();
		// 用于存需要增加字段的表名+结构
		Map<String, List<Object>> addTableMap = new HashMap<String, List<Object>>();
		// 用于存需要删除字段的表名+结构
		Map<String, List<Object>> removeTableMap = new HashMap<String, List<Object>>();
		// 用于存需要删除主键的表名+结构
		Map<String, List<Object>> dropKeyTableMap = new HashMap<String, List<Object>>();

		for (Class<?> clas : classes){
			Field[] fields = clas.getDeclaredFields();
			Table table = clas.getAnnotation(Table.class);

			// 用于存新增表的字段
			List<Object> newFieldList = new ArrayList<Object>();
			// 用于存删除的字段
			List<Object> removeFieldList = new ArrayList<Object>();
			// 用于存新增的字段
			List<Object> addFieldList = new ArrayList<Object>();
			// 用于存修改的字段
			List<Object> modifyFieldList = new ArrayList<Object>();
			// 用于存删除主键的字段
			List<Object> dropKeyFieldList = new ArrayList<Object>();

			for (Field field : fields){
				// 判断方法中是否有指定注解类型的注解
				boolean hasAnnotation = field.isAnnotationPresent(Column.class);
				if (hasAnnotation) {
					// 根据注解类型返回方法的指定类型注解
					Column column = field.getAnnotation(Column.class);
					CreateTableParam param = new CreateTableParam();
					param.setFieldName(column.name());
					param.setFieldType(column.type().toLowerCase());
					param.setFieldLength(column.length());
					param.setFieldDecimalLength(column.decimalLength());
					param.setFieldIsNull(column.isNull());
					param.setFieldIsKey(column.isKey());
					param.setFieldIsAutoIncrement(column.isAutoIncrement());
					param.setFieldDefaultValue(column.defaultValue());
					int length = (Integer) buildTypeLengthMap.get(column.type());
					param.setFileTypeLength(length);
					newFieldList.add(param);
				}
			}
			// 先查该表是否以存在
			int exist = createTablesMapper.findTableCountByTableName(table.name());

			// 不存在时
			if (exist == 0) {
				newTableMap.put(table.name(), newFieldList);
			}else{
				// 已存在时理论上做修改的操作
				List<SysMysqlColumns> tableColumnList = createTablesMapper.findTableEnsembleByTableName(table.name());

				// 验证对比从model中解析的fieldList与从数据库查出来的columnList
				// 1. 找出增加的字段
				// 2. 找出删除的字段
				// 3. 找出更新的字段

				// 从sysColumns中取出我们需要比较的列的List
				// 先取出name用来筛选出增加和删除的字段
				List<String> columnNames = CollectionsUtil.getPropertyValueList(tableColumnList, SysMysqlColumns.COLUMN_NAME);
				// 1. 找出增加的字段
				for (Object obj : newFieldList){
					CreateTableParam createTableParam = (CreateTableParam) obj;
					// 循环新的model中的字段，判断是否在数据库中已经存在
					if (!columnNames.contains(createTableParam.getFieldName())) {
						// 不存在，表示要在数据库中增加该字段
						addFieldList.add(obj);
					}
				}
				if (addFieldList.size() > 0) {
					addTableMap.put(table.name(), addFieldList);
				}

				// 将fieldList转成Map类型，字段名作为主键
				Map<String, CreateTableParam> fieldMap = new HashMap<String, CreateTableParam>();
				for (Object obj : newFieldList){
					CreateTableParam createTableParam = (CreateTableParam) obj;
					fieldMap.put(createTableParam.getFieldName(), createTableParam);
				}

				// 2. 找出删除的字段
				for (String fieldNm : columnNames){
					// 判断该字段在新的model结构中是否存在
					if (fieldMap.get(fieldNm) == null) {
						// 不存在，做删除处理
						removeFieldList.add(fieldNm);
					}
				}
				if (removeFieldList.size() > 0) {
					removeTableMap.put(table.name(), removeFieldList);
				}

				// 3. 找出更新的字段
				for (SysMysqlColumns sysColumn : tableColumnList){
					// 数据库中有该字段时
					CreateTableParam createTableParam = fieldMap.get(sysColumn.getColumn_name());
					if (createTableParam != null) {
						// 验证是否有更新
						// 1.验证类型
						if (!sysColumn.getData_type().toLowerCase().equals(createTableParam.getFieldType().toLowerCase())) {
							modifyFieldList.add(createTableParam);
							continue;
						}
						// 2.验证长度
						// 3.验证小数点位数
						int length = (Integer) buildTypeLengthMap.get(createTableParam.getFieldType().toLowerCase());
						String typeAndLength = createTableParam.getFieldType().toLowerCase();
						if (length == 1) {
							// 拼接出类型加长度，比如varchar(1)
							typeAndLength = typeAndLength + "(" + createTableParam.getFieldLength() + ")";
						}else if (length == 2) {
							// 拼接出类型加长度，比如varchar(1)
							typeAndLength = typeAndLength + "(" + createTableParam.getFieldLength() + ","
									+ createTableParam.getFieldDecimalLength() + ")";
						}
						// 判断类型+长度是否相同
						if (!sysColumn.getColumn_type().toLowerCase().equals(typeAndLength)) {
							modifyFieldList.add(createTableParam);
							continue;
						}
						
						// 4.验证主键
						if (!"PRI".equals(sysColumn.getColumn_key()) && createTableParam.isFieldIsKey()) {
							// 原本不是主键，现在变成了主键，那么要去做更新
							modifyFieldList.add(createTableParam);
							continue;
						}
						
						// 原本是主键，现在不是了，那么要去做删除主键的操作
						if ("PRI".equals(sysColumn.getColumn_key()) && !createTableParam.isFieldIsKey()) {
							dropKeyFieldList.add(createTableParam);
						}

						// 5.验证自增
						if ("auto_increment".equals(sysColumn.getExtra()) && !createTableParam.isFieldIsAutoIncrement()) {
							modifyFieldList.add(createTableParam);
							continue;
						}

						// 6.验证默认值
						if (Validator.isNullOrEmpty(sysColumn.getColumn_default())) {
							// 数据库默认值是null，model中注解设置的默认值不为NULL时，那么需要更新该字段
							if (!"NULL".equals(createTableParam.getFieldDefaultValue())) {
								modifyFieldList.add(createTableParam);
								continue;
							}
						}else if (!sysColumn.getColumn_default().equals(createTableParam.getFieldDefaultValue())) {
							// 两者不相等时，需要更新该字段
							modifyFieldList.add(createTableParam);
							continue;
						}

						// 7.验证是否可以为null
						if (sysColumn.getIs_nullable().equals("NO")) {
							if (createTableParam.isFieldIsNull()) {
								// 一个是可以一个是不可用，所以需要更新该字段
								modifyFieldList.add(createTableParam);
								continue;
							}
						}else if (sysColumn.getIs_nullable().equals("YES")) {
							if (!createTableParam.isFieldIsNull()) {
								// 一个是可以一个是不可用，所以需要更新该字段
								modifyFieldList.add(createTableParam);
								continue;
							}
						}

					}
				}
				if (modifyFieldList.size() > 0) {
					modifyTableMap.put(table.name(), modifyFieldList);
				}
				if (dropKeyFieldList.size() > 0) {
					dropKeyTableMap.put(table.name(), dropKeyFieldList);
				}

			}
		}

		// 做创建表操作
		if (newTableMap.size() > 0) {
			for (Entry<String, List<Object>> entry : newTableMap.entrySet()){
				Map<String, List<Object>> map = new HashMap<String, List<Object>>();
				map.put(entry.getKey(), entry.getValue());
				log.info("开始创建表："+entry.getKey());
				createTablesMapper.createTable(map);
				log.info("完成创建表："+entry.getKey());
			}
		}
		// 先去做删除主键的操作，这步操作必须在增加和修改字段之前！
		if (dropKeyTableMap.size() > 0) {
			for (Entry<String, List<Object>> entry : dropKeyTableMap.entrySet()){
				for (Object obj : entry.getValue()){
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(entry.getKey(), obj);
					CreateTableParam fieldProperties = (CreateTableParam)obj;
					log.info("开始为表"+entry.getKey()+"删除主键"+fieldProperties.getFieldName());
					createTablesMapper.dropKeyTableField(map);
					log.info("完成为表"+entry.getKey()+"删除主键"+fieldProperties.getFieldName());
				}
			}
		}
		
		// 做增加字段操作
		if (addTableMap.size() > 0) {
			for (Entry<String, List<Object>> entry : addTableMap.entrySet()){
				for (Object obj : entry.getValue()){
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(entry.getKey(), obj);
					CreateTableParam fieldProperties = (CreateTableParam)obj;
					log.info("开始为表"+entry.getKey()+"增加字段"+fieldProperties.getFieldName());
					createTablesMapper.addTableField(map);
					log.info("完成为表"+entry.getKey()+"增加字段"+fieldProperties.getFieldName());
				}
			}
		}
		// 做删除字段操作
		if (removeTableMap.size() > 0) {
			for (Entry<String, List<Object>> entry : removeTableMap.entrySet()){
				for (Object obj : entry.getValue()){
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(entry.getKey(), obj);
					String fieldName = (String)obj;
					log.info("开始删除表"+entry.getKey()+"中的字段"+fieldName);
					createTablesMapper.removeTableField(map);
					log.info("完成删除表"+entry.getKey()+"中的字段"+fieldName);
				}
			}
		}
		// 做修改字段操作
		if (modifyTableMap.size() > 0) {
			for (Entry<String, List<Object>> entry : modifyTableMap.entrySet()){
				for (Object obj : entry.getValue()){
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(entry.getKey(), obj);
					CreateTableParam fieldProperties = (CreateTableParam)obj;
					log.info("开始修改表"+entry.getKey()+"中的字段"+fieldProperties.getFieldName());
					createTablesMapper.modifyTableField(map);
					log.info("完成修改表"+entry.getKey()+"中的字段"+fieldProperties.getFieldName());
				}
			}
		}
	}

	/**
	 * 构建Map(字段名(小写),需要设置几个长度(0表示不需要设置，1表示需要设置一个，2表示需要设置两个))
	 */
	public Map<String, Object> buildTypeLengthMap(){
		Field[] fields = MySqlTypeConstant.class.getDeclaredFields();
		Map<String, Object> map = new HashMap<String, Object>();
		for (Field field : fields){
			LengthCount lengthCount = field.getAnnotation(LengthCount.class);
			map.put(field.getName().toLowerCase(), lengthCount.LengthCount());
		}
		return map;
	}

	/**
	 * 从包package中获取所有的Class
	 * 
	 * @param pack
	 * @return
	 */
	public static Set<Class<?>> getClasses(String pack){

		// 第一个class类的集合
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		// 是否循环迭代
		boolean recursive = true;
		// 获取包的名字 并进行替换
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try{
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements()){
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					System.err.println("file类型的扫描");
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				}else if ("jar".equals(protocol)) {
					// 如果是jar包文件
					// 定义一个JarFile
					System.err.println("jar类型的扫描");
					JarFile jar;
					try{
						// 获取jar
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						// 从此jar包 得到一个枚举类
						Enumeration<JarEntry> entries = jar.entries();
						// 同样的进行循环迭代
						while (entries.hasMoreElements()){
							// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							// 如果是以/开头的
							if (name.charAt(0) == '/') {
								// 获取后面的字符串
								name = name.substring(1);
							}
							// 如果前半部分和定义的包名相同
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								// 如果以"/"结尾 是一个包
								if (idx != -1) {
									// 获取包名 把"/"替换成"."
									packageName = name.substring(0, idx).replace('/', '.');
								}
								// 如果可以迭代下去 并且是一个包
								if ((idx != -1) || recursive) {
									// 如果是一个.class文件 而且不是目录
									if (name.endsWith(".class") && !entry.isDirectory()) {
										// 去掉后面的".class" 获取真正的类名
										String className = name.substring(packageName.length() + 1, name.length() - 6);
										try{
											// 添加到classes
											classes.add(Class.forName(packageName + '.' + className));
										}catch (ClassNotFoundException e){
											// log
											// .error("添加用户自定义视图类错误 找不到此类的.class文件");
											e.printStackTrace();
										}
									}
								}
							}
						}
					}catch (IOException e){
						// log.error("在扫描用户定义视图时从jar包获取文件出错");
						e.printStackTrace();
					}
				}
			}
		}catch (IOException e){
			e.printStackTrace();
		}

		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	public static void findAndAddClassesInPackageByFile(
			String packageName,
			String packagePath,
			final boolean recursive,
			Set<Class<?>> classes){
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			// log.warn("用户定义包名 " + packageName + " 下没有任何文件");
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter(){

			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file){
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles){
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
			}else{
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try{
					// 添加到集合中去
					// classes.add(Class.forName(packageName + '.' +
					// className));
					// 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
					classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
				}catch (ClassNotFoundException e){
					// log.error("添加用户自定义视图类错误 找不到此类的.class文件");
					e.printStackTrace();
				}
			}
		}
	}
}
