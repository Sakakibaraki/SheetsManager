// package server;

import java.io.*;
import java.net.*;

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
    System.out.println("hello1");
    this.PORT = port;
    System.out.println("hello2");
    this.socket = new ServerSocket(PORT);
    System.out.println("hello3");
    this.sheet = sheet;
    System.out.println("hello4");
    this.spreadSheetId = sheet.getId();
    System.out.println("hello5");
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
        } else {
          System.out.println(Thread.currentThread().getName() + " reads " + str);
          out.println("server : " + str);
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
