package id.ac.ui.cs.gatherlove.campaigndonationwallet;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class CampaignDonationWalletApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().directory("./").filename(".env").load();
        
        // Database properties
        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
        System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        
        // JWT properties
        System.setProperty("APP_JWT_SECRET", dotenv.get("APP_JWT_SECRET"));
        System.setProperty("APP_JWT_ISSUER", dotenv.get("APP_JWT_ISSUER"));
        System.setProperty("APP_JWT_ALGORITHM", dotenv.get("APP_JWT_ALGORITHM"));
        
        SpringApplication.run(CampaignDonationWalletApplication.class, args);
    }
}