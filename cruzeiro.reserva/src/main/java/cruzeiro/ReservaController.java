package cruzeiro;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

import org.json.JSONObject;

@RestController
public class ReservaController {

	@Value("${app.name}")
	String appName;

	@Autowired
	ReservaDAO dao;

	@SuppressWarnings("unchecked")
	private Iterable<CabineBean> getCabines() {
		String uri = "http://localhost:8081/obter";
		RestTemplate restTemplate = new RestTemplate();
		Iterable<CabineBean> cabines = (Iterable<CabineBean>) restTemplate.getForObject(uri, CabineBean.class);
		
		return cabines;	

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
											@PathVariable 
											@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
											Date data) {

		Iterable<CabineBean> cabines = getCabines();
		Iterable<ReservaBean> reservas = obterReservas();
		
		ArrayList<Integer> reservados = null;		
		for (ReservaBean reservado : reservas) {
			reservados.add(reservado.getIdCabine());
		}
		
		CabineBean reservar = null;
		for (CabineBean cabine : cabines) {
			if(!reservados.contains(cabine.getIdCabine())) {
				if(reservar == null || (cabine.getMaxPessoas() < reservar.getMaxPessoas() && cabine.getMaxPessoas() <= totalPessoas)) {
					reservar = cabine;
				}
			}
		}
		
		if(reservar != null) {
			ReservaBean dadoReserva = new ReservaBean();
			dadoReserva.setIdReserva(reservar.getIdCabine());
			dadoReserva.setData(data);
			dadoReserva.setTotalPessoas(totalPessoas);
			
			dao.save(dadoReserva);
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
