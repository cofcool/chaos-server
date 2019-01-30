package net.cofcool.chaos.server.cloud.stream.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

/**
 * @author CofCool
 */
@Service
@Slf4j
public class LogingService {

    private final LogingStream logingStream;

    public LogingService(LogingStream logingStream) {
        this.logingStream = logingStream;
    }

    public void writeLog(final Log data) {

        log.info("Sending greetings {}", data);

        MessageChannel messageChannel = logingStream.writeLogs();

        messageChannel.send(
            MessageBuilder
                .withPayload(data)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build()
        );

    }
}

