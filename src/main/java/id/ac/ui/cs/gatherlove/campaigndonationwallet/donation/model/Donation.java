package id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
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
    private String campaignId;
    private Float amount;
    private String message;
    private Date createdAt;

    @Transient
    private DonationState state;

    private String stateName;

    public Donation(UUID userId, String campaignId, Float amount, String message) {
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

    @PostLoad
    private void postLoad() {
        initializeState();
    }

    public void setState(DonationState state) {
        this.state = state;
        this.stateName = state.getName();
        state.setContext(this);
    }

    public void initializeState() {
        switch (stateName) {
            case "Pending":
                setState(new PendingState());
                break;
            case "Finished":
                setState(new FinishedState());
                break;
//            case "Cancelled":
//                setState(new CancelledState());
//                break;
            default:
                throw new IllegalStateException("Unknown state: " + stateName);
        }
    }

//    public void cancel() {
//        getState().cancel();
//    }
}


