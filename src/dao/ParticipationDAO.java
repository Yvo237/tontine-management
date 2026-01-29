package dao;

import database.DatabaseConnection;
import models.Participation;
import models.Membre;
import models.Tontine;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des participations
 * Projet INF2212 - Université de Yaoundé I
 */
public class ParticipationDAO {
    
    private DatabaseConnection dbConnection;
    private MembreDAO membreDAO;
    private TontineDAO tontineDAO;
    
    public ParticipationDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.membreDAO = new MembreDAO();
        this.tontineDAO = new TontineDAO();
    }
    
    /**
     * Ajoute une nouvelle participation
     */
    public boolean create(Participation participation) {
        String sql = "INSERT INTO participation (id_membre, id_tontine, nombre_parts, date_participation) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, participation.getIdMembre());
            pstmt.setInt(2, participation.getIdTontine());
            pstmt.setInt(3, participation.getNombreParts());
            pstmt.setDate(4, Date.valueOf(participation.getDateInscription()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    participation.setIdParticipation(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la création de la participation: " + ex.getMessage());
            if (ex.getSQLState().equals("23000")) {
                System.err.println("Ce membre participe déjà à cette tontine");
            }
        }
        
        return false;
    }
    
    /**
     * Met à jour une participation
     */
    public boolean update(Participation participation) {
        String sql = "UPDATE participation SET nombre_parts = ? WHERE id_participation = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, participation.getNombreParts());
            pstmt.setInt(2, participation.getIdParticipation());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la mise à jour de la participation: " + ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Supprime une participation
     */
    public boolean delete(int idParticipation) {
        String sql = "DELETE FROM Participation WHERE id_participation = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idParticipation);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la suppression de la participation: " + ex.getMessage());
            if (ex.getSQLState().equals("23000")) {
                System.err.println("Impossible de supprimer cette participation car elle a des cotisations ou des gains");
            }
        }
        
        return false;
    }
    
    /**
     * Recherche une participation par son ID
     */
    public Participation findById(int idParticipation) {
        String sql = "SELECT p.*, m.nom as membre_nom, m.prenom as membre_prenom, m.telephone as membre_telephone, " +
                    "t.nom as tontine_nom " +
                    "FROM participation p " +
                    "JOIN membre m ON p.id_membre = m.id_membre " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine " +
                    "WHERE p.id_participation = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idParticipation);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToParticipation(rs);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la recherche de la participation: " + ex.getMessage());
        }
        
        return null;
    }
    
    /**
     * Vérifie si un membre participe à une tontine
     */
    public Participation findByMembreAndTontine(int idMembre, int idTontine) {
        String sql = "SELECT p.*, m.nom as membre_nom, m.prenom as membre_prenom, m.telephone as membre_telephone, " +
                    "t.nom as tontine_nom " +
                    "FROM participation p " +
                    "JOIN membre m ON p.id_membre = m.id_membre " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine " +
                    "WHERE p.id_membre = ? AND p.id_tontine = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idMembre);
            pstmt.setInt(2, idTontine);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToParticipation(rs);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la recherche de participation: " + ex.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère toutes les participations
     */
    public List<Participation> findAll() {
        String sql = "SELECT p.*, m.nom as membre_nom, m.prenom as membre_prenom, m.telephone as membre_telephone, " +
                    "t.nom as tontine_nom " +
                    "FROM participation p " +
                    "JOIN membre m ON p.id_membre = m.id_membre " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine " +
                    "ORDER BY t.nom, m.nom, m.prenom";
        
        List<Participation> participations = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                participations.add(mapResultSetToParticipation(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des participations: " + ex.getMessage());
        }
        
        return participations;
    }
    
    /**
     * Récupère les participations par tontine
     */
    public List<Participation> findByTontine(int idTontine) {
        String sql = "SELECT p.*, m.nom as membre_nom, m.prenom as membre_prenom, m.telephone as membre_telephone, " +
                    "t.nom as tontine_nom " +
                    "FROM participation p " +
                    "JOIN membre m ON p.id_membre = m.id_membre " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine " +
                    "WHERE p.id_tontine = ? " +
                    "ORDER BY m.nom, m.prenom";
        
        List<Participation> participations = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idTontine);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                participations.add(mapResultSetToParticipation(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des participations par tontine: " + ex.getMessage());
        }
        
        return participations;
    }
    
    /**
     * Récupère les participations par membre
     */
    public List<Participation> findByMembre(int idMembre) {
        String sql = "SELECT p.*, m.nom as membre_nom, m.prenom as membre_prenom, m.telephone as membre_telephone, " +
                    "t.nom as tontine_nom " +
                    "FROM participation p " +
                    "JOIN membre m ON p.id_membre = m.id_membre " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine " +
                    "WHERE p.id_membre = ? " +
                    "ORDER BY t.nom";
        
        List<Participation> participations = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idMembre);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                participations.add(mapResultSetToParticipation(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des participations par membre: " + ex.getMessage());
        }
        
        return participations;
    }
    
    /**
     * Récupère les participations actives par tontine
     */
    public List<Participation> findActivesByTontine(int idTontine) {
        String sql = "SELECT p.*, m.nom as membre_nom, m.prenom as membre_prenom, m.telephone as membre_telephone, " +
                    "t.nom as tontine_nom " +
                    "FROM participation p " +
                    "JOIN membre m ON p.id_membre = m.id_membre " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine " +
                    "WHERE p.id_tontine = ? " +
                    "ORDER BY m.nom, m.prenom";
        
        List<Participation> participations = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idTontine);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                participations.add(mapResultSetToParticipation(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des participations actives: " + ex.getMessage());
        }
        
        return participations;
    }
    
    /**
     * Compte le nombre de participants par tontine
     */
    public int countByTontine(int idTontine) {
        String sql = "SELECT COUNT(*) FROM participation WHERE id_tontine = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idTontine);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors du comptage des participants: " + ex.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Compte le nombre total de parts par tontine
     */
    public int countPartsByTontine(int idTontine) {
        String sql = "SELECT COALESCE(SUM(nombre_parts), 0) FROM participation WHERE id_tontine = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idTontine);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors du comptage des parts: " + ex.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Convertit un ResultSet en objet Participation
     */
    private Participation mapResultSetToParticipation(ResultSet rs) throws SQLException {
        Participation participation = new Participation();
        participation.setIdParticipation(rs.getInt("id_participation"));
        participation.setIdMembre(rs.getInt("id_membre"));
        participation.setIdTontine(rs.getInt("id_tontine"));
        participation.setNombreParts(rs.getInt("nombre_parts"));
        participation.setDateInscription(rs.getDate("date_participation").toLocalDate());
        participation.setStatut("active"); // Valeur par défaut car pas de colonne statut
        
        // Créer et associer le Membre
        Membre membre = new Membre();
        membre.setIdMembre(rs.getInt("id_membre"));
        membre.setNom(rs.getString("membre_nom"));
        membre.setPrenom(rs.getString("membre_prenom"));
        membre.setTelephone(rs.getString("membre_telephone"));
        participation.setMembre(membre);
        
        // Créer et associer la Tontine
        Tontine tontine = new Tontine();
        tontine.setIdTontine(rs.getInt("id_tontine"));
        tontine.setNom(rs.getString("tontine_nom"));
        participation.setTontine(tontine);
        
        return participation;
    }
}
