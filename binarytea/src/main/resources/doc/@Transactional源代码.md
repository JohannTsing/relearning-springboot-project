### @Transactional
Describes a transaction attribute on an individual method or on a class.
> 描述单个方法或类的事务属性。

When this annotation is declared at the class level, it applies as a default to all methods of the declaring class and its subclasses. Note that it does not apply to ancestor classes up the class hierarchy; inherited methods need to be locally redeclared in order to participate in a subclass-level annotation. For details on method visibility constraints, consult the Transaction Management  section of the reference manual.
> 当在类级别声明此注解时，它将默认应用于声明类及其子类的所有方法。 请注意，它不适用于类层次结构中的祖先类； 继承的方法需要在本地重新声明才能参与子类级别的注解。 有关方法可见性约束的详细信息，请参阅[参考手册的事务管理部分](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction)。

This annotation is generally directly comparable to Spring's org.springframework.transaction.interceptor.RuleBasedTransactionAttribute class, and in fact AnnotationTransactionAttributeSource will directly convert this annotation's attributes to properties in RuleBasedTransactionAttribute, so that Spring's transaction support code does not have to know about annotations.
> 该注解一般可直接与 Spring 的 `org.springframework.transaction.interceptor.RuleBasedTransactionAttribute` 类相媲美，事实上，`AnnotationTransactionAttributeSource` 会直接将该注解的属性转换为 `RuleBasedTransactionAttribute` 中的属性，因此 Spring 的事务支持代码无需了解注解。

##### Attribute Semantics属性语义
If no custom rollback rules are configured in this annotation, the transaction will roll back on RuntimeException and Error but not on checked exceptions.
> 如果未在此注解中配置自定义回滚规则，事务将在出现 `RuntimeException` 和 `Error` 时回滚，但不会在出现`已检查异常`时回滚。

Rollback rules determine if a transaction should be rolled back when a given exception is thrown, and the rules are based on patterns. A pattern can be a fully qualified class name or a substring of a fully qualified class name for an exception type (which must be a subclass of Throwable), with no wildcard support at present. For example, a value of "javax.servlet.ServletException" or "ServletException" will match javax.servlet.ServletException and its subclasses.
> 回滚规则决定了当某个异常抛出时，事务是否应该回滚，该规则基于模式。模式可以是异常类型（必须是 Throwable 的子类）的全限定类名或全限定类名的子串，目前不支持通配符。例如，"`javax.servlet.ServletException` "或 "`ServletException` "的值将匹配 `javax.servlet.ServletException` 及其子类。

Rollback rules may be configured via rollbackFor/noRollbackFor and rollbackForClassName/noRollbackForClassName, which allow patterns to be specified as Class references or strings, respectively. When an exception type is specified as a class reference its fully qualified name will be used as the pattern. Consequently, @Transactional(rollbackFor = example.CustomException.class) is equivalent to @Transactional(rollbackForClassName = "example.CustomException").
> 可通过 `rollbackFor`/`noRollbackFor` 和 `rollbackForClassName`/`noRollbackForClassName` 配置回滚规则，这两种规则允许将模式分别指定为类引用或字符串。当异常类型被指定为类引用时，其全称将被用作模式。因此，`@Transactional(rollbackFor = example.CustomException.class)` 等同于 `@Transactional(rollbackForClassName = "example.CustomException")`。

WARNING: You must carefully consider how specific the pattern is and whether to include package information (which isn't mandatory). For example, "Exception" will match nearly anything and will probably hide other rules. "java.lang.Exception" would be correct if "Exception" were meant to define a rule for all checked exceptions. With more unique exception names such as "BaseBusinessException" there is likely no need to use the fully qualified class name for the exception pattern. Furthermore, rollback rules may result in unintentional matches for similarly named exceptions and nested classes. This is due to the fact that a thrown exception is considered to be a match for a given rollback rule if the name of thrown exception contains the exception pattern configured for the rollback rule. For example, given a rule configured to match on com.example.CustomException, that rule would match against an exception named com.example.CustomExceptionV2 (an exception in the same package as CustomException but with an additional suffix) or an exception named com.example.CustomException$AnotherException (an exception declared as a nested class in CustomException).
> 警告：您必须仔细考虑模式的具体程度，以及是否要包含软件包信息（这不是必须的）。例如，"Exception "几乎可以匹配任何内容，而且很可能会隐藏其他规则。如果 "Exception "的目的是为所有检查过的异常定义规则，那么 "java.lang.Exception "就是正确的。如果异常名称比较独特，如 "BaseBusinessException"，则可能不需要使用异常模式的完全限定类名。此外，回滚规则可能会导致无意中匹配名称相似的异常和嵌套类。这是因为，如果抛出的异常名称包含为回滚规则配置的异常模式，则抛出的异常会被视为与给定的回滚规则相匹配。例如，如果给定的规则配置为匹配 com.example.CustomException，则该规则将匹配名为 com.example.CustomExceptionV2（与 CustomException 位于同一软件包中但带有附加后缀的异常）的异常或名为 com.example.CustomException$AnotherException（作为 CustomException 中嵌套类声明的异常）的异常。

For specific information about the semantics of other attributes in this annotation, consult the TransactionDefinition and org.springframework.transaction.interceptor.TransactionAttribute javadocs.
> 有关此注解中其他属性语义的具体信息，请查阅 `TransactionDefinition` 和 `org.springframework.transaction.interceptor.TransactionAttribute` javadocs。

##### Transaction Management事务管理
This annotation commonly works with thread-bound transactions managed by a org.springframework.transaction.PlatformTransactionManager, exposing a transaction to all data access operations within the current execution thread. Note: This does NOT propagate to newly started threads within the method.
> 此注解通常适用于由 org.springframework.transaction.PlatformTransactionManager 管理的线程绑定事务，将事务暴露给当前执行线程中的所有数据访问操作。注意：这不会传播给方法中新启动的线程。

Alternatively, this annotation may demarcate a reactive transaction managed by a org.springframework.transaction.ReactiveTransactionManager which uses the Reactor context instead of thread-local variables. As a consequence, all participating data access operations need to execute within the same Reactor context in the same reactive pipeline.
> 另外，该注解还可以划分一个由 org.springframework.transaction.ReactiveTransactionManager 管理的反应式事务，该事务使用 Reactor 上下文而非线程本地变量。因此，所有参与的数据访问操作都需要在同一反应式管道中的同一反应式上下文中执行。

#### 源代码
```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {

	/**
	 * Alias for {@link #transactionManager}.
	 * @see #transactionManager
	 */
	@AliasFor("transactionManager")
	String value() default "";

	/**
	 * 指定事务的限定符值。
     * 可用于确定目标事务管理器，与特定 TransactionManager Bean 定义的限定符值（或 bean 名称）相匹配。
	 */
	@AliasFor("value")
	String transactionManager() default "";

	/**
	 * 定义 0 个或多个事务标签。
     * 标签可用于描述事务，并由各事务管理器进行评估。标签可以仅用于描述目的，也可以映射到预定义的事务管理器特定选项。
	 */
	String[] label() default {};

	/**
	 * 事务传播类型。
     * 默认为 Propagation.REQUIRED。
	 */
	Propagation propagation() default Propagation.REQUIRED;

	/**
	 * 事务隔离级别。
     * 默认为 Isolation.DEFAULT。
     * 由于只适用于新启动的事务，因此专门设计用于 Propagation.REQUIRED 或 Propagation.REQUIRES_NEW。
     * 如果希望隔离级别声明在参与具有不同隔离级别的现有事务时被拒绝，请考虑将事务管理器上的 "validateExistingTransactions "标记切换为 "true"。
	 */
	Isolation isolation() default Isolation.DEFAULT;

	/**
	 * 该事务的超时（秒）。
     * 默认为底层事务系统的默认超时。
     * 专为与 Propagation.REQUIRED 或 Propagation.REQUIRES_NEW 一起使用而设计，因为它只适用于新启动的事务。
	 */
	int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;

	/**
	 * 该事务的超时（秒）。
     * 默认为底层事务系统的默认超时。
     * 专为与 Propagation.REQUIRED 或 Propagation.REQUIRES_NEW 一起使用而设计，因为它只适用于新启动的事务。
	 */
	String timeoutString() default "";

	/**
	 * 如果事务实际上是只读的，则可以将其设置为 true 的布尔标志，从而允许在运行时进行相应的优化。
     * 默认为 false。
     * 这只是对实际事务子系统的一个提示，它不一定会导致写访问尝试失败。当请求只读事务时，无法解释只读提示的事务管理器不会抛出异常，而是默默地忽略该提示。
	 */
	boolean readOnly() default false;

	/**
	 * 定义零 (0) 个或多个异常类，这些异常类必须是 Throwable 的子类，指示哪些异常类型必须导致事务回滚。
     * 
     * 默认情况下，事务将在 RuntimeException 和 Error 时回滚，但在已检查的异常（业务异常）时不会回滚。 
     * 有关详细说明，请参阅 org.springframework.transaction.interceptor.DefaultTransactionAttribute.rollbackOn(Throwable) 。
     * 
     * 这是构造回滚规则的首选方法（与 rollbackForClassName 相比），匹配异常类型、其子类及其嵌套类。 
     * 有关回滚规则语义和有关可能的无意匹配的警告的更多详细信息，请参阅类级 javadoc。
	 */
	Class<? extends Throwable>[] rollbackFor() default {};

	/**
	 * 定义 0 个或多个异常名称模式（针对必须是 Throwable 子类的异常），指明哪些异常类型必须导致事务回滚。
	 */
	String[] rollbackForClassName() default {};

	/**
	 * 定义零 (0) 个或多个异常类，这些异常类必须是 Throwable 的子类，指示哪些异常类型不得导致事务回滚。
     * 
     * 这是构造回滚规则的首选方法（与 noRollbackForClassName 相比），匹配异常类型、其子类及其嵌套类。 
     * 有关回滚规则语义和有关可能的无意匹配的警告的更多详细信息，请参阅类级 javadoc。
	 */
	Class<? extends Throwable>[] noRollbackFor() default {};

	/**
	 * 定义零（0）个或多个异常名称模式（针对必须是 Throwable 子类的异常），指明哪些异常类型不得导致事务回滚。
	 */
	String[] noRollbackForClassName() default {};

}
```