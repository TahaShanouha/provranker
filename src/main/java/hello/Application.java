package hello;


import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableJpaRepositories("hello")
//(scanBasePackages={"hello", "Controller"})
//@EnableJpaRepositories("hello")
//@EntityScan("hello")

public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    
    
}
