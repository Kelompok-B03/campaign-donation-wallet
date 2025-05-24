package id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationRequest {
    private String campaignId;
    private Float amount;
    private String message;
}
