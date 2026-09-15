package org.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class AccountingApplication {

    public static void main( String[] args )
    {
        ConfigurableApplicationContext run = SpringApplication.run(AccountingApplication.class, args);;
    }

}
@Component
class MyCommandLineRunner implements CommandLineRunner {

    private final EmployeeService employeeService;

    MyCommandLineRunner(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override

    public void run(String... args) throws Exception {
        System.out.println("Arguments received:");
        for (String arg : args) {
            System.out.println(arg);
        }
        Employee employee1 = new Employee();

        employee1.setName("Даниил");
        employee1.setSurname("Рыбалкин");
        employee1.setMiddleName("Олегович");
        employee1.setEmail("123@.mail.com");
        employeeService.addEmployee(employee1);
        System.out.println(employeeService.getAllEmployee());
        System.out.println(employeeService.getEmployeeById(1L));
    }
}

