package models;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modèle représentant une cotisation de membre pour une séance
 * Projet INF2212 - Université de Yaoundé I
 */
public class Cotisation {
    
    private int idCotisation;
    private int idSeance;
    private int idMembre;
    private BigDecimal montant;
    private LocalDate datePaiement;
    
    // Associations
    private Seance seance;
    private Membre membre;
    
    // Constructeurs
    public Cotisation() {}
    
    public Cotisation(int idSeance, int idMembre, BigDecimal montant) {
        this.idSeance = idSeance;
        this.idMembre = idMembre;
        this.montant = montant;
        this.datePaiement = LocalDate.now();
    }
    
    // Getters et Setters
    public int getIdCotisation() {
        return idCotisation;
    }
    
    public void setIdCotisation(int idCotisation) {
        this.idCotisation = idCotisation;
    }
    
    public int getIdSeance() {
        return idSeance;
    }
    
    public void setIdSeance(int idSeance) {
        this.idSeance = idSeance;
    }
    
    public int getIdMembre() {
        return idMembre;
    }
    
    public void setIdMembre(int idMembre) {
        this.idMembre = idMembre;
    }
    
    public BigDecimal getMontant() {
        return montant;
    }
    
    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
    
    public LocalDate getDatePaiement() {
        return datePaiement;
    }
    
    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }
    
    // Associations
    public Seance getSeance() {
        return seance;
    }
    
    public void setSeance(Seance seance) {
        this.seance = seance;
    }
    
    public Membre getMembre() {
        return membre;
    }
    
    public void setMembre(Membre membre) {
        this.membre = membre;
    }
    
    // Méthodes utilitaires pour compatibilité avec l'interface existante
    public String getNomMembre() {
        return membre != null ? membre.getNomComplet() : "Inconnu";
    }
    
    public String getNomTontine() {
        return seance != null && seance.getTontine() != null ? 
               seance.getTontine().getNom() : "Inconnue";
    }
    
    @Override
    public String toString() {
        return getNomMembre() + " - " + montant + " FCFA (" + datePaiement + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Cotisation that = (Cotisation) obj;
        return idCotisation == that.idCotisation;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(idCotisation);
    }
}
