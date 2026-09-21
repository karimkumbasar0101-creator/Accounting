package org.example;

import org.example.entity.Employee;

public final class Html {

    private Html() {}

    public static String page(String title, String body) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"ru\"><head><meta charset=\"UTF-8\">" +
                "<title>" + esc(title) + "</title>" +
                "<style>" +
                "  body { font-family: system-ui, Arial, sans-serif; max-width: 1000px;" +
                "         margin: 20px auto; padding: 0 20px; color: #222; }" +
                "  nav { border-bottom: 1px solid #ddd; padding-bottom: 10px; margin-bottom: 20px; }" +
                "  nav a { margin-right: 15px; color: #06c; text-decoration: none; }" +
                "  nav a:hover { text-decoration: underline; }" +
                "  table { border-collapse: collapse; width: 100%; margin: 15px 0; }" +
                "  th, td { border: 1px solid #ccc; padding: 8px 12px; text-align: left; }" +
                "  th { background: #f5f5f5; }" +
                "  .btn { display: inline-block; padding: 6px 12px; background: #06c; color: #fff;" +
                "         border: none; border-radius: 4px; text-decoration: none;" +
                "         cursor: pointer; font-size: 14px; }" +
                "  .btn:hover { background: #05a; color: #fff; text-decoration: none; }" +
                "  .btn-danger { background: #c33; }" +
                "  .btn-danger:hover { background: #a22; }" +
                "  .btn-small { padding: 4px 8px; font-size: 13px; }" +
                "  input, select, textarea { padding: 6px; margin: 4px 0;" +
                "                            border: 1px solid #ccc; border-radius: 4px; }" +
                "  label { display: block; margin-top: 10px; }" +
                "  .error { color: #c33; padding: 10px; background: #fee; border-radius: 4px; }" +
                "  .ok { color: #393; padding: 10px; background: #efe; border-radius: 4px; }" +
                "  form.inline { display: inline; }" +
                "  h3 { margin-top: 25px; }" +
                "</style></head><body>" +
                "<nav>" +
                "  <a href=\"/\">Главная</a>" +
                "  <a href=\"/employees\">Сотрудники</a>" +
                "  <a href=\"/equipment\">Устройства</a>" +
                "</nav>" +
                "<h1>" + esc(title) + "</h1>" +
                body +
                "</body></html>";
    }

    public static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public static String employeeName(Employee e) {
        if (e == null) return "—";
        StringBuilder sb = new StringBuilder();
        sb.append(esc(e.getSurname())).append(" ").append(esc(e.getName()));
        if (e.getMiddleName() != null && !e.getMiddleName().isBlank()) {
            sb.append(" ").append(esc(e.getMiddleName()));
        }
        return sb.toString();
    }
}