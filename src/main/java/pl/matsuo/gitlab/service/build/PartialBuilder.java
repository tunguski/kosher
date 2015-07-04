package pl.matsuo.gitlab.service.build;

import pl.matsuo.gitlab.hook.PushEvent;


/**
 * Created by marek on 04.07.15.
 */
public interface PartialBuilder {


  String execute(PushEvent pushEvent);
}

