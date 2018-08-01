package jahv.example.springbootjms.listener;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private static AtomicInteger atomicInteger = new AtomicInteger();

    @JmsListener(destination = "inbound.jahv.jms.proof.resend")
    @SendTo("outbound.jahv.jms.proof")
    public String receiveResponseMessage(final Message jsonMessage) throws JMSException {
        LOGGER.info("Received message: " + jsonMessage);
        String response = handleMessage(jsonMessage);
        return response;
    }

    @JmsListener(destination = "inbound.jahv.jms.proof.noresend")
    public void receiveMessage(final Message jsonMessage) throws JMSException {
        LOGGER.info("Received message ["+ atomicInteger.addAndGet(1) +"]: " + jsonMessage);
        String response = handleMessage(jsonMessage);
        LOGGER.info(response);
    }

    private String handleMessage(Message jsonMessage) throws JMSException {
        String response = null;
        if(jsonMessage instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) jsonMessage;
            String messageData = textMessage.getText();
            Map map = new Gson().fromJson(messageData, Map.class);
            response = "Hello " + map.get("name");
        }
        return response;
    }
}
