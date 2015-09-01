package pl.matsuo.gitlab.service.build.jekyll;


import pl.matsuo.gitlab.hook.PushEvent;

/**
 * Created by marek on 29.08.15.
 */
public interface JekyllGenerateTemplateService {


  void generateTemplate(PushEvent pushEvent, JekyllProperties properties);
}

