package Omg.CakeBot.Service;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
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
import java.util.Collections;
import java.util.List;

public class GoogleSheetsCore {

    private static final String APPLICATION_NAME = "Google Sheets API Java"; // не менять, 404
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String SPREADSHEET_ID = "1YqZzWReUmmlqtpyBY50tCr8bh4Mckye1rYTZTJeMutY"; // айди нащей таблички
    private static final String RANGE = "Sheet1!A1:D10"; // Диапазон данных, название листа должно совпадать в таблице, иначе ошибка 400

    public static void main(String[] args) throws IOException, GeneralSecurityException { // вожможно надо убрать мейн
        // Загружаем учетку сервисного аккаунта, просто /name не работает, надо разобраться поч
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/excelbot-435816-c87e69d235e4.json"))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        // HTTP адаптер от гугла, не трогать
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();

        ValueRange response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, RANGE)
                .execute();
        List<List<Object>> values = response.getValues();

        // Читаем данные с Google Sheets выводим в консоль
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("Data from the sheet:");
            for (List row : values) {
                System.out.println(row);
            }
        }
    }
}
