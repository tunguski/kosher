package pl.matsuo.gitlab.service;

import org.eclipse.jgit.api.Git;

/**
 * Created by marek on 04.07.15.
 */
public interface GitRepositoryService {


  Git checkout(String userName, String projectName, String refName, String uri);
}
