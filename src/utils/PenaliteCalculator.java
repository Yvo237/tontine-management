package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import dao.CotisationDAO;
import dao.CreditDAO;
import dao.SeanceDAO;
import dao.TontineDAO;
import models.Cotisation;
import models.Credit;
import models.Seance;
import models.Tontine;
import models.TypeTontine;


public class PenaliteCalculator {
    
    private static final BigDecimal TAUX_PENALITE_RETRARD_COTISATION = new BigDecimal("0.05"); // 5% du montant
    private static final BigDecimal TAUX_PENALITE_RETRARD_REMBOURSEMENT = new BigDecimal("0.10"); // 10% du montant
    private static final BigDecimal PENALITE_FIXE_ABSENCE = new BigDecimal("1000"); // 1000 FCFA par absence
    
    private CotisationDAO cotisationDAO;
    private CreditDAO creditDAO;
    private SeanceDAO seanceDAO;
    private TontineDAO tontineDAO;
    
    public PenaliteCalculator() {
        this.cotisationDAO = new CotisationDAO();
        this.creditDAO = new CreditDAO();
        this.seanceDAO = new SeanceDAO();
        this.tontineDAO = new TontineDAO();
    }
    
   
    public BigDecimal calculerPenaliteRetardCotisation(int idMembre, int idTontine) {
        try {
            List<Seance> seances = seanceDAO.findByTontine(idTontine);
            if (seances.isEmpty()) {
                return BigDecimal.ZERO;
            }
            
            Seance derniereSeance = seances.get(seances.size() - 1);
            LocalDate dateEcheance = derniereSeance.getDateSeance();
            LocalDate aujourdHui = LocalDate.now();
            

            if (aujourdHui.isAfter(dateEcheance)) {
                List<Cotisation> cotisations = cotisationDAO.findAll();
                boolean cotisationPayee = cotisations.stream()
                    .filter(c -> c.getIdMembre() == idMembre)
                    .anyMatch(c -> c.getDatePaiement() != null && 
                               !c.getDatePaiement().isAfter(dateEcheance));
                
                if (!cotisationPayee) {
                    long joursRetard = ChronoUnit.DAYS.between(dateEcheance, aujourdHui);
                    
                    Tontine tontine = derniereSeance.getTontine();
                    BigDecimal montantCotisation = BigDecimal.ZERO;
                    if (tontine != null && tontine.getTypeTontine() != null) {
                        montantCotisation = tontine.getTypeTontine().getMontantCotisation();
                    }
                    
                    BigDecimal penalite = montantCotisation
                        .multiply(TAUX_PENALITE_RETRARD_COTISATION)
                        .multiply(new BigDecimal(joursRetard))
                        .divide(new BigDecimal(30), 2, RoundingMode.HALF_UP);
                    
                    return penalite;
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul de pénalité de retard de cotisation: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    public BigDecimal calculerPenaliteRetardRemboursement(int idCredit) {
        try {
            Credit credit = creditDAO.findById(idCredit);
            if (credit == null || credit.getStatut().equals("rembourse")) {
                return BigDecimal.ZERO;
            }
            
            LocalDate dateEcheance = credit.getDateEcheance();
            LocalDate aujourdHui = LocalDate.now();
            
            if (aujourdHui.isAfter(dateEcheance)) {
                long joursRetard = ChronoUnit.DAYS.between(dateEcheance, aujourdHui);
                
                BigDecimal montantRestant = credit.getMontantEmprunte().subtract(credit.getMontantRembourse());
                BigDecimal penalite = montantRestant
                    .multiply(TAUX_PENALITE_RETRARD_REMBOURSEMENT)
                    .multiply(new BigDecimal(joursRetard))
                    .divide(new BigDecimal(30), 2, RoundingMode.HALF_UP);
                
                return penalite;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul de pénalité de retard de remboursement: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    
    public BigDecimal calculerPenaliteAbsence(int idMembre, int idSeance) {
        try {
            Seance seance = seanceDAO.findById(idSeance);
            if (seance == null || !seance.getStatut().equals("effectuee")) {
                return BigDecimal.ZERO;
            }

            List<Cotisation> cotisations = cotisationDAO.findAll();
            boolean etaitPresent = cotisations.stream()
                .filter(c -> c.getIdMembre() == idMembre)
                .anyMatch(c -> c.getIdSeance() == idSeance);
            
            if (!etaitPresent) {
                return PENALITE_FIXE_ABSENCE;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul de pénalité d'absence: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    public BigDecimal calculerTotalPenalitesMembre(int idMembre) {
        BigDecimal total = BigDecimal.ZERO;
        
        try {
            List<Tontine> tontines = tontineDAO.findAll();
            for (Tontine tontine : tontines) {
                total = total.add(calculerPenaliteRetardCotisation(idMembre, tontine.getIdTontine()));
            }
            
            List<Credit> credits = creditDAO.findByMembre(idMembre);
            for (Credit credit : credits) {
                total = total.add(calculerPenaliteRetardRemboursement(credit.getIdCredit()));
            }
            
            LocalDate dateDebut = LocalDate.now().minusDays(30);
            List<Seance> seancesRecentes = seanceDAO.findAll();
            seancesRecentes = seancesRecentes.stream()
                .filter(s -> s.getDateSeance().isAfter(dateDebut))
                .collect(java.util.stream.Collectors.toList());
            for (Seance seance : seancesRecentes) {
                total = total.add(calculerPenaliteAbsence(idMembre, seance.getIdSeance()));
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul total des pénalités: " + e.getMessage());
        }
        
        return total;
    }
    

    public String genererRapportPenalites(int idMembre) {
        StringBuilder rapport = new StringBuilder();
        rapport.append("Rapport des pénalités calculées pour le membre #").append(idMembre).append(":\n\n");
        
        try {
            List<Tontine> tontines = tontineDAO.findAll();
            for (Tontine tontine : tontines) {
                BigDecimal penalite = calculerPenaliteRetardCotisation(idMembre, tontine.getIdTontine());
                if (penalite.compareTo(BigDecimal.ZERO) > 0) {
                    rapport.append("- Retard cotisation (").append(tontine.getNom()).append("): ")
                           .append(penalite).append(" FCFA\n");
                }
            }
            
            List<Credit> credits = creditDAO.findByMembre(idMembre);
            for (Credit credit : credits) {
                BigDecimal penalite = calculerPenaliteRetardRemboursement(credit.getIdCredit());
                if (penalite.compareTo(BigDecimal.ZERO) > 0) {
                    rapport.append("- Retard remboursement (Crédit #").append(credit.getIdCredit()).append("): ")
                           .append(penalite).append(" FCFA\n");
                }
            }
            
            LocalDate dateDebut = LocalDate.now().minusDays(30);
            List<Seance> seancesRecentes = seanceDAO.findAll();
            seancesRecentes = seancesRecentes.stream()
                .filter(s -> s.getDateSeance().isAfter(dateDebut))
                .collect(java.util.stream.Collectors.toList());
            for (Seance seance : seancesRecentes) {
                BigDecimal penalite = calculerPenaliteAbsence(idMembre, seance.getIdSeance());
                if (penalite.compareTo(BigDecimal.ZERO) > 0) {
                    rapport.append("- Absence (Séance du ").append(seance.getDateSeance()).append("): ")
                           .append(penalite).append(" FCFA\n");
                }
            }
            
            BigDecimal total = calculerTotalPenalitesMembre(idMembre);
            rapport.append("\nTotal des pénalités: ").append(total).append(" FCFA");
            
        } catch (Exception e) {
            rapport.append("Erreur lors de la génération du rapport: ").append(e.getMessage());
        }
        
        return rapport.toString();
    }
    
    public static BigDecimal getTauxPenaliteRetardCotisation() {
        return TAUX_PENALITE_RETRARD_COTISATION;
    }
    
    public static BigDecimal getTauxPenaliteRetardRemboursement() {
        return TAUX_PENALITE_RETRARD_REMBOURSEMENT;
    }
    
    public static BigDecimal getPenaliteFixeAbsence() {
        return PENALITE_FIXE_ABSENCE;
    }
}
