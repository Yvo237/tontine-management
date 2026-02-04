package dao;

import models.ProjetFIAC;
import models.Tontine;
import database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjetFIACDAO {
    
    private DatabaseConnection dbConnection;
    
    public ProjetFIACDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public boolean create(ProjetFIAC projet) {
        System.out.println("[PROJET FIAC DEBUG] Création projet FIAC...");
        System.out.println("  - id_tontine: " + projet.getIdTontine());
        System.out.println("  - nom_projet: " + projet.getNomProjet());
        System.out.println("  - description: " + projet.getDescription());
        System.out.println("  - montant_objectif: " + projet.getMontantObjectif());
        System.out.println("  - montant_collecte: " + projet.getMontantCollecte());
        System.out.println("  - date_debut: " + projet.getDateDebut());
        System.out.println("  - date_fin_prevue: " + projet.getDateFinPrevue());
        System.out.println("  - statut: " + projet.getStatut());
        
        String sql = "INSERT INTO projetfiac (id_tontine, nom_projet, description, montant_objectif, " +
                    "montant_collecte, date_debut, date_fin_prevue, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("  - SQL: " + sql);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, projet.getIdTontine());
            stmt.setString(2, projet.getNomProjet());
            stmt.setString(3, projet.getDescription());
            stmt.setDouble(4, projet.getMontantObjectif());
            stmt.setDouble(5, projet.getMontantCollecte());
            stmt.setDate(6, Date.valueOf(projet.getDateDebut()));
            stmt.setDate(7, projet.getDateFinPrevue() != null ? Date.valueOf(projet.getDateFinPrevue()) : null);
            stmt.setString(8, projet.getStatut());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    projet.setIdProjet(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du projet FIAC: " + e.getMessage());
        }
        return false;
    }
    
    public boolean update(ProjetFIAC projet) {
        String sql = "UPDATE projetfiac SET id_tontine = ?, nom_projet = ?, description = ?, " +
                    "montant_objectif = ?, montant_collecte = ?, date_debut = ?, date_fin_prevue = ?, " +
                    "statut = ? WHERE id_projet = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, projet.getIdTontine());
            stmt.setString(2, projet.getNomProjet());
            stmt.setString(3, projet.getDescription());
            stmt.setDouble(4, projet.getMontantObjectif());
            stmt.setDouble(5, projet.getMontantCollecte());
            stmt.setDate(6, Date.valueOf(projet.getDateDebut()));
            stmt.setDate(7, projet.getDateFinPrevue() != null ? Date.valueOf(projet.getDateFinPrevue()) : null);
            stmt.setString(8, projet.getStatut());
            stmt.setInt(9, projet.getIdProjet());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du projet FIAC: " + e.getMessage());
        }
        return false;
    }
    
    public boolean delete(int idProjet) {
        String sql = "DELETE FROM projetfiac WHERE id_projet = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProjet);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du projet FIAC: " + e.getMessage());
        }
        return false;
    }
    
    public ProjetFIAC findById(int idProjet) {
        String sql = "SELECT p.*, t.nom as nom_tontine FROM projetfiac p " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine WHERE p.id_projet = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProjet);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProjet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du projet FIAC: " + e.getMessage());
        }
        return null;
    }
    
    public List<ProjetFIAC> findAll() {
        List<ProjetFIAC> projets = new ArrayList<>();
        String sql = "SELECT p.*, t.nom as nom_tontine FROM projetfiac p " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine ORDER BY p.date_debut DESC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                projets.add(mapResultSetToProjet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des projets FIAC: " + e.getMessage());
        }
        return projets;
    }
    
    public List<ProjetFIAC> findByTontine(int idTontine) {
        List<ProjetFIAC> projets = new ArrayList<>();
        String sql = "SELECT p.*, t.nom as nom_tontine FROM projetfiac p " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine WHERE p.id_tontine = ? " +
                    "ORDER BY p.date_debut DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTontine);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                projets.add(mapResultSetToProjet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des projets FIAC pour la tontine: " + e.getMessage());
        }
        return projets;
    }
    
    public List<ProjetFIAC> findByStatut(String statut) {
        List<ProjetFIAC> projets = new ArrayList<>();
        String sql = "SELECT p.*, t.nom as nom_tontine FROM projetfiac p " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine WHERE p.statut = ? " +
                    "ORDER BY p.date_debut DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                projets.add(mapResultSetToProjet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des projets FIAC par statut: " + e.getMessage());
        }
        return projets;
    }
    
    public boolean updateMontantCollecte(int idProjet, double nouveauMontant) {
        String sql = "UPDATE projetfiac SET montant_collecte = ? WHERE id_projet = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, nouveauMontant);
            stmt.setInt(2, idProjet);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du montant collecté: " + e.getMessage());
        }
        return false;
    }
    
    private ProjetFIAC mapResultSetToProjet(ResultSet rs) throws SQLException {
        ProjetFIAC projet = new ProjetFIAC();
        projet.setIdProjet(rs.getInt("id_projet"));
        projet.setIdTontine(rs.getInt("id_tontine"));
        projet.setNomProjet(rs.getString("nom_projet"));
        projet.setDescription(rs.getString("description"));
        projet.setMontantObjectif(rs.getDouble("montant_objectif"));
        projet.setMontantCollecte(rs.getDouble("montant_collecte"));
        projet.setDateDebut(rs.getDate("date_debut").toLocalDate());
        
        Date dateFin = rs.getDate("date_fin_prevue");
        if (dateFin != null) {
            projet.setDateFinPrevue(dateFin.toLocalDate());
        }
        
        projet.setStatut(rs.getString("statut"));
        
        // Créer l'association avec la tontine
        Tontine tontine = new Tontine();
        tontine.setIdTontine(rs.getInt("id_tontine"));
        tontine.setNom(rs.getString("nom_tontine"));
        projet.setTontine(tontine);
        
        return projet;
    }
}
