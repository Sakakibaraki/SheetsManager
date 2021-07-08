// package server;

import java.io.*;
import java.net.*;

public class InventryServer extends Thread {
  /* フィールド */
  //=============================================================
  private static int size = 0;
  private int PORT = 8080;
  public ServerSocket socket = null;
  private BufferedReader in;
  private PrintWriter out;
  private String spreadSheetId;
  private Spreadsheet sheet;

  /* コンストラクタ */
  //=============================================================
  public InventryServer(int port, Spreadsheet sheet) throws IOException{
    super();
    size++;
    this.PORT = port + size;
    this.socket = new ServerSocket(PORT);
    this.spreadSheetId = sheet.getId();
    this.sheet = sheet;
    this.start();
  }

  /* メソッド */
  //=============================================================
  @Override
  public void run(){
    // スプレッドシートの存在確認
    if (sheet.values == null || sheet.values.isEmpty()) {
      System.out.println("No data found.");
    } else {
      System.out.println("Found SpreadSheet.");
    }

    Socket s = null;
    try {
      s = socket.accept(); // コネクション接続要求を待つ
      this.in = new BufferedReader(
                  new InputStreamReader(s.getInputStream()));
      this.out = new PrintWriter(
                  new BufferedWriter(
                    new OutputStreamWriter(s.getOutputStream())), true);
      while (true) {
        String str = in.readLine();
        if (str == null || str.equals("END")) break;
        System.out.println(Thread.currentThread().getName() + " reads " + str);
        out.println("server : " + str);
      }
    } catch (IOException ioe) {
    } finally {
      System.out.println(Thread.currentThread().getName() + " is closing...");
      try {
        s.close();
      } catch (IOException e) {
      }
    }
  }
  public int getSize() {
    return size;
  }
  public String sheetId() {
    return this.spreadSheetId;
  }
  // ソケットを改行を含まず送信する
  public void print(String str) {
    out.print(str);
  }
  // ソケットを改行を含んで送信する
  public void println(String str) {
    out.println(str);
  }
  // ソケットを受信する
  public String readLine() throws IOException {
    return in.readLine();
  }

}
