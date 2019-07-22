package pl.matsuo.gitlab.service.execute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

/** Created by marek on 04.09.15. */
@Service
public class ExecutionServiceImpl implements ExecutionService {

  @Autowired(required = false)
  TaskExecutor taskExecutor;

  @Override
  public void run(Runnable runnable) {
    // if taskExecutor is not configured, execute in synch
    if (taskExecutor != null) {
      taskExecutor.execute(runnable);
    } else {
      runnable.run();
    }
  }
}
