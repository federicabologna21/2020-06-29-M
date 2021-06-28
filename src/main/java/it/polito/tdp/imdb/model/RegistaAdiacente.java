package it.polito.tdp.imdb.model;

public class RegistaAdiacente {

	Director d;
	double peso; // il numero di attori condivisi coincide con il peso dell'arco
	
	public RegistaAdiacente(Director d, double peso) {
		super();
		this.d = d;
		this.peso = peso;
	}

	public Director getD() {
		return d;
	}

	public void setD(Director d) {
		this.d = d;
	}

	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

	@Override
	public String toString() {
		return d + " - # attori condivisi: " + peso;
	}
	
	
	
	
}
