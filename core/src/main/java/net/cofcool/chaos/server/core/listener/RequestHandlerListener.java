package net.cofcool.chaos.server.core.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.web.context.support.RequestHandledEvent;

/**
 * 输出请求事件日志
 *
 * @author CofCool
 */
public class RequestHandlerListener implements ApplicationListener<RequestHandledEvent> {

    private final Logger log = LoggerFactory.getLogger(RequestHandlerListener.class);

    @Override
    public void onApplicationEvent(RequestHandledEvent event) {
        log.info("request event: {}",event);
    }
}
