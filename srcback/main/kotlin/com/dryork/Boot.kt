package com.dryork

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ImportResource
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement



/**
 *
 *
 * spring boot starter
 *
 * @author jsen
 * @since 2018-04-08
 */
@SpringBootApplication(exclude = [(MultipartAutoConfiguration::class), (DataSourceAutoConfiguration::class)])
@EnableTransactionManagement
@EnableCaching
@EnableScheduling
@ImportResource(locations = ["classpath:dc-config.xml"])
open class Boot {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val springApplication = SpringApplication(Boot::class.java)
//            springApplication.addListeners(ApplicationEventListener())
            springApplication.run(*args)
        }
    }
}

