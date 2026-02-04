package dao;

import database.DatabaseConnection;
import models.Membre;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MembreDAO {
    
    private DatabaseConnection dbConnection;
    
    public MembreDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public boolean create(Membre membre) {
        System.out.println("[DEBUG] Tentative d'ajout de membre...");
        System.out.println("[DEBUG] Nom: " + membre.getNom());
        System.out.println("[DEBUG] Prénom: " + membre.getPrenom());
        System.out.println("[DEBUG] Téléphone: " + membre.getTelephone());
        System.out.println("[DEBUG] Email: " + membre.getEmail());
        System.out.println("[DEBUG] Adresse: " + membre.getAdresse());
        System.out.println("[DEBUG] Statut: " + membre.getStatut());
        System.out.println("[DEBUG] Date adhesion: " + membre.getDateAdhesion());
        
        // Vérifier que la date d'adhésion n'est pas nulle
        if (membre.getDateAdhesion() == null) {
            System.out.println("[DEBUG] Date d'adhésion nulle, utilisation de la date actuelle");
            membre.setDateAdhesion(LocalDate.now());
        }
        
        String sql = "INSERT INTO Membre (nom, prenom, telephone, email, adresse, date_adhesion, statut) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("[DEBUG] Requête SQL: " + sql);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            System.out.println("[DEBUG] Connexion établie, préparation des paramètres...");
            
            pstmt.setString(1, membre.getNom());
            pstmt.setString(2, membre.getPrenom());
            pstmt.setString(3, membre.getTelephone());
            pstmt.setString(4, membre.getEmail());
            pstmt.setString(5, membre.getAdresse());
            pstmt.setDate(6, Date.valueOf(membre.getDateAdhesion()));
            pstmt.setString(7, membre.getStatut());
            
            System.out.println("[DEBUG] Paramètres préparés, exécution de la requête...");
            
            int affectedRows = pstmt.executeUpdate();
            
            System.out.println("[DEBUG] Lignes affectées: " + affectedRows);
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    membre.setIdMembre(generatedId);
                    System.out.println("[DEBUG] ID généré: " + generatedId);
                }
                System.out.println("[SUCCESS] Membre créé avec succès!");
                return true;
            } else {
                System.out.println("[ERROR] Aucune ligne affectée lors de l'insertion");
            }
            
        } catch (SQLException ex) {
            System.err.println("[ERROR] Erreur SQL lors de la création du membre: " + ex.getMessage());
            System.err.println("[ERROR] Code d'erreur SQL: " + ex.getSQLState());
            System.err.println("[ERROR] Code d'erreur vendor: " + ex.getErrorCode());
            ex.printStackTrace();
            DatabaseConnection.afficherErreurConnexion(ex);
        } catch (Exception ex) {
            System.err.println("[ERROR] Erreur inattendue lors de la création du membre: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        System.out.println("[ERROR] Échec de la création du membre");
        return false;
    }
    
    public boolean update(Membre membre) {
        System.out.println("[DEBUG] Tentative de mise à jour du membre...");
        System.out.println("[DEBUG] ID: " + membre.getIdMembre());
        System.out.println("[DEBUG] Nom: " + membre.getNom());
        System.out.println("[DEBUG] Prénom: " + membre.getPrenom());
        System.out.println("[DEBUG] Téléphone: " + membre.getTelephone());
        System.out.println("[DEBUG] Email: " + membre.getEmail());
        System.out.println("[DEBUG] Adresse: " + membre.getAdresse());
        System.out.println("[DEBUG] Statut: " + membre.getStatut());
        
        String sql = "UPDATE Membre SET nom = ?, prenom = ?, telephone = ?, email = ?, " +
                    "adresse = ?, statut = ? WHERE id_membre = ?";
        
        System.out.println("[DEBUG] Requête UPDATE: " + sql);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            System.out.println("[DEBUG] Connexion établie, préparation des paramètres...");
            
            pstmt.setString(1, membre.getNom());
            pstmt.setString(2, membre.getPrenom());
            pstmt.setString(3, membre.getTelephone());
            pstmt.setString(4, membre.getEmail());
            pstmt.setString(5, membre.getAdresse());
            pstmt.setString(6, membre.getStatut());
            pstmt.setInt(7, membre.getIdMembre());
            
            System.out.println("[DEBUG] Paramètres préparés, exécution de l'UPDATE...");
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("[DEBUG] Lignes affectées: " + affectedRows);
            
            if (affectedRows > 0) {
                System.out.println("[SUCCESS] Membre mis à jour avec succès!");
                return true;
            } else {
                System.out.println("[ERROR] Aucune ligne affectée lors de l'UPDATE");
            }
            
        } catch (SQLException ex) {
            System.err.println("[ERROR] Erreur SQL lors de la mise à jour du membre: " + ex.getMessage());
            System.err.println("[ERROR] Code d'erreur SQL: " + ex.getSQLState());
            System.err.println("[ERROR] Code d'erreur vendor: " + ex.getErrorCode());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("[ERROR] Erreur inattendue lors de la mise à jour du membre: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        System.out.println("[ERROR] Échec de la mise à jour du membre");
        return false;
    }
    
    public boolean delete(int idMembre) {
        System.out.println("[DEBUG] Tentative de suppression du membre ID: " + idMembre);
        
        String sql = "DELETE FROM Membre WHERE id_membre = ?";
        System.out.println("[DEBUG] Requête DELETE: " + sql);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            System.out.println("[DEBUG] Connexion établie, préparation du paramètre...");
            
            pstmt.setInt(1, idMembre);
            
            System.out.println("[DEBUG] Paramètre préparé, exécution du DELETE...");
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("[DEBUG] Lignes affectées: " + affectedRows);
            
            if (affectedRows > 0) {
                System.out.println("[SUCCESS] Membre supprimé avec succès!");
                return true;
            } else {
                System.out.println("[ERROR] Aucune ligne affectée lors du DELETE");
            }
            
        } catch (SQLException ex) {
            System.err.println("[ERROR] Erreur SQL lors de la suppression du membre: " + ex.getMessage());
            System.err.println("[ERROR] Code d'erreur SQL: " + ex.getSQLState());
            System.err.println("[ERROR] Code d'erreur vendor: " + ex.getErrorCode());
            
            if (ex.getSQLState().equals("23000")) {
                System.err.println("[ERROR] Impossible de supprimer ce membre car il a des participations actives");
            }
            ex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("[ERROR] Erreur inattendue lors de la suppression du membre: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        System.out.println("[ERROR] Échec de la suppression du membre");
        return false;
    }
    
    public Membre findById(int idMembre) {
        String sql = "SELECT * FROM Membre WHERE id_membre = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idMembre);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMembre(rs);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la recherche du membre: " + ex.getMessage());
        }
        
        return null;
    }
    
    public Membre findByTelephone(String telephone) {
        String sql = "SELECT * FROM Membre WHERE telephone = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, telephone);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMembre(rs);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la recherche par téléphone: " + ex.getMessage());
        }
        
        return null;
    }
    
    public List<Membre> search(String critere) {
        String sql = "SELECT * FROM Membre WHERE nom LIKE ? OR prenom LIKE ? OR telephone LIKE ? " +
                    "ORDER BY nom, prenom";
        
        List<Membre> membres = new ArrayList<>();
        String searchPattern = "%" + critere + "%";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                membres.add(mapResultSetToMembre(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la recherche de membres: " + ex.getMessage());
        }
        
        return membres;
    }
    
    public List<Membre> findAll() {
        String sql = "SELECT * FROM Membre ORDER BY nom, prenom";
        
        List<Membre> membres = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                membres.add(mapResultSetToMembre(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des membres: " + ex.getMessage());
        }
        
        return membres;
    }
    
    public List<Membre> findActifs() {
        String sql = "SELECT * FROM Membre WHERE statut = 'actif' ORDER BY nom, prenom";
        
        List<Membre> membres = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                membres.add(mapResultSetToMembre(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des membres actifs: " + ex.getMessage());
        }
        
        return membres;
    }
    
    public List<Membre> findByStatut(String statut) {
        String sql = "SELECT * FROM Membre WHERE statut = ? ORDER BY nom, prenom";
        
        List<Membre> membres = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                membres.add(mapResultSetToMembre(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des membres par statut: " + ex.getMessage());
        }
        
        return membres;
    }
    
    public int count() {
        String sql = "SELECT COUNT(*) FROM Membre";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors du comptage des membres: " + ex.getMessage());
        }
        
        return 0;
    }
    
    public int countByStatut(String statut) {
        String sql = "SELECT COUNT(*) FROM Membre WHERE statut = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors du comptage des membres par statut: " + ex.getMessage());
        }
        
        return 0;
    }
    
    public boolean telephoneExists(String telephone) {
        String sql = "SELECT COUNT(*) FROM Membre WHERE telephone = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, telephone);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la vérification du téléphone: " + ex.getMessage());
        }
        
        return false;
    }
    
    private Membre mapResultSetToMembre(ResultSet rs) throws SQLException {
        Membre membre = new Membre();
        membre.setIdMembre(rs.getInt("id_membre"));
        membre.setNom(rs.getString("nom"));
        membre.setPrenom(rs.getString("prenom"));
        membre.setTelephone(rs.getString("telephone"));
        membre.setEmail(rs.getString("email"));
        membre.setAdresse(rs.getString("adresse"));
        
        // Gérer la date d'adhésion qui pourrait être nulle
        java.sql.Date dateSql = rs.getDate("date_adhesion");
        if (dateSql != null) {
            membre.setDateAdhesion(dateSql.toLocalDate());
        } else {
            membre.setDateAdhesion(LocalDate.now());
        }
        
        membre.setStatut(rs.getString("statut"));
        return membre;
    }
}
