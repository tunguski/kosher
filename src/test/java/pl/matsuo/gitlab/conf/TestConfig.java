package pl.matsuo.gitlab.conf;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * Created by marek on 04.07.15.
 */
@Configuration
@EnableAsync
public class TestConfig {


  @Bean
  public static PropertyPlaceholderConfigurer propConfig() {
    PropertyPlaceholderConfigurer ppc =  new PropertyPlaceholderConfigurer();
    ppc.setLocation(new ClassPathResource("test.properties"));
    return ppc;
  }
}

