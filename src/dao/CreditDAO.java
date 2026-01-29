package dao;

import database.DatabaseConnection;
import models.Credit;
import models.Membre;
import models.Tontine;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des crédits
 * Projet INF2212 - Université de Yaoundé I
 */
public class CreditDAO {
    
    private DatabaseConnection dbConnection;
    private MembreDAO membreDAO;
    private TontineDAO tontineDAO;
    
    public CreditDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.membreDAO = new MembreDAO();
        this.tontineDAO = new TontineDAO();
    }
    
    /**
     * Ajoute un nouveau crédit
     */
    public boolean create(Credit credit) {
        // Validation des données obligatoires
        if (credit.getMontantRembourse() == null) {
            credit.setMontantRembourse(java.math.BigDecimal.ZERO);
        }
        
        String sql = "INSERT INTO credit (id_membre, id_tontine, montant_emprunte, taux_interet, " +
                    "date_emprunt, date_echeance, montant_rembourse, statut) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, credit.getIdMembre());
            pstmt.setInt(2, credit.getIdTontine());
            pstmt.setBigDecimal(3, credit.getMontantEmprunte());
            pstmt.setBigDecimal(4, credit.getTauxInteret());
            pstmt.setDate(5, Date.valueOf(credit.getDateEmprunt()));
            pstmt.setDate(6, Date.valueOf(credit.getDateEcheance()));
            pstmt.setBigDecimal(7, credit.getMontantRembourse());
            pstmt.setString(8, credit.getStatut());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    credit.setIdCredit(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la création du crédit: " + ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Met à jour un crédit
     */
    public boolean update(Credit credit) {
        String sql = "UPDATE credit SET montant_rembourse = ?, statut = ? WHERE id_credit = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, credit.getMontantRembourse());
            pstmt.setString(2, credit.getStatut());
            pstmt.setInt(3, credit.getIdCredit());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la mise à jour du crédit: " + ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Supprime un crédit
     */
    public boolean delete(int idCredit) {
        String sql = "DELETE FROM credit WHERE id_credit = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCredit);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la suppression du crédit: " + ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Recherche un crédit par son ID
     */
    public Credit findById(int idCredit) {
        String sql = "SELECT c.*, m.nom as membre_nom, m.prenom as membre_prenom, " +
                    "t.nom as tontine_nom " +
                    "FROM Credit c " +
                    "JOIN Membre m ON c.id_membre = m.id_membre " +
                    "JOIN Tontine t ON c.id_tontine = t.id_tontine " +
                    "WHERE c.id_credit = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCredit);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCredit(rs);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la recherche du crédit: " + ex.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère tous les crédits
     */
    public List<Credit> findAll() {
        System.out.println("🔍 [DEBUG] Recherche de tous les crédits...");
        
        String sql = "SELECT c.*, m.nom as membre_nom, m.prenom as membre_prenom, " +
                    "t.nom as tontine_nom " +
                    "FROM credit c " +
                    "JOIN membre m ON c.id_membre = m.id_membre " +
                    "JOIN tontine t ON c.id_tontine = t.id_tontine " +
                    "ORDER BY c.date_emprunt DESC";
        
        System.out.println("🔍 [DEBUG] Requête SQL: " + sql);
        
        List<Credit> credits = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("🔍 [DEBUG] Connexion établie, exécution de la requête...");
            
            while (rs.next()) {
                credits.add(mapResultSetToCredit(rs));
            }
            
            System.out.println("🔍 [DEBUG] Nombre de crédits trouvés: " + credits.size());
            
        } catch (SQLException ex) {
            System.err.println("❌ [ERROR] Erreur lors de la récupération des crédits: " + ex.getMessage());
            System.err.println("❌ [ERROR] SQL State: " + ex.getSQLState());
            ex.printStackTrace();
        }
        
        return credits;
    }
    
    /**
     * Récupère les crédits par membre
     */
    public List<Credit> findByMembre(int idMembre) {
        String sql = "SELECT c.*, m.nom as membre_nom, m.prenom as membre_prenom, " +
                    "t.nom as tontine_nom " +
                    "FROM credit c " +
                    "JOIN membre m ON c.id_membre = m.id_membre " +
                    "JOIN tontine t ON c.id_tontine = t.id_tontine " +
                    "WHERE c.id_membre = ? " +
                    "ORDER BY c.date_emprunt DESC";
        
        List<Credit> credits = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idMembre);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                credits.add(mapResultSetToCredit(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des crédits par membre: " + ex.getMessage());
        }
        
        return credits;
    }
    
    /**
     * Récupère les crédits par tontine
     */
    public List<Credit> findByTontine(int idTontine) {
        String sql = "SELECT c.*, m.nom as membre_nom, m.prenom as membre_prenom, " +
                    "t.nom as tontine_nom " +
                    "FROM Credit c " +
                    "JOIN Membre m ON c.id_membre = m.id_membre " +
                    "JOIN Tontine t ON c.id_tontine = t.id_tontine " +
                    "WHERE c.id_tontine = ? " +
                    "ORDER BY c.date_emprunt DESC";
        
        List<Credit> credits = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idTontine);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                credits.add(mapResultSetToCredit(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des crédits par tontine: " + ex.getMessage());
        }
        
        return credits;
    }
    
    /**
     * Récupère les crédits par statut
     */
    public List<Credit> findByStatut(String statut) {
        String sql = "SELECT c.*, m.nom as membre_nom, m.prenom as membre_prenom, " +
                    "t.nom as tontine_nom " +
                    "FROM Credit c " +
                    "JOIN Membre m ON c.id_membre = m.id_membre " +
                    "JOIN Tontine t ON c.id_tontine = t.id_tontine " +
                    "WHERE c.statut = ? " +
                    "ORDER BY c.date_emprunt DESC";
        
        List<Credit> credits = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                credits.add(mapResultSetToCredit(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des crédits par statut: " + ex.getMessage());
        }
        
        return credits;
    }
    
    /**
     * Récupère les crédits en cours
     */
    public List<Credit> findEnCours() {
        return findByStatut("en_cours");
    }
    
    /**
     * Récupère les crédits en retard
     */
    public List<Credit> findEnRetard() {
        return findByStatut("en_retard");
    }
    
    /**
     * Met à jour le statut des crédits en retard
     */
    public int updateCreditsEnRetard() {
        String sql = "UPDATE Credit " +
                    "SET statut = 'en_retard' " +
                    "WHERE statut = 'en_cours' " +
                    "  AND date_echeance < CURRENT_DATE() " +
                    "  AND montant_rembourse < (montant_emprunte * (1 + taux_interet/100))";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            return pstmt.executeUpdate();
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la mise à jour des crédits en retard: " + ex.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Compte le nombre total de crédits
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM Credit";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors du comptage des crédits: " + ex.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Compte le nombre de crédits par statut
     */
    public int countByStatut(String statut) {
        String sql = "SELECT COUNT(*) FROM Credit WHERE statut = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors du comptage des crédits par statut: " + ex.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Calcule le montant total des crédits en cours
     */
    public BigDecimal getMontantTotalEnCours() {
        String sql = "SELECT COALESCE(SUM(montant_emprunte), 0) FROM Credit WHERE statut = 'en_cours'";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors du calcul du montant total: " + ex.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Enregistre un paiement pour un crédit
     */
    public boolean enregistrerPaiement(int idCredit, BigDecimal montantPaiement) {
        String sql = "UPDATE credit SET montant_rembourse = montant_rembourse + ?, " +
                    "statut = CASE WHEN montant_rembourse + ? >= (montant_emprunte * (1 + taux_interet/100)) " +
                    "THEN 'rembourse' ELSE statut END WHERE id_credit = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, montantPaiement);
            pstmt.setBigDecimal(2, montantPaiement);
            pstmt.setInt(3, idCredit);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de l'enregistrement du paiement: " + ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Convertit un ResultSet en objet Credit
     */
    private Credit mapResultSetToCredit(ResultSet rs) throws SQLException {
        Credit credit = new Credit();
        credit.setIdCredit(rs.getInt("id_credit"));
        credit.setIdMembre(rs.getInt("id_membre"));
        credit.setIdTontine(rs.getInt("id_tontine"));
        credit.setMontantEmprunte(rs.getBigDecimal("montant_emprunte"));
        credit.setTauxInteret(rs.getBigDecimal("taux_interet"));
        credit.setDateEmprunt(rs.getDate("date_emprunt").toLocalDate());
        credit.setDateEcheance(rs.getDate("date_echeance").toLocalDate());
        credit.setMontantRembourse(rs.getBigDecimal("montant_rembourse"));
        credit.setStatut(rs.getString("statut"));
        
        // Créer et associer le Membre
        Membre membre = new Membre();
        membre.setIdMembre(rs.getInt("id_membre"));
        membre.setNom(rs.getString("membre_nom"));
        membre.setPrenom(rs.getString("membre_prenom"));
        credit.setMembre(membre);
        
        // Créer et associer la Tontine
        Tontine tontine = new Tontine();
        tontine.setIdTontine(rs.getInt("id_tontine"));
        tontine.setNom(rs.getString("tontine_nom"));
        credit.setTontine(tontine);
        
        return credit;
    }
}
