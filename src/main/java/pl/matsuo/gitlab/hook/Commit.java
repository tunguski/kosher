package pl.matsuo.gitlab.hook;

import java.util.Date;


/**
 * Created by marek on 04.07.15.
 */
public class Commit {


  String id;
  String message;
  Date timestamp;
  String url;
  Author author;


  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }
  public Date getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public Author getAuthor() {
    return author;
  }
  public void setAuthor(Author author) {
    this.author = author;
  }
}

