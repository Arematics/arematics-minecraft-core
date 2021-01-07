package com.arematics.minecraft.core;

import com.arematics.minecraft.core.annotations.IgnoreInAppScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableJpaRepositories(basePackages = {"com.arematics.minecraft.data.global", "com.arematics.minecraft.data.share"})
public class GlobalDataAutoConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource globalDataSource() {
        return DataSourceBuilder.create().build();
    }


    private final Environment env;

    @Autowired
    public GlobalDataAutoConfiguration(Environment env){
        this.env = env;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(globalDataSource());
        em.setPackagesToScan("com.arematics.minecraft.data.global.model",
                "com.arematics.minecraft.data.share.model");

        return getLocalContainerEntityManagerFactoryBean(em, env);
    }

    @Bean
    public PlatformTransactionManager transactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                entityManagerFactory().getObject());
        return transactionManager;
    }

    static LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBean(LocalContainerEntityManagerFactoryBean em, Environment env) {
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
}
