package me.nzuguem.fruitstockmanagement.configurations;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.output.Slf4jLogConsumer;

public class ContainerLogsBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof Container<?> container) {
            var logger = LoggerFactory.getLogger(beanName);
            var logConsumer = new Slf4jLogConsumer(logger);
            container.withLogConsumer(logConsumer);
        }
        return bean;
    }
}
