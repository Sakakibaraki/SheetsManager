package client;

import java.io.*;
import java.net.*;

class Client {
  /* フィールド */
  //=============================================================
  private int PORT;
  private final InetAddress addr;
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;

  /* コンストラクタ */
  //=============================================================
  public Client(int port) throws IOException {
    this.PORT = port;
    this.addr = InetAddress.getByName("localhost");
    this.socket = new Socket(addr, PORT);
    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
  }

  /* メソッド */
  //=============================================================
  public void setPort(int port) throws IOException {
    this.PORT = port;
    this.socket = new Socket(addr, PORT);
    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
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
  // サーバーを閉じる
  public void close() throws IOException {
    socket.close();
  }
}
