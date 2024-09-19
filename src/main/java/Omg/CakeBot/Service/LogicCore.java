package Omg.CakeBot.Service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
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
    private int rowIndex = 2;  // Изначально предполагаем, что свободная строка найдется не ниже 2-й

    private static final String APPLICATION_NAME = "Google Sheets API Java";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String SPREADSHEET_ID = "1YqZzWReUmmlqtpyBY50tCr8bh4Mckye1rYTZTJeMutY";

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/excelbot-435816-753e1ba5d64a.json"))
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets"));
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public String getStandartReceipt(String messageText) {
        // Парсим данные из сообщения
        parseOrderDetails(messageText);

        String responseMessage = "Имя покупателя: " + name + "\n" +
                "Дата заказа: " + date + "\n" +
                "Название продукта: " + productName + "\n" +
                "Вес: " + weight + "\n" +
                "Адрес: " + address + "\n" +
                "Номер телефона: " + phoneNumber;

        try {
            findAvailableRow();  // Найти первую свободную строку перед записью
            writeToGoogleSheets();  // Записать данные в Google Sheets
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseMessage;
    }

    private void parseOrderDetails(String messageText) {
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
    }

    private void writeToGoogleSheets() throws Exception {
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(name, date.toString(), productName, weight, address, phoneNumber)
        );

        Sheets service = getSheetsService();
        String range = String.format("Sheet1!A%d:F%d", rowIndex, rowIndex);
        ValueRange body = new ValueRange().setValues(values);

        UpdateValuesResponse result = service.spreadsheets().values()
                .update(SPREADSHEET_ID, range, body)
                .setValueInputOption("RAW")
                .execute();

        System.out.println(result.getUpdatedCells() + " ячеек обновлено.");
    }

    private void findAvailableRow() throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        String rangeTemplate = "Sheet1!A%d:F%d";
        int rowIndex = 2;  // начинаем с 2-й строки

        while (true) {
            String range = String.format(rangeTemplate, rowIndex, rowIndex);
            ValueRange response = service.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();
            List<List<Object>> values = response.getValues();

            if (values == null || values.isEmpty() || values.get(0).isEmpty()) {
                this.rowIndex = rowIndex;
                break;  // Выход из цикла после нахождения первой пустой строки
            }
            rowIndex++;
        }
    }
}