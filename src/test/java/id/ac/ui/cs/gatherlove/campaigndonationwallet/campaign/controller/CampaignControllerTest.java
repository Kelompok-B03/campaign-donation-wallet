package id.ac.ui.cs.gatherlove.campaigndonationwallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Campaign;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.service.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CampaignController.class)
@Import(CampaignControllerTest.MockServiceConfig.class)
public class CampaignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CampaignService campaignService;

    private Campaign campaign;

    @TestConfiguration
    static class MockServiceConfig {
        @Bean
        public CampaignService campaignService() {
            return mock(CampaignService.class);
        }
    }

    @BeforeEach
    public void setUp() {
        campaign = new Campaign();
        campaign.setCampaignId("CAMP001");
        campaign.setTitle("Test Campaign");
        campaign.setDescription("A test campaign");
        campaign.setTargetAmount(50000);
        campaign.setFundsCollected(15000);
        campaign.setStartDate(LocalDate.of(2025, 4, 1));
        campaign.setEndDate(LocalDate.of(2025, 5, 30));
        campaign.setFundraiserId("USER123");
        campaign.setStatus("ONGOING");
    }

    private static String asJsonString(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // HAPPY PATHS

    @Test
    public void testGetAllCampaigns() throws Exception {
        when(campaignService.findAll()).thenReturn(List.of(campaign));

        mockMvc.perform(get("/api/campaign"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].campaignId").value("CAMP001"));
    }

    @Test
    public void testCreateCampaign() throws Exception {
        when(campaignService.create(any(Campaign.class))).thenReturn(campaign);

        mockMvc.perform(post("/api/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(campaign)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Campaign"));
    }

    @Test
    public void testGetCampaignsByUserId() throws Exception {
        when(campaignService.findByUserId("USER123")).thenReturn(List.of(campaign));

        mockMvc.perform(get("/api/campaign/user/USER123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fundraiserId").value("USER123"));
    }

    @Test
    public void testGetCampaignById() throws Exception {
        when(campaignService.findById("CAMP001")).thenReturn(campaign);

        mockMvc.perform(get("/api/campaign/CAMP001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaignId").value("CAMP001"));
    }

    @Test
    public void testDeleteCampaign() throws Exception {
        when(campaignService.findById("CAMP001")).thenReturn(campaign);
        doNothing().when(campaignService).delete(campaign);

        mockMvc.perform(delete("/api/campaign/CAMP001"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateCampaign() throws Exception {
        when(campaignService.findById("CAMP001")).thenReturn(campaign);
        doNothing().when(campaignService).update(any(Campaign.class));

        mockMvc.perform(put("/api/campaign/CAMP001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(campaign)))
                .andExpect(status().isOk());
    }

    // UNHAPPY PATHS

    @Test
    public void testGetCampaignByIdNotFound() throws Exception {
        when(campaignService.findById("CAMP999")).thenReturn(null);

        mockMvc.perform(get("/api/campaign/CAMP999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCampaignNotFound() throws Exception {
        when(campaignService.findById("CAMP999")).thenReturn(null);

        mockMvc.perform(delete("/api/campaign/CAMP999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateCampaignNotFound() throws Exception {
        when(campaignService.findById("CAMP999")).thenReturn(null);

        mockMvc.perform(put("/api/campaign/CAMP999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(campaign)))
                .andExpect(status().isNotFound());
    }
}
