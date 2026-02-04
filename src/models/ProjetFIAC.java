package models;

import java.time.LocalDate;

public class ProjetFIAC {
    private int idProjet;
    private int idTontine;
    private String nomProjet;
    private String description;
    private double montantObjectif;
    private double montantCollecte;
    private LocalDate dateDebut;
    private LocalDate dateFinPrevue;
    private String statut;
    
    // Association
    private Tontine tontine;
    
    public ProjetFIAC() {
        this.montantCollecte = 0.0;
        this.statut = "planifie";
    }
    
    public ProjetFIAC(int idProjet, int idTontine, String nomProjet, String description, 
                     double montantObjectif, double montantCollecte, LocalDate dateDebut, 
                     LocalDate dateFinPrevue, String statut) {
        this.idProjet = idProjet;
        this.idTontine = idTontine;
        this.nomProjet = nomProjet;
        this.description = description;
        this.montantObjectif = montantObjectif;
        this.montantCollecte = montantCollecte;
        this.dateDebut = dateDebut;
        this.dateFinPrevue = dateFinPrevue;
        this.statut = statut;
    }
    
    // Getters et Setters
    public int getIdProjet() {
        return idProjet;
    }
    
    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }
    
    public int getIdTontine() {
        return idTontine;
    }
    
    public void setIdTontine(int idTontine) {
        this.idTontine = idTontine;
    }
    
    public String getNomProjet() {
        return nomProjet;
    }
    
    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getMontantObjectif() {
        return montantObjectif;
    }
    
    public void setMontantObjectif(double montantObjectif) {
        this.montantObjectif = montantObjectif;
    }
    
    public double getMontantCollecte() {
        return montantCollecte;
    }
    
    public void setMontantCollecte(double montantCollecte) {
        this.montantCollecte = montantCollecte;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFinPrevue() {
        return dateFinPrevue;
    }
    
    public void setDateFinPrevue(LocalDate dateFinPrevue) {
        this.dateFinPrevue = dateFinPrevue;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public Tontine getTontine() {
        return tontine;
    }
    
    public void setTontine(Tontine tontine) {
        this.tontine = tontine;
    }
    
    // Méthodes utilitaires
    public double getPourcentageAvancement() {
        if (montantObjectif <= 0) return 0.0;
        return (montantCollecte / montantObjectif) * 100;
    }
    
    public double getResteACollecter() {
        return montantObjectif - montantCollecte;
    }
    
    public boolean estTermine() {
        return "termine".equals(statut) || montantCollecte >= montantObjectif;
    }
    
    public boolean estEnCours() {
        return "en_cours".equals(statut) && !estTermine();
    }
    
    @Override
    public String toString() {
        return String.format("ProjetFIAC{id=%d, nom='%s', objectif=%.0f FCFA, collecte=%.0f FCFA, avancement=%.1f%%, statut='%s'}", 
                           idProjet, nomProjet, montantObjectif, montantCollecte, getPourcentageAvancement(), statut);
    }
}
