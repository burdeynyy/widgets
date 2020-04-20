package com.miro.service.throttling;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

/**
 * Listener registering APIs placed in {@value API_PACKAGE} to limit requests rate for them.
 */
@Component
@RequiredArgsConstructor
public class EndpointsListener {
    private static final String API_PACKAGE = "com.miro.web.controller";

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final RateLimiterServiceImpl rateLimiterService;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            if (entry.getValue().getBeanType().getPackage().getName().equals(API_PACKAGE)) {
                rateLimiterService.registerApi(entry.getValue());
            }
        }

    }
}