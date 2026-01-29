package dao;

import database.DatabaseConnection;
import models.Cotisation;
import models.Seance;
import models.Membre;
import models.Tontine;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des cotisations
 * Projet INF2212 - Université de Yaoundé I
 */
public class CotisationDAO {
    
    private DatabaseConnection dbConnection;
    private SeanceDAO seanceDAO;
    private MembreDAO membreDAO;
    
    public CotisationDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.seanceDAO = new SeanceDAO();
        this.membreDAO = new MembreDAO();
    }
    
    /**
     * Ajoute une nouvelle cotisation
     */
    public boolean create(Cotisation cotisation) {
        String sql = "INSERT INTO cotisation (id_seance, id_membre, montant, date_paiement) " +
                    "VALUES (?, ?, ?, ?)";
        
        System.out.println("🔍 [DAO DEBUG] Tentative d'insertion cotisation:");
        System.out.println("  - SQL: " + sql);
        System.out.println("  - id_seance: " + cotisation.getIdSeance());
        System.out.println("  - id_membre: " + cotisation.getIdMembre());
        System.out.println("  - montant: " + cotisation.getMontant());
        System.out.println("  - date_paiement: " + cotisation.getDatePaiement());
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, cotisation.getIdSeance());
            pstmt.setInt(2, cotisation.getIdMembre());
            pstmt.setBigDecimal(3, cotisation.getMontant());
            pstmt.setDate(4, Date.valueOf(cotisation.getDatePaiement()));
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("🔍 [DAO DEBUG] affectedRows: " + affectedRows);
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    cotisation.setIdCotisation(generatedKeys.getInt(1));
                    System.out.println("✅ [DAO DEBUG] Cotisation créée avec ID: " + cotisation.getIdCotisation());
                }
                return true;
            } else {
                System.out.println("❌ [DAO DEBUG] Aucune ligne affectée");
            }
            
        } catch (SQLException ex) {
            System.err.println("❌ [DAO ERROR] Erreur lors de la création de la cotisation: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        System.out.println("❌ [DAO DEBUG] Création cotisation échouée");
        return false;
    }
    
    /**
     * Met à jour une cotisation
     */
    public boolean update(Cotisation cotisation) {
        String sql = "UPDATE cotisation SET id_seance = ?, id_membre = ?, montant = ?, date_paiement = ? WHERE id_cotisation = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cotisation.getIdSeance());
            pstmt.setInt(2, cotisation.getIdMembre());
            pstmt.setBigDecimal(3, cotisation.getMontant());
            pstmt.setDate(4, Date.valueOf(cotisation.getDatePaiement()));
            pstmt.setInt(5, cotisation.getIdCotisation());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la mise à jour de la cotisation: " + ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Supprime une cotisation
     */
    public boolean delete(int idCotisation) {
        String sql = "DELETE FROM cotisation WHERE id_cotisation = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCotisation);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la suppression de la cotisation: " + ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Recherche une cotisation par son ID
     */
    public Cotisation findById(int idCotisation) {
        String sql = "SELECT c.*, " +
                    "s.date_seance, s.numero_tour, s.lieu as seance_lieu, " +
                    "m.nom as membre_nom, m.prenom as membre_prenom, " +
                    "t.id_tontine, t.nom as tontine_nom " +
                    "FROM cotisation c " +
                    "JOIN seance s ON c.id_seance = s.id_seance " +
                    "JOIN membre m ON c.id_membre = m.id_membre " +
                    "JOIN tontine t ON s.id_tontine = t.id_tontine " +
                    "WHERE c.id_cotisation = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCotisation);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCotisation(rs);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la recherche de la cotisation: " + ex.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère toutes les cotisations
     */
    public List<Cotisation> findAll() {
        String sql = "SELECT c.*, " +
                    "s.date_seance, s.numero_tour, s.lieu as seance_lieu, " +
                    "m.nom as membre_nom, m.prenom as membre_prenom, " +
                    "t.id_tontine, t.nom as tontine_nom " +
                    "FROM cotisation c " +
                    "JOIN seance s ON c.id_seance = s.id_seance " +
                    "JOIN membre m ON c.id_membre = m.id_membre " +
                    "JOIN tontine t ON s.id_tontine = t.id_tontine " +
                    "ORDER BY s.date_seance DESC, m.nom, m.prenom";
        
        List<Cotisation> cotisations = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cotisations.add(mapResultSetToCotisation(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des cotisations: " + ex.getMessage());
        }
        
        return cotisations;
    }
    
    /**
     * Récupère les cotisations par séance
     */
    public List<Cotisation> findBySeance(int idSeance) {
        String sql = "SELECT c.*, " +
                    "s.date_seance, s.numero_tour, s.lieu as seance_lieu, " +
                    "m.nom as membre_nom, m.prenom as membre_prenom, " +
                    "t.id_tontine, t.nom as tontine_nom " +
                    "FROM cotisation c " +
                    "JOIN seance s ON c.id_seance = s.id_seance " +
                    "JOIN membre m ON c.id_membre = m.id_membre " +
                    "JOIN tontine t ON s.id_tontine = t.id_tontine " +
                    "WHERE c.id_seance = ? " +
                    "ORDER BY m.nom, m.prenom";
        
        System.out.println("🔍 [DAO DEBUG] Recherche cotisations pour séance " + idSeance);
        System.out.println("  - SQL: " + sql);
        
        List<Cotisation> cotisations = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idSeance);
            ResultSet rs = pstmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                cotisations.add(mapResultSetToCotisation(rs));
                count++;
                System.out.println("🔍 [DAO DEBUG] Cotisation trouvée: " + 
                    rs.getString("membre_nom") + " " + rs.getString("membre_prenom") + 
                    " - " + rs.getBigDecimal("montant") + " FCFA");
            }
            
            System.out.println("🔍 [DAO DEBUG] Total cotisations trouvées: " + count);
            
        } catch (SQLException ex) {
            System.err.println("❌ [DAO ERROR] Erreur lors de la récupération des cotisations par séance: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return cotisations;
    }
    
    /**
     * Récupère les cotisations par membre
     */
    public List<Cotisation> findByMembre(int idMembre) {
        String sql = "SELECT c.*, " +
                    "s.date_seance, s.numero_tour, s.lieu as seance_lieu, " +
                    "m.nom as membre_nom, m.prenom as membre_prenom, " +
                    "t.id_tontine, t.nom as tontine_nom " +
                    "FROM cotisation c " +
                    "JOIN seance s ON c.id_seance = s.id_seance " +
                    "JOIN membre m ON c.id_membre = m.id_membre " +
                    "JOIN tontine t ON s.id_tontine = t.id_tontine " +
                    "WHERE c.id_membre = ? " +
                    "ORDER BY s.date_seance DESC";
        
        List<Cotisation> cotisations = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idMembre);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                cotisations.add(mapResultSetToCotisation(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des cotisations par membre: " + ex.getMessage());
        }
        
        return cotisations;
    }
    
    /**
     * Calcule le total des cotisations pour une séance
     */
    public BigDecimal sumBySeance(int idSeance) {
        String sql = "SELECT COALESCE(SUM(montant), 0) FROM cotisation WHERE id_seance = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idSeance);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors du calcul des cotisations: " + ex.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Convertit un ResultSet en objet Cotisation
     */
    private Cotisation mapResultSetToCotisation(ResultSet rs) throws SQLException {
        Cotisation cotisation = new Cotisation();
        cotisation.setIdCotisation(rs.getInt("id_cotisation"));
        cotisation.setIdSeance(rs.getInt("id_seance"));
        cotisation.setIdMembre(rs.getInt("id_membre"));
        cotisation.setMontant(rs.getBigDecimal("montant"));
        
        Date datePaiement = rs.getDate("date_paiement");
        if (datePaiement != null) {
            cotisation.setDatePaiement(datePaiement.toLocalDate());
        }
        
        // Créer et associer le Membre
        Membre membre = new Membre();
        membre.setIdMembre(rs.getInt("id_membre"));
        membre.setNom(rs.getString("membre_nom"));
        membre.setPrenom(rs.getString("membre_prenom"));
        cotisation.setMembre(membre);
        
        // Créer et associer la Séance
        Seance seance = new Seance();
        seance.setIdSeance(rs.getInt("id_seance"));
        seance.setDateSeance(rs.getDate("date_seance").toLocalDate());
        seance.setNumeroTour(rs.getInt("numero_tour"));
        seance.setLieu(rs.getString("seance_lieu"));
        
        // Créer et associer la Tontine
        Tontine tontine = new Tontine();
        tontine.setIdTontine(rs.getInt("id_tontine"));
        tontine.setNom(rs.getString("tontine_nom"));
        seance.setTontine(tontine);
        
        cotisation.setSeance(seance);
        
        return cotisation;
    }
}
