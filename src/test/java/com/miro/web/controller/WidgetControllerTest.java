package com.miro.web.controller;

import com.miro.entity.Widget;
import com.miro.web.dto.WidgetDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.StringJoiner;

import static com.miro.TestEntityFactory.createDefaultWidget;
import static com.miro.TestEntityFactory.createDefaultWidgetDto;
import static com.miro.TestUtils.asJsonString;
import static com.miro.config.RestConfiguration.BASE_PREFIX;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Integration tests for Widgets REST API.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WidgetControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WidgetController widgetController;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Before
    public void setup() {
        this.mockMvc = standaloneSetup(this.widgetController)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(new WebRestControllerAdvice())
                .build();
    }

    @Test
    public void testWidgetsListDefault() throws Exception {
        mockMvc.perform(get(BASE_PREFIX + "/widgets").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("totalElements", is(14)))
                .andExpect(jsonPath("size", is(10)))
                .andExpect(jsonPath("numberOfElements", is(10)))

                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[2].id", is(3)))
                .andExpect(jsonPath("$.content[3].id", is(4)))
                .andExpect(jsonPath("$.content[4].id", is(5)))
                .andExpect(jsonPath("$.content[5].id", is(6)))
                .andExpect(jsonPath("$.content[6].id", is(7)))
                .andExpect(jsonPath("$.content[7].id", is(8)))
                .andExpect(jsonPath("$.content[8].id", is(9)))
                .andExpect(jsonPath("$.content[9].id", is(10)));
    }

    @Test
    public void testWidgetsListCustomPage() throws Exception {
        mockMvc.perform(get(BASE_PREFIX + "/widgets?page=1&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("totalElements", is(14)))
                .andExpect(jsonPath("size", is(1)))
                .andExpect(jsonPath("numberOfElements", is(1)))
                .andExpect(jsonPath("$.content[0].id", is(2)));
    }


    @Test
    public void testWithSearchesAndPaginationSelectedArea() throws Exception {

        StringJoiner params = new StringJoiner("&");
        params.add("lowerLeftX=" + -1);
        params.add("lowerLeftY=" + -5);
        params.add("upperRightX=" + 5);
        params.add("upperRightY=" + 5);

        mockMvc.perform(get(BASE_PREFIX + "/widgets?" + params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("totalElements", is(3)))
                .andExpect(jsonPath("size", is(10)))
                .andExpect(jsonPath("numberOfElements", is(3)))
                .andExpect(jsonPath("$.content[0].id", is(7)))
                .andExpect(jsonPath("$.content[1].id", is(8)))
                .andExpect(jsonPath("$.content[2].id", is(11)));

    }

    @Test
    public void testWithSearchesAndPaginationSelectAll() throws Exception {

        StringJoiner params = new StringJoiner("&");
        params.add("lowerLeftX=" + -11);
        params.add("lowerLeftY=" + -11);
        params.add("upperRightX=" + 11);
        params.add("upperRightY=" + 11);

        mockMvc.perform(get(BASE_PREFIX + "/widgets?" + params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("totalElements", is(14)))
                .andExpect(jsonPath("size", is(10)))
                .andExpect(jsonPath("numberOfElements", is(10)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[2].id", is(3)))
                .andExpect(jsonPath("$.content[3].id", is(4)))
                .andExpect(jsonPath("$.content[4].id", is(5)))
                .andExpect(jsonPath("$.content[5].id", is(6)))
                .andExpect(jsonPath("$.content[6].id", is(7)))
                .andExpect(jsonPath("$.content[7].id", is(8)))
                .andExpect(jsonPath("$.content[8].id", is(9)))
                .andExpect(jsonPath("$.content[9].id", is(10)));

    }

    @Test
    public void testWithSearchesAndPaginationSelectAboveXAxisPage1() throws Exception {

        StringJoiner params = new StringJoiner("&");
        params.add("page=" + 0);
        params.add("size=" + 5);
        params.add("lowerLeftX=" + -11);
        params.add("lowerLeftY=" + 0);
        params.add("upperRightX=" + 11);
        params.add("upperRightY=" + 11);

        mockMvc.perform(get(BASE_PREFIX + "/widgets?" + params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("totalElements", is(6)))
                .andExpect(jsonPath("size", is(5)))
                .andExpect(jsonPath("numberOfElements", is(5)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[2].id", is(3)))
                .andExpect(jsonPath("$.content[3].id", is(4)))
                .andExpect(jsonPath("$.content[4].id", is(5)));

    }

    @Test
    public void testWithSearchesAndPaginationSelectAboveXAxisPage2() throws Exception {

        StringJoiner params = new StringJoiner("&");
        params.add("page=" + 1);
        params.add("size=" + 5);
        params.add("lowerLeftX=" + -11);
        params.add("lowerLeftY=" + 0);
        params.add("upperRightX=" + 11);
        params.add("upperRightY=" + 11);

        mockMvc.perform(get(BASE_PREFIX + "/widgets?" + params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("totalElements", is(6)))
                .andExpect(jsonPath("size", is(5)))
                .andExpect(jsonPath("numberOfElements", is(1)))
                .andExpect(jsonPath("$.content[0].id", is(7)));

    }

    @Test
    public void testWrongSearchArgument() throws Exception {
        StringJoiner params = new StringJoiner("&");
        params.add("page=" + 1);
        params.add("size=" + 1);
        params.add("upperRightX=" + 11);
        String validationError = "Search params (x, y, width, height) all should be either empty " +
                "or filled with values";
        mockMvc.perform(get(BASE_PREFIX + "/widgets?" + params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description",
                is(validationError)));
    }


    @Test
    public void testGetById() throws Exception {
        mockMvc.perform(get(BASE_PREFIX + "/widgets/" + 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id", is(1)));
    }


    @Test
    public void testGetByIdNotFound() throws Exception {
        mockMvc.perform(get(BASE_PREFIX + "/widgets/" + 999).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.description",
                        is("Object of type Widget with id = 999 is not found")));
    }

    @Test
    public void testDeleteById() throws Exception {
        mockMvc.perform(delete(BASE_PREFIX + "/widgets/" + 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }


    @Test
    public void testCreateWithExistingId() throws Exception {

        WidgetDto widget = createDefaultWidgetDto();
        widget.setId(123L);

        mockMvc.perform(
                post(BASE_PREFIX + "/widgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(widget))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.description",
                        is("Entity should not contain filled id to create instance")));
    }


    @Test
    public void testCreate() throws Exception {

        Widget widget = createDefaultWidget();

        mockMvc.perform(
                post(BASE_PREFIX + "/widgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(widget))
        )
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.modificationDate", notNullValue()))
                .andExpect(jsonPath("$.width", is(widget.getWidth())))
                .andExpect(jsonPath("$.height", is(widget.getHeight())))
                .andExpect(jsonPath("$.x", is(widget.getX())))
                .andExpect(jsonPath("$.y", is(widget.getY())))
                .andExpect(jsonPath("$.z", is(widget.getZ())));
    }

    @Test
    public void testUpdate() throws Exception {

        WidgetDto widget = createDefaultWidgetDto();
        widget.setId(1L); //existing id

        mockMvc.perform(
                put(BASE_PREFIX + "/widgets/" + widget.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(widget))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.width", is(widget.getWidth())))
                .andExpect(jsonPath("$.height", is(widget.getHeight())))
                .andExpect(jsonPath("$.x", is(widget.getX())))
                .andExpect(jsonPath("$.y", is(widget.getY())))
                .andExpect(jsonPath("$.z", is(widget.getZ())));
    }

}
