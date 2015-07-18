package pl.matsuo.gitlab.conf;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


/**
 * Created by marek on 17.07.15.
 */
@Configuration
@EnableAsync
public class AppConfig implements AsyncConfigurer {


  @Bean
  public static PropertyPlaceholderConfigurer propConfig() {
    System.out.println("PropertyPlaceholderConfigurer configured!");
    PropertyPlaceholderConfigurer ppc =  new PropertyPlaceholderConfigurer();
    ppc.setLocation(new ClassPathResource("/app.properties"));
    return ppc;
  }

  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(7);
    executor.setMaxPoolSize(42);
    executor.setQueueCapacity(11);
    executor.setThreadNamePrefix("gitlab-listener-");
    executor.initialize();
    return executor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return null;
  }
}

