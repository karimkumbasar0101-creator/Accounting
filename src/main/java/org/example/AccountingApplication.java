package org.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

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
    private  final  EquipmentService equipmentService;
    private final EquipmentHistoryService equipmentHistoryService;
    private EquipmentHistoryService historyService;

    MyCommandLineRunner(EmployeeService employeeService, EquipmentService equipmentService, EquipmentHistoryService equipmentHistoryService) {
        this.employeeService = employeeService;
        this.equipmentService = equipmentService;
        this.equipmentHistoryService = equipmentHistoryService;
        this.historyService = equipmentHistoryService;
    }


    @Override

    public void run(String... args) throws Exception {
        System.out.println("Arguments received:");
        for (String arg : args) {
            System.out.println(arg);
        }
        Employee employee = new Employee();
        employee.setName("Даниил");
        employee.setSurname("Рыбалкин");
        employee.setEmail("12345@mail.com");
        employeeService.addEmployee(employee);

        Equipment eq = new Equipment();
        eq.setType(Type.LAPTOP);
        eq.setModel("Ноутбук");
        eq.setSerialNumber("123455");
        eq.setPurchaseDate(LocalDate.now());
        eq.setStatus(Status.IN_STOCK);
        equipmentService.addEquipment(eq);

        equipmentService.issueEquipment(eq.getId(), employee.getId());

        System.out.println("После выдачи:");
        System.out.println(equipmentService.getEquipmentById(eq.getId()));
        System.out.println("История:");
        System.out.println(historyService.getHistoryByEquipment(eq.getId()));

    }
}

