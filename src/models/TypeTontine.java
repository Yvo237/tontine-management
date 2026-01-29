package models;

import java.math.BigDecimal;

/**
 * Modèle représentant un type de tontine
 * Projet INF2212 - Université de Yaoundé I
 */
public class TypeTontine {
    
    private int idType;
    private String nom;
    private String description;
    private boolean estObligatoire;
    private BigDecimal montantCotisation;
    private String frequence;
    
    // Constructeurs
    public TypeTontine() {}
    
    public TypeTontine(String nom, String description, boolean estObligatoire, 
                      BigDecimal montantCotisation, String frequence) {
        this.nom = nom;
        this.description = description;
        this.estObligatoire = estObligatoire;
        this.montantCotisation = montantCotisation;
        this.frequence = frequence;
    }
    
    // Getters et Setters
    public int getIdType() {
        return idType;
    }
    
    public void setIdType(int idType) {
        this.idType = idType;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isEstObligatoire() {
        return estObligatoire;
    }
    
    public void setEstObligatoire(boolean estObligatoire) {
        this.estObligatoire = estObligatoire;
    }
    
    public BigDecimal getMontantCotisation() {
        return montantCotisation;
    }
    
    public void setMontantCotisation(BigDecimal montantCotisation) {
        this.montantCotisation = montantCotisation;
    }
    
    public String getFrequence() {
        return frequence;
    }
    
    public void setFrequence(String frequence) {
        this.frequence = frequence;
    }
    
    // Méthodes utilitaires
    public boolean isObligatoire() {
        return estObligatoire;
    }
    
    public boolean isOptionnelle() {
        return !estObligatoire;
    }
    
    public String getTypeDescription() {
        return estObligatoire ? "Obligatoire" : "Optionnelle";
    }
    
    @Override
    public String toString() {
        return nom + " (" + getTypeDescription() + ") - " + montantCotisation + " FCFA";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        TypeTontine that = (TypeTontine) obj;
        return idType == that.idType;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(idType);
    }
}
