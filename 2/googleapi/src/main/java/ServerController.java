// package server;

import com.google.api.services.sheets.v4.Sheets;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerController {
  /* フィールド */
  //=============================================================
  private static final int PORT = 8080;
  private int size = 0;
  private ServerSocket ss = null;
  private Set<InventryServer> servers =
      Collections.synchronizedSet(new HashSet<InventryServer>());

  /* メソッド */
  //=============================================================
  // クライアントからの接続を受けてサーバーを新規作成する
  InventryServer accept(Sheets service) throws IOException {
    Socket socket = null;
    InventryServer is = null;
    try {
      socket = ss.accept();               // コネクション接続要求を待つ
      System.out.println("\nConnection accepted: " + socket);

      BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(
                          new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())), true);
      System.out.println("reading spreadsheet ID...");
      String id = in.readLine();          // スプレッドシートIDの受信
      System.out.println("ID: " + id);
      System.out.println("reading spreadsheet range...");
      // String range = in.readLine();       // スプレッドシートの読み取る範囲を受信
      String range = null;
      while ((range=in.readLine()) == null);
      System.out.println("range: " + range);

      // スプレッドシートの取得
      Spreadsheet sheet = new Spreadsheet(service, id, range);
      System.out.println("Found spreadsheet successfully");

      // サーバーの割当
      //=============================================================
      // id を元に servers 内に同じ id のサーバーが存在するか調べる
      // まずサーバーのインスタンスを新規作成する
      // id が一致するサーバーが存在したらそのインスタンスを渡す
      is = new InventryServer(PORT, sheet);  // サーバーの新規作成
      for (InventryServer server : servers) {
        if (server.sheetId().equals(id)) {
          is = server;
          System.out.println("server exist.");
        }
      }

      out.println(PORT + is.getSize());   // 指定のポート番号の送信
      System.out.println("New Server: " + is.socket);
      System.out.println(is.sheetId() + ": log in");
    } catch (IOException ie) {
    } finally {
      socket.close();
    }
    return is;
  }
  // サーバーを追加する
  void add(InventryServer is) {
    servers.add(is);
    size++;
  }
  // サーバーを全て廃棄する
  void clear() throws IOException {
    if (size>0) {
      for (InventryServer s: servers) {
        s.socket.close();
      }
      System.out.println("closed all server-sockets");
    }
  }
  void setServerSocket(ServerSocket ss) {
    this.ss = ss;
  }
  ServerSocket getServerSocket() {
    return ss;
  }
}
