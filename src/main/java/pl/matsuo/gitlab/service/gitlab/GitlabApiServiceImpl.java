package pl.matsuo.gitlab.service.gitlab;

import org.gitlab.api.GitlabAPI;
import pl.matsuo.gitlab.hook.PushEvent;

/** Created by marek on 12.09.15. */
public class GitlabApiServiceImpl implements GitlabApiService {

  @Override
  public void comment(PushEvent pushEvent, String markdownText) {
    GitlabAPI gitlabAPI = GitlabAPI.connect("gitlab", "test");

    // POST /projects/:id/repository/commits/:sha/comments
    // gitlabAPI.createNote();
  }
}
