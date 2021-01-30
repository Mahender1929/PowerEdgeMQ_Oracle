package com.cebi.configuration;



import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.cebi.entity.ApplicationLabel;
import com.cebi.entity.AuditHistory;
import com.cebi.entity.Banks;
import com.cebi.entity.BranchInformation;
import com.cebi.entity.BranchInformationId;
import com.cebi.entity.Ccdp;
import com.cebi.entity.Ccdp010;
import com.cebi.entity.PreDefineReport;
import com.cebi.entity.QueryData;
import com.cebi.entity.ReportQueueData;
import com.cebi.entity.RequiredField;
import com.cebi.entity.TellerMaster;
import com.cebi.entity.ViewInfo;
import com.cebi.utility.AES;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class DbConfiguration {

	@Autowired
	private Environment env;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	/*---------- session factory for static reports --------------*/

	 public static HikariDataSource ds;
	
	 
	@Bean(name = "staticReportbasicDataSource")
	public DataSource mySqlStaticReportDataSource() throws Exception {

		        HikariConfig config = new HikariConfig();
		        String appName = env.getProperty("application.static");
		        config.setDriverClassName(env.getProperty(appName + ".driverClassName"));
		        config.setJdbcUrl(env.getProperty(appName + ".databaseurl"));
		        config.setUsername(env.getProperty(appName + ".username"));
		        //config.setPassword(env.getProperty(appName + ".password"));  
		        config.setPassword(AES.decrypt(env.getProperty(appName + ".password")));
		        config.addDataSourceProperty("cachePrepStmts", true);
		        config.addDataSourceProperty("prepStmtCacheSize", 250);
		        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

		        HikariDataSource ds = new HikariDataSource(config);
		
		        return ds;
	}
	
	public static Connection getConn() throws SQLException {
        return ds.getConnection();
    }
	

	@Bean(name = "staticReportSessionFactory")
	public LocalSessionFactoryBean getStaticReportSessionFactory(@Qualifier("staticReportbasicDataSource") DataSource ds) {
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		localSessionFactoryBean.setDataSource(ds);
		localSessionFactoryBean.setAnnotatedClasses(BranchInformation.class, BranchInformationId.class, Ccdp.class, Ccdp010.class);
		localSessionFactoryBean.setHibernateProperties(getHibernateProperties());
		return localSessionFactoryBean;
	}

	@Bean(name = "jdbcTemplate")
	public JdbcTemplate getJdbcTemplate(@Qualifier("staticReportbasicDataSource") DataSource source) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(source);
		return jdbcTemplate;
	}


	/* ---------end of session factory for static report ------------- 

	 --------- session factory for prod ------------- */
	
	@Bean(name = "basicDataSource")
	public DataSource mySqlDataSource() throws Exception {
		
		   HikariConfig config = new HikariConfig();
	        String appName = env.getProperty("application.name");
	        config.setDriverClassName(env.getProperty(appName + ".driverClassName"));
	        config.setJdbcUrl(env.getProperty(appName + ".databaseurl"));
	        config.setUsername(env.getProperty(appName + ".username"));
	        //config.setPassword(env.getProperty(appName + ".password"));  
	        config.setPassword(AES.decrypt(env.getProperty(appName + ".password")));
	        config.addDataSourceProperty("cachePrepStmts", "true");
	        config.addDataSourceProperty("prepStmtCacheSize", "250");
	        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

	        HikariDataSource ds = new HikariDataSource(config);
	
	        return ds;
		
	}

	@Bean(name = "sessionFactory")
	public LocalSessionFactoryBean getSessionFactory(@Qualifier("basicDataSource") DataSource ds) {
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		localSessionFactoryBean.setDataSource(ds);
		localSessionFactoryBean.setAnnotatedClasses(PreDefineReport.class,ReportQueueData.class, QueryData.class, ViewInfo.class, ApplicationLabel.class, TellerMaster.class, RequiredField.class, AuditHistory.class, QueryData.class, Banks.class);
		localSessionFactoryBean.setHibernateProperties(getHibernateProperties());
		return localSessionFactoryBean;
	}

	/* ---------end of session factory for prod report ------------- */
	protected Properties getHibernateProperties() {
		
		Properties properties = new Properties();
		properties.put("hibernate.show_sql", "true");
		properties.put("hibernate.dialect", env.getProperty("Banc-Edge.dialect"));
		properties.put("hibernate.hikari.connectionTimeout", "20000");
		properties.put("hibernate.hikari.minimumIdle", "10");
		properties.put("hibernate.hikari.maximumPoolSize", "120");
		properties.put("hibernate.hikari.idleTimeout", "300000");
		properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
		properties.put("hibernate.enable_lazy_load_no_trans",true);
		return properties;
	}

	@Autowired
	@Bean(name = "transactionManager")
	public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
		return new HibernateTransactionManager(sessionFactory);
	}

}