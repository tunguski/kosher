package pl.matsuo.gitlab.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.Database;

import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * Created by marek on 04.07.15.
 */
@Controller
public class GitLabWebHookController {


  @Autowired
  Database db;


  @RequestMapping(method = POST)
  public void pushEvent(@RequestBody PushEvent pushEvent) throws JsonProcessingException {
    db.put("push_event_" + System.currentTimeMillis(), pushEvent);
  }
}

