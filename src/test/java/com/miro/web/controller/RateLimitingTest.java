package com.miro.web.controller;

import com.miro.config.properties.ApplicationProperties;
import com.miro.service.throttling.RateLimiterService;
import com.miro.web.dto.RateLimitDto;
import com.miro.web.filter.RateLimitingFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static com.miro.TestUtils.asJsonString;
import static com.miro.config.RestConfiguration.BASE_PREFIX;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Tests for rate throttling.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class RateLimitingTest {
    private MockMvc mockMvc;

    @Autowired
    private WidgetController widgetController;

    @Autowired
    private RateLimitController rateLimitController;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private RateLimiterService rateLimiterService;

    @Autowired
    private ApplicationProperties properties;

    @Before
    public void setup() {
        this.mockMvc = standaloneSetup(widgetController, rateLimitController)
                .addFilter(new RateLimitingFilter(requestMappingHandlerMapping, rateLimiterService))
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(new WebRestControllerAdvice())
                .build();
    }

    @Test
    public void testExceedRateLimit() throws Exception {

        //call api N allowed times...
        for (int i = 0; i <properties.getDefaultRateLimit(); i++) {
            mockMvc.perform(get(BASE_PREFIX + "/widgets/" + 1).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        //call one time yet to get 429
        mockMvc.perform(get(BASE_PREFIX + "/widgets/" + 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests());

    }


    /**
     *  Note: Since the new value will be set beginning from the next time window, we could wait some time,
     *  make a call for the rate limit info and check $.availableRequests value, but I am not sure if it's ok
     *  to wait any time in the tests...
     *  Taking into account that integration and unit tests are usually run separately, it might actually be
     *  acceptable, but in my case I will leave it as it is.
     *
     */
    @Test
    public void testSetRateLimit() throws Exception {

        RateLimitDto rateLimit = new RateLimitDto();
        rateLimit.setValue(5);

        String apiName = "WidgetController-list";

        mockMvc.perform(put(BASE_PREFIX + "/rateLimits/" + apiName).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(rateLimit)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.apiName", is(apiName)))
                .andExpect(jsonPath("$.limit", is((int) rateLimit.getValue())))
                .andExpect(jsonPath("$.availableRequests", is(properties.getDefaultRateLimit())))
                .andExpect(jsonPath("$.nextResetTme", notNullValue()));

    }

}