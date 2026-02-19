package ch.bfh.eglim8.spring.jms.jms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class Publisher implements CommandLineRunner {

    @Value("${chat.queue}")
    private String queue;
    @Value("${chat.user}")
    private String user;

    private final JmsTemplate jmsTemplate;

    public Publisher(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter messages to sent...");
        while (true) {
            String text = scanner.nextLine();
            if (text.isEmpty()) break;

            // Variante 1: TextMessage senden (Queue als Property)
            // jmsTemplate.convertAndSend(queue, text);

            // Variante 2: Message ist typisiert mit ChatMessage (Queue & User als Property)
            ChatMessage message = new ChatMessage(user, text);
            jmsTemplate.convertAndSend(queue, message);


 /*           jmsTemplate.send(queue, session -> {
                Message message = session.createTextMessage(text);
                message.setStringProperty("user", user);
                return message;
            });
 */       }
    }
}
