package com.microsoft.test.sample.Game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.microsoft.bot.integration.AdapterWithErrorHandler;
import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.integration.Configuration;
import com.microsoft.bot.integration.spring.BotController;
import com.microsoft.bot.integration.spring.BotDependencyConfiguration;

/**
 * Hello world!
 *
 */

@SpringBootApplication
@Import({BotController.class})
public class Application extends BotDependencyConfiguration
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        SpringApplication.run(Application.class, args);
    }
    
    @Override
    	public BotFrameworkHttpAdapter getBotFrameworkHttpAdaptor(Configuration configuration) {
    		return new AdapterWithErrorHandler(configuration);
    	}
    
}
