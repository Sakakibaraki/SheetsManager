// package server;

import java.io.*;
import java.net.*;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.util.List;

public class InventryServer extends Thread {
  /* フィールド */
  //=============================================================
  public  int PORT             = 8080;
  public  ServerSocket socket  = null;
  private BufferedReader in    = null;
  private PrintWriter out      = null;
  public  String spreadSheetId = null;  // スプレッドシート名(簡易的な識別子)
  public  Spreadsheet sheet    = null;  // My Class(シートの詳細情報)

  /* コンストラクタ */
  //=============================================================
  public InventryServer(int port, Spreadsheet sheet) throws IOException {
    this.PORT = port;
    this.socket = new ServerSocket(PORT);
    this.sheet = sheet;
    this.spreadSheetId = sheet.getId();
  }

  /* オーバーライドメソッド */
  //=============================================================
  @Override
  public void run() {
    // 個別のスレッドでやってほしいところ
    //=============================================================
    Socket s = null;
    try {
      s = socket.accept(); // コネクション接続要求を待つ
      socket.close();
      this.in = new BufferedReader(
                  new InputStreamReader(s.getInputStream()));
      this.out = new PrintWriter(
                  new BufferedWriter(
                    new OutputStreamWriter(s.getOutputStream())), true);

      while (true) {
        String str = in.readLine();

        if (str == null || str.equals("END")) break;
        if (str.equals("!getSheetInfo")) {
          printSheetInfo();
        } else if (str.equals("!changeSheet")) {
          // シートの設定変更
          //=============================================================
          out.println(str);
          try {
            // データ範囲の受信
            String range = in.readLine();
            if (range == null) throw new IOException();
            System.out.println("range: " + range);
            // スプレッドシートの取得
            sheet = new Spreadsheet(this.sheet.service, this.sheet.getId(), range);
            out.println("!sheetFound");
          } catch (IOException e) {
            out.println("!sheetNotFound");
            System.out.println("Spreadsheet is not found");
          }

          // スプレッドシートの存在確認
          if (sheet.values == null || sheet.values.isEmpty()) {
            System.out.println("No data found.");
            throw new IOException();
          } else {
            System.out.println("Found data.");
          }

        } else if (str.equals("!checkSheet")) {
          // シートの内容確認
          //=============================================================
          out.println(str);
          try {
            if (sheet.values == null || sheet.values.isEmpty()) {
              out.println("!dataNotFound");
              System.out.println("No data found.");
              throw new IOException();
            } else {
              System.out.println("Found data.");
              out.println("!dataFound");

              // values獲得
              // values送信
              ValueRange response = sheet.service.spreadsheets().values()
                                            .get(sheet.getId(), sheet.range)
                                              .execute();
              List<List<Object>> values = response.getValues();
              for (List row : values) {
                for (Object data : row) {
                  out.println(data);
                }
                out.println("!nextRow");
              }
              out.println("!finish");
            }
          } catch (IOException e) {
            System.out.println("Spreadsheet data is not found");
          }

          // スプレッドシートの存在確認

        } else if (str.equals("!editValue")) {
          // シートの内容確認
          //=============================================================
          out.println(str);
          try {
            str = in.readLine();  // 値を書き換える場所を取得

            // 値の書き換え

            out.println("!dataChanged");
            System.out.println("changed data.");
          } catch (IOException e) {
            out.println("!dataNotChanged");
            System.out.println("No data changed.");
          }

        } else {
          System.out.println(Thread.currentThread().getName() + " reads " + str);
          out.println(str);
        }
      }

    } catch (IOException e) {
    } finally {
      System.out.println(Thread.currentThread().getName() + " is closing...");
      try {
        s.close();
      } catch (IOException e) {
      }
    }
  }
  @Override
  public void start() {
    System.out.println(Thread.currentThread().getName() + " starts.");
    super.start();
  }

  /* メソッド */
  //=============================================================
  // シートの情報を全て送信する
  public synchronized void printSheetInfo() {
    // System.out.println(sheet.getId());
    // System.out.println(sheet.getItemColumn());
    // System.out.println(sheet.getQuantityColumn());
    // System.out.println(sheet.getTopRow());
    out.println(sheet.getId());
    out.println(sheet.getItemColumn());
    out.println(sheet.getQuantityColumn());
    out.println(sheet.getTopRow());
  }
  // ソケットを改行を含まず送信する
  public synchronized void print(String str) {
    out.print(str);
  }
  // ソケットを改行を含んで送信する
  public synchronized void println(String str) {
    out.println(str);
  }
  // ソケットを受信する(文字列形式のみ)
  public synchronized String readLine() throws IOException {
    return in.readLine();
  }

}
