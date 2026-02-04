package models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Credit {
    
    private int idCredit;
    private int idMembre;
    private int idTontine;
    private BigDecimal montantEmprunte;
    private BigDecimal tauxInteret;
    private LocalDate dateEmprunt;
    private LocalDate dateEcheance;
    private BigDecimal montantRembourse;
    private String statut;
    
    // Associations
    private Membre membre;
    private Tontine tontine;
    
    public Credit() {}
    
    public Credit(int idMembre, int idTontine, BigDecimal montantEmprunte, 
                BigDecimal tauxInteret, LocalDate dateEmprunt, LocalDate dateEcheance) {
        this.idMembre = idMembre;
        this.idTontine = idTontine;
        this.montantEmprunte = montantEmprunte;
        this.tauxInteret = tauxInteret;
        this.dateEmprunt = dateEmprunt;
        this.dateEcheance = dateEcheance;
        this.montantRembourse = BigDecimal.ZERO;
        this.statut = "en_cours";
    }
    
    public int getIdCredit() {
        return idCredit;
    }
    
    public void setIdCredit(int idCredit) {
        this.idCredit = idCredit;
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
    
    public BigDecimal getMontantEmprunte() {
        return montantEmprunte;
    }
    
    public void setMontantEmprunte(BigDecimal montantEmprunte) {
        this.montantEmprunte = montantEmprunte;
    }
    
    public BigDecimal getTauxInteret() {
        return tauxInteret;
    }
    
    public void setTauxInteret(BigDecimal tauxInteret) {
        this.tauxInteret = tauxInteret;
    }
    
    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }
    
    public void setDateEmprunt(LocalDate dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }
    
    public LocalDate getDateEcheance() {
        return dateEcheance;
    }
    
    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }
    
    public BigDecimal getMontantRembourse() {
        return montantRembourse;
    }
    
    public void setMontantRembourse(BigDecimal montantRembourse) {
        this.montantRembourse = montantRembourse;
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
    
    public boolean isEnCours() {
        return "en_cours".equals(statut);
    }
    
    public boolean isRembourse() {
        return "rembourse".equals(statut);
    }
    
    public boolean isEnRetard() {
        return "en_retard".equals(statut);
    }
    
    public BigDecimal getMontantTotal() {
        if (montantEmprunte == null || tauxInteret == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal taux = tauxInteret.divide(BigDecimal.valueOf(100));
        return montantEmprunte.multiply(BigDecimal.ONE.add(taux));
    }
    
    public BigDecimal getResteARembourser() {
        BigDecimal total = getMontantTotal();
        if (montantRembourse == null) {
            return total;
        }
        BigDecimal reste = total.subtract(montantRembourse);
        // Ne jamais retourner de montant négatif
        return reste.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : reste;
    }
    
    public boolean estCompletementRembourse() {
        return montantRembourse != null && 
               montantRembourse.compareTo(getMontantTotal()) == 0;
    }
    
    public boolean estEnRetardDePaiement() {
        return dateEcheance != null && 
               dateEcheance.isBefore(LocalDate.now()) && 
               !estCompletementRembourse();
    }
    
    public int getJoursRetard() {
        if (dateEcheance == null || !dateEcheance.isBefore(LocalDate.now())) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dateEcheance, LocalDate.now());
    }
    
    public double getPourcentageRembourse() {
        BigDecimal total = getMontantTotal();
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        if (montantRembourse == null) {
            return 0;
        }
        return montantRembourse.multiply(BigDecimal.valueOf(100))
                             .divide(total, 2, java.math.RoundingMode.HALF_UP)
                             .doubleValue();
    }
    
    public String getNomMembre() {
        return membre != null ? membre.getNomComplet() : "Inconnu";
    }
    
    public String getNomTontine() {
        return tontine != null ? tontine.getNom() : "Inconnue";
    }
    
    public String getStatutAffichage() {
        switch (statut) {
            case "en_cours": return "En cours";
            case "rembourse": return "Remboursé";
            case "en_retard": return "En retard";
            default: return statut;
        }
    }
    
    public BigDecimal getMontantRestant() {
        return getResteARembourser();
    }
    
    public LocalDate getDateDebut() {
        return dateEmprunt;
    }
    
    public int getNombreMois() {
        if (dateEmprunt == null || dateEcheance == null) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.MONTHS.between(dateEmprunt, dateEcheance);
    }
    
    public void setNombreMois(int nombreMois) {
        if (dateEmprunt != null) {
            this.dateEcheance = dateEmprunt.plusMonths(nombreMois);
        }
    }
    
    @Override
    public String toString() {
        return getNomMembre() + " - " + getNomTontine() + 
               " (" + montantEmprunte + " FCFA)";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Credit credit = (Credit) obj;
        return idCredit == credit.idCredit;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(idCredit);
    }
}
