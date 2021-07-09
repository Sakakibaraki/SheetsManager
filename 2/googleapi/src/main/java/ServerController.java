// package server;
// 同一のポートに対するクライアントが複数存在するとき、一人しか読み取られない

import com.google.api.services.sheets.v4.Sheets;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerController {
  /* フィールド */
  //=============================================================
  private static final int PORT = 8080;
  public ServerSocket ss = null;
  public Set<InventryServer> servers =
      Collections.synchronizedSet(new HashSet<InventryServer>());

  /* コンストラクタ */
  //=============================================================
  public ServerController(ServerSocket ss) {
    this.ss = ss;
  }

  /* メソッド */
  //=============================================================
  // クライアントからの接続を受けてサーバーを新規作成する
  public InventryServer accept(Sheets service) throws IOException {
    Socket socket = null;
    InventryServer is = null;
    try {
      // コネクション接続要求を待つ
      socket = ss.accept();
      System.out.println("\nConnection accepted: " + socket);

      BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(
                          new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())), true);

      // スプレッドシートIDの受信
      System.out.println("Reading spreadsheet ID and range...");
      String id = in.readLine();
      if (id == null) throw new IOException();
      System.out.println("ID: " + id);
      // データ範囲の受信
      String range = in.readLine();
      if (range == null) throw new IOException();
      System.out.println("range: " + range);

      // スプレッドシートの取得
      Spreadsheet sheet = new Spreadsheet(service, id, range);
      System.out.println("Found spreadsheet successfully");

      // スプレッドシートの存在確認
      if (sheet.values == null || sheet.values.isEmpty()) {
        System.out.println("No data found.");
        throw new IOException();
      } else {
        System.out.println("Found data.");
      }

      // サーバーの割当
      //=============================================================
      // id を元に servers 内に同じ id のサーバーが存在するか調べる
      // id が一致するサーバーが存在したらそのインスタンスを渡す
      // 存在しなければサーバーのインスタンスを新規作成する
      int port = 0;
      for (InventryServer server : servers) {
        if (server.sheet.getId().equals(id)) {
          port = server.PORT;
          sheet = server.sheet;
          break;
        }
      }
      if (port > 0) {
        is = new InventryServer(port, sheet);
        System.out.println("sheet server exist.");
        System.out.println("Commit Server: " + is.socket);
      } else {
        is = new InventryServer(PORT+servers.size()+1, sheet);
        System.out.println("build new server.");
        System.out.println("New Server: " + is.socket);
      }

      try {
        is.start();
      } catch (IllegalThreadStateException e) {
      }

      // 割り当てたサーバーのポート番号を送信
      System.out.println(is.PORT);
      out.println(is.PORT);

      // System.out.println(is.sheetId() + ": log in");
    } catch (IOException e) {
      try {
        if (is != null) is.socket.close();
      } catch (IOException e2) {
      }
    } finally {
      try {
        if (socket != null) socket.close();
      } catch (IOException e) {
      }
    }
    return is;
  }
  // サーバーを全て廃棄する
  public void clear() throws IOException {
    for (InventryServer is: servers) {
      if (is != null) is.socket.close();
    }
    System.out.println("closed all server-sockets");
  }
  // サーバーソケットを廃棄する
  public void close() throws IOException {
    if (ss != null) ss.close();
  }
}
