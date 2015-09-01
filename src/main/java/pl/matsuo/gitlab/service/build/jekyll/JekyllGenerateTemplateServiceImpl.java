package pl.matsuo.gitlab.service.build.jekyll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.data.ProjectInfo;
import pl.matsuo.gitlab.file.YamlFileConverterProvider;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.jekyll.model.projects.Projects;
import pl.matsuo.gitlab.service.db.Database;

import static pl.matsuo.gitlab.file.FilePath.*;
import static pl.matsuo.gitlab.util.PushEventUtil.*;


/**
 * Created by marek on 29.08.15.
 */
@Service
public class JekyllGenerateTemplateServiceImpl implements JekyllGenerateTemplateService {


  @Autowired
  YamlFileConverterProvider yamlFileConverterProvider;
  @Autowired
  Database db;


  @Override
  public void generateTemplate(PushEvent pushEvent, JekyllProperties properties) {
    String source = properties.source();

    if (!properties.generateTemplate()) {
      return;
    }

    // base directory is "_data" directory in source root
    file(source)
        // all files should be in yaml format
        .configure(yamlFileConverterProvider)
        // base documentation source directory
        .with(sourceDir -> {
          ProjectInfo projectInfo = db.get(subPath(getUser(pushEvent), getRepository(pushEvent)), ProjectInfo.class);

          // if index.html and index.md not exists, create it
          sourceDir.without("index.html", "index.md").without(indexMd -> {
            // create index.md - should contain list of branches (?)
            indexMd.overwrite("# Automatically generated project site stub!\n\n");

            for (String branchName : projectInfo.getBranches()) {
              indexMd.appendln("* [" + branchName + "]("
                  + subPath(getUser(pushEvent), getRepository(pushEvent), branchName) + ")");
            }
          });

          // for each branch check does branch page exist - generate if not
          for (String branchName : projectInfo.getBranches()) {
            sourceDir.without("branch_" + branchName + ".html", "branch_" + branchName + ".md").without(branchMd -> {
              String idCommit = db.get(subPath(getUser(pushEvent), getRepository(pushEvent)), String.class);
              // fixme: some more impresive page for branch
              branchMd.overwrite(db.get(subPath(getUser(pushEvent), getRepository(pushEvent), idCommit), String.class));
            });
          }

          // _data directory
          sourceDir
              .with("_data", dataDir -> {
                // get projects.yml
                dataDir.with("projects.yml", projectsFile -> {
                  // map projects to object
                  projectsFile.content(Projects.class, projects -> {
                    // for each project found
                    projects.getProjects().forEach(project -> {
                      // generate project description file
                    });
                  });
                });
              });
        });
  }
}

