package org.example;

import lombok.RequiredArgsConstructor;
import org.example.entity.Employee;
import org.example.entity.Equipment;
import org.example.entity.EquipmentHistory;
import org.example.enumType.Status;
import org.example.enumType.Type;
import org.example.service.EmployeeService;
import org.example.service.EquipmentHistoryService;
import org.example.service.EquipmentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

//@Component
@RequiredArgsConstructor
public class ConsoleMenu implements CommandLineRunner {

    private final EmployeeService employeeService;
    private final EquipmentService equipmentService;
    private final EquipmentHistoryService historyService;
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run(String... args) {
        System.out.println("Система учёта оборудования");
        while (true) {
            printMainMenu();
            int choice = readInt("Выбор: ");

            switch (choice) {
                case 1 -> addEmployee();
                case 2 -> addEquipment();
                case 3 -> issueEquipment();
                case 4 -> returnEquipment();
                case 5 -> sendToRepair();
                case 6 -> returnFromRepair();
                case 7 -> showSearchMenu();
                case 8 -> showAllEquipment();
                case 9 -> showEquipmentHistory();
                case 0 -> { System.out.println("Завершение"); return; }
                default -> System.out.println("Неизвестная команда\n");
            }
        }
    }

 //меню
 private void printMainMenu() {
     System.out.println("""
            
            Главное меню:
              1. Добавить сотрудника
              2. Добавить устройство
              3. Выдать устройство
              4. Вернуть устройство
              5. Отправить в ремонт
              6. Вернуть из ремонта
              7. Поиск
              8. Показать все устройства
              9. История устройства
              0. Выход""");
 }


//поиск
    private void showSearchMenu() {
        while (true) {
            System.out.println("""
                    
                    Поиск:
                      1. Найти по серийному номеру
                      2. Найти по статусу
                      3. Найти по сотруднику
                      0. Назад""");
            int choice = readInt("Выбор: ");

            switch (choice) {
                case 1 -> searchBySerialNumber();
                case 2 -> searchByStatus();
                case 3 -> searchByEmployee();
                case 0 -> { return; }
                default -> System.out.println("Неизвестная команда\n");
            }
        }
    }

    private void searchBySerialNumber() {
        String sn = readLine("Серийный номер: ");
        Optional<Equipment> result = equipmentService.findBySerialNumber(sn);

        if (result.isPresent()) {
            printEquipment(result.get());
        } else {
            System.out.println("Устройство не найдено");
        }
    }

    private void searchByStatus() {
        System.out.println("""
                Выберите статус:
                  1. На складе
                  2. Выдан
                  3. В ремонте
                  4. Списан""");
        int choice = readInt("Выбор: ");

        Status status = switch (choice) {
            case 1 -> Status.IN_STOCK;
            case 2 -> Status.ISSUED;
            case 3 -> Status.IN_REPAIR;
            case 4 -> Status.WRITTEN_OFF;
            default -> null;
        };

        if (status == null) {
            System.out.println("Неверный статус");
            return;
        }

        List<Equipment> found = equipmentService.findByStatus(status);
        printEquipmentList(found);
    }

    private void searchByEmployee() {
        Long employeeId = (long) readInt("ID сотрудника: ");
        List<Equipment> found = equipmentService.findByEmployee(employeeId);
        printEquipmentList(found);
    }

//нов сотрудник
    private void addEmployee() {
        String name    = readLine("Имя: ");
        String surname = readLine("Фамилия: ");
        String middlename = readLine("Отчество");
        String email   = readLine("Email: ");

        Employee employee = new Employee();
        employee.setName(name);
        employee.setSurname(surname);
        employee.setEmail(email);
        employee.setMiddleName(middlename);
        Employee saved = employeeService.addEmployee(employee);
        System.out.println("Сотрудник создан: id=" + saved.getId());
    }

    private void addEquipment() {
        System.out.println("""
                Тип устройства:
                  1. Ноутбук
                  2. Монитор
                  3. Телефон
                  4. Другое""");
        int typeChoice = readInt("Выбор: ");
        Type type = switch (typeChoice) {
            case 1 -> Type.LAPTOP;
            case 2 -> Type.MONITOR;
            case 3 -> Type.PHONE;
            case 4 -> Type.OTHER;
            default -> null;
        };
        if (type == null) {
            System.out.println("Неверный тип");
            return;
        }

        String model = readLine("Модель: ");
        String sn    = readLine("Серийный номер: ");

        Equipment eq = new Equipment();
        eq.setType(type);
        eq.setModel(model);
        eq.setSerialNumber(sn);
        eq.setPurchaseDate(LocalDate.now());
        eq.setStatus(Status.IN_STOCK);

        Equipment saved = equipmentService.addEquipment(eq);
        System.out.println("Устройство добавлено: id=" + saved.getId());
    }

    private void issueEquipment() {
        Long equipmentId = (long) readInt("ID устройства: ");
        Long employeeId  = (long) readInt("ID сотрудника: ");

        try {
            equipmentService.issueEquipment(equipmentId, employeeId);
            System.out.println("Устройство выдано");
        } catch (RuntimeException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void returnEquipment() {
        Long equipmentId = (long) readInt("ID устройства: ");

        try {
            equipmentService.returnEquipment(equipmentId);
            System.out.println("Устройство возвращено на склад");
        } catch (RuntimeException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    private void sendToRepair() {
        Long equipmentId = (long) readInt("ID устройства: ");
        String comment = readLine("Причина (что сломалось): ");

        try {
            equipmentService.sendToRepair(equipmentId, comment);
            System.out.println("Устройство отправлено в ремонт");
        } catch (RuntimeException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void returnFromRepair() {
        Long equipmentId = (long) readInt("ID устройства: ");

        try {
            equipmentService.returnFromRepair(equipmentId);
            System.out.println("Устройство вернулось на склад");
        } catch (RuntimeException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showAllEquipment() {
        List<Equipment> all = equipmentService.getAllEquipment();
        if (all.isEmpty()) {
            System.out.println("Устройств в базе нет");
            return;
        }
        all.forEach(this::printEquipment);
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число");
            }
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private void printEquipment(Equipment eq) {
        System.out.printf("id=%d | %s %s | SN=%s | статус=%s | сотрудник=%s%n",
                eq.getId(),
                eq.getType(),
                eq.getModel(),
                eq.getSerialNumber(),
                eq.getStatus(),
                eq.getCurrentEmployee() == null ? "Нет" : eq.getCurrentEmployee().getSurname());
    }

    private void printEquipmentList(List<Equipment> list) {
        if (list.isEmpty()) {
            System.out.println("Ничего не найдено");
            return;
        }
        for (Equipment eq : list) {
            printEquipment(eq);
        }
    }
    private void showEquipmentHistory() {
        Long equipmentId = (long) readInt("ID устройства: ");

        // Сначала проверим, что устройство вообще есть — иначе выведем понятную ошибку
        Optional<Equipment> maybe = equipmentService.getEquipmentById(equipmentId);
        if (maybe.isEmpty()) {
            System.out.println("Устройство с id=" + equipmentId + " не найдено");
            return;
        }
        Equipment eq = maybe.get();

        List<EquipmentHistory> history = historyService.getHistoryByEquipment(equipmentId);
        if (history.isEmpty()) {
            System.out.println("История пуста");
            return;
        }

        System.out.println();
        System.out.printf("История устройства: id=%d | %s %s | SN=%s%n",
                eq.getId(), eq.getType(), eq.getModel(), eq.getSerialNumber());
        System.out.println("────────────────────────────────────────────────────────────");

        for (EquipmentHistory h : history) {
            String who = h.getEmployee() == null
                    ? "—"
                    : h.getEmployee().getSurname() + " " + h.getEmployee().getName();
            String comment = h.getComment() == null ? "" : h.getComment();

            System.out.printf("%s | %s → %s | %s | %s | %s%n",
                    h.getChangedAt().format(DATE_TIME_FORMAT),
                    h.getOldStatus() == null ? "—" : h.getOldStatus(),
                    h.getNewStatus(),
                    h.getAction(),
                    who,
                    comment);
        }
        System.out.println("────────────────────────────────────────────────────────────");
    }
}