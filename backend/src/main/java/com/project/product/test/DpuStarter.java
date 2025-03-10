package com.project.product.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Order(value = 1)
public class DpuStarter implements ApplicationListener<ApplicationReadyEvent> {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        applicationEventPublisher.publishEvent(new DpuStarterEvent(this));
    }

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
