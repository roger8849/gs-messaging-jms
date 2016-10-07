package hello;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@SpringBootApplication
@EnableJms
public class Application {
	
	private static final String DEFAULT_BROKER_URL = "tcp://localhost:61616";
	
	private static final String TEST_QUEUE = "test";
	
	@Bean
	public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) throws JMSException {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		connectionFactory = new ActiveMQConnectionFactory(DEFAULT_BROKER_URL);
		configurer.configure(factory, connectionFactory);
		return factory;
	}
	
	@Bean //Serialize message content to json using TextMessage
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}
	
	public static void main(String[] args) {
		//Launch the application
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		
		JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
		jmsTemplate.setDefaultDestinationName(TEST_QUEUE);
		
		//Send a message with a POKO - the template reuse the message converter.
		System.out.println( "Sending an email message." );
		jmsTemplate.convertAndSend( new Email("info@example.com", "Hello Roger this is a Active MQ message."));
		
	}

}
