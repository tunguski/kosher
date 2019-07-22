package pl.matsuo.gitlab.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.matsuo.gitlab.AbstractSampleProjectTest;
import pl.matsuo.gitlab.conf.TestConfig;
import pl.matsuo.gitlab.function.ThrowingExceptionsConsumer;
import pl.matsuo.gitlab.service.execute.ExecutionServiceImpl;

/** Created by tunguski on 19.12.13. */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, ExecutionServiceImpl.class})
public abstract class AbstractControllerRequestTest extends AbstractSampleProjectTest {

  @Autowired protected WebApplicationContext wac;
  protected MockMvc mockMvc;
  @Autowired protected ObjectMapper objectMapper;

  protected MockHttpServletRequestBuilder post(String url, Object content)
      throws JsonProcessingException {
    return MockMvcRequestBuilders.post(url)
        .content(objectMapper.writeValueAsString(content))
        .contentType(APPLICATION_JSON);
  }

  protected MockHttpServletRequestBuilder post(String url) throws JsonProcessingException {
    return MockMvcRequestBuilders.post(url);
  }

  @Before
  public void setup() {
    super.setup();
    mockMvc = webAppContextSetup(wac).build();
  }

  protected void performAndCheck(
      MockHttpServletRequestBuilder request, ThrowingExceptionsConsumer<String>... checks)
      throws Exception {
    performAndCheckStatus(request, status().isOk(), checks);
  }

  protected void performAndCheckStatus(
      MockHttpServletRequestBuilder request,
      ResultMatcher status,
      ThrowingExceptionsConsumer<String>... checks)
      throws Exception {
    ResultActions result = mockMvc.perform(request);
    result.andExpect(status);
    String html = result.andReturn().getResponse().getContentAsString();

    for (ThrowingExceptionsConsumer<String> check : checks) {
      check.accept(html);
    }
  }
}
