package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.controller;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model.Campaign;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.service.CampaignService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CampaignControllerTest {

    @InjectMocks
    private CampaignController campaignController;

    @Mock
    private CampaignService campaignService;

    private Campaign sampleCampaign;

    @BeforeEach
    void setUp() {
        sampleCampaign = new Campaign();
        sampleCampaign.setCampaignId("123");
        sampleCampaign.setTitle("Save Oceans");
        sampleCampaign.setFundraiserId("user123");
        sampleCampaign.setStartDate(LocalDate.now());
        sampleCampaign.setEndDate(LocalDate.now().plusDays(30));
    }

    private JwtAuthenticationToken mockJwt(String userId, boolean isAdmin) {
        Jwt jwt = Jwt.withTokenValue("token")
                .claim("userId", userId)
                .header("alg", "none")
                .build();

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (isAdmin) authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return new JwtAuthenticationToken(jwt, authorities);
    }

    @Test
    public void testGetCampaignById_Happy() {
        when(campaignService.findById("123")).thenReturn(sampleCampaign);
        Campaign result = campaignController.getCampaignById("123");
        assertNotNull(result);
        assertEquals("Save Oceans", result.getTitle());
    }

    @Test
    public void testGetCampaignById_NotFound() {
        when(campaignService.findById("404")).thenReturn(null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                campaignController.getCampaignById("404"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    public void testWithdrawCampaign_Happy() {
        JwtAuthenticationToken jwt = mockJwt("user123", false);
        when(campaignService.findById("123")).thenReturn(sampleCampaign);
        assertDoesNotThrow(() -> campaignController.withdrawCampaign("123", jwt));
        verify(campaignService).withdrawCampaign("123");
    }

    @Test
    public void testWithdrawCampaign_Unauthorized() {
        JwtAuthenticationToken jwt = mockJwt("anotherUser", false);
        when(campaignService.findById("123")).thenReturn(sampleCampaign);
        SecurityException ex = assertThrows(SecurityException.class, () ->
                campaignController.withdrawCampaign("123", jwt));
        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    public void testUpgradeCampaignStatus_Happy() {
        assertDoesNotThrow(() -> campaignController.upgradeCampaignStatus("123"));
        verify(campaignService).upgradeCampaignStatus("123");
    }

    @Test
    public void testUpgradeCampaignStatus_NotFound() {
        doThrow(new EntityNotFoundException("Campaign tidak ditemukan"))
                .when(campaignService).upgradeCampaignStatus("123");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                campaignController.upgradeCampaignStatus("123"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    public void testUpgradeCampaignStatus_InvalidState() {
        doThrow(new IllegalStateException("Status campaign sudah aktif"))
                .when(campaignService).upgradeCampaignStatus("123");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                campaignController.upgradeCampaignStatus("123"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    public void testGetCampaignsByUserId_Happy() {
        JwtAuthenticationToken jwt = mockJwt("user123", false);
        when(campaignService.findByUserId("user123")).thenReturn(List.of(sampleCampaign));
        List<Campaign> result = campaignController.getCampaignsByUserId("user123", jwt);
        assertEquals(1, result.size());
        assertEquals("Save Oceans", result.get(0).getTitle());
    }

    @Test
    public void testGetCampaignsByUserId_Unauthorized() {
        JwtAuthenticationToken jwt = mockJwt("anotherUser", false);
        SecurityException ex = assertThrows(SecurityException.class, () ->
                campaignController.getCampaignsByUserId("user123", jwt));
        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    public void testDeleteCampaign_Happy() {
        JwtAuthenticationToken jwt = mockJwt("user123", false);
        when(campaignService.findById("123")).thenReturn(sampleCampaign);
        assertDoesNotThrow(() -> campaignController.deleteCampaign("123", jwt));
    }

    @Test
    public void testDeleteCampaign_NotFound() {
        when(campaignService.findById("notfound")).thenReturn(null);
        JwtAuthenticationToken jwt = mockJwt("user123", true);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                campaignController.deleteCampaign("notfound", jwt));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    public void testUpdateCampaign_Happy() {
        Campaign updated = new Campaign();
        updated.setTitle("Updated");
        JwtAuthenticationToken jwt = mockJwt("user123", false);
        when(campaignService.findById("123")).thenReturn(sampleCampaign);
        assertDoesNotThrow(() -> campaignController.updateCampaign("123", updated, jwt));
    }

    @Test
    public void testUploadUsageProofLink_Happy() {
        JwtAuthenticationToken jwt = mockJwt("user123", false);
        when(campaignService.findById("123")).thenReturn(sampleCampaign);
        assertDoesNotThrow(() -> campaignController.uploadUsageProofLink("123", "https://bukti.com/link", jwt));
        verify(campaignService).updateUsageProofLink("123", "https://bukti.com/link");
    }

    @Test
    public void testUploadUsageProofLink_Unauthorized() {
        JwtAuthenticationToken jwt = mockJwt("other", false);
        when(campaignService.findById("123")).thenReturn(sampleCampaign);
        SecurityException ex = assertThrows(SecurityException.class, () ->
                campaignController.uploadUsageProofLink("123", "https://link", jwt));
        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    public void testCompleteCampaign_Happy() {
        JwtAuthenticationToken jwt = mockJwt("user123", false);
        when(campaignService.findById("123")).thenReturn(sampleCampaign);
        assertDoesNotThrow(() -> campaignController.completeCampaign("123", jwt));
        verify(campaignService).completeCampaign("123", "user123");
    }

    @Test
    public void testCompleteCampaign_Unauthorized() {
        JwtAuthenticationToken jwt = mockJwt("otherUser", false);
        when(campaignService.findById("123")).thenReturn(sampleCampaign);
        SecurityException ex = assertThrows(SecurityException.class, () ->
                campaignController.completeCampaign("123", jwt));
        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    public void testCompleteCampaign_NotFound() {
        JwtAuthenticationToken jwt = mockJwt("user123", false);
        when(campaignService.findById("123")).thenReturn(null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                campaignController.completeCampaign("123", jwt));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    public void testGetCampaignsByStatus() {
        when(campaignService.findCampaignsByStatus("SEDANG_BERLANGSUNG")).thenReturn(List.of(sampleCampaign));
        List<Campaign> result = campaignController.getCampaignsByStatus("SEDANG_BERLANGSUNG");
        assertEquals(1, result.size());
        assertEquals("Save Oceans", result.get(0).getTitle());
    }
}

