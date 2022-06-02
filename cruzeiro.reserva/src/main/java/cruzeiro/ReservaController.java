package cruzeiro;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ReservaController {

	@Value("${app.name}")
	String appName;

	@Autowired
	ReservaDAO dao;
	
	@Autowired
	ReservaProducer producer;

	@GetMapping("/obterCabines")
	private CabineBean[] getCabines() {
		String uri = "http://localhost:8081/obter";
		RestTemplate restTemplate = new RestTemplate();
		CabineBean[] cabines = restTemplate.getForObject(uri, CabineBean[].class);
		//Iterator<CabineBean> cabines = Arrays.stream(response).iterator();
		
		return cabines;	

	}
	
	@GetMapping("/teste")
	public ResponseEntity<Iterable<ReservaBean>> reservas() {
		return new ResponseEntity<Iterable<ReservaBean>>(dao.findAll(), HttpStatus.OK);
	}
	
	private Iterable<ReservaBean> obterReservas() {
		return dao.findAll();
	}

	// Verificar qual a cabine que comporta o total de pessoas (sempre a menor que
	// possa comportar o total de
	// pessoas requerido) e se não está já reservada na data informada (integrar com
	// o end point Cabine);

	@GetMapping("/reserva/{totalPessoas}/{data}")
	public ResponseEntity<String> obterReserva(	
											@PathVariable Integer totalPessoas,
											@DateTimeFormat(pattern="yyyy-MM-dd") 
											@PathVariable LocalDate data) {
		
		System.out.println("---> "+data);
		
		CabineBean[] cabines = getCabines();
		Iterable<ReservaBean> reservas = obterReservas();
		
		ArrayList<Integer> reservados = new ArrayList<Integer>();	

		for (ReservaBean reservado : reservas) {
			if(reservado.getData().compareTo(data) == 0) {
				reservados.add(reservado.getIdCabine());
			}
			
		}
		System.out.println(reservados.toString());
		CabineBean reservar = null;
		for (CabineBean cabine : cabines) {
			if(!reservados.contains(cabine.getIdCabine())) {
				if(cabine.getMaxPessoas() >= totalPessoas) {
					if(reservar == null || cabine.getMaxPessoas() < reservar.getMaxPessoas()) {
						reservar = cabine;
					}
				}
			}
		}
		
		if(reservar != null) {
			ReservaBean dadoReserva = new ReservaBean();
			dadoReserva.setIdCabine(reservar.getIdCabine());
			dadoReserva.setData(data);
			dadoReserva.setTotalPessoas(totalPessoas);
			
			dao.save(dadoReserva);
			
			JSONObject jsonObject = new JSONObject(dadoReserva);
			String msg = jsonObject.toString();
			
			System.out.println(msg);
			producer.enviar(msg);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<String, String>();

		errors.put("codigo", "BAD");

		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		return errors;
	}
}
