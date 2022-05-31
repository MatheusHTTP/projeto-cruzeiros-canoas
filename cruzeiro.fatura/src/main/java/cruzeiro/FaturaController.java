package cruzeiro;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class FaturaController {
	@Value("${app.name}")
	String appName;

	@Autowired
	FaturaDAO dao;
	
	@PostMapping("/fatura")
	public ResponseEntity<String> emitirFatura(@Valid @RequestBody ReservaBean reserva) {
		double preco = getCabinePreco(reserva.getIdCabine());
		
		FaturaBean fatura = new FaturaBean();
		fatura.setIdCabine(reserva.getIdCabine());
		fatura.setDataFatura(reserva.getData());
		fatura.setTotal(reserva.getTotalPessoas()*preco);
		
		dao.save(fatura);
		return new ResponseEntity<>(HttpStatus.OK);

	}
	
	@GetMapping("/obter")
	public ResponseEntity<Iterable<FaturaBean>> obterFaturas() {
		return new ResponseEntity<Iterable<FaturaBean>>(dao.findAll(), HttpStatus.OK);

	}
	
	private double getCabinePreco(int idCabine) {
		String uri = "http://localhost:8081/obter/"+idCabine;
		RestTemplate restTemplate = new RestTemplate();
		CabineBean cabine = restTemplate.getForObject(uri, CabineBean.class);

		return cabine.getPrecoPessoa();	

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
