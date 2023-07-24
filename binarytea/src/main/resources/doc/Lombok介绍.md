Lombok是一个用于简化Java代码的开源库。它通过注解的方式,可以在编译时自动生成getter/setter、equals、hashcode、tostring、logger等方法,从而减少冗余的代码。

主要功能和用途如下:

| 注解 | 说明 |
|-|-|
| @Getter、@Setter | 自动生成成员属性的 Getter 和 Setter 方法 |
| @ToString | 自动生成 tostring()方法，默认拼接所有的成员属性，也可以排除指定的属性 |
| @NoArgsConstructor、@RequiredArgsConstructor、@AllArgsConstructor | 自动生成无参数的构造方法，必要参数的构造方法以及包含全部参数的构造方法 |
| @EqualsAndHashCode | 自动生成 equals() 与 hashCode() 方法 |
| @Data | 相当于添加了 @ToString、@EqualsAndHashCode、 @Getter、@Setter和 @Required-ArgsConstructor 注解 |
| @Builder | 提供了一个灵活的构造器，能够设置各个成员变量，再据此创建对象实例 |
| @Slf4j、@CommonsLog、@Log4j2 | 自动生成对应日志框架的日志类，例如定义了一个 Logger 类型的 log，方便输出日志 |
| @SneakyThrows | 自动抛出受检查异常,无需显式抛出 |
| @NonNull | 标记参数或返回值不能为null |
| @Cleanup | 在资源使用完后自动关闭资源 |

