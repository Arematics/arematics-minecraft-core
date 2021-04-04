package com.arematics.minecraft.core.configurations.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = {"com.arematics.minecraft.data.mode"},
        entityManagerFactoryRef = "gameModeEntityManager",
        transactionManagerRef = "gameModeTransactionManager")
public class GameModeDataAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix="spring.gamemode-db")
    public DataSource gameModeDataSource() {
        return DataSourceBuilder.create().build();
    }

    private final Environment env;

    @Autowired
    public GameModeDataAutoConfiguration(Environment env){
        this.env = env;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean gameModeEntityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(gameModeDataSource());
        em.setPackagesToScan("com.arematics.minecraft.data.mode.model", "com.arematics.minecraft.data.share.model");

        return GlobalDataAutoConfiguration.getLocalContainerEntityManagerFactoryBean(em, env);
    }

    @Bean
    public PlatformTransactionManager gameModeTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                gameModeEntityManager().getObject());
        return transactionManager;
    }
}
