package pl.matsuo.gitlab.data;

import java.util.Set;
import java.util.TreeSet;

/** Created by marek on 27.08.15. */
public class ProjectInfo {

  Set<String> branches = new TreeSet<>();

  public Set<String> getBranches() {
    return branches;
  }

  public void setBranches(Set<String> projects) {
    this.branches = projects;
  }
}
