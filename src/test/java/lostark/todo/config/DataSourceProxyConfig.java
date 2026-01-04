package lostark.todo.config;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.List;

@TestConfiguration
public class DataSourceProxyConfig {

    @Bean
    public BeanPostProcessor dataSourceProxyBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof DataSource && !(bean.getClass().getName().contains("Proxy"))) {
                    return ProxyDataSourceBuilder.create((DataSource) bean)
                            .name("QueryCountingDataSource")
                            .listener(new QueryExecutionListener() {
                                @Override
                                public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                                }

                                @Override
                                public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                                    QueryCounter.increment();
                                }
                            })
                            .build();
                }
                return bean;
            }
        };
    }
}
