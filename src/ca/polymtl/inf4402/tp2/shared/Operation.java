package ca.polymtl.inf4402.tp2.shared;

import java.io.Serializable;

public class Operation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nom ;
	private int operande;
	
	public Operation(String nom, int operande) {
		this.nom = nom;
		this.operande = operande;
	}
	
	public String getNom() {
		return nom;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public int getOperande() {
		return operande;
	}
	
	public void setOperande(int operande) {
		this.operande = operande;
	}
}
