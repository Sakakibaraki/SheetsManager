package client;

import java.io.*;
import java.util.Scanner;

public class InventryClient {
  /* フィールド */
  //=============================================================
  public int command;
  public Client client;
  public Sheet sheet = new Sheet(null);
  // public List<Order> orders;

  /* コンストラクタ */
  //=============================================================
  public InventryClient(int port) throws IOException {
    this.client = new Client(port);
  }

  /* mainメソッド */
  //=============================================================
  public static void main(String[] args) throws IOException {
    InventryClient ic = new InventryClient(8080);
    Scanner sc = new Scanner(System.in);

    // スプレッドシートを登録する -> ポート番号を受け取る
    System.out.print("Input Spreadsheet ID: ");
    String id = sc.nextLine();            // スプレッドシートID入力
    ic.client.println(id);                // スプレッドシートID送信
    ic.setSheet(id);                      // スプレッドシート設定
    System.out.print("Input Spreadsheet range: ");
    String range = sc.nextLine();         // データ範囲入力
    ic.client.println(range);             // データ範囲送信
    // 返ってきたポート番号でポート番号を更新する
    int port = Integer.parseInt(ic.client.readLine());
    ic.client.setPort(port);

    // コマンド待ち画面
    try {
      while (true) {
        System.out.print("Input :");
        String str = sc.nextLine();               // 入力の読み取り
        ic.client.println(str);                   // スキャナーへの入力を送信
        if (str.equals("END")) break;
        String message = ic.client.readLine();
        System.out.println("Server:" + message); // 受信した内容を標準出力
      }
    } catch (IOException e) {
      System.out.println(e);
    } finally {
      ic.client.close();
      sc.close();
    }
  }

  /* メソッド */
  //=============================================================
  // サーバーにsheetが存在するかどうか
  public void canAccess() {
  }
  // シートの設定をする
  public void setSheet(String id) throws IOException {
    sheet.setId(id);
    // 初期値
    // int COLUMN_OF_ITEMS = 1;
    // int COLUMN_OF_QUANTITIES = 2;
    // int ROW_OF_TOP = 2;
    // // 入力
    // Scanner sc = new Scanner(System.in);
    // if (/*hasSheet()*/false) {
      // クライアントの対象とするシートが存在する場合, 品目列や在庫数列を編集可能
      // System.out.println("シート設定編集");
      //
      // System.out.println("----現在の設定----");
      // System.out.println("ID : " + sheet.getId());
      // System.out.println("Item Column : " + sheet.getItemColumn());
      // System.out.println("Quantity Column : " + sheet.getQuantityColumn());
      // System.out.println("Top Row : " + sheet.getTopRow());
      //
      // System.out.println("-----設定-----");
      // System.out.print("ID : " + sheet.getId());
      // System.out.print("Item Column : "); sheet.setItemColumn(sc.nextInt());
      // System.out.print("Quantity Column : "); sheet.setQuantityColumn(sc.nextInt());
      // System.out.print("Top Row : "); sheet.setTopRow(sc.nextInt());
    // } else {
      // クライアントの対象とするシートが存在しない場合, 初期設定
      // System.out.println("----新規作成----");
      // サーバーコントローラに取り次ぎサーバーのポート番号を獲得
      // client.println(spreadsheetId);                  // サーバーにスプレッドシートIDを提出
      // int port = Integer.parseInt(client.readLine()); // サーバーは応答としてポート番号を返す
      // client = new Client(port);                      // クライアントのソケットを更新
      // クライアントのポート番号に登録
      // sheet.setId(str);
      // System.out.println("ID : " + spreadsheetId);
      // System.out.print("Item Column : "); COLUMN_OF_ITEMS = sc.nextInt();
      // System.out.print("Quantity Column : "); COLUMN_OF_QUANTITIES = sc.nextInt();
      // System.out.print("Top Row : "); ROW_OF_TOP = sc.nextInt();
      // sheet = new Sheet(spreadsheetId, COLUMN_OF_ITEMS, COLUMN_OF_QUANTITIES, ROW_OF_TOP);
    // }
    // System.out.println("----作成終了----");
  }
  // シートの情報を得る
  public void getSheetInfo(){
    System.out.println("Sheet ID: " + sheet.getId());
  //   if (hasSheet()) {
  //     System.out.println("ID : " + sheet.getId());
  //     System.out.println("Item Column : " + sheet.getItemColumn());
  //     System.out.println("Quantity Column : " + sheet.getQuantityColumn());
  //     System.out.println("Top Row : " + sheet.getTopRow());
  //   } else {
  //     System.out.println("ERROR: No sheet is registered.");
  //   }
  // }
  // シートが設定されているかどうか
  // public int hasSheet(){
  //   if (sheet==null) {
  //     return 0;
  //   } else {
  //     return 1;
  //   }
  }
  // シートを閲覧し, 新しい品目を追加
  public void addItem(){
  }
  // シートを閲覧し, 指定した品目の行を削除
  public void removeItem(){
  }
}
