package com.app.km;


import com.app.km.entity.UsersEntity;
import com.app.km.respository.UsersRepository;
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
public class UsersControllerTest {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;


    @Autowired
    private WebApplicationContext webApplicationContext;


    @Autowired
    private UsersRepository usersRepository;

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
    public void getOneUserAsGuestShouldFail() throws Exception {

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getOneUserAsUserShouldSucceed() throws Exception {

        mockMvc.perform(get("/api/users/2").with(httpBasic("user","user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("user")));
    }

    @Test
    public void getOneUserWithOtherUserIdAsAdminShouldFail() throws Exception {

        mockMvc.perform(get("/api/users/2").with(httpBasic("admin","admin")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAllUsersAsUserShouldFail() throws Exception {

        mockMvc.perform(get("/api/users/").with(httpBasic("user","user")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAllUsersAsAdminShouldSucceed() throws Exception {
        int size = usersRepository.findAll().size();
        mockMvc.perform(get("/api/users/").with(httpBasic("admin","admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(size)));
    }

    @Test
    public void addUserAsGuestShouldSucceed() throws Exception {
        UsersEntity user = new UsersEntity();
        user.setName("test");
        user.setLastname("test");
        user.setUsername("test");
        user.setPassword("test");
        user.setEmail("test");
        String userJson = json(user);
        this.mockMvc.perform(put("/api/users/")
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void updateUserAsUserShouldSucceed() throws Exception {
        UsersEntity user = new UsersEntity();
        user.setName("test1");
        user.setLastname("test1");
        user.setUsername("test1");
        user.setPassword("test");
        user.setEmail("test1");
        this.mockMvc.perform(post("/api/users/2").with(httpBasic("user","user"))
                .contentType(contentType)
                .content(json(user)))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUserAsGuestShouldFail() throws Exception {
        UsersEntity user = new UsersEntity();
        user.setName("test1");
        user.setLastname("test1");
        user.setUsername("test1");
        user.setPassword("test");
        user.setEmail("test1");
        this.mockMvc.perform(post("/api/users/2")
                .contentType(contentType)
                .content(json(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteUserAsUserShouldFail() throws Exception {
        this.mockMvc.perform(delete("/api/users/2").with(httpBasic("user","user"))
                .contentType(contentType))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteUserAsAdminShouldSucceed() throws Exception {
        this.mockMvc.perform(delete("/api/users/2").with(httpBasic("admin","admin"))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteAllUsersAsUserShouldFail() throws Exception {
        this.mockMvc.perform(delete("/api/users/").with(httpBasic("user","user"))
                .contentType(contentType))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteAllUsersAsAdminShouldSucceed() throws Exception {
        this.mockMvc.perform(delete("/api/users/").with(httpBasic("admin","admin"))
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
