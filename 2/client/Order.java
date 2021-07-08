package client;

class Order {
  /* フィールド */
  private String item;
  private int quantity;

  /* コンストラクタ */
  public Order(String item, int quantity) {
    this.item = item;
    this.quantity = quantity;
  }

  /* メソッド */
  public void setItem(String item) {
    this.item = item;
  }
  public String getItem() {
    return this.item;
  }
  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
  public int getQuantity() {
    return this.quantity;
  }
}
