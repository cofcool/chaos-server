package net.cofcool.chaos.server.cloud;

import net.cofcool.chaos.server.cloud.stream.kafka.LogingStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

/**
 * @author CofCool
 * @date 2018/7/16
 */
@Configuration
@EnableBinding(LogingStream.class)
public class CloudStreamApplicationConfig {


}
