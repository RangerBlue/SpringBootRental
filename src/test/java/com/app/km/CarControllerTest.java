package com.app.km;

import com.app.km.entity.CarEntity;
import com.app.km.respository.CarRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


/**
 * Created by Kamil-PC on 21.05.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KmApplication.class)
@WebAppConfiguration
@Transactional
public class CarControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;


    @Autowired
    private WebApplicationContext webApplicationContext;


    @Autowired
    private CarRepository carRepository;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

    }

    @Test
    public void getOneCarAsGuestShouldSucceed() throws Exception {

        mockMvc.perform(get("/api/car/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idcar", is(1)))
                .andExpect(jsonPath("$.brand", is("Porshe")))
                .andExpect(jsonPath("$.model", is("711")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    public void getAllCarsAsGuestShouldSucceed() throws Exception {

        int size = carRepository.findAll().size();
        mockMvc.perform(get("/api/car/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(size)));
    }


    @Test
    public void addCarAsUserShouldFail() throws Exception {
        CarEntity car = new CarEntity();
        car.setBrand("test");
        car.setModel("test");
        car.setAvailable(true);
        String carJson = json(car);
        this.mockMvc.perform(put("/api/car/").with(httpBasic("user", "user"))
                .contentType(contentType)
                .content(carJson))
                .andExpect(status().isForbidden());
    }

    @Test
    public void addCarAsAdminShouldSucceed() throws Exception {
        CarEntity car = new CarEntity();
        car.setBrand("test");
        car.setModel("test");
        car.setAvailable(true);
        String carJson = json(car);
        this.mockMvc.perform(put("/api/car/").with(httpBasic("admin", "admin"))
                .contentType(contentType)
                .content(carJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void deleteCarAsUserShouldFail() throws Exception {
        int id = carRepository.findAll().size();
        this.mockMvc.perform(delete("/api/car/" + id).with(httpBasic("user", "user"))
                .contentType(contentType))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteCarAsAdminShouldSucceed() throws Exception {
        int id = carRepository.findAll().size();
        this.mockMvc.perform(delete("/api/car/" + id).with(httpBasic("admin", "admin"))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }


    @Test
    public void updateCarAsUserShouldFail() throws Exception {
        CarEntity car = new CarEntity();
        car.setBrand("test");
        car.setModel("test");
        car.setAvailable(true);
        this.mockMvc.perform(post("/api/car/1").with(httpBasic("user", "user"))
                .contentType(contentType)
                .content(json(car)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateCarAsAdminShouldSucceed() throws Exception {
        CarEntity car = new CarEntity();
        car.setBrand("test");
        car.setModel("test");
        car.setAvailable(true);
        this.mockMvc.perform(post("/api/car/1").with(httpBasic("admin", "admin"))
                .contentType(contentType)
                .content(json(car)))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteAllCarsAsUserShouldFail() throws Exception {
        this.mockMvc.perform(delete("/api/car/").with(httpBasic("user", "user"))
                .contentType(contentType))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteAllCarsAsAdminShouldFail() throws Exception {
        this.mockMvc.perform(delete("/api/car/").with(httpBasic("admin", "admin"))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }


}
