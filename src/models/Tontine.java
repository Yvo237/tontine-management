package models;

import java.time.LocalDate;

public class Tontine {
    
    private int idTontine;
    private int idType;
    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int nombreTours;
    private int tourActuel;
    private String statut;
    private TypeTontine typeTontine; // Association
    
    public Tontine() {}
    
    public Tontine(int idType, String nom, LocalDate dateDebut, int nombreTours) {
        this.idType = idType;
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.nombreTours = nombreTours;
        this.tourActuel = 1;
        this.statut = "active";
    }
    
    public int getIdTontine() {
        return idTontine;
    }
    
    public void setIdTontine(int idTontine) {
        this.idTontine = idTontine;
    }
    
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
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    
    public int getNombreTours() {
        return nombreTours;
    }
    
    public void setNombreTours(int nombreTours) {
        this.nombreTours = nombreTours;
    }
    
    public int getTourActuel() {
        return tourActuel;
    }
    
    public void setTourActuel(int tourActuel) {
        this.tourActuel = tourActuel;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public TypeTontine getTypeTontine() {
        return typeTontine;
    }
    
    public void setTypeTontine(TypeTontine typeTontine) {
        this.typeTontine = typeTontine;
    }
    
    public boolean isActive() {
        return "active".equals(statut);
    }
    
    public boolean isTerminee() {
        return "terminee".equals(statut);
    }
    
    public boolean isSuspendue() {
        return "suspendue".equals(statut);
    }
    
    public boolean isEnCours() {
        return isActive() && tourActuel <= nombreTours;
    }
    
    public int getToursRestants() {
        return Math.max(0, nombreTours - tourActuel + 1);
    }
    
    public double getProgression() {
        if (nombreTours == 0) return 0;
        return (double) (tourActuel - 1) / nombreTours * 100;
    }
    
    public boolean isObligatoire() {
        return typeTontine != null && typeTontine.isObligatoire();
    }
    
    public boolean isOptionnelle() {
        return typeTontine != null && typeTontine.isOptionnelle();
    }
    
    @Override
    public String toString() {
        return nom + " (Tour " + tourActuel + "/" + nombreTours + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Tontine tontine = (Tontine) obj;
        return idTontine == tontine.idTontine;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(idTontine);
    }
}
