package com.woojin.batchsample.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 Spring boot에서 2개 이상의 DB를 연결하려면 Config 클래스를 필수적으로 작성해줘야한다.
 이때 충돌을 방지하기 위해서 @Primary를 통해서 우선순위를 지정할 수 있다.
 스프링 배치의 기본적인 메타 데이터는 @Primary로 잡혀 있는 DB 소스에 초기화되게 되어 있다.
 */
@Configuration
public class MetadataConfig {

    @Primary // 충돌 방지를 위한 우선순위 설정을 해준다
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-meta")
    public DataSource metaDBSource() {
        // DB 연결 작업을 수행한다.
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager metaTransactionManager() {

        return new DataSourceTransactionManager(metaDBSource());
    }
}
