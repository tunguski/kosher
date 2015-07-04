package pl.matsuo.gitlab.service;

import pl.matsuo.gitlab.hook.PushEvent;


/**
 * Created by marek on 04.07.15.
 */
public interface PartialBuilder {


  String execute(PushEvent pushEvent);
}

