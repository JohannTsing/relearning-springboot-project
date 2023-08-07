### @EnableTransactionManagement

Enables `Spring's` annotation-driven transaction management capability, similar to the support found in `Spring's` `<tx:*> XML namespace`. To be used on @Configuration classes to configure traditional, imperative transaction management or reactive transaction management.
> 启用 Spring 的注解驱动事务管理功能，类似于 Spring 的 `<tx:*>` XML 命名空间中的支持。可用于 @Configuration 类，以配置传统、命令式事务管理或反应式事务管理。

The following example demonstrates imperative transaction management using a `PlatformTransactionManager`. For reactive transaction management, configure a `ReactiveTransactionManager` instead.
> 下面的示例使用 `PlatformTransactionManager` 演示了命令式事务管理。要进行反应式事务管理，请配置 `ReactiveTransactionManager`。

```java
@Configuration
@EnableTransactionManagement
public class AppConfig {

  @Bean
  public FooRepository fooRepository() {
      // configure and return a class having @Transactional methods
      // 配置并返回一个具有 @Transactional 方法的类
      return new JdbcFooRepository(dataSource());
  }

  @Bean
  public DataSource dataSource() {
      // configure and return the necessary JDBC DataSource
      // 配置并返回必要的 JDBC 数据源
  }

  @Bean
  public PlatformTransactionManager txManager() {
      return new DataSourceTransactionManager(dataSource());
  }
}
```
For reference, the example above can be compared to the following Spring XML configuration:
> 以上示例可与以下 Spring XML 配置进行比较，以供参考：

```xml
<beans>
    <tx:annotation-driven/>

    <bean id="fooRepository" class="com.foo.JdbcFooRepository">
        <constructor-arg ref="dataSource"/>
    </bean>

    <bean id="dataSource" class="com.vendor.VendorDataSource"/>

    <bean id="transactionManager" class="org.sfwk...DataSourceTransactionManager">
        <constructor-arg ref="dataSource"/>
    </bean>
</beans>
```

In both of the scenarios above, `@EnableTransactionManagement` and `<tx:annotation-driven/>` are responsible for registering the necessary Spring components that power annotation-driven transaction management, such as the `TransactionInterceptor` and the `proxy- or AspectJ-based` advice that weaves the interceptor into the call stack when `JdbcFooRepository's @Transactional` methods are invoked.
> 在上述两种情况下，``@EnableTransactionManagement` 和 `<tx:annotation-driven/>`负责注册必要的 Spring 组件，以支持注释驱动的事务管理，
> 例如 `TransactionInterceptor` 和基于proxy 或 `AspectJ` 的建议，当调用 `JdbcFooRepository` 的 `@Transactional` 方法时，将拦截器编织到调用堆栈中。

A minor difference between the two examples lies in the naming of the `TransactionManager` bean: In the @Bean case, the name is `"txManager"` (per the name of the method); in the XML case, the name is `"transactionManager"`. `<tx:annotation-driven/>` is hard-wired to look for a bean named `"transactionManager"` by default, however `@EnableTransactionManagement` is more flexible; it will fall back to a by-type lookup for any `TransactionManager` bean in the container. Thus the name can be `"txManager"`, `"transactionManager"`, or `"tm"`: it simply does not matter.
> 这两个示例的细微差别在于 `TransactionManager` Bean 的命名：在 @Bean 的情况下，名称是 `"txManager"`（根据方法的名称）；而在 XML 的情况下，名称是 `"transactionManager"`。
> 默认情况下，`<tx:annotation-driven/>` 被硬连线为查找名为 `"transactionManager "`的 Bean，但 `@EnableTransactionManagement` 更为灵活；它会回退到按类型查找容器中的任何 `TransactionManager` Bean。因此，名称可以是 `"txManager"`、`"transactionManager "`或 `"tm"`：这并不重要。

For those that wish to establish a more direct relationship between `@EnableTransactionManagement` and the exact transaction manager bean to be used, the `TransactionManagementConfigurer` callback interface may be implemented - notice the implements clause and the @Override-annotated method below:
> 如果希望在 `@EnableTransactionManagement` 和要使用的事务管理器 bean 之间建立更直接的关系，可以实现 `TransactionManagementConfigurer` 回调接口--
> 注意下面的实现子句和 @Override 注解方法：

```java
@Configuration
@EnableTransactionManagement
public class AppConfig implements TransactionManagementConfigurer {

  @Bean
  public FooRepository fooRepository() {
      // configure and return a class having @Transactional methods
      // 配置并返回一个具有 @Transactional 方法的类
      return new JdbcFooRepository(dataSource());
  }

  @Bean
  public DataSource dataSource() {
      // configure and return the necessary JDBC DataSource
      // 配置并返回必要的 JDBC 数据源
  }

  @Bean
  public PlatformTransactionManager txManager() {
      return new DataSourceTransactionManager(dataSource());
  }

  @Override
  public PlatformTransactionManager annotationDrivenTransactionManager() {
      return txManager();
  }
}
```
This approach may be desirable simply because it is more explicit, or it may be necessary in order to distinguish between two `TransactionManager` beans present in the same container. As the name suggests, the `annotationDrivenTransactionManager()` will be the one used for processing @Transactional methods. See `TransactionManagementConfigurer Javadoc` for further details.
> 这种方法可能是可取的，因为它更明确，也可能是必要的，以便区分存在于同一容器中的两个 `TransactionManager` Bean。顾名思义，``annotationDrivenTransactionManager()`` 将用于处理 @Transactional 方法。有关详细信息，请参阅 `TransactionManagementConfigurer Javadoc`。

The mode attribute controls how advice is applied: If the mode is `AdviceMode.PROXY` (the default), then the other attributes control the behavior of the `proxying`. Please note that proxy mode allows for interception of calls through the proxy only; local calls within the same class cannot get intercepted that way.
> mode 属性控制建议的应用方式：如果模式为 `AdviceMode.PROXY`（默认），则其他属性控制代理的行为。 请注意，代理模式仅允许通过代理拦截调用； 同一类中的本地调用不能以这种方式被拦截。

Note that if the mode is set to `AdviceMode.ASPECTJ`, then the value of the `proxyTargetClass` attribute will be ignored. Note also that in this case the spring-aspects module JAR must be present on the `classpath`, with compile-time weaving or load-time weaving applying the aspect to the affected classes. There is no proxy involved in such a scenario; local calls will be intercepted as well.
> 请注意，如果模式设置为 `AdviceMode.ASPECTJ`，那么 `proxyTargetClass` 属性的值将被忽略。还请注意，在这种情况下，`classpath` 上必须有 spring-aspects 模块 JAR，编译时编织或加载时编织会将 aspect 应用到受影响的类。这种情况下不涉及代理；本地调用也将被拦截。

#### `@EnableTransactionManagement`源代码

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TransactionManagementConfigurationSelector.class)
public @interface EnableTransactionManagement {

	/**
	 * 指示是否创建基于子类（CGLIB）的代理（true），而不是基于标准 Java 接口的代理（false）。默认值为 false。
     * 仅当 mode() 设置为 AdviceMode.PROXY 时适用。
     * 
     * 请注意，将此属性设置为 true 将影响所有需要代理的 Spring 管理 Bean，而不仅仅是那些标记了 @Transactional 的 Bean。
     * 例如，使用 Spring 的 @Async 注解标记的其他 Bean 将同时升级为子类代理。
     * 这种方法在实践中不会产生负面影响，除非在测试中明确期望使用一种类型的代理与另一种类型的代理。
	 */
	boolean proxyTargetClass() default false;

	/**
	 * 指出应如何应用`事务建议`。
     * 默认为 AdviceMode.PROXY。请注意，代理模式只允许拦截通过代理进行的调用。
     * 同一类中的本地调用无法通过这种方式拦截；本地调用中的此类方法上的事务注解将被忽略（this.方法()不会生效），因为 Spring 的拦截器甚至不会在这种运行时情况下启动。
     * 如需更高级的拦截模式，可考虑将其切换为 AdviceMode.ASPECTJ。
	 */
	AdviceMode mode() default AdviceMode.PROXY;

	/**
	 * 指示在特定连接点应用多个建议时事务顾问程序的执行顺序。
     * 默认为 Ordered.LOWEST_PRECEDENCE，表示事务建议将在所有其他建议之后执行，这是最安全的设置。
	 */
	int order() default Ordered.LOWEST_PRECEDENCE;

}
```

### TransactionManagementConfigurer
Interface to be implemented by @Configuration classes annotated with `@EnableTransactionManagement` that wish to (or need to) explicitly specify the default `PlatformTransactionManager` bean (or `ReactiveTransactionManager` bean) to be used for annotation-driven transaction management, as opposed to the default approach of a by-type lookup. One reason this might be necessary is if there are two `PlatformTransactionManager` beans (or two `ReactiveTransactionManager` beans) present in the container.
> 一个接口，该接口将由使用了 `@EnableTransactionManagement`注解 的 @Configuration 类实现，这些类希望（或需要）显式地指定用于注释驱动事务管理的默认 `PlatformTransactionManager` Bean（或 `ReactiveTransactionManager` Bean），而不是采用按类型查找的默认方法。如果容器中有两个 `PlatformTransactionManager` Bean（或两个 `ReactiveTransactionManager` Bean），那么就有必要这样做。

See `@EnableTransactionManagement` for general examples and context; see `annotationDrivenTransactionManager()` for detailed instructions.
> 有关一般示例和上下文，请参阅 `@EnableTransactionManagement`；有关详细说明，请参阅 `annotationDrivenTransactionManager()`。

Note that in by-type lookup disambiguation cases, an alternative approach to implementing this interface is to simply mark one of the offending `PlatformTransactionManager` @Bean methods (or `ReactiveTransactionManager` @Bean methods) as @Primary. This is even generally preferred since it doesn't lead to early initialization of the `TransactionManager` bean.
> 请注意，在按类型查找消除歧义的情况下，实现此接口的另一种方法是简单地将一个违规的 `PlatformTransactionManager` @Bean 方法（或 `ReactiveTransactionManager` @Bean 方法）标记为 @Primary 方法。这种方法通常更受欢迎，因为它不会导致 `TransactionManager` Bean 的过早初始化。
> 

#### 源代码
```java
public interface TransactionManagementConfigurer {

	/**
	 * 返回用于注解驱动的数据库事务管理（即处理 @Transactional 方法时）的默认事务管理器 Bean。
     * 实施这种方法有两种基本方法：
     * 
     * 1. 实现方法并用 @Bean 进行注解
     * 在这种情况下，实现 @Configuration 的类会实现该方法，用 @Bean 标记，并直接在方法体中配置和返回事务管理器：
     * @Bean
     * @Override
     * public PlatformTransactionManager annotationDrivenTransactionManager() {
     *     return new DataSourceTransactionManager(dataSource());
     * }
     * 
     * 2. 实现不带 @Bean 的方法，并将其委托给另一个现有的 @Bean 方法
     * @Bean
     * public PlatformTransactionManager txManager() {
     *     return new DataSourceTransactionManager(dataSource());
     * }
     * 
     * @Override
     * public PlatformTransactionManager annotationDrivenTransactionManager() {
     *     return txManager(); // 引用上述现有的 @Bean 方法
     * }
     * 如果采用第 2 种方法，请确保只有一个方法使用 @Bean 标记！
     * 
     * 无论是方案 1 还是方案 2，PlatformTransactionManager 实例都必须作为容器中的 Spring Bean 来管理，
     * 因为大多数 PlatformTransactionManager 实现都会利用 Spring 生命周期回调（如 InitializingBean 和 BeanFactoryAware）。
     * 请注意，同样的指导原则也适用于 ReactiveTransactionManager Bean。
	 */
	TransactionManager annotationDrivenTransactionManager();

```