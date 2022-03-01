package pl.gpiwosz.tictactoe.model;

public class Message {
  public Message(String from, String text, String time) {

  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  private String from;
  private String text;
}
