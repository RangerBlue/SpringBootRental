package com.app.km;

import com.app.km.request.RentRequest;
import com.app.km.respository.CarRepository;
import com.app.km.respository.RentRepository;
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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import javax.servlet.Filter;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Created by Kamil-PC on 21.05.2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KmApplication.class)
@WebAppConfiguration
@Transactional
public class RentControllerTest {
        private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
                MediaType.APPLICATION_JSON.getSubtype(),
                Charset.forName("utf8"));

        private MockMvc mockMvc;

        private HttpMessageConverter mappingJackson2HttpMessageConverter;


        @Autowired
        private WebApplicationContext webApplicationContext;


        @Autowired
        private RentRepository rentRepository;

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
    public void getOneRentAsGuestShouldFail() throws Exception {

        mockMvc.perform(get("/api/rent/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getOneRentAsUserShouldSucceed() throws Exception {
        mockMvc.perform(get("/api/rent/1").with(httpBasic("user","user")))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllRentsAsGuestShouldFailed() throws Exception {
        mockMvc.perform(get("/api/rent/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllRentsAsUserShouldSucceed() throws Exception {
        int size = rentRepository.findAll().size();
        mockMvc.perform(get("/api/rent/").with(httpBasic("user","user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(size)));
    }

    @Test
    public void addRentAsGuestShouldFailed() throws Exception {
        RentRequest rent = new RentRequest();
        rent.setIdcar(1);
        String rentJson = json(rent);
        this.mockMvc.perform(put("/api/rent/")
                .contentType(contentType)
                .content(rentJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void addRentWithAvailableCarAsUserShouldSucceed() throws Exception {
        RentRequest rent = new RentRequest();
        rent.setIdcar(1);
        String rentJson = json(rent);
        this.mockMvc.perform(put("/api/rent/").with(httpBasic("user","user"))
                .contentType(contentType)
                .content(rentJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void addRentWithNotAvailableCarAsUserShouldFailed() throws Exception {
        RentRequest rent = new RentRequest();
        rent.setIdcar(3);
        String rentJson = json(rent);
        this.mockMvc.perform(put("/api/rent/").with(httpBasic("user","user"))
                .contentType(contentType)
                .content(rentJson))
                .andExpect(status().isConflict());
    }

    @Test
    public void updateRentToChangeCarAsGuestShouldFailed() throws Exception {
        RentRequest rent = new RentRequest();
        rent.setIdcar(2);
        String rentJson = json(rent);
        this.mockMvc.perform(put("/api/rent/1")
                .contentType(contentType)
                .content(rentJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateRentToChangeCarAsUserShouldSucceed() throws Exception {
        RentRequest rent = new RentRequest();
        rent.setIdcar(2);
        String rentJson = json(rent);
        this.mockMvc.perform(post("/api/rent/1").with(httpBasic("user","user"))
                .contentType(contentType)
                .content(rentJson))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/car/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(false)));

    }

    @Test
    public void updateToFinishRentAndSetCarAsAvailableAsUserShouldSucceed() throws Exception {
        this.mockMvc.perform(post("/api/rent/finish/1").with(httpBasic("user","user"))
                .contentType(contentType))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/car/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    public void deleteRentAsUserShouldFail() throws Exception {
        this.mockMvc.perform(delete("/api/rent/1").with(httpBasic("user","user"))
                .contentType(contentType))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteRentAsAdminShouldSucceed() throws Exception {
        this.mockMvc.perform(delete("/api/rent/1").with(httpBasic("admin","admin"))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteAllRentAsUserShouldFail() throws Exception {
        this.mockMvc.perform(delete("/api/rent/").with(httpBasic("user","user"))
                .contentType(contentType))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteAllRentAsAdminShouldSucceed() throws Exception {
        this.mockMvc.perform(delete("/api/rent/").with(httpBasic("admin","admin"))
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
