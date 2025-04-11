package id.ac.ui.cs.gatherlove.campaigndonationwallet.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Donation {

    @Id
    private UUID donationId;

    private UUID userId;
    private UUID campaignId;

    private Float amount;
    private String message;
    private Date createdAt;

    @Transient
    private DonationState state;

    private String stateName;

    public Donation(UUID userId, UUID campaignId, Float amount, String message) {
        if (userId == null || campaignId == null) throw new NullPointerException("User ID and Campaign ID must not be null");
        if (amount == null || amount <= 0) throw new IllegalArgumentException("Amount must be positive");

        this.donationId = UUID.randomUUID();
        this.userId = userId;
        this.campaignId = campaignId;
        this.amount = amount;
        this.message = message;
        this.createdAt = new Date();

        this.setState(new PendingState());
    }

    public void setState(DonationState state) {
        this.state = state;
        this.stateName = state.getName();
        state.setContext(this);
    }

    public DonationState getState() {
        if (state == null) {
            switch (stateName) {
                case "Pending" -> setState(new PendingState());
                case "Finished" -> setState(new FinishedState());
                case "Cancelled" -> setState(new CancelledState());
                default -> throw new IllegalStateException("Unknown state: " + stateName);
            }
        }
        return state;
    }

    public void updateStatus() {
        getState().updateStatus();
    }

    public void cancel() {
        getState().cancel();
    }
}


