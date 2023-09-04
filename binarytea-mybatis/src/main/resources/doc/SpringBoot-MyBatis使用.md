
### 定义MyBatis映射
MyBatis 支持通过XML和注解两种方式来配置映射。

#### 通过注解定义常用的映射
在使用JPA时，我们的映射关系是定义在实体类上的，但在 MyBatis 中，我们对实体类没有什么要求，也无须添加特定的注解，各种映射都是通过 Mapper 接口来定义的。

MyBatis中常用的注解：

| 注解 | 作用   | 说明                                                                                |
| --- |------|-----------------------------------------------------------------------------------|
| @Insert | 插入操作 | value 为具体使用的 SQL 语句                                                               |
| @Delete | 删除操作 | 同上                                                                                |
| @Update | 更新操作 | 同上                                                                                |
| @select | 查询操作 | 同上                                                                                |
| @Param | 指定参数名称，方便在 SQL 中使用对应参数 (一般不用指定)   |                                                                                   |
| @Results | 指定返回对象的映射方式，具体内容通过 @Result 注解设置   | id 用来设置结果映射的ID，以便复用                                                               |
| @Result | 指定具体字段、属性的映射关系   |                                                                                   |
| @ResultMap | 引用其他地方已事先定义好的映射关系   |                                                                                   |
| @Options | 设置开关和配置选项   | useGeneratedKeys: 使用生成的主键; keyProperty: 主键属性名; fetchSize: 获取结果集的条数; timeout: 超时时间 |
| @One | 指定复杂的单个属性映射   | Select一一指定查询使用的 Java 方法                                                                                |
| @Many | 指定复杂的集合属性映射   | 同上                                                                                |

#### 自定义类型映射
MvBatis 是通过 `TypeHandler` 来实现特殊类型的处理的。

size属性是一个枚举，通常枚举在数据库中有两种保存方式： 
- 一种是保存枚举名，用的就是`EnunTypeHandler`，例如，size 枚举的 SMALL、MEDIUM 和 LARGE;
- 另一种是保存枚举的顺序，用的是`EnumOrdinalTypeHandler`，例如，0、1和2分别对应了前面 size 枚举的 SMALL、MEDIUM 和 LARGE.

MyBatis 中默认使用`EnumTypeHandler` 来处理枚举类型。

#### 一对多、多对多的关系映射
通过在查询方法的`@Result`注解中添加`@One`或`@Many`注解，可以实现一对多、多对多的关系映射。

`@One`与`@Many`这两个注解中的属性：
- `select`用来指定查询使用的 Java 方法，给定的是具体获取方法的全限定类名加方法名;
- `fetchType`属性用来指定嵌套语句的获取策略;
- `columnPrefix`属性用来指定关联的列名的前缀;
- `resultMap`属性用来指定用于映射集合的结果映射ID。

```java
// MenuItemMapper

// OrderMapper

// TeaMakerMapper
```

### Spring中配置并使用MyBatis
`MyBatis-Spring`为MyBatis提供了与Spring Framework 无缝集成的能力，其中包括:
- 与`Spring`事务的集成，主要靠 `SpringManagedTransaction` 与 `SpringManagedTransactionFactory` 来实现:
- `SqlSession` 的构建，主要靠 `SqlSessionFactoryBean` 实现;
- `Mapper` 的构建，手动构建靠 `MapperFactoryBean`，也可以通过 `MapperScannerConfigurer` 来自动扫描;
- `异常`的解析与转换，由 `MyBatisExceptionTranslator` 实现.

实际使用时，只需要在上下文中配置以下几个Bean即可：
```xml
<!-- 配置数据源 -->
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <property name="mapperLocations" value="classpath*:mapper/*.xml"/>
</bean>

<!-- 按需定义mapper bean -->
<bean id="menuItemMapper" class="org.mybatis.spring.mapper,MapperFactoryBean">
    <property name="sqlSessionFactory" ref="sqlSessionFactory"/>
    <property name="mapperInterface" value="com.johann.binaryteamybatis.mapper.MenuItemMapper"/>
</bean>
```
如果 mapper 较多，此时可以直接通过扫描来实现mapper的自动注册：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:mybatis="http://mybatis.org/schema/mybatis-spring"
       xsi:schemaLocation="
            http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
            http://mybatis.org/schema/mybatis-spring http://mybatis.org/schema/mybatis-spring.xsd">

    <!-- 扫描mapper接口所在的包 -->
    <mybatis:scan base-package="com.johann.binaryteamybatis.mapper" />

    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
    </bean>
</beans>
```
或是通过注解来指定扫描范围：
```java
@MapperScan("com.johann.binaryteamybatis.mapper")
public class Config{}
```

### SpringBoot中配置并使用MyBatis
在SpringBoot中，只需要添加`mybatis-spring-boot-starter`依赖，剩下的就交给 `MybatisAutoConfiguration` 和 `MybatisLanguageDriverAutoConfiguration` 来做自动配置即可。

MyBatis-Spring-Boot-AutoConfigure 支持的一些配置项：
```properties
# 映射的 POJO类型放置的包路径
mybatis.type-aliases-package=com.johann.binaryteamybatis.model
# 类型映射所需的 TypeHandler 放置的包路径
mybatis.type-handlers-package=com.johann.binaryteamybatis.support.handler
# 映射文件的位置
# mybatis.mapper-locations=classpath:mapper/*.xml
# MyBatis 配置文件的位置
# mybatis.config-location=classpath:mybatis-config.xml
# MyBatis 核心配置，例如下面两个，不能和 mybatis.config-location 一起使用
# mybatis.configuration.*=value
# 是否将下划线映射为驼峰规则
mybatis.configuration.map-underscore-to-camel-case=true
# 默认语句超时时间
mybatis.configuration.default-statement-timeout=1000
```

### MyBatis 映射生成工具
MyBatis Generator (MBG) 是一个MyBatis的代码生成器，可以根据数据库的元数据和配置文件，为我们生成如下内容:
- 与数据表对应的 POJO类;
- Mapper 接口，如果用注解或混合方式配置映射，接口上会有对应的注解;
- SQLMap 映射XML 文件 (仅在 XML 方式或混合方式时生成)。 

MyBatis Generator 配置示例【generatorConfig.xml】:
```xml
<!DOCTYPE generatorConfiguration PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>
    <!-- MyBatis Generator 的一些全局配置 -->
    <!--
    # 数据库连接信息
    jdbc.driver=com.mysql.jdbc.Driver
    jdbc.url=jdbc:mysql://localhost:3306/mydatabase
    jdbc.username=yourUsername
    jdbc.password=yourPassword

    使用引用的属性值
    <jdbcConnection driverClass="${jdbc.driver}"
        connectionURL="${jdbc.url}"
        userId="${jdbc.username}"
        password="${jdbc.password}">
    </jdbcConnection>
    -->
    <properties resource="generator.properties" />

    <!-- 插件配置，配置了生成构建器的插件 (类似 Lombok 的 @Builder)、生成tostring() 方法的插件、分页插件 -->
    <plugin type="org.mybatis.generator.plugins.FluentBuilderMethodsPlugin"/>
    <plugin type="org.mybatis.generator.plugins.ToStringPlugin"/>
    <plugin type="org.mybatis.generator.plugins.SerializablePlugin"/>
    <plugin type="org.mybatis.generator.plugins.RowBoundsPlugin”/>

    <!-- 数据库连接信息 -->
    <context id="MyBatisGenerator" targetRuntime="MyBatis3">
        <jdbcConnection driverClass="com.mysql.cj.jdbc.Driver"
                        connectionURL="jdbc:mysql://localhost:3306/mydatabase"
                        userId="yourUsername"
                        password="yourPassword">
        </jdbcConnection>

        <!-- Java Model 生成配置 -->
        <javaModelGenerator targetPackage="com.example.model"
                            targetProject="src/main/java">
            <!-- 是否允许子包，即 targetPackage.schemaName.tableName -->
            <property name="enableSubPackages" value="true" />
            <property name="trimStrings" value="true" />
        </javaModelGenerator>

        <!-- SQL Map XML 文件生成配置 -->
        <sqlMapGenerator targetPackage="com.example.mapper"
                         targetProject="src/main/resources/mapper">
            <property name="enableSubPackages" value="true" />
        </sqlMapGenerator>

        <!-- Mapper 接口文件生成配置 -->
        <!-- type的取值有三个：
        1. XMLMAPPER：【默认值】表示生成的Mapper接口类将使用XML配置文件来进行SQL映射，通常是使用传统的XML方式来定义SQL语句。
        2. ANNOTATEDMAPPER：表示生成的Mapper接口类将使用注解（annotations）来进行SQL映射，而不是XML配置文件。这种方式更加现代化和类型安全，但相对于XML配置需要更多的注解和模板代码。
        3. MIXEDMAPPER 表示生成的Mapper接口类将同时生成XML配置文件和注解，即混合模式。
        -->
        <javaClientGenerator type="MIXEDMAPPER"
                             targetPackage="com.example.mapper"
                             targetProject="src/main/java">
            <property name="enableSubPackages" value="true" />
        </javaClientGenerator>

        <!-- 数据库表及生成规则配置 -->
        <table tableName="your_table_name">
            <!--`sqlStatement` 属性用于指定在插入记录时获取自动生成的主键值的SQL语句
            0. JDBC：使用JDBC驱动程序的默认方式生成主键。
            
            1. AUTO_INCREMENT（MySQL）：
                当你使用MySQL数据库时，可以将 sqlStatement 设置为 "AUTO_INCREMENT"，以表示主键列是一个自增主键。
                MySQL会自动为主键列生成一个自增的整数值，无需指定具体的SQL语句。

            2. IDENTITY（SQL Server、DB2、Sybase）：
                对于数据库系统如SQL Server、DB2、Sybase等，可以将 sqlStatement 设置为 "IDENTITY"，以表示主键列是一个标识列。
                这些数据库系统会自动为主键列生成唯一的整数值，也无需指定具体的SQL语句。

            3. CALL IDENTITY()（HSQLDB、H2、Derby）：
                对于一些嵌入式数据库系统如HSQLDB、H2、Derby等，可以使用 "CALL IDENTITY()" 来获取自动生成的主键值。
                这个SQL语句通常用于执行插入操作后立即获取生成的主键值。

            4. SELECT LAST_INSERT_ID()（SQLite）：
                当使用SQLite数据库时，可以将 sqlStatement 设置为 "SELECT LAST_INSERT_ID()"，以获取最后插入的自增主键值。
                这个SQL语句会返回最后插入行的自增主键值。

            5. CURRVAL 或 NEXTVAL（Oracle）：
                对于Oracle数据库，可以使用 CURRVAL 或 NEXTVAL 关键字来获取自动生成的序列（sequence）的当前值或下一个值。
                例如，sqlStatement 可以设置为 "SELECT your_sequence_name.CURRVAL FROM DUAL"。
            -->
            <generatedKey column="id" sqlStatement="AUTO_INCREMENT" identity="true" />
            <columnOverride column="price" javaType="org.joda.money.Money" jdbcType="BIGINT"
                            typeHandler="com.johann.binaryteamybatis.support.handler.MoneyTypeHandler"/>
        </table>
    </context>
</generatorConfiguration>
```

运行 MyBatis Generator:
1. 命令行执行：`java -jar mybatis-generator-core-x.y.z.jar -configfile generatorConfig.xml`
2. Maven 插件执行：在pom文件中配置完MyBatis Generator插件后，在项目根目录下打开命令行，运行`mvn mybatis-generator:generate`即可。
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.mybatis.generator</groupId>
            <artifactId>mybatis-generator-maven-plugin</artifactId>
            <version>x.y.z</version> <!-- 插件的版本 -->
            <configuration>
                <!-- 配置文件的路径 -->
                <configurationFile>generatorConfig.xml</configurationFile> 
                <!-- 可选：其他配置项 -->
            </configuration>
            <dependencies>
                <!-- 可选：如果需要指定数据库驱动，可以在这里添加依赖 -->
            </dependencies>
        </plugin>
    </plugins>
</build>
```

### Mybatis 分页插件
PageHelper 是一个 MyBatis 分页插件，支持多种数据库方言，可以在不修改原 SQL 语句的情况下实现物理分页。
```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.4.3</version>
</dependency>
```

[PageHelper how to use](https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md)
PageHelper 在 Spring Boot 中的一些设置:
```properties
# 分页插件 com.github.pagehelper.PageHelper 的属性配置
# 在使用 RowBounds 作为分页参数时，将 offset 作为页码
pagehelper.offset-as-page-num=true
# 使用 RowBounds 分页时，是否进行 count 查询
pagehelper.row-bounds-with-count=true
# 如果分页大小为 0，则返回所有结果
pagehelper.page-size-zero=true
# 合理化分页，传入的页码小于等于 0 时返回第一页，大于最大页时返回最后一页
pagehelper.reasonable=true
# 从方法的参数中获取分页所需的信息
pagehelper.support-methods-arguments=true
```

### MyBatis Plus
MyBatis Plus 是一个 MyBatis 的增强工具，简化了 MyBatis 的开发，提供了常用的 CRUD 操作的封装，以及一些通用的功能，例如分页、排序、树形结构、批量操作等。
- 提供支持通用增删改查功能的 Mapper；
- 内置代码生成器；
- 内置分页插件；
- 支持 ActiveRecord 形式的操作
```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.1</version>
</dependency>
```