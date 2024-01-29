package com.sap.cap.incident_management;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IncidentsODataTests {
	
	private static final String incidentsURI = "/odata/v4/ProcessorService/Incidents";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "alice")
    void incidentReturned() throws Exception {
        mockMvc.perform(get(incidentsURI + "?$top=1"))
            .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", hasSize(4)))
            .andExpect(jsonPath("$.value[0].title").value(containsString("Solar panel broken")));

    }

}