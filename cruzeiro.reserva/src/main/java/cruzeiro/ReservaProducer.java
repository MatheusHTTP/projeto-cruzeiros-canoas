package cruzeiro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service
public class ReservaProducer {
	
	@Value("${topic.name.producer}")
	private String topicName;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	public void enviar(ReservaBean reserva) {
		Gson gson = new Gson();
		String enviar = gson.toJson(reserva);
		
		kafkaTemplate.send(topicName, enviar);
	}
}