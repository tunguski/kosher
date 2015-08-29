package pl.matsuo.gitlab.data;

import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by marek on 04.07.15.
 */
public class BuildInfo {


  String id;
  BuildStatus status = BuildStatus.Pending;
  Map<String, PartialBuildInfo> partialStatuses = new HashMap<String, PartialBuildInfo>();
  PushEvent pushEvent;


  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public BuildStatus getStatus() {
    return status;
  }
  public void setStatus(BuildStatus status) {
    this.status = status;
  }
  public Map<String, PartialBuildInfo> getPartialStatuses() {
    return partialStatuses;
  }
  public PushEvent getPushEvent() {
    return pushEvent;
  }
  public void setPushEvent(PushEvent pushEvent) {
    this.pushEvent = pushEvent;
  }
}

