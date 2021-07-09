// package client;

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class InventryClient {
  /* フィールド */
  //=============================================================
  private static final int PORT = 8080;
  public int command;
  public Client client;
  public Sheet sheet = new Sheet(null);
  public List<Order> orders = new ArrayList<Order>();

  /* コンストラクタ */
  //=============================================================
  public InventryClient(int port) throws IOException {
    this.client = new Client(port);
  }

  /* mainメソッド */
  //=============================================================
  public static void main(String[] args) {
    InventryClient ic = null;
    Scanner sc = null;

    try {
      ic = new InventryClient(PORT);
      sc = new Scanner(System.in);

      // スプレッドシートを登録する -> ポート番号を受け取る
      ic.sheet.setSheet(sc);                  // スプレッドシート初期設定
      ic.client.println(ic.sheet.getId());    // スプレッドシートID送信
      ic.client.println(ic.sheet.getRange()); // データ範囲送信

      // 返ってきたポート番号でポート番号を更新する
      String pt = ic.client.readLine();
      if (pt == null) throw new NullPointerException();
      System.out.println("PORT: " + pt);
      int port = Integer.parseInt(pt);
      ic = new InventryClient(port);

      // スプレッドシート情報をサーバーのものに合わせる
      // ic.getSheetInfo();

      // コマンド待ち画面
      while (true) {
        System.out.print("Input :");
        String str = sc.nextLine();              // 入力の読み取り
        ic.client.println(str);                  // スキャナーへの入力を送信

        if (str.equals("END")) break;

        String message = ic.client.readLine();
        System.out.println("Server:" + message); // 受信した内容を標準出力
      }

    } catch (IOException ioe) {
      System.out.println(ioe);
    } catch (NullPointerException npe) {
      System.out.println(npe);
    } finally {
      try {
        if (ic!=null) ic.client.close();
        if (sc!=null) sc.close();
      } catch (IOException e) {
      }
    }

  }

  /* メソッド */
  //=============================================================
  // サーバーにsheetが存在するかどうか
  public void canAccess() {
  }
  // サーバーのシートの情報を得る
  public void getSheetInfo() throws IOException{
    client.println("!getSheetInfo");
    sheet.setName(client.readLine());
    sheet.setItemColumn(Integer.parseInt(client.readLine()));
    sheet.setQuantityColumn(Integer.parseInt(client.readLine()));
    sheet.setTopRow(Integer.parseInt(client.readLine()));
    System.out.println("Sheet ID: " + sheet.getId());
    System.out.println("server sheet name                : " + sheet.getName());
    System.out.println("server Column number of Items    : " + sheet.getItemColumn());
    System.out.println("server Column number of Quantitys: " + sheet.getQuantityColumn());
    System.out.println("server    Row number of Top      : " + sheet.getTopRow());
  }
  // シートを閲覧し, 新しい品目を追加
  public void addItem(){
  }
  // シートを閲覧し, 指定した品目の行を削除
  public void removeItem(){
  }
}
