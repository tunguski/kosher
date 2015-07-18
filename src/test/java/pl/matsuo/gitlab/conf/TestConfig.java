package pl.matsuo.gitlab.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;
import static com.fasterxml.jackson.databind.DeserializationFeature.*;


/**
 * Created by marek on 04.07.15.
 */
@Configuration
public class TestConfig {


  @Bean
  public static PropertyPlaceholderConfigurer propConfig() {
    PropertyPlaceholderConfigurer ppc =  new PropertyPlaceholderConfigurer();
    ppc.setLocation(new ClassPathResource("test.properties"));
    return ppc;
  }


  @Bean
  public ObjectMapper objectMapper(MappingJackson2HttpMessageConverter converter) {
    return converter.getObjectMapper();
  }


  @Bean
  public MappingJackson2HttpMessageConverter converter() {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    ObjectMapper objectMapper = converter.getObjectMapper();

    objectMapper.setSerializationInclusion(NON_NULL);
    objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    return converter;
  }
}

