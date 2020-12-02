package com.arematics.minecraft.core;

import com.arematics.minecraft.core.annotations.IgnoreInAppScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@IgnoreInAppScan
@Configuration
@PropertySource({"classpath:application.properties"})
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

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",
                env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect",
                env.getProperty("hibernate.dialect"));
        properties.put("hibernate.logging",
                env.getProperty("warn"));
        properties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        properties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        em.setJpaPropertyMap(properties);

        return em;
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
