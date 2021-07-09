// package client;

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class InventryClient {
  /* フィールド */
  //=============================================================
  private static final int PORT = 8080;
  private static final int QUIT = -1;
  private static final int HOME =  0;
  private static final int SET  =  1;
  private static final int CHECK=  2;
  private static final int EDIT =  3;
  private static final int SETFIN =  11;

  public static int status = HOME;
  public Client client;
  public Sheet sheet = new Sheet(null);
  public List<Order> orders = new ArrayList<Order>();

  /* コンストラクタ */
  //=============================================================
  public InventryClient(int port) throws IOException {
    this.client = new Client(port);
  }
  public InventryClient(int port, Sheet sh) throws IOException {
    this.client = new Client(port);
    this.sheet = sh;
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
      System.out.println("PORT  : " + pt);
      int port = Integer.parseInt(pt);
      ic = new InventryClient(port, ic.sheet);

      // コマンド待ち画面
      while (status != QUIT) {
        ic.home(sc);

        // System.out.print("Input : ");
        // String str = sc.nextLine();              // 入力の読み取り
        // ic.client.println(str);                  // スキャナーへの入力を送信
        // if (str.equals("END")) break;
        // String message = ic.client.readLine();
        // System.out.println("Server: " + message); // 受信した内容を標準出力
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
  // 画面関連
  // コマンド待機画面と遷移
  private void home(Scanner sc) throws IOException {
    status = HOME;
    System.out.print("\033[H\033[2J");
    System.out.println("----------------------------------------------------");
    System.out.println("##################      HOME      ##################");
    System.out.println("----------------------------------------------------");
    System.out.println("");
    System.out.println("                   S : シート設定");
    System.out.println("                   C : 在庫参照");
    System.out.println("                   E : 在庫入力");
    System.out.println("                   Q : 終了");
    System.out.println("");
    System.out.println("----------------------------------------------------");
      System.out.print(" コマンド : ");

    while (status == HOME) {
      char com = sc.next().charAt(0);
      if (com=='S' || com=='s') {
        setSheet(sc);
      } else if (com=='C' || com=='c') {
        ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
        ArrayList<String> row = new ArrayList<String>();
        try {
          client.println("!checkSheet");
          if (!client.readLine().equals("!checkSheet")) throw new IOException();

          client.println(sheet.getRange()); // データ範囲送信
          String str = client.readLine();

          if (str.equals("!dataFound")) {
            // values受信
            while (true) {
              str = client.readLine();
              if (str.equals("!finish")) break;
              if (str.equals("!nextRow")) {
                values.add(row);
                row = new ArrayList<String>();
              } else {
                row.add(str);
              }
            }

          } else if (str.equals("!dataNotFound")) {
            System.out.println("data is not found");
            throw new IOException();
          } else {
            System.out.println("unknown reply");
            throw new IOException();
          }
        } catch (IOException e) {
          throw new IOException();
        }
        checkSheet(sc, values);
      } else if (com=='E' || com=='e') {
        editSheet(sc);
      } else if (com=='Q' || com=='q') {
        quitApp();
      } else {
        home(sc);
      }
    }
  }
  // シート設定画面
  private void setSheet(Scanner sc) throws IOException {
    status = SET;
    System.out.print("\033[H\033[2J");
    System.out.println("----------------------------------------------------");
    System.out.println("##################      設定      ##################");
    System.out.println("----------------------------------------------------");
    System.out.println("   スプレッドシートID:");
    System.out.println("      " + sheet.getId());
    System.out.println();
    System.out.println("         シート名: " + sheet.getName());
    System.out.println("         品目名  : " + String.valueOf((char)(sheet.getItemColumn()+64)) + "列");
    System.out.println("         在庫数  : " + String.valueOf((char)(sheet.getQuantityColumn()+64)) + "列");
    System.out.println("         開始行  : " + sheet.getTopRow() + "行");
    System.out.println();
    System.out.println("                   E : 編集する");
    System.out.println("                   H : ホームに戻る");
    System.out.println();
    System.out.println("----------------------------------------------------");
      System.out.print(" コマンド : ");

    // コマンド受付
    while (status == SET) {
      char com = sc.next().charAt(0);
      if (com=='E' || com=='e') {
        // 設定したsheet値をserverに確認を取り, はねられたら再設定
        try {
          settingSheet(sc, 'o');
          client.println("!changeSheet");
          if (!client.readLine().equals("!changeSheet")) throw new IOException();
          client.println(sheet.getRange()); // データ範囲送信
          String str = client.readLine();
          if (str.equals("!sheetFound")) {
            System.out.println("sheet is found");
          } else if (str.equals("!sheetNotFound")) {
            System.out.println("sheet is not found");
            throw new IOException();
          } else {
            System.out.println("unknown reply");
            throw new IOException();
          }
        } catch (IOException e) {
          throw new IOException();
        }
        setSheet(sc);
      } else if (com=='H' || com=='h') {
        home(sc);
      } else {
        setSheet(sc);
      }
    }
  }
  // シート設定編集画面
  private void settingSheet(Scanner sc, char com) throws IOException {
    System.out.print("\033[H\033[2J");
    System.out.println("----------------------------------------------------");
    System.out.println("#################     設定編集     #################");
    System.out.println("----------------------------------------------------");
    System.out.println("   スプレッドシートID:");
    System.out.println("      " + sheet.getId());
    System.out.println();
    System.out.println("     [s] シート名: " + sheet.getName());
    System.out.println("     [i] 品目名  : " + String.valueOf((char)(sheet.getItemColumn()+64)) + "列  (A~Z)");
    System.out.println("     [n] 在庫数  : " + String.valueOf((char)(sheet.getQuantityColumn()+64)) + "列  (A~Z)");
    System.out.println("     [r] 開始行  : " + sheet.getTopRow() + "行  (1~)");
    System.out.println();
    System.out.println("             編集したい項目を選んでください");
    System.out.println("                  F : 設定終了");
    System.out.println();
    System.out.println("----------------------------------------------------");
    if (com=='S' || com=='s' || com=='シ') {
      System.out.print(" シート名 : ");
      sheet.setName(sc.next());
      settingSheet(sc, 'o');
    } else if (com=='I' || com=='i' || com=='品') {
      System.out.print(" 品目数   : ");
      char c = sc.next().charAt(0);
      if (c>64 && c<91) {
        sheet.setItemColumn((int)(c-64));
        settingSheet(sc, 'o');
      } else {
        settingSheet(sc, com);
      }
    } else if (com=='N' || com=='n' || com=='在') {
      System.out.print(" 在庫数   : ");
      char c = sc.next().charAt(0);
      if (c>64 && c<91) {
        sheet.setQuantityColumn((int)(c-64));
        settingSheet(sc, 'o');
      } else {
        settingSheet(sc, com);
      }
    } else if (com=='R' || com=='r' || com=='開') {
      System.out.print(" 開始行   : ");
      int n = sc.nextInt();
      if (n>0) {
        sheet.setTopRow(n);
        settingSheet(sc, 'o');
      } else {
        settingSheet(sc, com);
      }
    } else if (com=='F' || com=='f') {
      return;
    } else {
      System.out.print(" コマンド : ");
      com = sc.next().charAt(0);
      settingSheet(sc, com);
    }
  }
  // 在庫参照画面
  private void checkSheet(Scanner sc, ArrayList<ArrayList<String>> values) throws IOException {
    status = CHECK;
    System.out.print("\033[H\033[2J");
    System.out.println("----------------------------------------------------");
    System.out.println("##################     シート     ##################");
    System.out.println("----------------------------------------------------");
    System.out.println("   スプレッドシートID:");
    System.out.println("      " + sheet.getId());
    System.out.println();
    System.out.println("         シート名: " + sheet.getName() + " (" + sheet.getTopRow() + "行〜)" );
    System.out.println();
    // System.out.println("         " + String.valueOf((char)(sheet.getItemColumn()+64)) + " (品目)"
    //                                  + "                  "
    //                                   + String.valueOf((char)(sheet.getQuantityColumn()+64)) + " (在庫数)" );
    System.out.println();

    // ここにスプレッドシートのデータを貼る
    for (List<String> row : values) {
      System.out.print("           ");
      for (String data : row) System.out.print(data + "  ");
      System.out.println();
      // System.out.printf("         %s, %s\n", row.get(sheet.getItemColumn()), row.get(sheet.getQuantityColumn()));
    }


    System.out.println();
    // System.out.println("                   A : 前の10組");
    // System.out.println("                   D : 次の10組");
    System.out.println("                   H : ホームに戻る");
    System.out.println();
    System.out.println("----------------------------------------------------");
      System.out.print(" コマンド : ");

    while (status == CHECK) {
      char com = sc.next().charAt(0);
      // if (com=='A' || com=='a') {
      //   if (index>9) checkSheet(sc, index-10);
      //   else checkSheet(sc, index);
      // } else if (com=='D' || com=='d') {
      //   checkSheet(sc, index+10);
      // } else
      if (com=='H' || com=='h') {
        home(sc);
      } else {
        checkSheet(sc, values);
      }
    }
  }
  // 在庫入力画面
  private void editSheet(Scanner sc) throws IOException {
    status = EDIT;
    System.out.print("\033[H\033[2J");
    System.out.println("----------------------------------------------------");
    System.out.println("##################      編集      ##################");
    System.out.println("----------------------------------------------------");
    System.out.println("         シート名: " + sheet.getName());
    System.out.println("         品目名  : " + String.valueOf((char)(sheet.getItemColumn()+64)) + "列");
    System.out.println("         在庫数  : " + String.valueOf((char)(sheet.getQuantityColumn()+64)) + "列");
    System.out.println("         開始行  : " + sheet.getTopRow() + "行");
    System.out.println("                                          ");
    System.out.println("                   E : 編集する");
    System.out.println("                   H : ホームに戻る");
    System.out.println("                                          ");
    System.out.println("----------------------------------------------------");
      System.out.print(" コマンド : ");

    // シートの値を確認できる
    // その値を書き換えられる
    while (status == EDIT) {
      char com = sc.next().charAt(0);
      if (com=='E' || com=='e') {
        // System.out.print(" 列を入力 : ");
        // char col = sc.next().charAt(0);
        // System.out.print(" 行を入力 : ");
        // int row = sc.nextInt();
        // System.out.print(" 値を入力 : ");
        // String val = sc.next();

        // // 書き換えのメッセージを送る
        // try {
        //   client.println("!editValue");
        //   if (!client.readLine().equals("!editValue")) throw new IOException();

        //   // 書き換える値送信

        //   String str = client.readLine(); // 結果受信
        //   if (str.equals("!dataChanged")) {
        //   } else if (str.equals("!dataNotChanged")) {
        //     throw new IOException();
        //   } else {
        //     System.out.println("unknown reply");
        //     throw new IOException();
        //   }
        // } catch (IOException e) {
        //   throw new IOException();
        // }

        editSheet(sc);
      } else if (com=='H' || com=='h') {
        home(sc);
      } else {
        editSheet(sc);
      }
    }
  }
  // 終了
  private void quitApp() throws IOException {
    System.out.print("\033[H\033[2J");
    System.out.println("\n  プログラムは正常に終了しました\n");
    status = QUIT;
  }
  //=============================================================
  // クラス関連
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
