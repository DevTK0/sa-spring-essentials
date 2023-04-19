package rewards;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import config.RewardsConfig;

//           The section titled "Build and Run using Command Line tools".
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Import(RewardsConfig.class)
@EnableConfigurationProperties(RewardsRecipientProperties.class)
public class RewardsApplication {
    static final String SQL = "SELECT count(*) FROM T_ACCOUNT";

    final Logger logger = LoggerFactory.getLogger(RewardsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RewardsApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {

        return args -> logger.info("Hello, there are {} accounts",
                jdbcTemplate.queryForObject(SQL, String.class));
    }

    @Bean
    CommandLineRunner printRecipient(RewardsRecipientProperties props) {
        return args -> logger.info(
                "Recipient is {}", props.getName());
    }

}
