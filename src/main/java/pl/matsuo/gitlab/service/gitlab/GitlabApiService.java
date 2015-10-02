package pl.matsuo.gitlab.service.gitlab;


import pl.matsuo.gitlab.hook.PushEvent;

/**
 * Created by marek on 12.09.15.
 */
public interface GitlabApiService {


  void comment(PushEvent pushEvent, String markdownText);
}

