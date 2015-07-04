package pl.matsuo.gitlab.hook;


/**
 * Created by marek on 04.07.15.
 */
public class PartialBuildInfo {

  String name;
  String status = "Pending";
  String log;


  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
  public String getLog() {
    return log;
  }
  public void setLog(String log) {
    this.log = log;
  }
}

