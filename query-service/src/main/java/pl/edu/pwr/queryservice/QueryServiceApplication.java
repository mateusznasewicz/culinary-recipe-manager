package pl.edu.pwr.queryservice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableRabbit
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class QueryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueryServiceApplication.class, args);
    }

}
