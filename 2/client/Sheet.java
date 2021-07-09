// package client;

import java.util.Scanner;

class Sheet {
  private String spreadsheetId = null;
  private String sheetName = null;
  private int COLUMN_OF_ITEMS = 1;
  private int COLUMN_OF_QUANTITIES = 2;
  private int ROW_OF_TOP = 2;

  /* コンストラクタ */
  public Sheet(String id) {
    this.spreadsheetId = id;
  }
  public Sheet(String id, String name, int itemColumn, int quantityColumn, int topRow) {
    this.spreadsheetId = id;
    this.sheetName = name;
    this.COLUMN_OF_ITEMS = itemColumn;
    this.COLUMN_OF_QUANTITIES = quantityColumn;
    this.ROW_OF_TOP = topRow;
  }

  /* メソッド */
  public void setSheet(Scanner sc) {
    System.out.print("Input sheet ID  : ");
    spreadsheetId = sc.nextLine();
    System.out.print("Input sheet name: ");
    sheetName = sc.nextLine();
    System.out.print("Input Column number of Items    : ");
    COLUMN_OF_ITEMS = Integer.parseInt(sc.nextLine());
    System.out.print("Input Column number of Quantitys: ");
    COLUMN_OF_QUANTITIES = Integer.parseInt(sc.nextLine());
    System.out.print("Input    Row number of Top      : ");
    ROW_OF_TOP = Integer.parseInt(sc.nextLine());
  }
  public String getRange() {
    String str = null;
    str = sheetName + "!"
        + String.valueOf((char)(64 + COLUMN_OF_ITEMS))
        + String.valueOf(ROW_OF_TOP) + ":"
        + String.valueOf((char)(64 + COLUMN_OF_QUANTITIES));
    System.out.println(str);
    return str;
  }
  public void setId(String id) {
    this.spreadsheetId = id;
  }
  public String getId() {
    return this.spreadsheetId;
  }
  public void setName(String name) {
    sheetName = name;
  }
  public String getName() {
    return sheetName;
  }
  public void setItemColumn(int itemColumn) {
    this.COLUMN_OF_ITEMS = itemColumn;
  }
  public int getItemColumn() {
    return this.COLUMN_OF_ITEMS;
  }
  public void setQuantityColumn(int quantityColumn) {
    this.COLUMN_OF_QUANTITIES = quantityColumn;
  }
  public int getQuantityColumn() {
    return this.COLUMN_OF_QUANTITIES;
  }
  public void setTopRow(int topRow) {
    this.ROW_OF_TOP = topRow;
  }
  public int getTopRow() {
    return this.ROW_OF_TOP;
  }

}
