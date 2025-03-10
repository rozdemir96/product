package com.project.product.test;

import org.springframework.context.ApplicationEvent;

public class DpuStarterEvent extends ApplicationEvent {
    public DpuStarterEvent(Object source) {
        super(source);
    }
}
