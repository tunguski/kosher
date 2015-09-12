package pl.matsuo.gitlab.util;

@FunctionalInterface
public interface ThrowingRunnable {

  void run() throws Exception;
}
