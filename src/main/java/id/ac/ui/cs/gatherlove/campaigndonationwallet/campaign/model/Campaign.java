package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.enums.CampaignStatus;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // atau IDENTITY jika kamu pakai Long
    private String campaignId;
    private String title;
    private String description;
    private int targetAmount;
    private int fundsCollected;
    private LocalDate startDate;
    private LocalDate endDate;
    private String fundraiserId;
    private String status;
    private Boolean withdrawed;

    public void setStatus(String status) {
        if (CampaignStatus.contains(status)) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }
}
