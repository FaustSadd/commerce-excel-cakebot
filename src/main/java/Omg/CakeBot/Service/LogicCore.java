package Omg.CakeBot.Service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogicCore {
    private String name;
    private LocalDate date;
    private String productName;
    private float weight;
    private String address;
    private String phoneNumber;
    GoogleSheetsCore googleSheetsCore = new GoogleSheetsCore();  // Экземпляр класса для работы с Google Sheets


    private static final String SPREADSHEET_ID = "1YqZzWReUmmlqtpyBY50tCr8bh4Mckye1rYTZTJeMutY";
    private static final String RANGE = "Sheet1!A1:F1";  // Укажите реальный диапазон ячеек в таблице


    public String getStandartReceipt(String messageText) {
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

        // Парсим название продукта
        Pattern productPattern = Pattern.compile("Торт (.+?) - \\d|(.+?) \\| \\d");
        Matcher productMatcher = productPattern.matcher(messageText);
        if (productMatcher.find()) {
            productName = productMatcher.group(1) != null ? productMatcher.group(1) : productMatcher.group(2);
        }

        // Парсим адрес или самовывоз
        String addressPattern = "(Платёжный адрес\\s*\\n)([\\s\\S]+?)(\\d{10,12})";
        Pattern pattern = Pattern.compile(addressPattern);
        Matcher addressMatcher = pattern.matcher(messageText);
        if (addressMatcher.find()) {
            address = addressMatcher.group(2).trim();
        } else {
            // Парсим самовывоз
            Pattern pickupPattern = Pattern.compile("(Самовывоз.+)");
            Matcher pickupMatcher = pickupPattern.matcher(messageText);
            if (pickupMatcher.find()) {
                address = pickupMatcher.group(1).trim();
            }
        }

        // Парсим номер телефона
        Pattern phonePattern = Pattern.compile("(\\+?\\d{10,12})");
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

        // Попробуем записать данные в Google Sheets через try catch, так как необходимо обработать исключение
        try {
            writeToGoogleSheets();  // Вызов метода записи в таблицу
        } catch (Exception e) {
            e.printStackTrace();  // Вывод ошибки в консоль
        }
        return responseMessage;
    }

    // Метод для записи данных в Google Sheets
    public void writeToGoogleSheets() throws Exception {
        // Данные в одной строке
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(name, date.toString(), productName, weight, address, phoneNumber)
        );

        System.out.println("Записываем данные: " + values);

        // Получаем экземпляр Sheets через googleSheetsCore
        Sheets service = googleSheetsCore.Sheets();

        // Создаем объект ValueRange и передаем данные
        ValueRange body = new ValueRange().setValues(values);
        System.out.println("Диапазон: " + RANGE + ", Spreadsheet ID: " + SPREADSHEET_ID);

        // Обновляем таблицу Google Sheets
        UpdateValuesResponse result = service.spreadsheets().values()
                .update(SPREADSHEET_ID, RANGE, body)
                .setValueInputOption("RAW")
                .execute();

        // Выводим результат обновления
        System.out.println(result.getUpdatedCells() + " ячеек обновлено.");
    }
}
