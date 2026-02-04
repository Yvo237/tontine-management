package models;

import java.time.LocalDate;

public class ContributionFIAC {
    private int idContribution;
    private int idProjet;
    private int idMembre;
    private double montant;
    private LocalDate dateContribution;
    
    // Associations
    private ProjetFIAC projet;
    private Membre membre;
    
    public ContributionFIAC() {
    }
    
    public ContributionFIAC(int idContribution, int idProjet, int idMembre, 
                           double montant, LocalDate dateContribution) {
        this.idContribution = idContribution;
        this.idProjet = idProjet;
        this.idMembre = idMembre;
        this.montant = montant;
        this.dateContribution = dateContribution;
    }
    
    // Getters et Setters
    public int getIdContribution() {
        return idContribution;
    }
    
    public void setIdContribution(int idContribution) {
        this.idContribution = idContribution;
    }
    
    public int getIdProjet() {
        return idProjet;
    }
    
    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }
    
    public int getIdMembre() {
        return idMembre;
    }
    
    public void setIdMembre(int idMembre) {
        this.idMembre = idMembre;
    }
    
    public double getMontant() {
        return montant;
    }
    
    public void setMontant(double montant) {
        this.montant = montant;
    }
    
    public LocalDate getDateContribution() {
        return dateContribution;
    }
    
    public void setDateContribution(LocalDate dateContribution) {
        this.dateContribution = dateContribution;
    }
    
    public ProjetFIAC getProjet() {
        return projet;
    }
    
    public void setProjet(ProjetFIAC projet) {
        this.projet = projet;
    }
    
    public Membre getMembre() {
        return membre;
    }
    
    public void setMembre(Membre membre) {
        this.membre = membre;
    }
    
    @Override
    public String toString() {
        return String.format("ContributionFIAC{id=%d, projet='%s', membre='%s', montant=%.0f FCFA, date=%s}", 
                           idContribution, 
                           projet != null ? projet.getNomProjet() : "N/A", 
                           membre != null ? membre.getNomComplet() : "N/A", 
                           montant, 
                           dateContribution);
    }
}
