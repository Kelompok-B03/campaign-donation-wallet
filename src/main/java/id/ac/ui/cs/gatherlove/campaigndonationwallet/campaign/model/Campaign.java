package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
}
