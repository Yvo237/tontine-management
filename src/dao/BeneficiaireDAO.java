package dao;

import models.Beneficiaire;
import models.Participation;
import models.Seance;
import models.Membre;
import models.Tontine;
import database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BeneficiaireDAO {
    
    private DatabaseConnection dbConnection;
    
    public BeneficiaireDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public boolean create(Beneficiaire beneficiaire) {
        String sql = "INSERT INTO beneficiaire (id_seance, id_participation, montant_gain, " +
                    "date_attribution, mode_paiement, statut) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, beneficiaire.getIdSeance());
            stmt.setInt(2, beneficiaire.getIdParticipation());
            stmt.setDouble(3, beneficiaire.getMontantGain());
            stmt.setDate(4, Date.valueOf(beneficiaire.getDateAttribution()));
            stmt.setString(5, beneficiaire.getModePaiement());
            stmt.setString(6, beneficiaire.getStatut());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    beneficiaire.setIdBeneficiaire(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du bénéficiaire: " + e.getMessage());
        }
        return false;
    }
    
    public boolean update(Beneficiaire beneficiaire) {
        String sql = "UPDATE beneficiaire SET id_seance = ?, id_participation = ?, " +
                    "montant_gain = ?, date_attribution = ?, mode_paiement = ?, statut = ? " +
                    "WHERE id_beneficiaire = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, beneficiaire.getIdSeance());
            stmt.setInt(2, beneficiaire.getIdParticipation());
            stmt.setDouble(3, beneficiaire.getMontantGain());
            stmt.setDate(4, Date.valueOf(beneficiaire.getDateAttribution()));
            stmt.setString(5, beneficiaire.getModePaiement());
            stmt.setString(6, beneficiaire.getStatut());
            stmt.setInt(7, beneficiaire.getIdBeneficiaire());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du bénéficiaire: " + e.getMessage());
        }
        return false;
    }
    
    public boolean delete(int idBeneficiaire) {
        String sql = "DELETE FROM beneficiaire WHERE id_beneficiaire = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBeneficiaire);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du bénéficiaire: " + e.getMessage());
        }
        return false;
    }
    
    public Beneficiaire findById(int idBeneficiaire) {
        String sql = "SELECT b.*, s.numero_tour, s.date_seance as date_seance, " +
                    "p.nombre_parts, m.nom as nom_membre, m.prenom as prenom_membre, " +
                    "t.nom as nom_tontine " +
                    "FROM beneficiaire b " +
                    "JOIN seance s ON b.id_seance = s.id_seance " +
                    "JOIN participation p ON b.id_participation = p.id_participation " +
                    "JOIN membre m ON p.id_membre = m.id_membre " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine " +
                    "WHERE b.id_beneficiaire = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBeneficiaire);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBeneficiaire(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du bénéficiaire: " + e.getMessage());
        }
        return null;
    }
    
    public List<Beneficiaire> findAll() {
        List<Beneficiaire> beneficiaires = new ArrayList<>();
        String sql = "SELECT b.*, s.numero_tour, s.date_seance as date_seance, " +
                    "p.nombre_parts, m.nom as nom_membre, m.prenom as prenom_membre, " +
                    "t.nom as nom_tontine " +
                    "FROM beneficiaire b " +
                    "JOIN seance s ON b.id_seance = s.id_seance " +
                    "JOIN participation p ON b.id_participation = p.id_participation " +
                    "JOIN membre m ON p.id_membre = m.id_membre " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine " +
                    "ORDER BY b.date_attribution DESC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                beneficiaires.add(mapResultSetToBeneficiaire(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des bénéficiaires: " + e.getMessage());
        }
        return beneficiaires;
    }
    
    public List<Beneficiaire> findBySeance(int idSeance) {
        List<Beneficiaire> beneficiaires = new ArrayList<>();
        String sql = "SELECT b.*, s.numero_tour, s.date_seance as date_seance, " +
                    "p.nombre_parts, m.nom as nom_membre, m.prenom as prenom_membre, " +
                    "t.nom as nom_tontine " +
                    "FROM beneficiaire b " +
                    "JOIN seance s ON b.id_seance = s.id_seance " +
                    "JOIN participation p ON b.id_participation = p.id_participation " +
                    "JOIN membre m ON p.id_membre = m.id_membre " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine " +
                    "WHERE b.id_seance = ? " +
                    "ORDER BY b.date_attribution DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idSeance);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                beneficiaires.add(mapResultSetToBeneficiaire(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des bénéficiaires pour la séance: " + e.getMessage());
        }
        return beneficiaires;
    }
    
    public List<Beneficiaire> findByMembre(int idMembre) {
        List<Beneficiaire> beneficiaires = new ArrayList<>();
        String sql = "SELECT b.*, s.numero_tour, s.date_seance as date_seance, " +
                    "p.nombre_parts, m.nom as nom_membre, m.prenom as prenom_membre, " +
                    "t.nom as nom_tontine " +
                    "FROM beneficiaire b " +
                    "JOIN seance s ON b.id_seance = s.id_seance " +
                    "JOIN participation p ON b.id_participation = p.id_participation " +
                    "JOIN membre m ON p.id_membre = m.id_membre " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine " +
                    "WHERE p.id_membre = ? " +
                    "ORDER BY b.date_attribution DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMembre);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                beneficiaires.add(mapResultSetToBeneficiaire(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des bénéficiaires pour le membre: " + e.getMessage());
        }
        return beneficiaires;
    }
    
    public List<Beneficiaire> findByStatut(String statut) {
        List<Beneficiaire> beneficiaires = new ArrayList<>();
        String sql = "SELECT b.*, s.numero_tour, s.date_seance as date_seance, " +
                    "p.nombre_parts, m.nom as nom_membre, m.prenom as prenom_membre, " +
                    "t.nom as nom_tontine " +
                    "FROM beneficiaire b " +
                    "JOIN seance s ON b.id_seance = s.id_seance " +
                    "JOIN participation p ON b.id_participation = p.id_participation " +
                    "JOIN membre m ON p.id_membre = m.id_membre " +
                    "JOIN tontine t ON p.id_tontine = t.id_tontine " +
                    "WHERE b.statut = ? " +
                    "ORDER BY b.date_attribution DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                beneficiaires.add(mapResultSetToBeneficiaire(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des bénéficiaires par statut: " + e.getMessage());
        }
        return beneficiaires;
    }
    
    public boolean marquerCommePaye(int idBeneficiaire) {
        String sql = "UPDATE beneficiaire SET statut = 'paye' WHERE id_beneficiaire = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBeneficiaire);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors du marquage comme payé: " + e.getMessage());
        }
        return false;
    }
    
    private Beneficiaire mapResultSetToBeneficiaire(ResultSet rs) throws SQLException {
        Beneficiaire beneficiaire = new Beneficiaire();
        beneficiaire.setIdBeneficiaire(rs.getInt("id_beneficiaire"));
        beneficiaire.setIdSeance(rs.getInt("id_seance"));
        beneficiaire.setIdParticipation(rs.getInt("id_participation"));
        beneficiaire.setMontantGain(rs.getDouble("montant_gain"));
        beneficiaire.setDateAttribution(rs.getDate("date_attribution").toLocalDate());
        beneficiaire.setModePaiement(rs.getString("mode_paiement"));
        beneficiaire.setStatut(rs.getString("statut"));
        
        // Créer et associer la séance
        Seance seance = new Seance();
        seance.setIdSeance(rs.getInt("id_seance"));
        seance.setNumeroTour(rs.getInt("numero_tour"));
        seance.setDateSeance(rs.getDate("date_seance").toLocalDate());
        beneficiaire.setSeance(seance);
        
        // Créer et associer la participation
        Participation participation = new Participation();
        participation.setIdParticipation(rs.getInt("id_participation"));
        participation.setNombreParts(rs.getInt("nombre_parts"));
        
        // Créer et associer le membre
        Membre membre = new Membre();
        membre.setNom(rs.getString("nom_membre"));
        membre.setPrenom(rs.getString("prenom_membre"));
        participation.setMembre(membre);
        
        // Créer et associer la tontine
        Tontine tontine = new Tontine();
        tontine.setNom(rs.getString("nom_tontine"));
        participation.setTontine(tontine);
        
        beneficiaire.setParticipation(participation);
        
        return beneficiaire;
    }
}
