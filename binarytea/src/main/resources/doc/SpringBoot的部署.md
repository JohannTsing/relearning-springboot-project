### 可执行Jar及其原理
以前，要运行 Java EE 的应用程序需要一个应用容器，比如JBoss 或者 Tomcat。但随着技术的发展，外置容器已经不再是必选项了。
Spring Boot 可以内嵌 Tomcat、Jetty 等容器，一句简单的java -jar 命令就能让我们的工程像个普通进程一样运行起来。

#### 使用Maven打包
1. 使用`maven`进行打包前，需要在pom.xml中添加以下插件：
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```
2. 打包命令
```shell
#  mvn 命令后的clean 代表清理目标目录,package 是执行打包动作-Dmaven.test.skip 是为了加速打包过程，跳过测试。
mvn clean package -Dmaven.test.skip
```
3. 打包后，在target目录下的内容如下：
    * `classes`目录：包含了项目编译后的class文件，是项目运行所需的文件；
    * `generated-sources`目录：包含了项目编译过程中生成的源文件，通常由编译器自动生成；
    * `maven-archiver`目录：包含了项目打包过程中生成的元数据文件；
    * `maven-status`目录：用于存储Maven构建的状态信息，包括构建的开始时间、结束时间、构建的结果等；
    * `binarytea-0.0.1-SNAPSHOT.jar`：Spring Boot项目的fat jar，包含了项目的所有依赖，可以直接运行；
    * `binarytea-0.0.1-SNAPSHOT.jar.original`：Spring Boot项目的普通jar，只包含了项目的代码，不能直接运行，需要依赖其他的jar才能运行。
4. 运行jar包
```shell
java -jar target/binarytea-0.0.1-SNAPSHOT.jar
```

#### 可执行 Jar 背后的原理
解压`binarytea-0.0.1-SNAPSHOT.jar`，可以看到以下目录结构：
* `META-INF`，工程的元数据，例如 Maven 的描述文件与 spring.factories 文件;
* `org/springframework/boot/loader`，Spring Boot 用来引导工程启动的 Loader 相关类 
* `BOOT-INF/classes`，工程自身的类与资源文件; 
* `BOOT-INF/Lib`，工程所依赖的各种其他 Jar 文件。

运行这个 Jar所需要的信息都记录在了 `META-INF/MANIFEST.MF` 中，具体内容如下所示：
```shell
Manifest-Version: 1.0
Spring-Boot-Classpath-Index: BOOT-INF/classpath.idx
Implementation-Title: binarytea
Implementation-Version: 0.0.1-SNAPSHOT
Spring-Boot-Layers-Index: BOOT-INF/layers.idx
Start-Class: com.johann.binarytea.BinaryteaApplication
Spring-Boot-Classes: BOOT-INF/classes/
Spring-Boot-Lib: BOOT-INF/lib/
Build-Jdk-Spec: 1.8
Spring-Boot-Version: 2.7.12
Created-By: Maven JAR Plugin 3.2.2
Main-Class: org.springframework.boot.loader.JarLauncher
```

java 命令会找到 `Main-class` 作为启动类，Spring Boot 提供了三种不同的 Launcher，可以从内嵌文件中加载启动所需的资源:
* `JarLauncher`，从Jar 包的固定位置加载内嵌资源，即 BOOT-INF/Lib/；
* `warLauncher`，从 War 包的固定位置加载内嵌资源，分别为WEB-INF/Lb/ 和 WEB-INF/lib-provided/；
* `PropertiesLauncher` ，默认从 BOOT-INF/Lb/ 加载资源，可以通过环境变量来指定额外的位置。

默认生成的工程会使用 `JarLauncher` 生成可执行 `Jar` 包，而启动后执行的具体代码则由 `Start-Class` 指定，我们可以看到这个类就是添加了 `SpringBootApplication` 注解的类。

用这种方式打包有两个局限：
* Jar 文件其实就是一个 ZIP 文件，ZIP 文件可以设置压缩力度，从仅存储 (不压缩)到最大化压缩，在内嵌 Jar 的情况下，我们只能使用 ZipEntry.STORED，即仅存储，不压缩的方式。
* 要获取 ClassLoader 时必须使用 `Thread.getContextClassLoader()`，而不能使用`classLoader.getSystemClassLoader()`。

### 构建启动代码
#### 自定义 SpringApplication
```text
public static void main(String[] args) {
  new SpringApplicationBuilder()
          .sources(BinaryteaApplication.class)
          .main(BinaryteaApplication.class)
          .bannerMode(Banner.Mode.LOG)
          .web(WebApplicationType.SERVLET)
          .run(args);
}
```

#### 通过 FailureAnalyzer 提供失败原因分析
Spring Boot 可以通过FailureAnalyzer 来分析失败，并打印分析出的原因。

Spring Boot 内置了近 20 种不同的分析器，可以分析出各种不同的失败原因，例如：
* `NoSuchBeanDefinitionFailureAnalyzer`，分析出 Bean 不存在的原因；
* `NoUniqueBeanDefinitionFailureAnalyzer`，分析出 Bean 不唯一的原因；
* `DataAccessResourceFailureAnalyzer`，分析出数据访问资源失败的原因；
* `InvalidConfigurationPropertyNameFailureAnalyzer`，分析出配置属性名不合法的原因；
* `BindFailureAnalyzer`，分析出配置绑定失败的原因；
* `ConnectFailureAnalyzer`，分析出连接失败的原因；
* `HttpMessageConversionFailureAnalyzer`，分析出 HTTP 消息转换失败的原因；
* `MethodArgumentNotValidFailureAnalyzer`，分析出方法参数校验失败的原因；
* `NoHandlerFoundFailureAnalyzer`，分析出找不到处理器的原因；
* `NoSuchFieldErrorFailureAnalyzer`，分析出字段不存在的原因；
* `NoSuchMethodErrorFailureAnalyzer`，分析出方法不存在的原因；
* `UndeclaredThrowableFailureAnalyzer`，分析出未声明的异常的原因；
* `UnsatisfiedDependencyFailureAnalyzer`，分析出依赖注入失败的原因；
* `WebServerPortInUseFailureAnalyzer`，分析出 Web 服务器端口被占用的原因；

如果想要实现自己的FailureAnalyzer，可以继承`AbstractFailureAnalyzer<T extends Throwable>` 这个抽象类。

#### 自定义 Banner 栏
1. 在resources目录下创建一个banner.txt文件（如果banner文件名是自定义的，此时需要使用`spring.banner.location=my-banner.txt`指定文件位置）。
```text
${AnsiColor.BRIGHT_YELLOW}
////////////////////////////////////////////////////////////////////
//                          _ooOoo_                               //
//                         o8888888o                              //
//                         88" . "88                              //
//                         (| ^_^ |)                              //
//                         O\  =  /O                              //
//                      ____/`---'\____                           //
//                    .'  \\|     |//  `.                         //
//                   /  \\|||  :  |||//  \                        //
//                  /  _||||| -:- |||||-  \                       //
//                  |   | \\\  -  /// |   |                       //
//                  | \_|  ''\---/''  |   |                       //
//                  \  .-\__  `-`  ___/-. /                       //
//                ___`. .'  /--.--\  `. . ___                     //
//              ."" '<  `.___\_<|>_/___.'  >'"".                  //
//            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 //
//            \  \ `-.   \_ __\ /__ _/   .-` /  /                 //
//      ========`-.____`-.___\_____/___.-`____.-'========         //
//                           `=---='                              //
//      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //
//            佛祖保佑       永不宕机     永无BUG                     //
////////////////////////////////////////////////////////////////////
${AnsiColor.BRIGHT_GREEN}
Application Version: ${application.version}${application.formatted-version}
Spring Boot Version: ${spring-boot.version}${spring-boot.formatted-version}
```
2. 创建一个Banner类，实现Banner接口，重写printBanner方法。
```java
public class MyBanner implements Banner {

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream printStream) {
        printStream.printf("\nSpring Boot Version: %s", SpringBootVersion.getVersion());
        printStream.printf("\nWelcome to %s!", environment.getProperty("spring.application.name"));
    }

}
```
随后，在启动类中，通过`SpringApplication.setBanner(new MyBanner())`方法（`SpringApplicationBuilder.banner(new MyBanner())`）设置自定义的Banner。

### 启动后的一次性执行逻辑
在系统启动时，可能会有些初始化后需要立即执行的逻辑，也有可能这就是一个命令行程序，执行完特定逻辑后程序就该退出了。

这时我们该怎么办?写一个 Bean，在它的 @PostConstruct 方法中执行逻辑么? 其实，在Spring Boot 中有更好的选择。

Spring Boot 为我们提供了两个接口，分别是 ApplicationRunner 与 CommandLineRunner，它们的功能基本是相同的，只是方法的参数不同：
```java
public interface ApplicationRunner {
   /**
    * 通过 ApplicationArguments，我们可以更方便灵活地控制命令行中的参数。
    * @param args
    * @throws Exception
    */
    void run(ApplicationArguments args) throws Exception;
}

public interface CommandLineRunner {
   /**
    * CommandLineRunner 的 run(String... args)传入的参数与 main(String... args)方法一样，就是命令行的所有参数。
    * @param args
    * @throws Exception
    */
    void run(String... args) throws Exception;
}
```
如果Spring上下文中存在多个`ApplicationRunner` 或`CommandLineRunner` Bean，可以通过`@0rder` 注解或`Ordered` 接口来指定运行的顺序。

#### 通过 ApplicationRunner 与 CommandLineRunner 执行一次性逻辑
下面是一个 CommandLineRunner 的实现，它的作用是打印所有的命令行参数：
```java
@Component
@Slf4j
// 通过@0rder 注解或Ordered 接口来指定运行的顺序
@Order(1)
public class ArgsPrinterRunner implements CommandLineRunner {
    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("共传入了 {} 个参数。分别是:{}",args.length, StringUtils.arrayToCommaDelimitedString(args));
    }
}
```
下面是一个ApplicationRunner 的实现，它会根据命令行上传入的参数来决定是否等待。

如果我们通过 wait 选项设置了等待时间，则等待时间即为程序里 sleep 对应的秒数，没有 wait就直接结束。
```java
@Component
@Slf4j
public class WaitForOpenRunner implements ApplicationRunner, ApplicationContextAware, Ordered {

    /**
     * Set the {@code ApplicationContext} that this object runs in.
     * Setter注入ApplicationContext
     * ApplicationContextAware接口的作用是让Bean获取ApplicationContext中的所有Bean
     */
    @Setter
    private ApplicationContext applicationContext;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        boolean needWait = args.containsOption("wait");
        if (!needWait) {
            log.info("未营业，无需等待了...");
        }else {
            List<String> waitSeconds = args.getOptionValues("wait");
            if (waitSeconds != null && !waitSeconds.isEmpty()) {
                int seconds = NumberUtils.parseNumber(waitSeconds.get(0),
                        Integer.class);
                log.info("还没开门，先等{}秒。", seconds);
                Thread.sleep(seconds * 1000);
            }
            log.info("其他参数：{}",
                    StringUtils.collectionToCommaDelimitedString(
                            args.getNonOptionArgs()));
        }
        System.exit(SpringApplication.exit(applicationContext));
    }

    /**
     * 通过@0rder 注解或Ordered 接口来指定运行的顺序
     * 获取此对象的顺序值。
     * 数值越大，优先级越低。因此，值最小的对象优先级最高（有点类似于 Servlet 启动时的加载值）。
     * 相同的顺序值将导致受影响对象任意排序。
     *
     * @return the order value
     */
    @Override
    public int getOrder() {
        return 2;
    }
}
```
> ApplicationArguments 中的方法：
> - boolean containsOption(String name)：判断是否包含某个选项
> - Set<String> getOptionNames()：获取所有的选项名称
> - List<String> getOptionValues(String name)：根据选项名称获取对应的值
> - List<String> getNonOptionArgs()：获取非选项类型的参数列表
> - String[] getSourceArgs()：获取原始的参数列表
> 
> 所谓的选项类型，其形式是： --name=value 或 --name value，非选项类型就是普通的参数，如 name=value 或 value。
> java -jar target\customer-0.0.1-SNAPSHOT.jar --wait=3 --times 12 name=Hello Johann Jessie

打包项目：`mvn clean package -Dmaven.test.skip`

启动Jar：`java -jar target\customer-0.0.1-SNAPSHOT.jar --wait=3 --times 12 name=Hello Johann Jessie`

#### 通过 ExitCodeGenerator 接口设置退出码
在Java里，可以调用 `System.exit()` 退出程序，这个方法能够传入需要返回的退出码。

SpringBoot为我们提供了`ExitCodeGenerator` 接口，通过实现该接口我们可以加入自己的逻辑来控制退出码，如果存在多个 Bean，可以和前文一样用 `@Order` 注解或 `Ordered` 接口来控制顺序。
调用`SpringApplication.exit()`方法即可获得最终计算出的退出码，把它传入 `System.exit()` 就可以了。

```text
/**
* 控制退出码
* 如果命令行里给了wait选项，返回0，否则返回1
* @param args
* @return
*/
public ExitCodeGenerator waitExitCodeGenerator(ApplicationArguments args){
  return () -> {
      int wait = args.containsOption("wait") ? 0 : 0;
      return wait;
  };
}
```
在上述`WaitForOpenRunner`中，希望在执行完日志输出后调用退出逻辑`System.exit(SpringApplication.exit(applicationContext));`。

`SpringApplication.exit()`是一个静态辅助函数，可用于退出 SpringApplication 并获取成功（0）或失败的代码。
该方法需要传入 `ApplicationContext`，因此我们让 `WaitForOpenRunner` 实现`ApplicationContextAware`，这里的 Setter 方法直接通过 Lombok 的 @Setter 来实现。
