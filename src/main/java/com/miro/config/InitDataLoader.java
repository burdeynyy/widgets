package com.miro.config;

import com.miro.entity.Widget;
import com.miro.repository.impl.WidgetRepositoryNativeImpl;
import com.miro.util.TestData;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Component loading test data for native storage implementation.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnBean(value = WidgetRepositoryNativeImpl.class)
public class InitDataLoader {

    private final WidgetRepositoryNativeImpl widgetRepositoryNative;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        for (Widget widget: TestData.getTestData()) {
            widgetRepositoryNative.create(widget);
        }
    }

}