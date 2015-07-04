package pl.matsuo.gitlab.conf;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;


/**
 * Created by marek on 04.07.15.
 */
@Configuration
public class TestConfig {


  @Bean
  PropertyPlaceholderConfigurer propConfig() {
    PropertyPlaceholderConfigurer ppc =  new PropertyPlaceholderConfigurer();
    ppc.setLocation(new ClassPathResource("test.properties"));
    return ppc;
  }
}

