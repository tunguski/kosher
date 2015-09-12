package pl.matsuo.gitlab.service.gitlab;

import org.gitlab.api.GitlabAPI;


/**
 * Created by marek on 12.09.15.
 */
public class GitlabApiServiceImpl implements GitlabApiService {


  @Override
  public void comment(String markdownText) {
    GitlabAPI gitlabAPI = GitlabAPI.connect("gitlab", "test");
  }
}

