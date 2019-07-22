package pl.matsuo.gitlab.data;

import java.util.Set;
import java.util.TreeSet;

/** Created by marek on 27.08.15. */
public class UserInfo {

  Set<String> projects = new TreeSet<>();

  public Set<String> getProjects() {
    return projects;
  }

  public void setProjects(Set<String> projects) {
    this.projects = projects;
  }
}
