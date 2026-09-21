package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.Html;
import org.example.entity.Employee;
import org.example.service.EmployeeService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // GET /employees — список всех
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String list() {
        List<Employee> all = employeeService.getAllEmployee();
        StringBuilder sb = new StringBuilder();
        sb.append("<p><a class=\"btn\" href=\"/employees/new\">Добавить сотрудника</a></p>");

        if (all.isEmpty()) {
            sb.append("<p>Сотрудников пока нет.</p>");
        } else {
            sb.append("<table>")
                    .append("<tr><th>ID</th><th>Фамилия</th><th>Имя</th><th>Отчество</th>")
                    .append("<th>Email</th><th>Устройства</th></tr>");
            for (Employee e : all) {
                sb.append("<tr>")
                        .append("<td>").append(e.getId()).append("</td>")
                        .append("<td>").append(Html.esc(e.getSurname())).append("</td>")
                        .append("<td>").append(Html.esc(e.getName())).append("</td>")
                        .append("<td>").append(Html.esc(e.getMiddleName())).append("</td>")
                        .append("<td>").append(Html.esc(e.getEmail())).append("</td>")
                        .append("<td>")
                        .append("<a class=\"btn btn-small\" href=\"/equipment/search/employee?employeeId=")
                        .append(e.getId()).append("\">Показать</a>")
                        .append("</td>")
                        .append("</tr>");
            }
            sb.append("</table>");
        }
        return Html.page("Сотрудники", sb.toString());
    }

    // GET /employees/new — форма
    @GetMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String newForm() {
        return Html.page("Новый сотрудник", """
            <form method="post" action="/employees">
                <label>Имя:<br><input type="text" name="name" required></label>
                <label>Фамилия:<br><input type="text" name="surname" required></label>
                <label>Отчество:<br><input type="text" name="middleName"></label>
                <label>Email:<br><input type="email" name="email"></label>
                <p><button class="btn" type="submit">Создать</button>
                   <a href="/employees">Отмена</a></p>
            </form>
            """);
    }

    // POST /employees — создать
    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam String surname,
                         @RequestParam(required = false) String middleName,
                         @RequestParam(required = false) String email) {
        Employee e = new Employee();
        e.setName(name);
        e.setSurname(surname);
        e.setMiddleName(middleName);
        e.setEmail(email);
        employeeService.addEmployee(e);
        return "redirect:/employees";
    }
}