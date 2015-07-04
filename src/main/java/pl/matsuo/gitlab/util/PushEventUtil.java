package pl.matsuo.gitlab.util;


import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.hook.Repository;

/**
 * Created by marek on 04.07.15.
 */
public class PushEventUtil {


  public static String getUser(PushEvent pushEvent) {
    Repository repository = pushEvent.getRepository();
    String url = repository.getUrl();
    if (url.startsWith("git@")) {
      String[] splitted = url.split(":")[1].replaceAll("\\.git", "").split("/");
      return splitted[0];
    } else {
      String[] splitted = url.replaceAll("\\.git", "").split("/");
      return splitted[splitted.length - 2];
    }
  }


  public static String getRepository(PushEvent pushEvent) {
    Repository repository = pushEvent.getRepository();
    String url = repository.getUrl();
    if (url.startsWith("git@")) {
      String[] splitted = url.split(":")[1].replaceAll("\\.git", "").split("/");
      return splitted[1];
    } else {
      String[] splitted = url.replaceAll("\\.git", "").split("/");
      return splitted[splitted.length - 1];
    }
  }


  public static String getRef(PushEvent pushEvent) {
    String[] ref = pushEvent.getRef().split("/");
    return ref[ref.length - 1];
  }
}

