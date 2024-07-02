```java
/**
 * TODO
 * OpenEntityManagerInViewInterceptor, OpenEntityManagerInViewFilter, OpenSessionInViewInterceptor, OpenSessionInViewFilter 
 * 它们之间的区别
 */
```

### LazyInitializationException-JPA_Hibernate懒加载问题
Hibernate延迟加载异常：`org.hibernate.LazyInitializationException: could not initialize proxy [xxxxx] - no Session`

懒加载的目的是在加载主对象时不将相关对象加载到内存中，从而节省资源。相反，我们会将懒实体的初始化推迟到需要的时候。

Hibernate 使用代理和集合封装器来实现懒加载。

在检索懒加载的数据时，有两个步骤。首先是填充主对象，其次是在代理中检索数据。加载数据总是需要在 Hibernate 中打开会话。

当第二步发生在事务关闭之后时，问题就出现了，这会导致懒惰初始化异常（LazyInitializationException）。

- 异常发生的原因：
1, 开启一个Session，然后开启一个事务，接着从数据库中查询一个对象并将其组装成一个POJO。
2, 如果这个POJO中有懒加载的对象，此时并不会发出sql去查询这个懒加载对象，而是直接返回一个懒加载的代理对象，且这个代理对象里只有id。
3, 提交事务，关闭Session。
4, 访问懒加载代理对象的id以外的属性，此时需要发出sql去查询这个懒加载对象，但是此时Session已经关闭了，所以就会报错。

- 解决方法：
1. 【不推荐】关闭懒加载：在实体类上添加`@Proxy(lazy = false)`，或者将关联的属性设置为`FetchType.EAGER`，这样就会在查询主对象时同时查询关联的对象。
2. 【不推荐】设置`spring.jpa.open-in-view=true`，这种方法可以让Session在整个HTTP请求周期内保持活跃，但可能导致连接被长时间占用，并引发其他问题。
3. 【不推荐】设置`spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true`，这个设置可以为每个懒加载的实体打开一个临时的Session，延迟加载的关联实体越多，请求附加的连接也就越多。
4. 【推荐】在Service的查询方法上增加 `@Transactional(readOnly = true)` 注解来划分事务边界。
5. 【推荐】在Spring Data JPA定义查询方法上，使用`@EntityGraph(attributePaths = {"maker", "items"})`注解定义实体图，来急切地获取关联属性。（这种方式会使用一个大SQL查出来所有的属性，可以避免 n+1 select 问题）


[A Guide to Spring’s Open Session in View](https://www.baeldung.com/spring-open-session-in-view)

[Quick Guide to Hibernate enable_lazy_load_no_trans](https://www.baeldung.com/hibernate-lazy-loading-workaround)

[Spring Data JPA and Named Entity Graphs](https://www.baeldung.com/spring-data-jpa-named-entity-graphs)