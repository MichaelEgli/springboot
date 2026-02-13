package ch.bfh.eglim8.spring.jms.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Listener2 {
    @JmsListener(destination = "${chat.topic}")
    public void receiveMessage(ChatMessage message) {
        System.out.println("Listener2 empfängt: " + message);
    }

}
