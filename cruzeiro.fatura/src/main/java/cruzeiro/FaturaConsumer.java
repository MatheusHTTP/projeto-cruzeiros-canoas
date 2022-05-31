package cruzeiro;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FaturaConsumer {
	
	@Value("${topic.name.consumer}")
	private String topicName;
	
	@Autowired
	FaturaController ctrl;
	
	private static final Logger log = LoggerFactory.getLogger(FaturaConsumer.class);
	
	@KafkaListener(topics = "${topic.name.consumer}", groupId = "group_id")
	public void consume(ConsumerRecord<String, String> payload) throws JsonMappingException, JsonProcessingException{
		log.info("Tópico: {}", topicName);
		log.info("key: {}", payload.key());
		log.info("Headers: {}", payload.headers());
		log.info("Partion: {}", payload.partition());
		log.info("Valor: {}", payload.value());
		
		ObjectMapper mapper = new ObjectMapper();
		ReservaBean reserva = mapper.readValue(payload.value(), ReservaBean.class);
		
		ctrl.emitirFatura(reserva);
	}
}
