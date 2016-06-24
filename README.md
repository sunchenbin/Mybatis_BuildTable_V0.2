# Mybatis_BuildTable_V0.2
该项目架构使用的是SpringMvc+Mybatis+Maven，功能特点是通过配置model注解的方式来创建表，修改表结构，目前仅支持Mysql

使用规范：

核心代码都在model-store-repo中

1.SysMysqlColumns.java这个对象里面配置的是mysql的数据类型，这里配置的类型越多，意味着创建表时能使用的类型越多

2.LengthCount.java是一个自定义的注解，用于标记在SysMysqlColumns.java里面配置的数据类型上的，标记该类型需要设置几个长度，如datetime/varchar(1)/decimal(5,2)，分别是需要设置0个1个2个

3.Column.java也是一个自定义的注解，用于标记model中的字段上，作为创建表的依据如不标记，不会被扫描到，有几个属性用来设置字段名、字段类型、长度等属性的设置，详细请看代码上的注释

4.Table.java也是一个自定义的注解，用于标记在model对象上，有一个属性name，用于设置该model生成表后的表名，如不设置该注解，则该model不会被扫描到

ok，系统启动后会去自动调用SysMysqlCreateTableManagerImpl.java的createMysqlTable()方法，没错，这就是核心方法了，复杂创建、删除、修改表。

model-store-frontend/resources/config/autoCreateTable.properties

你会发现有这样一个配置文件，其中有两项配置

1.mybatis.table.auto=update

2.mybatis.model.pack=com.sunchenbin.store.model

本系统提供两种模式：

1.当mybatis.table.auto=create时，系统启动后，会将所有的表删除掉，然后根据model中配置的结构重新建表，该操作会破坏原有数据。

2.当mybatis.table.auto=update时，系统会自动判断哪些表是新建的，哪些字段要修改类型等，哪些字段要删除，哪些字段要新增，该操作不会破坏原有数据。

3.mybatis.model.pack这个配置是用来配置要扫描的用于创建表的对象的包名

系统配置的是使用maven来启动的，web依赖repo，frontend和mobile依赖web，所以要运行frontend和mobile，必须先instal一下web和repo

至于如何用maven启动项目....不再多说了
