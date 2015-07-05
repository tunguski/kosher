package pl.matsuo.gitlab.hook;


/**
 * Created by marek on 04.07.15.
 */
public class PartialBuildInfo {


  String name;
  String status = "Pending";
  Integer executionResult;
  String log;
  // database id of object containing custom builder result data
  String idReport;


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
  public Integer getExecutionResult() {
    return executionResult;
  }
  public void setExecutionResult(Integer executionResult) {
    this.executionResult = executionResult;
  }
  public String getIdReport() {
    return idReport;
  }
  public void setIdReport(String idReport) {
    this.idReport = idReport;
  }
}

