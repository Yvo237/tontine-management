package models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Penalite {
    
    private int idPenalite;
    private int idMembre;
    private Integer idSeance;
    private String motif;
    private BigDecimal montant;
    private LocalDate datePenalite;
    private boolean payee;
    private LocalDateTime createdAt;
    
    
    private Membre membre;
    private Seance seance;
    
    public Penalite() {
        this.payee = false;
        this.createdAt = LocalDateTime.now();
    }
    
    public Penalite(int idMembre, String motif, BigDecimal montant, LocalDate datePenalite) {
        this();
        this.idMembre = idMembre;
        this.motif = motif;
        this.montant = montant;
        this.datePenalite = datePenalite;
    }
    
    public Penalite(int idMembre, Integer idSeance, String motif, BigDecimal montant, LocalDate datePenalite) {
        this(idMembre, motif, montant, datePenalite);
        this.idSeance = idSeance;
    }
    
    // Getters et Setters
    public int getIdPenalite() {
        return idPenalite;
    }
    
    public void setIdPenalite(int idPenalite) {
        this.idPenalite = idPenalite;
    }
    
    public int getIdMembre() {
        return idMembre;
    }
    
    public void setIdMembre(int idMembre) {
        this.idMembre = idMembre;
    }
    
    public Integer getIdSeance() {
        return idSeance;
    }
    
    public void setIdSeance(Integer idSeance) {
        this.idSeance = idSeance;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    public BigDecimal getMontant() {
        return montant;
    }
    
    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
    
    public LocalDate getDatePenalite() {
        return datePenalite;
    }
    
    public void setDatePenalite(LocalDate datePenalite) {
        this.datePenalite = datePenalite;
    }
    
    public boolean isPayee() {
        return payee;
    }
    
    public void setPayee(boolean payee) {
        this.payee = payee;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Membre getMembre() {
        return membre;
    }
    
    public void setMembre(Membre membre) {
        this.membre = membre;
    }
    
    public Seance getSeance() {
        return seance;
    }
    
    public void setSeance(Seance seance) {
        this.seance = seance;
    }
    
    
    public void marquerCommePayee() {
        this.payee = true;
    }
    
    public boolean isImpayee() {
        return !payee;
    }
    
    @Override
    public String toString() {
        return String.format("Pénalité #%d - %s: %.2f FCFA (%s)", 
                idPenalite, motif, montant, payee ? "Payée" : "Impayée");
    }
}
