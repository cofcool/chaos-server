package net.cofcool.chaos.server.cloud.stream.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author CofCool
 * @date 2018/9/21
 */
public interface LogingStream {

    String INPUT = "logs-in";


    @Input(INPUT)
    SubscribableChannel writeLogs();

}
