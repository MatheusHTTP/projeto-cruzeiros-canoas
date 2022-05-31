package cruzeiro;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity(name = "Cabine")
public class CabineBean {
	@Column(name = "idCabine")
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idCabine;
	
	@NotNull (message = "Informe o máximo de pessoas por cabine.")
	private int maxPessoas;
	
	@NotNull (message = "Informe o preço por pessoa.")
	private double precoPessoa;


	public CabineBean() {
		super();
	}
	
	public CabineBean(int idCabine, int maxPessoas, double precoPessoa) {
		super();
		this.idCabine = idCabine;
		this.maxPessoas = maxPessoas;
		this.precoPessoa = precoPessoa;
	}

	public int getIdCabine() {
		return idCabine;
	}

	public void setIdCabine(int idCabine) {
		this.idCabine = idCabine;
	}

	public int getMaxPessoas() {
		return maxPessoas;
	}

	public void setMaxPessoas(int maxPessoas) {
		this.maxPessoas = maxPessoas;
	}

	public double getPrecoPessoa() {
		return precoPessoa;
	}

	public void setPrecoPessoa(double precoPessoa) {
		this.precoPessoa = precoPessoa;
	}
	
}
