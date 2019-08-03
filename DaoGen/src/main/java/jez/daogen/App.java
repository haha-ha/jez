package jez.daogen;

import jez.daogen.generator.Generator;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@Log
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class App implements CommandLineRunner {

    @Autowired
    Generator generator;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            log.info("dao code generation started.");
            generator.generate();
            log.info("dao code generation ended.");
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}