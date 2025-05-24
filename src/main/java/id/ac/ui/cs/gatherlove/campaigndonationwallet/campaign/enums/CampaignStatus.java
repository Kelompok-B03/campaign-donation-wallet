package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.enums;

import lombok.Getter; // Assuming @Getter comes from Lombok

@Getter
public enum CampaignStatus {
    MENUNGGU_VERIFIKASI("MENUNGGU_VERIFIKASI"),
    SEDANG_BERLANGSUNG("SEDANG_BERLANGSUNG"),
    SELESAI("SELESAI");

    private final String value;

    CampaignStatus(String value) {
        this.value = value;
    }

    public static boolean contains(String param) {
        for (CampaignStatus campaignStatus : CampaignStatus.values()) {
            if (campaignStatus.name().equals(param)) {
                return true;
            }
        }
        return false;
    }
}