package client;

class Sheet {
  private String spreadsheetId = null;
  private int COLUMN_OF_ITEMS = 1;
  private int COLUMN_OF_QUANTITIES = 2;
  private int ROW_OF_TOP = 2;

  /* コンストラクタ */
  public Sheet(String id) {
    this.spreadsheetId = id;
  }
  public Sheet(String id, int itemColumn, int quantityColumn, int topRow) {
    this.spreadsheetId = id;
    this.COLUMN_OF_ITEMS = itemColumn;
    this.COLUMN_OF_QUANTITIES = quantityColumn;
    this.ROW_OF_TOP = topRow;
  }

  /* メソッド */
  public void setId(String id) {
    this.spreadsheetId = id;
  }
  public String getId() {
    return this.spreadsheetId;
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
