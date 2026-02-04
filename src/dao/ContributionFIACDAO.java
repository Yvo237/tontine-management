package dao;

import models.ContributionFIAC;
import models.ProjetFIAC;
import models.Membre;
import database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContributionFIACDAO {
    
    private DatabaseConnection dbConnection;
    
    public ContributionFIACDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public boolean create(ContributionFIAC contribution) {
        String sql = "INSERT INTO contributionfiac (id_projet, id_membre, montant, date_contribution) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, contribution.getIdProjet());
            stmt.setInt(2, contribution.getIdMembre());
            stmt.setDouble(3, contribution.getMontant());
            stmt.setDate(4, Date.valueOf(contribution.getDateContribution()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    contribution.setIdContribution(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la contribution FIAC: " + e.getMessage());
        }
        return false;
    }
    
    public boolean update(ContributionFIAC contribution) {
        String sql = "UPDATE contributionfiac SET id_projet = ?, id_membre = ?, montant = ?, date_contribution = ? WHERE id_contribution = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, contribution.getIdProjet());
            stmt.setInt(2, contribution.getIdMembre());
            stmt.setDouble(3, contribution.getMontant());
            stmt.setDate(4, Date.valueOf(contribution.getDateContribution()));
            stmt.setInt(5, contribution.getIdContribution());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la contribution FIAC: " + e.getMessage());
        }
        return false;
    }
    
    public boolean delete(int idContribution) {
        String sql = "DELETE FROM contributionfiac WHERE id_contribution = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idContribution);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la contribution FIAC: " + e.getMessage());
        }
        return false;
    }
    
    public ContributionFIAC findById(int idContribution) {
        String sql = "SELECT c.*, p.nom_projet, m.nom || ' ' || m.prenom as nom_membre " +
                    "FROM contributionfiac c " +
                    "JOIN projetfiac p ON c.id_projet = p.id_projet " +
                    "JOIN membre m ON c.id_membre = m.id_membre " +
                    "WHERE c.id_contribution = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idContribution);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToContribution(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la contribution FIAC: " + e.getMessage());
        }
        return null;
    }
    
    public List<ContributionFIAC> findAll() {
        List<ContributionFIAC> contributions = new ArrayList<>();
        String sql = "SELECT c.*, p.nom_projet, m.nom || ' ' || m.prenom as nom_membre " +
                    "FROM contributionfiac c " +
                    "JOIN projetfiac p ON c.id_projet = p.id_projet " +
                    "JOIN membre m ON c.id_membre = m.id_membre " +
                    "ORDER BY c.date_contribution DESC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                contributions.add(mapResultSetToContribution(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des contributions FIAC: " + e.getMessage());
        }
        return contributions;
    }
    
    public List<ContributionFIAC> findByProjet(int idProjet) {
        List<ContributionFIAC> contributions = new ArrayList<>();
        String sql = "SELECT c.*, p.nom_projet, m.nom || ' ' || m.prenom as nom_membre " +
                    "FROM contributionfiac c " +
                    "JOIN projetfiac p ON c.id_projet = p.id_projet " +
                    "JOIN membre m ON c.id_membre = m.id_membre " +
                    "WHERE c.id_projet = ? " +
                    "ORDER BY c.date_contribution DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProjet);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                contributions.add(mapResultSetToContribution(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des contributions FIAC pour le projet: " + e.getMessage());
        }
        return contributions;
    }
    
    public List<ContributionFIAC> findByMembre(int idMembre) {
        List<ContributionFIAC> contributions = new ArrayList<>();
        String sql = "SELECT c.*, p.nom_projet, m.nom || ' ' || m.prenom as nom_membre " +
                    "FROM contributionfiac c " +
                    "JOIN projetfiac p ON c.id_projet = p.id_projet " +
                    "JOIN membre m ON c.id_membre = m.id_membre " +
                    "WHERE c.id_membre = ? " +
                    "ORDER BY c.date_contribution DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMembre);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                contributions.add(mapResultSetToContribution(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des contributions FIAC pour le membre: " + e.getMessage());
        }
        return contributions;
    }
    
    public double getTotalByProjet(int idProjet) {
        String sql = "SELECT COALESCE(SUM(montant), 0) as total FROM contributionfiac WHERE id_projet = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProjet);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du total des contributions: " + e.getMessage());
        }
        return 0.0;
    }
    
    private ContributionFIAC mapResultSetToContribution(ResultSet rs) throws SQLException {
        ContributionFIAC contribution = new ContributionFIAC();
        contribution.setIdContribution(rs.getInt("id_contribution"));
        contribution.setIdProjet(rs.getInt("id_projet"));
        contribution.setIdMembre(rs.getInt("id_membre"));
        contribution.setMontant(rs.getDouble("montant"));
        contribution.setDateContribution(rs.getDate("date_contribution").toLocalDate());
        
        // Créer les associations
        ProjetFIAC projet = new ProjetFIAC();
        projet.setIdProjet(rs.getInt("id_projet"));
        projet.setNomProjet(rs.getString("nom_projet"));
        contribution.setProjet(projet);
        
        Membre membre = new Membre();
        membre.setIdMembre(rs.getInt("id_membre"));
        // Extraire nom et prénom depuis la concaténation
        String nomComplet = rs.getString("nom_membre");
        if (nomComplet != null && nomComplet.contains(" ")) {
            String[] parts = nomComplet.split(" ", 2);
            membre.setNom(parts[0]);
            membre.setPrenom(parts[1]);
        } else {
            membre.setNom(nomComplet);
            membre.setPrenom("");
        }
        contribution.setMembre(membre);
        
        return contribution;
    }
}
