package Omg.CakeBot.Service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GoogleSheetsService {
    private static final String APPLICATION_NAME = "CakeBot";
    private static final String TOKENS_DIRECTORY_PATH = "src/main/resources/tokens";
    private static final Collection<String> SCOPES = Collections.singleton(SheetsScopes.SPREADSHEETS);

    private Sheets sheetsService;
    private String spreadsheetId = "1v1mwUwGze_LdQpmytXgRujCURKVKd_GbDDxXj_ePoiI";

    public GoogleSheetsService() throws Exception {
        this.sheetsService = getSheetsService();
    }
    public Sheets getSheetsService() throws Exception {
        // Загружаем учетные данные
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(),
                new InputStreamReader(GoogleSheetsService.class.getResourceAsStream("/credentials.json")));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        Credential credential = flow.loadCredential("83005727004-raucrd07ccms7hub2i1lv0v0q0g0th2h.apps.googleusercontent.com");
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

}
