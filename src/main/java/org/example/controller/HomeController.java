package org.example.controller;

import org.example.Html;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String home() {
        return Html.page("Система учёта оборудования", """
            <p>Выберите раздел:</p>
            <ul>
                <li><a href="/employees">Сотрудники</a> — список и добавление</li>
                <li><a href="/equipment">Устройства</a> — список, добавление, выдача,
                    возврат, ремонт, история</li>
            </ul>
            """);
    }
}