package models;

import java.time.LocalDate;

public class Beneficiaire {
    
    private int idBeneficiaire;
    private int idSeance;
    private int idParticipation;
    private double montantGain;
    private LocalDate dateAttribution;
    private String modePaiement;
    private String statut;
    
    // Associations
    private Seance seance;
    private Participation participation;
    
    public Beneficiaire() {}
    
    public Beneficiaire(int idSeance, int idParticipation, double montantGain) {
        this.idSeance = idSeance;
        this.idParticipation = idParticipation;
        this.montantGain = montantGain;
        this.dateAttribution = LocalDate.now();
        this.statut = "attribue";
        this.modePaiement = "especes";
    }
    
    public int getIdBeneficiaire() {
        return idBeneficiaire;
    }
    
    public void setIdBeneficiaire(int idBeneficiaire) {
        this.idBeneficiaire = idBeneficiaire;
    }
    
    public int getIdSeance() {
        return idSeance;
    }
    
    public void setIdSeance(int idSeance) {
        this.idSeance = idSeance;
    }
    
    public int getIdParticipation() {
        return idParticipation;
    }
    
    public void setIdParticipation(int idParticipation) {
        this.idParticipation = idParticipation;
    }
    
    public double getMontantGain() {
        return montantGain;
    }
    
    public void setMontantGain(double montantGain) {
        this.montantGain = montantGain;
    }
    
    public LocalDate getDateAttribution() {
        return dateAttribution;
    }
    
    public void setDateAttribution(LocalDate dateAttribution) {
        this.dateAttribution = dateAttribution;
    }
    
    public String getModePaiement() {
        return modePaiement;
    }
    
    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public Seance getSeance() {
        return seance;
    }
    
    public void setSeance(Seance seance) {
        this.seance = seance;
    }
    
    public Participation getParticipation() {
        return participation;
    }
    
    public void setParticipation(Participation participation) {
        this.participation = participation;
    }
    
    public boolean estAttribue() {
        return "attribue".equals(statut);
    }
    
    public boolean estPaye() {
        return "paye".equals(statut);
    }
    
    public String getStatutAffichage() {
        switch (statut) {
            case "attribue": return "Attribué";
            case "paye": return "Payé";
            default: return statut;
        }
    }
    
    public String getModePaiementAffichage() {
        switch (modePaiement) {
            case "especes": return "Espèces";
            case "virement": return "Virement";
            case "cheque": return "Chèque";
            case "mobile_money": return "Mobile Money";
            default: return modePaiement;
        }
    }
    
    public String getNomBeneficiaire() {
        return participation != null && participation.getMembre() != null 
            ? participation.getMembre().getNomComplet() 
            : "Inconnu";
    }
    
    public String getNomTontine() {
        return participation != null && participation.getTontine() != null 
            ? participation.getTontine().getNom() 
            : "Inconnue";
    }
    
    @Override
    public String toString() {
        return getNomBeneficiaire() + " - " + String.format("%.0f FCFA", montantGain) + 
               " (" + getStatutAffichage() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Beneficiaire beneficiaire = (Beneficiaire) obj;
        return idBeneficiaire == beneficiaire.idBeneficiaire;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(idBeneficiaire);
    }
}
