package pl.matsuo.gitlab.service.build.jekyll.model;


/**
 * Created by marek on 05.09.15.
 */
public class SiteConfig {


  String source;
  String destination;
  String styleRepository;
  String styleBranch;
  String styleDirectory;
  PageConfig page;
  ProjectConfig project;

  String layout;
  String highlighter;


  public String getSource() {
    return source;
  }
  public void setSource(String source) {
    this.source = source;
  }
  public String getDestination() {
    return destination;
  }
  public void setDestination(String destination) {
    this.destination = destination;
  }
  public String getStyleRepository() {
    return styleRepository;
  }
  public void setStyleRepository(String styleRepository) {
    this.styleRepository = styleRepository;
  }
  public String getStyleBranch() {
    return styleBranch;
  }
  public void setStyleBranch(String styleBranch) {
    this.styleBranch = styleBranch;
  }
  public String getStyleDirectory() {
    return styleDirectory;
  }
  public void setStyleDirectory(String styleDirectory) {
    this.styleDirectory = styleDirectory;
  }
  public PageConfig getPage() {
    return page;
  }
  public void setPage(PageConfig page) {
    this.page = page;
  }
  public String getLayout() {
    return layout;
  }
  public void setLayout(String layout) {
    this.layout = layout;
  }
  public String getHighlighter() {
    return highlighter;
  }
  public void setHighlighter(String highlighter) {
    this.highlighter = highlighter;
  }
  public ProjectConfig getProject() {
    return project;
  }
  public void setProject(ProjectConfig project) {
    this.project = project;
  }
}

