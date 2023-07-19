package com.johann.binarytea;

import com.johann.binarytea.actuator.SalesMetrics;
import com.johann.config.MyBanner;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/*
 * @SpringBootApplication 注解是一个复合注解，包括 @ComponentScan、@SpringBootConfiguration、@EnableAutoConfiguration 三个注解。
 * @ComponentScan 注解默认扫描的是当前包及其子包下的所有类，所以我们一般将Application类放在root package下，
 * 这样所有的类都会被Spring Boot扫描到。
 * @SpringBootConfiguration 注解继承自@Configuration 注解，二者功能也一致，标注当前类是配置类，
 * 并会将当前类内声明的一个或多个以@Bean注解标记的方法的实例纳入到spring容器中，并且实例名就是方法名。
 * @EnableAutoConfiguration 的作用启动自动的配置。意思就是SpringBoot根据你添加的jar包来配置你项目的默认配置，
 * 比如：根据spring-boot-starter-web，来判断你的项目是否需要添加了webmvc和tomcat，就会自动的帮你配置web项目中所需要的默认配置。
 * Spring Boot的这种做法是为了简化开发，也是符合开箱即用的思想。
 */
@SpringBootApplication
/*
 * @RestController 注解相当于@Controller+@ResponseBody两个注解的结合，
 * @Controller 表示该类是一个控制器，它用于标识一个Spring类的实例是Spring MVC Controller对象。
 * @Controller 注解标识的类的方法可以使用 @RequestMapping、@GetMapping、@PostMapping等来映射请求。
 * @ResponseBody 表示该类下的所有方法返回值都是字符串。
 *
 * @RestController 注解是Spring4之后加入的注解，如果我们只是使用@RestController 注解Controller，
 * 则Controller中的方法无法返回jsp页面，或者html，配置的视图解析器InternalResourceViewResolver不起作用，返回的内容就是return里的内容。
 * 例如：本来应该到success.jsp页面的，则其显示success。
 */
@RestController
/*
 * @Log4j2注解是lombok提供的，用来简化日志的使用。
 */
@Log4j2
@EnableScheduling
public class BinaryteaApplication {

    /**
     * Spring Boot应用的入口，main方法，用于启动应用程序。
     * @param args
     */
//    public static void main(String[] args) {
//        SpringApplication.run(BinaryteaApplication.class, args);
//    }

    /**
     * 自定义SpringApplication，用于启动应用程序。
     * @param args
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(BinaryteaApplication.class)
                .main(BinaryteaApplication.class)
                .bannerMode(Banner.Mode.LOG)
                //.banner(new MyBanner())
                .web(WebApplicationType.SERVLET)
                .run(args);
    }



    // http://localhost:8080/helloworld
    /**
     * @RequestMapping 注解用来映射请求，即指明请求的URL。
     * 可以用来标注一个类，此时这个类就是一个Controller。
     * 可以用来标注一个方法，此时这个方法就是一个Action。
     */
    @RequestMapping("/helloworld")
    public String helloworld() {
        log.info("Hello World!");
        return "Hello World!";
    }

    private Random random = new Random();
    @Autowired
    private SalesMetrics salesMetrics;

    /**
     * 每隔5秒创建一个订单
     */
    @Scheduled(fixedRate = 5000,initialDelay = 1000)
    public void periodicallyMakeAnOrder(){
        int amount = random.nextInt(100);
        salesMetrics.makeNewOrder(amount);
        log.info("Make a new order,amount is {}.",amount);
    }
}
