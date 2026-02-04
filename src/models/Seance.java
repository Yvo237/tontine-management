package models;

import java.time.LocalDate;

public class Seance {
    
    private int idSeance;
    private int idTontine;
    private int numeroTour;
    private LocalDate dateSeance;
    private String lieu;
    private String statut;
    private String observations;
    
    // Association
    private Tontine tontine;
    
    public Seance() {}
    
    public Seance(int idTontine, int numeroTour, LocalDate dateSeance) {
        this.idTontine = idTontine;
        this.numeroTour = numeroTour;
        this.dateSeance = dateSeance;
        this.statut = "planifiee";
    }
    
    public Seance(int idTontine, int numeroTour, LocalDate dateSeance, String lieu) {
        this(idTontine, numeroTour, dateSeance);
        this.lieu = lieu;
    }
    
    public int getIdSeance() {
        return idSeance;
    }
    
    public void setIdSeance(int idSeance) {
        this.idSeance = idSeance;
    }
    
    public int getIdTontine() {
        return idTontine;
    }
    
    public void setIdTontine(int idTontine) {
        this.idTontine = idTontine;
    }
    
    public int getNumeroTour() {
        return numeroTour;
    }
    
    public void setNumeroTour(int numeroTour) {
        this.numeroTour = numeroTour;
    }
    
    public LocalDate getDateSeance() {
        return dateSeance;
    }
    
    public void setDateSeance(LocalDate dateSeance) {
        this.dateSeance = dateSeance;
    }
    
    public String getLieu() {
        return lieu;
    }
    
    public void setLieu(String lieu) {
        this.lieu = lieu;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public String getObservations() {
        return observations;
    }
    
    public void setObservations(String observations) {
        this.observations = observations;
    }
    
    public Tontine getTontine() {
        return tontine;
    }
    
    public void setTontine(Tontine tontine) {
        this.tontine = tontine;
    }
    
    public boolean isPlanifiee() {
        return "planifiee".equals(statut);
    }
    
    public boolean isEnCours() {
        return "en_cours".equals(statut);
    }
    
    public boolean isTerminee() {
        return "terminee".equals(statut);
    }
    
    public boolean estDansLePasse() {
        return dateSeance != null && dateSeance.isBefore(LocalDate.now());
    }
    
    public boolean estAujourdhui() {
        return dateSeance != null && dateSeance.isEqual(LocalDate.now());
    }
    
    public boolean estDansLeFutur() {
        return dateSeance != null && dateSeance.isAfter(LocalDate.now());
    }
    
    public String getNomTontine() {
        return tontine != null ? tontine.getNom() : "Inconnue";
    }
    
    public String getStatutAffichage() {
        switch (statut) {
            case "planifiee": return "Planifiée";
            case "en_cours": return "En cours";
            case "terminee": return "Terminée";
            default: return statut;
        }
    }
    
    public String getTitreComplet() {
        return "Tour " + numeroTour + " - " + getNomTontine();
    }
    
    @Override
    public String toString() {
        return getTitreComplet() + " (" + dateSeance + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Seance seance = (Seance) obj;
        return idSeance == seance.idSeance;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(idSeance);
    }
}
