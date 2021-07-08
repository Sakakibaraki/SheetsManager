import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

// import java.io.FileNotFoundException;
// import java.io.IOException;
// import java.io.InputStream;
// import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
// import java.util.Collections;
// import java.util.List;
import java.io.*;
import java.net.*;
import java.util.*;

class Application {
  private static final int PORT = 8080;
  private static final String APPLICATION_NAME = "SheetsManager";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "tokens";
  private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
    // Load client secrets.
    InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
        throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    }
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  public static void main(String... args) throws IOException, GeneralSecurityException {
    // GoogleAPIサービスの利用宣言
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    Sheets service = new Sheets.Builder(HTTP_TRANSPORT,JSON_FACTORY,getCredentials(HTTP_TRANSPORT))
                                  .setApplicationName(APPLICATION_NAME)
                                    .build();
    System.out.println(service.DEFAULT_BASE_URL);

    ServerController controller = new ServerController();
    System.out.println("\nSheets-Manager Server start");

    // サーバーの起動
    try {
      controller.setServerSocket(new ServerSocket(PORT));
      System.out.println("Started: " + controller.getServerSocket() + "\n");
      while (true) {
        // クライアントから接続を受けてサーバーを新規追加する
        controller.add(controller.accept(service));
      }
    } catch (IOException e) {
      System.out.println(e);
    } finally {
      try {
        controller.clear();                               // サーバー群を全て閉じる
        if (controller.getServerSocket() != null) controller.getServerSocket().close(); // メインサーバーを閉じる
      } catch (IOException e) {
        System.out.println(e);
      }
    } //finally
  }
}
