
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