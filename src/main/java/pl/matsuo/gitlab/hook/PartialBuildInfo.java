package pl.matsuo.gitlab.hook;

/** Created by marek on 04.07.15. */
public class PartialBuildInfo {

  String name;
  String status = "Pending";
  Integer executionResult;

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

  public Integer getExecutionResult() {
    return executionResult;
  }

  public void setExecutionResult(Integer executionResult) {
    this.executionResult = executionResult;
  }
}
