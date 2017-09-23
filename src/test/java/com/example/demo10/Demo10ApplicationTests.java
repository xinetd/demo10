package com.example.demo10;

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
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import com.example.demo10.model.EntityX;
import com.example.demo10.repository.EntityXRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Demo10Application.class)
@WebAppConfiguration
public class Demo10ApplicationTests<webApplicationContext> {
	
    private MediaType contentType = new MediaType("application", "hal+json", Charset.forName("UTF-8"));
	
    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private EntityX entityX;

    @Autowired
    private EntityXRepository entityXRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

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
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.entityXRepository.deleteAll();

        this.entityX = entityXRepository.save(new EntityX("Batman"));
    }

    @Test
    public void notFoundEntityX() throws Exception {
        mockMvc.perform(post("/entityXes/x")
                .content(this.json(new EntityX("x")))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void createEntityX() throws Exception {
        String entityXJson = json(new EntityX("Catwoman"));

        mockMvc.perform(post("/entityXes")
                .contentType(contentType)
                .content(entityXJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/entityXes"))
			.andExpect(jsonPath("$._embedded.entityXes", hasSize(2)))
	        .andExpect(status().isOk());
    }

    @Test
    public void readEntityX() throws Exception {
        mockMvc.perform(get("/entityXes/" + entityX.getId()))
                .andExpect(status().isOk())
        		.andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name", is(entityX.getName())));
    }

    @Test
    public void updateEntityX() throws Exception {
    	entityX.setName("Batman & Robin");
        String entityXJson = json(entityX);

        mockMvc.perform(put("/entityXes/" + entityX.getId())
            .contentType(contentType)
            .content(entityXJson))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/entityXes/" + entityX.getId()))
        .andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
        .andExpect(jsonPath("$.name", is(entityX.getName())));
    }

    @Test
    public void deleteEntityX() throws Exception {
        mockMvc.perform(delete("/entityXes/" + entityX.getId()))
            .andExpect(status().isNoContent());
        
        mockMvc.perform(get("/entityXes"))
			.andExpect(jsonPath("$._embedded.entityXes", hasSize(0)))
	        .andExpect(status().isOk());
        
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }    
    
}
