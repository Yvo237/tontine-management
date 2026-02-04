package dao;

import database.DatabaseConnection;
import models.Seance;
import models.Tontine;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SeanceDAO {
    
    private DatabaseConnection dbConnection;
    private TontineDAO tontineDAO;
    
    public SeanceDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.tontineDAO = new TontineDAO();
    }
    
    public boolean create(Seance seance) {
        String sql = "INSERT INTO seance (id_tontine, numero_tour, date_seance, lieu, statut, observations) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, seance.getIdTontine());
            pstmt.setInt(2, seance.getNumeroTour());
            pstmt.setDate(3, Date.valueOf(seance.getDateSeance()));
            pstmt.setString(4, seance.getLieu());
            pstmt.setString(5, seance.getStatut());
            pstmt.setString(6, seance.getObservations());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    seance.setIdSeance(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la création de la séance: " + ex.getMessage());
            if (ex.getSQLState().equals("23000")) {
                System.err.println("Ce numéro de tour existe déjà pour cette tontine");
            }
        }
        
        return false;
    }
    
    public boolean update(Seance seance) {
        String sql = "UPDATE seance SET date_seance = ?, lieu = ?, statut = ?, observations = ? " +
                    "WHERE id_seance = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(seance.getDateSeance()));
            pstmt.setString(2, seance.getLieu());
            pstmt.setString(3, seance.getStatut());
            pstmt.setString(4, seance.getObservations());
            pstmt.setInt(5, seance.getIdSeance());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la mise à jour de la séance: " + ex.getMessage());
        }
        
        return false;
    }
    
    public boolean delete(int idSeance) {
        System.out.println("[DEBUG] Tentative de suppression de la séance ID: " + idSeance);
        
        try (Connection conn = dbConnection.getConnection()) {
            // Désactiver l'auto-commit pour gérer la transaction
            conn.setAutoCommit(false);
            
            try {
                // 1. D'abord supprimer toutes les cotisations liées à cette séance
                String sqlCotisations = "DELETE FROM cotisation WHERE id_seance = ?";
                try (PreparedStatement pstmtCotisations = conn.prepareStatement(sqlCotisations)) {
                    pstmtCotisations.setInt(1, idSeance);
                    int cotisationsDeleted = pstmtCotisations.executeUpdate();
                    System.out.println("[DEBUG] Cotisations supprimées: " + cotisationsDeleted);
                }
                
                // 2. Ensuite supprimer la séance
                String sqlSeance = "DELETE FROM seance WHERE id_seance = ?";
                try (PreparedStatement pstmtSeance = conn.prepareStatement(sqlSeance)) {
                    pstmtSeance.setInt(1, idSeance);
                    int seanceDeleted = pstmtSeance.executeUpdate();
                    System.out.println("[DEBUG] Séance supprimée: " + (seanceDeleted > 0 ? "OUI" : "NON"));
                    
                    if (seanceDeleted > 0) {
                        // Valider la transaction
                        conn.commit();
                        System.out.println("[DEBUG] Transaction validée - Séance et cotisations supprimées");
                        return true;
                    } else {
                        // Annuler la transaction
                        conn.rollback();
                        System.out.println("[DEBUG] Transaction annulée - Séance non trouvée");
                        return false;
                    }
                }
                
            } catch (SQLException ex) {
                // Annuler la transaction en cas d'erreur
                conn.rollback();
                System.err.println("[ERROR] Erreur lors de la suppression: " + ex.getMessage());
                ex.printStackTrace();
                return false;
            }
            
        } catch (SQLException ex) {
            System.err.println("[ERROR] Erreur de connexion: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    public Seance findById(int idSeance) {
        String sql = "SELECT s.*, t.nom as tontine_nom, t.statut as tontine_statut, t.nombre_tours " +
                    "FROM seance s " +
                    "JOIN tontine t ON s.id_tontine = t.id_tontine " +
                    "WHERE s.id_seance = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idSeance);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSeance(rs);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la recherche de la séance: " + ex.getMessage());
        }
        
        return null;
    }
    
    public List<Seance> findAll() {
        String sql = "SELECT s.*, t.nom as tontine_nom, t.statut as tontine_statut, t.nombre_tours " +
                    "FROM seance s " +
                    "JOIN tontine t ON s.id_tontine = t.id_tontine " +
                    "ORDER BY s.date_seance DESC";
        
        List<Seance> seances = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                seances.add(mapResultSetToSeance(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des séances: " + ex.getMessage());
        }
        
        return seances;
    }
    
    public List<Seance> findByTontine(int idTontine) {
        String sql = "SELECT s.*, t.nom as tontine_nom, t.statut as tontine_statut, t.nombre_tours " +
                    "FROM seance s " +
                    "JOIN tontine t ON s.id_tontine = t.id_tontine " +
                    "WHERE s.id_tontine = ? " +
                    "ORDER BY s.numero_tour";
        
        List<Seance> seances = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idTontine);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                seances.add(mapResultSetToSeance(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des séances par tontine: " + ex.getMessage());
        }
        
        return seances;
    }
    
    public List<Seance> findByStatut(String statut) {
        String sql = "SELECT s.*, t.nom as tontine_nom, t.statut as tontine_statut, t.nombre_tours " +
                    "FROM seance s " +
                    "JOIN tontine t ON s.id_tontine = t.id_tontine " +
                    "WHERE s.statut = ? " +
                    "ORDER BY s.date_seance DESC";
        
        List<Seance> seances = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                seances.add(mapResultSetToSeance(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des séances par statut: " + ex.getMessage());
        }
        
        return seances;
    }
    
    public List<Seance> findMoisEnCours() {
        String sql = "SELECT s.*, t.nom as tontine_nom, t.statut as tontine_statut, t.nombre_tours " +
                    "FROM seance s " +
                    "JOIN tontine t ON s.id_tontine = t.id_tontine " +
                    "WHERE MONTH(s.date_seance) = MONTH(CURRENT_DATE()) " +
                    "  AND YEAR(s.date_seance) = YEAR(CURRENT_DATE()) " +
                    "ORDER BY s.date_seance DESC";
        
        List<Seance> seances = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                seances.add(mapResultSetToSeance(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des séances du mois: " + ex.getMessage());
        }
        
        return seances;
    }
    
    public List<Seance> findAVenir() {
        String sql = "SELECT s.*, t.nom as tontine_nom, t.statut as tontine_statut, t.nombre_tours " +
                    "FROM seance s " +
                    "JOIN tontine t ON s.id_tontine = t.id_tontine " +
                    "WHERE s.date_seance >= CURRENT_DATE() " +
                    "ORDER BY s.date_seance";
        
        List<Seance> seances = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                seances.add(mapResultSetToSeance(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des séances à venir: " + ex.getMessage());
        }
        
        return seances;
    }
    
    public boolean existsTour(int idTontine, int numeroTour) {
        String sql = "SELECT COUNT(*) FROM seance WHERE id_tontine = ? AND numero_tour = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idTontine);
            pstmt.setInt(2, numeroTour);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la vérification du tour: " + ex.getMessage());
        }
        
        return false;
    }
    
    public int countByTontine(int idTontine) {
        String sql = "SELECT COUNT(*) FROM seance WHERE id_tontine = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idTontine);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors du comptage des séances: " + ex.getMessage());
        }
        
        return 0;
    }
    
    public int getLastNumeroTour(int idTontine) {
        String sql = "SELECT COALESCE(MAX(numero_tour), 0) FROM seance WHERE id_tontine = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idTontine);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération du dernier tour: " + ex.getMessage());
        }
        
        return 0;
    }
    
    private Seance mapResultSetToSeance(ResultSet rs) throws SQLException {
        Seance seance = new Seance();
        seance.setIdSeance(rs.getInt("id_seance"));
        seance.setIdTontine(rs.getInt("id_tontine"));
        seance.setNumeroTour(rs.getInt("numero_tour"));
        seance.setDateSeance(rs.getDate("date_seance").toLocalDate());
        seance.setLieu(rs.getString("lieu"));
        seance.setStatut(rs.getString("statut"));
        seance.setObservations(rs.getString("observations"));
        
        // Créer et associer la Tontine
        Tontine tontine = new Tontine();
        tontine.setIdTontine(rs.getInt("id_tontine"));
        tontine.setNom(rs.getString("tontine_nom"));
        tontine.setStatut(rs.getString("tontine_statut"));
        tontine.setNombreTours(rs.getInt("nombre_tours"));
        
        seance.setTontine(tontine);
        
        return seance;
    }
}
