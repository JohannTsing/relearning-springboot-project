package com.johann.customer;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

    /**
     * 控制退出码
     * 如果命令行里给了wait选项，返回0，否则返回1
     * @param args
     * @return
     */
    @Bean
    public ExitCodeGenerator waitExitCodeGenerator(ApplicationArguments args){
        return () -> {
            int wait = args.containsOption("wait") ? 0 : 1;
            return wait;
        };
    }

}
