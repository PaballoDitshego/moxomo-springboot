package za.co.moxomo.config.sms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class SmsConfig {

    @Bean
    public MessageChannel smsOutboundChannel(){return new DirectChannel();}

}
