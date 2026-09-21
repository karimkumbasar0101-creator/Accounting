package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.Html;
import org.example.entity.Employee;
import org.example.entity.Equipment;
import org.example.entity.EquipmentHistory;
import org.example.enumType.Status;
import org.example.enumType.Type;
import org.example.service.EmployeeService;
import org.example.service.EquipmentHistoryService;
import org.example.service.EquipmentService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final EmployeeService employeeService;
    private final EquipmentHistoryService historyService;

    // ================= СПИСОК =================

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String list() {
        List<Equipment> all = equipmentService.getAllEquipment();
        StringBuilder sb = new StringBuilder();
        sb.append("<p>")
                .append("<a class=\"btn\" href=\"/equipment/new\">Добавить устройство</a> ")
                .append("<a class=\"btn\" href=\"/equipment/search\">Поиск</a>")
                .append("</p>");

        if (all.isEmpty()) {
            sb.append("<p>Устройств пока нет.</p>");
        } else {
            sb.append("<table>")
                    .append("<tr><th>ID</th><th>Тип</th><th>Модель</th><th>SN</th>")
                    .append("<th>Дата покупки</th><th>Статус</th><th>Сотрудник</th><th></th></tr>");
            for (Equipment e : all) {
                sb.append("<tr>")
                        .append("<td>").append(e.getId()).append("</td>")
                        .append("<td>").append(e.getType()).append("</td>")
                        .append("<td>").append(Html.esc(e.getModel())).append("</td>")
                        .append("<td>").append(Html.esc(e.getSerialNumber())).append("</td>")
                        .append("<td>").append(e.getPurchaseDate()).append("</td>")
                        .append("<td>").append(e.getStatus()).append("</td>")
                        .append("<td>").append(Html.employeeName(e.getCurrentEmployee())).append("</td>")
                        .append("<td><a class=\"btn btn-small\" href=\"/equipment/")
                        .append(e.getId()).append("\">Открыть</a></td>")
                        .append("</tr>");
            }
            sb.append("</table>");
        }
        return Html.page("Устройства", sb.toString());
    }

    // ================= ДОБАВЛЕНИЕ =================

    @GetMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String newForm() {
        return Html.page("Новое устройство", """
            <form method="post" action="/equipment">
                <label>Тип:
                    <select name="type" required>
                        <option value="LAPTOP">Ноутбук</option>
                        <option value="MONITOR">Монитор</option>
                        <option value="PHONE">Телефон</option>
                        <option value="OTHER">Другое</option>
                    </select>
                </label>
                <label>Модель:<br><input type="text" name="model" required></label>
                <label>Серийный номер:<br><input type="text" name="serialNumber" required></label>
                <label>Дата покупки:<br><input type="date" name="purchaseDate" required></label>
                <p><button class="btn" type="submit">Создать</button>
                   <a href="/equipment">Отмена</a></p>
            </form>
            """);
    }

    @PostMapping
    public String create(@RequestParam Type type,
                         @RequestParam String model,
                         @RequestParam String serialNumber,
                         @RequestParam String purchaseDate) {
        Equipment e = new Equipment();
        e.setType(type);
        e.setModel(model);
        e.setSerialNumber(serialNumber);
        e.setPurchaseDate(LocalDate.parse(purchaseDate));
        e.setStatus(Status.IN_STOCK);
        equipmentService.addEquipment(e);
        return "redirect:/equipment";
    }

    // ================= КАРТОЧКА УСТРОЙСТВА =================

    @GetMapping(value = "/{id}", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String detail(@PathVariable Long id,
                         @RequestParam(required = false) String action) {
        Optional<Equipment> maybe = equipmentService.getEquipmentById(id);
        if (maybe.isEmpty()) {
            return Html.page("Не найдено",
                    "<p class=\"error\">Устройство id=" + id + " не найдено</p>"
                            + "<p><a href=\"/equipment\">← К списку</a></p>");
        }
        Equipment e = maybe.get();
        StringBuilder sb = new StringBuilder();

        if ("issued".equals(action))   sb.append("<p class=\"ok\">Устройство выдано</p>");
        if ("returned".equals(action)) sb.append("<p class=\"ok\">Устройство возвращено на склад</p>");
        if ("repaired".equals(action)) sb.append("<p class=\"ok\">Устройство отправлено в ремонт</p>");
        if ("repairEnd".equals(action))sb.append("<p class=\"ok\">Устройство вернулось из ремонта</p>");

        sb.append("<table>")
                .append("<tr><th>ID</th><td>").append(e.getId()).append("</td></tr>")
                .append("<tr><th>Тип</th><td>").append(e.getType()).append("</td></tr>")
                .append("<tr><th>Модель</th><td>").append(Html.esc(e.getModel())).append("</td></tr>")
                .append("<tr><th>Серийный номер</th><td>").append(Html.esc(e.getSerialNumber())).append("</td></tr>")
                .append("<tr><th>Дата покупки</th><td>").append(e.getPurchaseDate()).append("</td></tr>")
                .append("<tr><th>Статус</th><td>").append(e.getStatus()).append("</td></tr>")
                .append("<tr><th>Сотрудник</th><td>").append(Html.employeeName(e.getCurrentEmployee())).append("</td></tr>")
                .append("</table>");

        // Доступные действия — в зависимости от текущего статуса
        if (e.getStatus() == Status.IN_STOCK) {
            sb.append("<h3>Выдать сотруднику</h3>");
            sb.append("<form method=\"post\" action=\"/equipment/").append(id).append("/issue\">");
            sb.append("<select name=\"employeeId\" required>");
            sb.append("<option value=\"\">— выберите сотрудника —</option>");
            for (Employee emp : employeeService.getAllEmployee()) {
                sb.append("<option value=\"").append(emp.getId()).append("\">")
                        .append(Html.employeeName(emp))
                        .append(" (id=").append(emp.getId()).append(")</option>");
            }
            sb.append("</select> ");
            sb.append("<button class=\"btn\" type=\"submit\">Выдать</button>");
            sb.append("</form>");
        }

        if (e.getStatus() == Status.ISSUED) {
            sb.append("<h3>Вернуть на склад</h3>");
            sb.append("<form class=\"inline\" method=\"post\" action=\"/equipment/")
                    .append(id).append("/return\">");
            sb.append("<button class=\"btn\" type=\"submit\">Вернуть</button>");
            sb.append("</form>");
        }

        if (e.getStatus() == Status.IN_STOCK || e.getStatus() == Status.ISSUED) {
            sb.append("<h3>Отправить в ремонт</h3>");
            sb.append("<form method=\"post\" action=\"/equipment/").append(id).append("/repair\">");
            sb.append("<input type=\"text\" name=\"comment\" placeholder=\"Что сломалось\" required> ");
            sb.append("<button class=\"btn btn-danger\" type=\"submit\">В ремонт</button>");
            sb.append("</form>");
        }

        if (e.getStatus() == Status.IN_REPAIR) {
            sb.append("<h3>Вернуть из ремонта</h3>");
            sb.append("<form class=\"inline\" method=\"post\" action=\"/equipment/")
                    .append(id).append("/repair-end\">");
            sb.append("<button class=\"btn\" type=\"submit\">Вернуть на склад</button>");
            sb.append("</form>");
        }

        sb.append("<h3>История</h3>");
        sb.append("<p><a href=\"/equipment/").append(id).append("/history\">")
                .append("Показать историю устройства</a></p>");

        return Html.page("Устройство #" + id, sb.toString());
    }

    // ================= ДЕЙСТВИЯ (POST) =================

    @PostMapping("/{id}/issue")
    public String issue(@PathVariable Long id, @RequestParam Long employeeId) {
        equipmentService.issueEquipment(id, employeeId);
        return "redirect:/equipment/" + id + "?action=issued";
    }

    @PostMapping("/{id}/return")
    public String returnToStock(@PathVariable Long id) {
        equipmentService.returnEquipment(id);
        return "redirect:/equipment/" + id + "?action=returned";
    }

    @PostMapping("/{id}/repair")
    public String toRepair(@PathVariable Long id, @RequestParam String comment) {
        equipmentService.sendToRepair(id, comment);
        return "redirect:/equipment/" + id + "?action=repaired";
    }

    @PostMapping("/{id}/repair-end")
    public String fromRepair(@PathVariable Long id) {
        equipmentService.returnFromRepair(id);
        return "redirect:/equipment/" + id + "?action=repairEnd";
    }

    // ================= ИСТОРИЯ =================

    @GetMapping(value = "/{id}/history", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String history(@PathVariable Long id) {
        Optional<Equipment> maybe = equipmentService.getEquipmentById(id);
        if (maybe.isEmpty()) {
            return Html.page("Не найдено",
                    "<p class=\"error\">Устройство id=" + id + " не найдено</p>");
        }
        Equipment e = maybe.get();
        List<EquipmentHistory> history = historyService.getHistoryByEquipment(id);

        StringBuilder sb = new StringBuilder();
        sb.append("<p>Устройство: <b>").append(e.getType()).append(" ")
                .append(Html.esc(e.getModel())).append("</b> (SN: ")
                .append(Html.esc(e.getSerialNumber())).append(")</p>");

        if (history.isEmpty()) {
            sb.append("<p>История пуста.</p>");
        } else {
            sb.append("<table>")
                    .append("<tr><th>Когда</th><th>Было</th><th>Стало</th>")
                    .append("<th>Действие</th><th>Сотрудник</th><th>Комментарий</th></tr>");
            for (EquipmentHistory h : history) {
                sb.append("<tr>")
                        .append("<td>").append(h.getChangedAt()).append("</td>")
                        .append("<td>").append(h.getOldStatus() == null ? "—" : h.getOldStatus()).append("</td>")
                        .append("<td>").append(h.getNewStatus()).append("</td>")
                        .append("<td>").append(h.getAction()).append("</td>")
                        .append("<td>").append(Html.employeeName(h.getEmployee())).append("</td>")
                        .append("<td>").append(Html.esc(h.getComment())).append("</td>")
                        .append("</tr>");
            }
            sb.append("</table>");
        }
        sb.append("<p><a href=\"/equipment/").append(id).append("\">← К устройству</a> | ")
                .append("<a href=\"/equipment\">К списку</a></p>");
        return Html.page("История устройства #" + id, sb.toString());
    }

    // ================= ПОИСК =================

    @GetMapping(value = "/search", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String searchForm() {
        StringBuilder sb = new StringBuilder();

        sb.append("<h3>По серийному номеру</h3>")
                .append("<form method=\"get\" action=\"/equipment/search/serial\">")
                .append("<input type=\"text\" name=\"sn\" required> ")
                .append("<button class=\"btn\" type=\"submit\">Найти</button>")
                .append("</form>");

        sb.append("<h3>По статусу</h3>")
                .append("<form method=\"get\" action=\"/equipment/search/status\">")
                .append("<select name=\"status\" required>")
                .append("<option value=\"IN_STOCK\">На складе</option>")
                .append("<option value=\"ISSUED\">Выдан</option>")
                .append("<option value=\"IN_REPAIR\">В ремонте</option>")
                .append("<option value=\"WRITTEN_OFF\">Списан</option>")
                .append("</select> ")
                .append("<button class=\"btn\" type=\"submit\">Найти</button>")
                .append("</form>");

        sb.append("<h3>По сотруднику</h3>")
                .append("<form method=\"get\" action=\"/equipment/search/employee\">")
                .append("<select name=\"employeeId\" required>")
                .append("<option value=\"\">— выберите сотрудника —</option>");
        for (Employee e : employeeService.getAllEmployee()) {
            sb.append("<option value=\"").append(e.getId()).append("\">")
                    .append(Html.employeeName(e))
                    .append(" (id=").append(e.getId()).append(")</option>");
        }
        sb.append("</select> ")
                .append("<button class=\"btn\" type=\"submit\">Найти</button>")
                .append("</form>");

        return Html.page("Поиск устройств", sb.toString());
    }

    @GetMapping(value = "/search/serial", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String searchBySerial(@RequestParam String sn) {
        return equipmentService.findBySerialNumber(sn)
                .map(e -> Html.page("Найдено", renderTable(List.of(e))))
                .orElseGet(() -> Html.page("Не найдено",
                        "<p class=\"error\">Устройство с SN=" + Html.esc(sn) + " не найдено</p>"
                                + "<p><a href=\"/equipment/search\">← К поиску</a></p>"));
    }

    @GetMapping(value = "/search/status", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String searchByStatus(@RequestParam Status status) {
        List<Equipment> found = equipmentService.findByStatus(status);
        return Html.page("Поиск: " + status, renderTable(found));
    }

    @GetMapping(value = "/search/employee", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String searchByEmployee(@RequestParam Long employeeId) {
        List<Equipment> found = equipmentService.findByEmployee(employeeId);
        return Html.page("Устройства сотрудника #" + employeeId, renderTable(found));
    }

    // ============ УТИЛИТА ============

    private String renderTable(List<Equipment> list) {
        if (list.isEmpty()) {
            return "<p>Ничего не найдено.</p>"
                    + "<p><a href=\"/equipment/search\">← К поиску</a></p>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table>")
                .append("<tr><th>ID</th><th>Тип</th><th>Модель</th><th>SN</th>")
                .append("<th>Статус</th><th>Сотрудник</th><th></th></tr>");
        for (Equipment e : list) {
            sb.append("<tr>")
                    .append("<td>").append(e.getId()).append("</td>")
                    .append("<td>").append(e.getType()).append("</td>")
                    .append("<td>").append(Html.esc(e.getModel())).append("</td>")
                    .append("<td>").append(Html.esc(e.getSerialNumber())).append("</td>")
                    .append("<td>").append(e.getStatus()).append("</td>")
                    .append("<td>").append(Html.employeeName(e.getCurrentEmployee())).append("</td>")
                    .append("<td><a class=\"btn btn-small\" href=\"/equipment/")
                    .append(e.getId()).append("\">Открыть</a></td>")
                    .append("</tr>");
        }
        sb.append("</table>")
                .append("<p><a href=\"/equipment/search\">← К поиску</a></p>");
        return sb.toString();
    }
}