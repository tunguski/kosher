package pl.matsuo.gitlab.hook;


/**
 * Created by marek on 04.07.15.
 */
public class Repository {


  String name;
  String url;
  String description;
  String homepage;
  String git_http_url;
  String git_ssh_url;
  Integer visibility_level;


  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getHomepage() {
    return homepage;
  }
  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }
  public String getGit_http_url() {
    return git_http_url;
  }
  public void setGit_http_url(String git_http_url) {
    this.git_http_url = git_http_url;
  }
  public String getGit_ssh_url() {
    return git_ssh_url;
  }
  public void setGit_ssh_url(String git_ssh_url) {
    this.git_ssh_url = git_ssh_url;
  }
  public Integer getVisibility_level() {
    return visibility_level;
  }
  public void setVisibility_level(Integer visibility_level) {
    this.visibility_level = visibility_level;
  }
}

