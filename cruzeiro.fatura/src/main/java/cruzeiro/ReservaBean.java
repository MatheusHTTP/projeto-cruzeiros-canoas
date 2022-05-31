package cruzeiro;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity(name = "Reserva")
public class ReservaBean {
	@Column(name = "idReserva")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idReserva;

	@NotNull(message = "Informe a data da reserva.")
	private LocalDate data;

	@NotNull(message = "Informe o total de pessoas na cabine.")
	private int totalPessoas;

	@NotNull
	private int idCabine;

	public ReservaBean() {
		super();
	}

	public ReservaBean(int idCabine, int idReserva, LocalDate data, int totalPessoas) {
		super();
		this.idReserva = idReserva;
		this.idCabine = idCabine;
		this.data = data;
		this.totalPessoas = totalPessoas;
	}

	public int getIdReserva() {
		return idReserva;
	}

	public void setIdReserva(int idReserva) {
		this.idReserva = idReserva;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public int getTotalPessoas() {
		return totalPessoas;
	}

	public void setTotalPessoas(int totalPessoas) {
		this.totalPessoas = totalPessoas;
	}

	public int getIdCabine() {
		return idCabine;
	}

	public void setIdCabine(int idCabine) {
		this.idCabine = idCabine;
	}

}
