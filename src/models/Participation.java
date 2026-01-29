package models;

import java.time.LocalDate;

/**
 * Modèle représentant la participation d'un membre à une tontine
 * Projet INF2212 - Université de Yaoundé I
 */
public class Participation {
    
    private int idParticipation;
    private int idMembre;
    private int idTontine;
    private int nombreParts;
    private LocalDate dateInscription;
    private String statut;
    
    // Associations
    private Membre membre;
    private Tontine tontine;
    
    // Constructeurs
    public Participation() {}
    
    public Participation(int idMembre, int idTontine, int nombreParts) {
        this.idMembre = idMembre;
        this.idTontine = idTontine;
        this.nombreParts = nombreParts;
        this.dateInscription = LocalDate.now();
        this.statut = "active";
    }
    
    // Getters et Setters
    public int getIdParticipation() {
        return idParticipation;
    }
    
    public void setIdParticipation(int idParticipation) {
        this.idParticipation = idParticipation;
    }
    
    public int getIdMembre() {
        return idMembre;
    }
    
    public void setIdMembre(int idMembre) {
        this.idMembre = idMembre;
    }
    
    public int getIdTontine() {
        return idTontine;
    }
    
    public void setIdTontine(int idTontine) {
        this.idTontine = idTontine;
    }
    
    public int getNombreParts() {
        return nombreParts;
    }
    
    public void setNombreParts(int nombreParts) {
        this.nombreParts = nombreParts;
    }
    
    public LocalDate getDateInscription() {
        return dateInscription;
    }
    
    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public Membre getMembre() {
        return membre;
    }
    
    public void setMembre(Membre membre) {
        this.membre = membre;
    }
    
    public Tontine getTontine() {
        return tontine;
    }
    
    public void setTontine(Tontine tontine) {
        this.tontine = tontine;
    }
    
    // Méthodes utilitaires
    public boolean isActive() {
        return "active".equals(statut);
    }
    
    public boolean isRetiree() {
        return "retiree".equals(statut);
    }
    
    public boolean isTontineObligatoire() {
        return tontine != null && tontine.isObligatoire();
    }
    
    public boolean isTontineOptionnelle() {
        return tontine != null && tontine.isOptionnelle();
    }
    
    public String getNomMembre() {
        return membre != null ? membre.getNomComplet() : "Inconnu";
    }
    
    public String getNomTontine() {
        return tontine != null ? tontine.getNom() : "Inconnue";
    }
    
    public String getDescription() {
        if (membre != null && tontine != null) {
            return membre.getNomComplet() + " - " + tontine.getNom() + " (" + nombreParts + " part(s))";
        }
        return "Participation " + idParticipation;
    }
    
    @Override
    public String toString() {
        return getNomMembre() + " → " + getNomTontine() + " (" + nombreParts + " parts)";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Participation that = (Participation) obj;
        return idParticipation == that.idParticipation;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(idParticipation);
    }
}
