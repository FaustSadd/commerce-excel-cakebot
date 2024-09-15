package Omg.CakeBot.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;




public class LogicCore {
    private String name;
    private LocalDate date;
    private String productName;
    private float weight;
    private String address;
    private String phoneNumber;



    public String getStandartReceipt(String messageText){
        // Парсим имя
        Pattern namePattern = Pattern.compile("Получен заказ от покупателя (.+) :");
        Matcher nameMatcher = namePattern.matcher(messageText);
        if (nameMatcher.find()) {
            name = nameMatcher.group(1);
        }

        // Парсим дату
        Pattern datePattern = Pattern.compile("\\((\\d{2}\\.\\d{2}\\.\\d{4})\\)");
        Matcher dateMatcher = datePattern.matcher(messageText);
        if (dateMatcher.find()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            date = LocalDate.parse(dateMatcher.group(1), formatter);
        }

        // Парсим название продукта и вес
        Pattern productPattern = Pattern.compile("Торт (.+) - \\d\\n- Выбор веса:\\n(\\d+)");
        Matcher productMatcher = productPattern.matcher(messageText);
        if (productMatcher.find()) {
            productName = productMatcher.group(1);
            weight = Float.parseFloat(productMatcher.group(2));
        }

        // Парсим адрес
        String addressPattern = "(Платёжный адрес\\s*\\n)([\\s\\S]+?)(\\+7[\\d]{10})";
        Pattern pattern = Pattern.compile(addressPattern);
        Matcher addressMatcher = pattern.matcher(messageText);

        if (addressMatcher.find()) {
            address = addressMatcher.group(2).trim();
        }

        // Парсим номер телефона
        Pattern phonePattern = Pattern.compile("(\\+\\d{11,12})");  // Поддерживает формат +7XXXXXXXXXX или +7XXXXXXXXXXX (для +7)
        Matcher phoneMatcher = phonePattern.matcher(messageText);
        if (phoneMatcher.find()) {
            phoneNumber = phoneMatcher.group(1);
        }

        String responseMessage = "Имя покупателя: " + name + "\n" +
                "Дата заказа: " + date + "\n" +
                "Название продукта: " + productName + "\n" +
                "Вес: " + weight + "\n" +
                "Адрес: " + address + "\n" +
                "Номер телефона: " + phoneNumber;
        return responseMessage;
    }
}
