package id.ac.ui.cs.gatherlove.campaigndonationwallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class CampaignDonationWalletApplication {


    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().directory("./").filename(".env").load();
        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
        System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
    
        SpringApplication.run(CampaignDonationWalletApplication.class, args);
    }    

}
