package com.sap.cap.incident_management;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.hamcrest.Matchers.hasSize;
import com.jayway.jsonpath.JsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IncidentsODataTests {
	
	private static final String incidentsURI = "/odata/v4/ProcessorService/Incidents";
    private static final String customerURI = "/odata/v4/ProcessorService/Customers";
    private static final String expandEntityURI = "/odata/v4/ProcessorService/Customers?$select=firstName&$expand=incidents";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "alice")
    void incidentReturned() throws Exception {
        mockMvc.perform(get(incidentsURI))
            .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", hasSize(4)));

    }
    @Test
    @WithMockUser(username = "alice")
    void customertReturned() throws Exception {
        mockMvc.perform(get(customerURI))
            .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", hasSize(3)));

    }

    @Test
    @WithMockUser(username = "alice")
    void expandEntityEndpoint() throws Exception {
        mockMvc.perform(get(expandEntityURI))
        .andExpect(jsonPath("$.value[0].incidents[0]").isMap())
        .andExpect(jsonPath("$.value[0].incidents[0]").isNotEmpty());

    }
    @Test
    @WithMockUser(username = "alice")
    void createIncident() throws Exception {
        String incidentJson = "{ \"title\": \"Urgent attention required!\", \"status_code\": \"N\", \"urgency_code\": \"M\", \"IsActiveEntity\": true }";

        mockMvc.perform(MockMvcRequestBuilders.post("/odata/v4/ProcessorService/Incidents")
                .content(incidentJson)
                .contentType("application/json")
                .accept("application/json"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.title").value("Urgent attention required!"))
                .andExpect(jsonPath("$.status_code").value("N"))
                .andExpect(jsonPath("$.urgency_code").value("H"));
    }

    @Test
    @WithMockUser(username = "alice")
    void updateIncident() throws Exception {
        String incidentCreateJson = "{ \"title\": \"Urgent attention required!\", \"status_code\": \"N\", \"IsActiveEntity\": true }";
        String incidentUpdateJson = "{\"status_code\": \"C\"}";
        String closedIncidentUpdateJson = "{\"status_code\": \"I\"}";

        MvcResult createResult=  mockMvc.perform(MockMvcRequestBuilders.post("/odata/v4/ProcessorService/Incidents")
                .content(incidentCreateJson)
                .contentType("application/json")
                .accept("application/json"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Urgent attention required!"))
                .andExpect(jsonPath("$.status_code").value("N"))
                .andReturn();

                String createResponseContent = createResult.getResponse().getContentAsString();
                String ID = JsonPath.read(createResponseContent, "$.ID");
                System.out.println("Incident ID : " + ID);

        MvcResult updateResult=  mockMvc.perform(MockMvcRequestBuilders.patch("/odata/v4/ProcessorService/Incidents(ID="+ID+",IsActiveEntity=true)")
                .content(incidentUpdateJson)
                .contentType("application/json")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Urgent attention required!"))
                .andExpect(jsonPath("$.status_code").value("C"))
                .andReturn();

                String updateResponseContent = updateResult.getResponse().getContentAsString();
                String statusCode = JsonPath.read(updateResponseContent, "$.status_code");
                System.out.println("status code : " + statusCode);


            mockMvc.perform(MockMvcRequestBuilders.patch("/odata/v4/ProcessorService/Incidents(ID="+ID+",IsActiveEntity=true)")
                .content(closedIncidentUpdateJson)
                .contentType("application/json")
                .accept("application/json"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.message").value("Can't modify a closed incident"));



    }

}