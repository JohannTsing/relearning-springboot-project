### @OneToOne
Specifies a single-valued association to another entity that has one-to-one multiplicity. It is not normally necessary to specify the associated target entity explicitly since it can usually be inferred from the type of the object being referenced. 

If the relationship is bidirectional, the non-owning side must use the `mappedBy` element of the `OneToOne` annotation to specify the relationship field or property of the owning side.

> 指定与具有"一对一"多重性的另一个实体的"单值关联"。通常不需要明确指定关联的目标实体，因为它通常可以从被引用对象的类型中推断出来。
>
> 如果关系是双向的，非拥有方（没有持有外键的一方）必须使用 `OneToOne` 注解的 `mappedBy` 元素来指定拥有方的关系字段或属性。

The `OneToOne` annotation may be used within an embeddable class to specify a relationship from the embeddable class to an entity class.

If the relationship is bidirectional and the entity containing the embeddable class is on the owning side of the relationship, the non-owning side must use the `mappedBy` element of the `OneToOne` annotation to specify the relationship field or property of the embeddable class. 

The dot (".") notation syntax must be used in the `mappedBy` element to indicate the relationship attribute within the embedded attribute. The value of each identifier used with the dot notation is the name of the respective embedded field or property.

> `OneToOne` 注解可以在可嵌入类中使用，以指定从可嵌入类到实体类的关系。
>
> 如果关系是双向的，且包含可嵌入类的实体处于关系的拥有方（持有外键的一方），则非拥有方必须使用 `OneToOne` 注解的 `mappedBy` 元素来指定可嵌入类的关系字段或属性。
>
> 在`mappedBy` 元素中必须使用点（“.”）符号语法来表示嵌入属性中的关系属性。与点符号一起使用的每个标识符的值是相应嵌入字段或属性的名称。



1. *Example 1: One-to-one association that maps a foreign key column.* 
映射外键列的一对一关联

```java
/**
 * Customer和CustomerRecord，建立了一对一双向关联，Customer类负责维护关联关系（持有外键）,CustomerRecord通过mappedBy指定拥有方的关系字段或属性（Customer【关系拥有方】类中的customerRecord属性。）
 * 外键列`CUSTREC_ID`在Customer表中,该列的值引用了CustomerRecord表的主键,建立两表之间的一对一关系。
 */

// On Customer class:
@OneToOne(optional=false)
@JoinColumn(
    name="CUSTREC_ID", unique=true, nullable=false, updatable=false)
public CustomerRecord getCustomerRecord() {
    return customerRecord; 
}

// On CustomerRecord class:
// mappedBy="customerRecord"表示关联关系由Customer类中的customerRecord属性维护,该属性引用了CustomerRecord。
@OneToOne(optional=false, mappedBy="customerRecord")
public Customer getCustomer() {
    return customer; 
}
```

2. *Example 2: One-to-one association that assumes both the source and target share the same primary key values.* 
一对一关联，假定源和目标共享相同的主键值。

```java
/**
 * @MapsId 实现了一对一关联时双方共享同一主键,Employee和EmployeeInfo的id字段值将完全一致,实现一对一主键关联映射。
 */

// On Employee class:
@Entity
public class Employee {
    @Id 
    Integer id;
    
    @OneToOne 
    @MapsId
    EmployeeInfo info;
    //...
}

// On EmployeeInfo class:
@Entity
public class EmployeeInfo {
    @Id 
    Integer id;
    //...
}
```

3. *Example 3: One-to-one association from an embeddable class to another entity.*
从一个可嵌入类到另一个实体的一对一关联。

```java
@Entity
public class Employee {
    @Id 
    int id;
    
    // @Embedded 注解表示这是一个嵌入类，而不是一个单独的表。
    @Embedded 
    LocationDetails location;
    //...
}

@Embeddable
public class LocationDetails {
    int officeNumber;
    
    // 在可嵌入类中使用`OneToOne`注解，以指定从可嵌入类到实体类的关系。
    @OneToOne 
    ParkingSpot parkingSpot;
    //...
}

@Entity
public class ParkingSpot {
    @Id int id;
    String garage;
    
    // 在`mappedBy` 元素中必须使用点（“.”）符号语法来表示嵌入属性中的关系属性。与点符号一起使用的每个标识符的值是相应嵌入字段或属性的名称。
    @OneToOne(mappedBy="location.parkingSpot") 
    Employee assignedTo;
    //... 
} 
```

#### `OneToOne`源代码
```java
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface OneToOne {

    /** 
     * (可选）关联目标的实体类。
     * 默认为存储关联的字段或属性的类型。 
     */
    Class targetEntity() default void.class;

    /**
     * (可选）必须级联到关联目标的操作。
     * 默认情况下不会级联任何操作。
     */
    CascadeType[] cascade() default {};

    /** 
     * （可选）关联是否应延迟加载或必须急切获取。
     * EAGER 策略是对持久化提供程序运行时的要求，必须急切地获取关联实体。【默认】
     * LAZY 策略是对持久化提供程序运行时的提示。
     */
    FetchType fetch() default EAGER;

    /** 
     * (可选）关联是否可选。如果设置为 false，则非空关系必须始终存在。
     */
    boolean optional() default true;

    /** 
     * (可选）拥有该关系的字段。该元素仅在关联的反向（非拥有，不持有外键）侧指定。
     */
    String mappedBy() default "";

    /**
     * (可选）是否将移除操作应用于已从关系中移除的实体，并将移除操作级联到这些实体。
     * @since 2.0
     */
    boolean orphanRemoval() default false;
}
```

### @OneToMany
Specifies a many-valued association with one-to-many multiplicity.
> 指定具有"一对多"多重性的"多值关联"。

If the collection is defined using generics to specify the element type, the associated target entity type need not be specified; otherwise the target entity class must be specified. 

If the relationship is bidirectional, the `mappedBy` element must be used to specify the relationship field or property of the entity that is the owner of the relationship.
> 如果集合是使用泛型来指定元素类型，则无需指定相关的目标实体类型；否则必须指定目标实体类。
> 
> 如果关系是双向的，则必须使用`mappedBy`元素来指定关系所有者的实体的关系字段或属性。

The `OneToMany` annotation may be used within an embeddable class contained within an entity class to specify a relationship to a collection of entities. 

If the relationship is bidirectional, the `mappedBy` element must be used to specify the relationship field or property of the entity that is the owner of the relationship. 

When the collection is a `java.util.Map`, the `cascade` element and the `orphanRemoval` element apply to the map value.
> `OneToMany` 注解可用于包含在实体类中的可嵌入类，以指定与实体集合的关系。
>
> 如果该关系是双向的，则必须使用 `mappedBy` 元素来指定该关系的所有者实体的关系字段或属性。
>
> 当集合是 `java.util.Map` 时，`cascade` 元素和 `orphanRemoval` 元素适用于映射值。



1. *Example 1: One-to-Many association using generics*
使用泛型实现 "一对多 "关联

```java
/**
 * Customer和Order，建立了一对多双向关联，Order类负责维护关联关系（持有外键）,Customer通过mappedBy指定关联关系被Order类中的customer属性维护。
 * 
 * cascade=ALL这个设置表示,在Order这一端进行的一些操作会自动级联到Customer这一端。
 * 比如:
 *   1, 保存Order,如果Customer还没保存的话,会同时保存关联的Customer对象。
 *   2, 删除Order,如果这个Customer只与要删除的Order相关联,没有其他关联,会同时删除关联的Customer对象。
 *   3, 更新Order,会同时更新关联的Customer对象中的相关数据。
 * 
 * 简单来说,包含cascade=ALL的一方是关系的维护端,这一端进行的持久化操作会自动传播到另一端,从而保证两端的数据一致性。
 * 如果不设置cascade,就需要在两端都进行操作才能维护一致性,这样代码会非常繁琐。
 * 比如保存Order对象的时候,还要额外保存Customer对象;删除Order不会删除Customer,需要额外删除Customer等等。
 */

// In Customer class:
// cascade=ALL表示Order的操作会被级联到Customer。mappedBy="customer"表示维护关系的属性是Order中的customer属性。
@OneToMany(cascade=ALL, mappedBy="customer")
public Set<Order> getOrders() { 
   return orders; 
}

// In Order class:
@ManyToOne
// Order表中有一个外键列CUST_ID指向Customer表的主键
@JoinColumn(name="CUST_ID", nullable=false)
public Customer getCustomer() { 
   return customer; 
}
```

2. *Example 2: One-to-Many association without using generics*
不使用泛型的一对多关联

```java
// In Customer class:
@OneToMany(targetEntity=com.acme.Order.class, cascade=ALL,
        mappedBy="customer")
public Set getOrders() { 
   return orders; 
}

// In Order class:
@ManyToOne
@JoinColumn(name="CUST_ID", nullable=false)
public Customer getCustomer() { 
   return customer; 
}
```

3. *Example 3: Unidirectional One-to-Many association using a foreign key mapping*
使用外键映射的单向 "一对多 "关联

```java
// In Customer class:
@OneToMany(orphanRemoval=true)
@JoinColumn(name="CUST_ID") // join column is in table for Order
public Set<Order> getOrders() {
   return orders;
}
```

#### `OneToMany`源代码
```java
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface OneToMany {

    /**
     * (可选）作为关联目标的实体类。只有在使用 Java 泛型定义集合属性时才可选。否则必须指定。
     * 使用泛型定义时，默认为集合的参数化类型。
     */
    Class targetEntity() default void.class;

    /** 
     * (可选）必须级联到关联目标的操作。
     * 默认为不级联任何操作。
     * 当目标集合是 java.util.Map 时，级联元素适用于 map 值。
     */
    CascadeType[] cascade() default {};

    /** 
     * (可选）关联是否应延迟加载或必须急切获取。
     * EAGER 策略是对持久化提供程序运行时的要求，即必须急于获取关联实体。
     * LAZY 策略是对持久化提供程序运行时的提示。【默认】
     */
    FetchType fetch() default LAZY;

    /** 
     * 拥有该关系的字段。必须填写，除非关系是单向的。
     */
    String mappedBy() default "";

    /**
     * (可选）是否将移除操作应用于已从关系中移除的实体，并将移除操作级联到这些实体。
     * @since 2.0
     */
    boolean orphanRemoval() default false;
}
```

### @ManyToOne
Specifies a single-valued association to another entity class that has many-to-one multiplicity. It is not normally necessary to specify the target entity explicitly since it can usually be inferred from the type of the object being referenced. 

If the relationship is bidirectional, the non-owning `OneToMany` entity side must used the `mappedBy` element to specify the relationship field or property of the entity that is the owner of the relationship.
> 指定与另一个具有"多对一"多重性的实体类的"单值关联"。 通常不需要显式指定目标实体，因为它通常可以从所引用的对象的类型推断出来。
> 
> 如果该关系是双向的，则非拥有 `OneToMany` 实体的一方必须使用 `mappedBy` 元素来指定该关系所有者实体的关系字段或属性。

The `ManyToOne` annotation may be used within an embeddable class to specify a relationship from the embeddable class to an entity class. 

If the relationship is bidirectional, the non-owning `OneToMany` entity side must use the `mappedBy` element of the `OneToMany` annotation to specify the relationship field or property of the embeddable field or property on the owning side of the relationship. 

The dot (".") notation syntax must be used in the `mappedBy` element to indicate the relationship attribute within the embedded attribute. The value of each identifier used with the dot notation is the name of the respective embedded field or property.
> `ManyToOne` 注解可在可嵌入类中使用，以指定从可嵌入类到实体类的关系。
> 
> 如果关系是双向的，则非拥有 `OneToMany` 实体的一方必须使用 `OneToMany` 注解的 `mappedBy` 元素来指定关系拥有方的可嵌入字段或属性的【关系字段或属性】。
> 
> 在`mappedBy` 元素中必须使用点（“.”）符号语法来表示嵌入属性中的关系属性。与点符号一起使用的每个标识符的值是相应嵌入字段或属性的名称。



1. Example 1:

```java
@ManyToOne(optional=false) 
@JoinColumn(name="CUST_ID", nullable=false, updatable=false)
public Customer getCustomer() { 
   return customer; 
}
```

2. Example 2:

```java
@Entity
public class Employee {
   @Id 
   int id;
   
   @Embedded 
   JobInfo jobInfo;
   //...
}

@Embeddable
public class JobInfo {
   String jobDescription; 
   
   @ManyToOne 
   ProgramManager pm; // Bidirectional
}

@Entity
public class ProgramManager {
   @Id 
   int id;
   
   @OneToMany(mappedBy="jobInfo.pm")
   Collection<Employee> manages;
}
```

#### `ManyToOne`源代码
```java
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface ManyToOne {

    /** 
     * (可选）关联目标的实体类。
     * 默认为存储关联的字段或属性的类型。
     */
    Class targetEntity() default void.class;

    /**
     * (可选）必须级联到关联目标的操作。
     * 默认情况下不会级联任何操作。
     */
    CascadeType[] cascade() default {};

    /** 
     * (可选）关联实体是否应延迟加载或必须急切获取。
     * EAGER 策略是对持久化提供程序运行时的要求，即必须急于获取关联实体。【默认】
     * LAZY 策略是对持久化提供程序运行时的提示。
     */
    FetchType fetch() default EAGER;

    /** 
     * (可选）关联是否可选。如果设置为 false，则非空关系必须始终存在。
     */
    boolean optional() default true;
}
```
### @ManyToMany
Specifies a many-valued association with many-to-many multiplicity.
> 指定具有"多对多"多重性的"多值关联"。

Every many-to-many association has two sides, the owning side and the non-owning, or inverse, side. 

The join table is specified on the owning side. If the association is bidirectional, either side may be designated as the owning side. 

If the relationship is bidirectional, the non-owning side must use the `mappedBy` element of the `ManyToMany` annotation to specify the relationship field or property of the owning side.
> 每个多对多关联都有两个方面，拥有方和非拥有方或反向方。 
> 
> 连接表在拥有方指定。 如果关联是双向的，则任一方都可以指定为拥有方。 
> 
> 如果关系是双向的，则非拥有方必须使用 `ManyToMany` 注解的 `mappedBy` 元素来指定拥有方的关系字段或属性。

The join table for the relationship, if not defaulted, is specified on the owning side.
> 如果没有默认设置，则在拥有方指定关系的连接表。

The `ManyToMany` annotation may be used within an embeddable class contained within an entity class to specify a relationship to a collection of entities. 

If the relationship is bidirectional and the entity containing the embeddable class is the owner of the relationship, the non-owning side must use the `mappedBy` element of the `ManyToMany` annotation to specify the relationship field or property of the embeddable class. 

The dot (".") notation syntax must be used in the `mappedBy` element to indicate the relationship attribute within the embedded attribute. The value of each identifier used with the dot notation is the name of the respective embedded field or property.
> `ManyToMany` 注解可用于实体类中包含的可嵌入类，以指定与实体集合的关系。
> 
> 如果该关系是双向的，且包含可嵌入类的实体是该关系的所有者，则非拥有方一方必须使用 `ManyToMany` 注解的 `mappedBy` 元素来指定可嵌入类的关系字段或属性。
> 
> 在`mappedBy `元素中必须使用点（“.”）符号语法来表示嵌入属性中的关系属性。与点符号一起使用的每个标识符的值是相应嵌入字段或属性的名称。



1. Example 1:

```java
// In Customer class:
@ManyToMany
@JoinTable(name="CUST_PHONES")
public Set<PhoneNumber> getPhones() { 
   return phones; 
}

// In PhoneNumber class:
@ManyToMany(mappedBy="phones")
public Set<Customer> getCustomers() { 
   return customers; 
}
```

2. Example 2:

```java
// In Customer class:
@ManyToMany(targetEntity=com.acme.PhoneNumber.class)
public Set getPhones() { 
   return phones; 
}

// In PhoneNumber class:
@ManyToMany(targetEntity=com.acme.Customer.class, mappedBy="phones")
public Set getCustomers() { 
   return customers; 
}
```

3. Example 3:

```java
// In Customer class:
@ManyToMany
@JoinTable(name="CUST_PHONE",
    joinColumns=
        @JoinColumn(name="CUST_ID", referencedColumnName="ID"),
    inverseJoinColumns=
        @JoinColumn(name="PHONE_ID", referencedColumnName="ID")
)
public Set<PhoneNumber> getPhones() { 
   return phones; 
}

// In PhoneNumber class:
@ManyToMany(mappedBy="phones")
public Set<Customer> getCustomers() { 
   return customers; 
}
```

#### `ManyToMany`源代码
```java
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface ManyToMany {

    /**
     * (可选）作为关联目标的实体类。只有在使用 Java 泛型定义集合值关系属性时才可选。否则必须指定。
     * 使用泛型定义时，默认为集合的参数化类型。
     */
    Class targetEntity() default void.class;

    /** 
     * (可选）必须级联到关联目标的操作。
     * 当目标集合是 java.util.Map 时，级联元素适用于 map 值。
     * 默认不级联任何操作。
     */
    CascadeType[] cascade() default {};

    /** 
     * (可选）关联是否应延迟加载或必须急切获取。
     * EAGER 策略是对持久化提供程序运行时的要求，即必须急于获取关联实体。
     * LAZY 策略是对持久化提供程序运行时的提示.【默认】
     */
    FetchType fetch() default LAZY;

    /** 
     * 拥有该关系的字段。必须填写，除非关系是单向的。
     */
    String mappedBy() default "";
}
```



### 单向关联和双向关联的区别

以"一对多"关联举例：

```java
/** 
 * 单向"一对多"关联:只有一方维护了关联关系。例如,一个部门有多个员工,只有Employee实体包含一个Department字段,Department实体没有反向引用Employee实体。
 */

// 单向一对多
@Entity 
public class Department {
  @Id
  private long id;
  private String name;
}

@Entity
public class Employee {
  @Id
  private long id;
  
  @ManyToOne
  @JoinColumn(name="dept_id")
  private Department department; 
}
```

```java
/** 
 * 双向"一对多"关联:两方都维护了关联关系。例如,一个部门有多个员工,Department实体包含一个Employee集合,Employee实体包含一个Department字段。
 */

// 多向一对多
@Entity
public class Department {
  @OneToMany(mappedBy="department")
  private List<Employee> employees;
} 

@Entity 
public class Employee {
  @ManyToOne
  @JoinColumn(name="dept_id") 
  private Department department;
}
```

#### 区别：

1.  单向关联只能通过Employee查询Department,不能通过Department反向查询Employee。双向关联两方都可以查询。
2.  单向关联Department可以脱离Employee独立保存。双向关联通常需要级联保存两方数据。
3.  双向关联可以设置额外的Cascade操作,如删除部门时连员工也删除。单向关联不可以。
4.  无论是单向还是双向,数据库表都会包含外键约束来表示一对多的关系。
5.  单向关联和双向关联对应的数据库表结构是一样的,只是在代码表示层面采用了不同的对象关系映射方式。

