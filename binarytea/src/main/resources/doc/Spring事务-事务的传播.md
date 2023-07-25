> [带你读懂Spring 事务——事务的传播机制](http://te-amo.site/article/23)

### 模拟场景
现使用伪代码，模拟以下场景:
```text
// 将对象A插入到表A中
insertIntoTableA(a);

// 将对象B插入到表B中
insertIntoTableB(b);


// 方法A调用方法B
public void funcA() {
    insertIntoTableA(a);
    funcB();
}
public void funcB() {
    insertIntoTableB(b1);
    throw Exception;
    insertIntoTableB(b2);
}
```
> 提示：
> 
> Spring中事务的默认实现使用的是AOP，也就是代理的方式，如果大家在使用代码测试时，同一个Service类中的方法相互调用需要使用注入的对象来调用，不要直接使用`this.方法名`来调用，`this.方法名`调用是对象内部方法调用，不会通过Spring代理，事务不会起作用。

### 没有事务的情况
在没有事务的情况下，执行funcA()，此时对象a和对象b1都会被插入到数据库中，而对象b2不会被插入到数据库中。

### 0, REQUIRED(0)
通俗理解就是：当前方法设置事务传播类型为`REQUIRED`，在执行时，如果调用“我”的方法存在事务，则“我”加入这个事务；如果调用“我”的方法没有事务，则“我”新建一个事务。
```text
/**
 * 支持当前事务；如果不存在，则创建一个新事务。类似于 EJB 的同名事务属性。
 * 这通常是事务定义的默认设置，并且通常定义了事务同步范围。
 */
REQUIRED(TransactionDefinition.PROPAGATION_REQUIRED), //REQUIRED(0),
```

#### 0-1, REQUIRED-示例1
在funcA()和funcB()上声明事务，设置传播行为`REQUIRED`，伪代码如下：
```text
@Transactional(propagation = Propagation.REQUIRED)
public void funcA() {
    insertIntoTableA(a);
    funcB();
}
@Transactional(propagation = Propagation.REQUIRED)
public void funcB() {
    insertIntoTableB(b1);
    throw Exception;
    insertIntoTableB(b2);
}
```
执行funcA()，此时不会插入任何新的数据，数据库保持着执行funcA()之前的状态。

在funcA()上声明了事务，根据REQUIRED的传播行为，此时funcB()会加入到funcA()的事务中，funcB()抛出异常会发生事务回滚。
由于两者使用的是同一事务，所以事务回滚后，funcA()和funcB()都不会插入任何新的数据，数据库保持着执行funcA()之前的状态。

#### 0-2, REQUIRED-示例2
仅在funcB()上声明事务，设置传播行为`REQUIRED`，伪代码如下：
```text
public void funcA() {
    insertIntoTableA(a);
    funcB();
}
@Transactional(propagation = Propagation.REQUIRED)
public void funcB() {
    insertIntoTableB(b1);
    throw Exception;
    insertIntoTableB(b2);
}
```
执行funcA()，此时对象a存储到数据库中，对象b1和b2不会存储到数据库中。

在funcA()上没有声明事务，而在funcB()上声明了事务且是`REQUIRED`的传播行为，所以funB()会创建一个新的事务，funcB()抛出异常会发生事务回滚。funA()没有事务不会发生回滚。

### 1, SUPPORTS(1)
通俗理解就是：当前方法设置事务传播类型为`SUPPORTS`，在执行时，如果当前调用“我”的方法存在事务，则“我”加入这个事务；如果当前没有事务，“我”就以非事务方法执行。
```text
/**
 * 支持当前事务，如果不存在则以非事务方式执行。类似于 EJB 的同名事务属性。
 * 注意：对于具有事务同步功能的事务管理器来说，SUPPORTS 与没有事务略有不同，因为它定义了同步功能将适用的事务范围。
 * 因此，相同的资源（JDBC 连接、Hibernate 会话等）将在整个指定范围内共享。请注意，这取决于事务管理器的实际同步配置。
 * 
 * 一般来说，请谨慎使用 PROPAGATION_SUPPORTS！ 特别是，不要依赖 PROPAGATION_SUPPORTS 范围内的 PROPAGATION_REQUIRED 或 
 * PROPAGATION_REQUIRES_NEW （这可能会导致运行时同步冲突）。 
 * 如果这种嵌套是不可避免的，请确保正确配置事务管理器（通常切换到“实际事务同步”）。
 */
SUPPORTS(TransactionDefinition.PROPAGATION_SUPPORTS), //SUPPORTS(1),
```

#### 1-1, SUPPORTS-示例1
仅在funcB()上声明事务，设置传播行为`SUPPORTS`，伪代码如下：
```text
public void funcA() {
    insertIntoTableA(a);
    funcB();
}
@Transactional(propagation = Propagation.SUPPORTS)
public void funcB() {
    insertIntoTableB(b1);
    throw Exception;
    insertIntoTableB(b2);
}
```
执行funcA()，此时对象a和对象b1存储到数据库中，对象b2不会存储到数据库中。

在funcA()上没有声明事务，在funcB上声明事务且传播行为是`SUPPORTS`，所以funcB()会以非事务方式执行，funcB()抛出异常不会发生事务回滚。funcA()没有事务不会发生回滚。

如果funcA()上声明了事务，那么funcB()会加入到funcA()的事务中，funcB()抛出异常会发生事务回滚，它们使用的同一事务，所以事务回滚后，funcA()和funcB()都不会插入任何新的数据。

### 2, MANDATORY(2)
通俗理解就是：当前方法设置事务传播类型为`SUPPORTS`，在执行时，如果当前调用“我”的方法存在事务，则“我”加入这个事务；如果当前调用“我”的方法不存在事务，则“我”抛出异常。
```text
/**
 * 支持当前事务；如果不存在当前事务，则抛出异常。类似于 EJB 的同名事务属性。
 * 
 * 请注意，PROPAGATION_MANDATORY 作用域中的事务同步总是由周围的事务驱动。
 */
MANDATORY(TransactionDefinition.PROPAGATION_MANDATORY), //MANDATORY(2),
```

#### 2-1, MANDATORY-示例1
仅在funcB()上声明事务，设置传播行为`MANDATORY`，伪代码如下：
```text
public void funcA() {
    insertIntoTableA(a);
    funcB();
}
@Transactional(propagation = Propagation.MANDATORY)
public void funcB() {
    insertIntoTableB(b1);
    throw Exception;
    insertIntoTableB(b2);
}
```
执行funcA()，此时对象a存入到数据库中，对象b1和b2不会存储到数据库中（没有存储成功的原因并不是事务回滚，而是根本没有执行任何插入方法）。

在funcA()上没有声明事务，在funcB上声明事务且传播行为是`MANDATORY`，所以执行funcB()会直接抛出异常，此时funcB()中的方法根本没有执行。

如果在funcA()上声明了事务，那么funcB()会加入到funcA()的事务中，funcB()抛出异常会发生事务回滚，它们使用的同一事务，所以事务回滚后，funcA()和funcB()都不会插入任何新的数据。

### 3, REQUIRES_NEW(3)
通俗理解就是：当前方法设置事务传播类型为`REQUIRES_NEW`，在执行时，不论当前调用“我”的方法是否存在事务，“我”总是会新建一个事务。如果调用“我”的方法存在一个事务则将其挂起。
```text
/**
 * 创建一个新事务，暂停当前事务（如果存在）。类似于 EJB 的同名事务属性。
 * 注意：实际事务暂停不会在所有事务管理器上立即生效。这尤其适用于 org.springframework.transaction.jta.JtaTransactionManager，
 * 它要求 javax.transaction.TransactionManager 对其可用（这在标准 Java EE 中是服务器特定的）。
 * 
 * PROPAGATION_REQUIRES_NEW 作用域总是定义它自己的事务同步。现有的同步将被适当地暂停和恢复。
 */
REQUIRES_NEW(TransactionDefinition.PROPAGATION_REQUIRES_NEW), //REQUIRES_NEW(3),
```

#### 3-1, REQUIRES_NEW-示例1
在funcA()声明事务且传播行为是`REQUIRES`，在funcB上声明事务，设置传播行为`REQUIRES_NEW`，伪代码如下：
```text
@Transactional(propagation = Propagation.REQUIRES)
public void funcA() {
    insertIntoTableA(a);
    funcB();
    throw Exception;
}
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void funcB() {
    insertIntoTableB(b1);
    insertIntoTableB(b2);
}
```
执行funcA()，此时对象a没有存储到数据库，对象b1和b2存储到数据库中。

在funcA()上声明了事务，由于抛出异常后事务回滚，对象a不会存储到数据库中。
funcB()上声明的事务传播行为是`REQUIRES_NEW`，此时会新建一个事务（和funcA上的事务不是同一个），所以对象b1和b2存储到数据库中。

如果funcB()上的事务传播行为是`REQUIRES`，则此时funcB()和funcA()上的事务是同一个，发生异常导致这个事务回滚，对象a、b1和b2都不会存储到数据库中。

### 4, NOT_SUPPORTED(4)
通俗理解就是：当前方法设置事务传播类型为`NOT_SUPPORTED`，在执行时，不论当前调用“我”的方法是否存在事务，“我”都会以非事务的方式运行。
```text
/**
 * 不支持当前事务；而是始终以非事务方式执行。类似于 EJB 的同名事务属性。
 * 注意：实际事务暂停不会在所有事务管理器上立即生效。这尤其适用于 org.springframework.transaction.jta.JtaTransactionManager，
 * 它要求 javax.transaction.TransactionManager 对其可用（这在标准 Java EE 中是服务器特定的）。
 * 
 * 请注意，事务同步在 PROPAGATION_NOT_SUPPORTED 作用域中不可用。现有的同步将被适当地暂停和恢复。
 */
NOT_SUPPORTED(TransactionDefinition.PROPAGATION_NOT_SUPPORTED), //NOT_SUPPORTED(4),
```

#### 4-1, NOT_SUPPORTED-示例1
在funcA()声明事务且传播行为是`REQUIRES`，在funcB上声明事务，设置传播行为`REQUIRES_NEW`，伪代码如下：
```text
@Transactional(propagation = Propagation.REQUIRES)
public void funcA() {
    insertIntoTableA(a);
    funcB();
}
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public void funcB() {
    insertIntoTableB(b1);
    throw Exception;
    insertIntoTableB(b2);
}
```
执行funcA()，此时对象a和对象b2没有存储到数据库中，对象b1存储到数据库中。

在funcA()上声明一个事务，funcB()上没有事务，发生异常时，funcB()不会发生事务回滚，所以b1存储到数据库中，而b2没有存储到数据库中。
当funcA()检测到funcB()抛出的异常时，发生事务回滚，所以对象a没有存储到数据库中。

### 5, NEVER(5)
通俗理解就是：当前方法设置事务传播类型为`NEVER`，在执行时，“我”不使用事务，且调用“我”的方法也不允许存在事务，否则“我”抛出异常。
```text
/**
 * 不支持当前事务；如果存在当前事务，则抛出异常。类似于 EJB 的同名事务属性。
 * 请注意，事务同步在 PROPAGATION_NEVER 作用域中不可用。
 */
NEVER(TransactionDefinition.PROPAGATION_NEVER), //NEVER(5),
```

#### 5-1, NEVER-示例1
在funcA()声明事务且传播行为是`REQUIRES`，在funcB上声明事务，设置传播行为`NEVER`，伪代码如下：
```text
@Transactional(propagation = Propagation.REQUIRES)
public void funcA() {
    insertIntoTableA(a);
    funcB();
}
@Transactional(propagation = Propagation.NEVER)
public void funcB() {
    insertIntoTableB(b1);
    insertIntoTableB(b2);
}
```
执行funcA()，此时对象a、b1和b2都不会存储到数据库中。

funcB()设置的事务传播行为是`NEVER`，由于funcA()存在事务，此时funcB()直接抛出异常，所以对象b1和b2不会存储到数据库中。
当funcA()检测到funcB()抛出的异常时，发生事务回滚，所以对象a没有存储到数据库中。

### 6, NESTED(6)
通俗理解就是：当前方法设置事务传播类型为`NESTED`，如果调用“我”的方法存在事务，那么“我”会在这个事务中新建一个子事务，否则“我”会新建一个事务。
父事务发生回滚，子事务也会发生回滚；子事务抛出异常发生回滚，父事务可以捕获这个异常，不发生回滚。
> 1, 与 `REQUIRES_NEW` 的区别
>   * 调用“我”的方法没有事务：此时`REQUIRES_NEW`和`NESTED`的效果是一样的，都会新建一个事务。
>   * 调用“我”的方法有事务：此时`REQUIRES_NEW`会新建一个事务，而`NESTED`会在这个事务中新建一个子事务。
>   在`NESTED`情况下，父事务发生回滚，子事务也会发生回滚；子事务抛出异常发生回滚，父事务可以捕获这个异常，不发生回滚。
> 在`REQUIRES_NEW`情况下，原有事务发生回滚，新建事务不会受到影响；新建事务抛出异常发生回滚，原有事务捕获这个异常，不会受到影响。
> 
> 2, 与 `REQUIRED` 的区别
>   * 调用“我”的方法没有事务：此时`REQUIRED`和`NESTED`的效果是一样的，都会新建一个事务。
>   * 调用“我”的方法有事务：此时`REQUIRED`会使用这个事务，而`NESTED`会在这个事务中新建一个子事务。
>   在`NESTED`情况下，父事务发生回滚，子事务也会发生回滚；子事务抛出异常发生回滚，父事务可以捕获这个异常，不发生回滚。
> 在`REQUIRED`情况下，由于共用一个事务，无论哪个方法抛出异常发生回滚，这个事务都会发生回滚。（“我”抛出异常后，即使在调用我的方法中捕获这个异常，这个事务还是会发生回滚）
```text
/**
 * 如果存在当前事务，则在嵌套事务中执行，否则行为类似于 PROPAGATION_REQUIRED。EJB 中没有类似的功能。
 * 注意：嵌套事务的实际创建只适用于特定的事务管理器。在 JDBC 3.0 驱动程序上运行时，这仅适用于 JDBC org.springframework.jdbc.datasource.DataSourceTransactionManager。某些 JTA 提供商可能也支持嵌套事务。
 */
NESTED(TransactionDefinition.PROPAGATION_NESTED); //NESTED(6);
```

#### 6-1, NESTED-示例1
在funcA()声明事务且传播行为是`REQUIRES`，在funcB上声明事务，设置传播行为`NESTED`，异常发生在funcA()中。伪代码如下：
```text
@Transactional(propagation = Propagation.REQUIRES)
public void funcA() {
    insertIntoTableA(a);
    throw Exception;
}
@Transactional(propagation = Propagation.NESTED)
public void funcB() {
    insertIntoTableB(b1);
    insertIntoTableB(b2);
}
```
执行funcA()，此时对象a、b1和b2都不会存储到数据库中。

在funcA()上声明了事务，由于抛出异常后事务回滚，对象a不会存储到数据库中。
此时在funcB()上声明的事务传播行为是`NESTED`，所以funcB()会在funcA()的事务中新建一个子事务。
父事务发生回滚后，子事务也跟着发生回滚，所以对象b1和b2不会存储到数据库中。

#### 6-2, NESTED-示例2
在funcA()声明事务且传播行为是`REQUIRES`，在funcB上声明事务，设置传播行为`NESTED`，异常发生在funcB()中。伪代码如下：
```text
@Transactional(propagation = Propagation.REQUIRES)
public void funcA() {
    insertIntoTableA(a1);
    try {
        funcB();
    }catch (Exception e) {
        System.out.println("funcB()抛出异常");
    }
    insertIntoTableA(a2);
}
@Transactional(propagation = Propagation.NESTED)
public void funcB() {
    insertIntoTableB(b1);
    throw Exception;
    insertIntoTableB(b2);
}
```
执行funcA()，此时对象a1和对象a2存储到数据库中，对象b1和对象b2不会存储到数据库中。

在funcA()上声明了事务，此时在funcB()上声明的事务传播行为是`NESTED`，所以funcB()会在funcA()的事务中新建一个子事务。
funcB()抛出异常后，子事务发生回滚，对象b1和b2不会存储到数据库。
在funcA()中，父事务捕获了这个异常，不发生回滚，所以对象a1和a2存储到数据库中。

如果funcB()上的事务传播行为是`REQUIRES`，此时funcB()和funcA()上的事务是同一个，发生异常导致这个事务回滚，即使在funcA()中捕获了异常，事务照样发生回滚，对象a1、a2、b1和b2都不会存储到数据库中。
