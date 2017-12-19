//package bcs.testing.test
//
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.core.io.Resource
//import org.springframework.jdbc.datasource.DriverManagerDataSource
//import org.springframework.jdbc.datasource.init.DataSourceInitializer
//import org.springframework.jdbc.datasource.init.DatabasePopulator
//import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
//import javax.sql.DataSource
//
//@Configuration
//class DataSourceTokenStore {
//    @Value("classpath:schema.sql")
//    lateinit var schemaScript: Resource
//
//    @Bean
//    fun dataSourceInitializer(dataSource : DataSource) : DataSourceInitializer {
//        var initialiser : DataSourceInitializer = DataSourceInitializer()
//        initialiser.setDataSource(dataSource)
//        initialiser.setDatabasePopulator(databasePopulator())
//        return initialiser
//    }
//
//    fun databasePopulator() : DatabasePopulator {
//        var populator : ResourceDatabasePopulator = ResourceDatabasePopulator()
//        populator.addScript(schemaScript)
//        return populator
//    }
//
//    @Bean
//    fun dataSource() : DataSource {
//        var dataSource : DriverManagerDataSource = DriverManagerDataSource()
//        dataSource.setDriverClassName(System.getProperty("driverClassName"))
//        dataSource.setUrl(System.getProperty())
//    }
//}
//
