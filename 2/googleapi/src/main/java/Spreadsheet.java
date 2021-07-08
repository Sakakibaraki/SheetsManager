// package googleapi.build.classes.java.main;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

class Spreadsheet {
  private final Sheets service;
  private String spreadsheetId;
  private String range;
  public ValueRange response;
  public List<List<Object>> values;
  // private int COLUMN_OF_ITEMS = 1;
  // private int COLUMN_OF_QUANTITIES = 2;
  // private int ROW_OF_TOP = 2;

  /* コンストラクタ */
  public Spreadsheet(Sheets service, String Id, String range) throws IOException {
    this.service = service;
    this.spreadsheetId = Id;
    this.range = range;
    this.response = service.spreadsheets().values().get(spreadsheetId, range).execute();
    this.values = this.response.getValues();
  }

  /* メソッド */
  public void setId(String id) {
    this.spreadsheetId = id;
  }
  public String getId() {
    return this.spreadsheetId;
  }
  // public void setItemColumn(int itemColumn) {
  //   this.COLUMN_OF_ITEMS = itemColumn;
  // }
  // public int getItemColumn() {
  //   return this.COLUMN_OF_ITEMS;
  // }
  // public void setQuantityColumn(int quantityColumn) {
  //   this.COLUMN_OF_QUANTITIES = quantityColumn;
  // }
  // public int getQuantityColumn() {
  //   return this.COLUMN_OF_QUANTITIES;
  // }
  // public void setTopRow(int topRow) {
  //   this.ROW_OF_TOP = topRow;
  // }
  // public int getTopRow() {
  //   return this.ROW_OF_TOP;
  // }
}
