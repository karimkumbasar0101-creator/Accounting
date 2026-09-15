package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
@SpringBootApplication
public class AccountingApplication {

    public static void main( String[] args )
    {
        ConfigurableApplicationContext run = SpringApplication.run(AccountingApplication.class, args);;
    }
}
