package pl.matsuo.gitlab.hook;

import java.util.List;

/**
 * Created by marek on 04.07.15.
 */
public class AbstractEvent {


  String object_kind;
  String before;
  String after;
  String ref;
  Integer user_id;
  String user_name;
  String user_email;
  Integer project_id;
  Repository repository;
  List<Commit> commits;
  Integer total_commits_count;


  public String getObject_kind() {
    return object_kind;
  }
  public void setObject_kind(String object_kind) {
    this.object_kind = object_kind;
  }
  public String getBefore() {
    return before;
  }
  public void setBefore(String before) {
    this.before = before;
  }
  public String getAfter() {
    return after;
  }
  public void setAfter(String after) {
    this.after = after;
  }
  public String getRef() {
    return ref;
  }
  public void setRef(String ref) {
    this.ref = ref;
  }
  public Integer getUser_id() {
    return user_id;
  }
  public void setUser_id(Integer user_id) {
    this.user_id = user_id;
  }
  public String getUser_name() {
    return user_name;
  }
  public void setUser_name(String user_name) {
    this.user_name = user_name;
  }
  public String getUser_email() {
    return user_email;
  }
  public void setUser_email(String user_email) {
    this.user_email = user_email;
  }
  public Integer getProject_id() {
    return project_id;
  }
  public void setProject_id(Integer project_id) {
    this.project_id = project_id;
  }
  public Repository getRepository() {
    return repository;
  }
  public void setRepository(Repository repository) {
    this.repository = repository;
  }
  public List<Commit> getCommits() {
    return commits;
  }
  public void setCommits(List<Commit> commits) {
    this.commits = commits;
  }
  public Integer getTotal_commits_count() {
    return total_commits_count;
  }
  public void setTotal_commits_count(Integer total_commits_count) {
    this.total_commits_count = total_commits_count;
  }
}
