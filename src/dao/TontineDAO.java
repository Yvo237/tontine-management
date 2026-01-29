package dao;

import database.DatabaseConnection;
import models.Tontine;
import models.TypeTontine;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des tontines
 * Projet INF2212 - Université de Yaoundé I
 */
public class TontineDAO {
    
    private DatabaseConnection dbConnection;
    
    public TontineDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Ajoute une nouvelle tontine dans la base de données
     */
    public boolean create(Tontine tontine) {
        String sql = "INSERT INTO tontine (type, nom, montant_part, frequence, date_debut, date_fin, statut, id_type, nombre_tours, tour_actuel) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("🔍 [TONTINE DEBUG] Création tontine...");
        System.out.println("  - type: " + tontine.getIdType());
        System.out.println("  - nom: " + tontine.getNom());
        System.out.println("  - date_debut: " + tontine.getDateDebut());
        System.out.println("  - date_fin: " + tontine.getDateFin());
        System.out.println("  - nombre_tours: " + tontine.getNombreTours());
        System.out.println("  - tour_actuel: " + tontine.getTourActuel());
        System.out.println("  - statut: " + tontine.getStatut());
        System.out.println("  - SQL: " + sql);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Récupérer le type depuis typetontine
            String typeNom = "OPTIONNELLE";
            try {
                String sqlType = "SELECT nom FROM typetontine WHERE id_type = ?";
                try (PreparedStatement pstmtType = conn.prepareStatement(sqlType)) {
                    pstmtType.setInt(1, tontine.getIdType());
                    ResultSet rs = pstmtType.executeQuery();
                    if (rs.next()) {
                        String nomType = rs.getString("nom").toUpperCase();
                        // Normaliser pour respecter la contrainte CHECK
                        if (nomType.contains("PRÉSENCE") || nomType.contains("PRESENCE")) {
                            typeNom = "PRESENCE";
                        } else if (nomType.contains("OPTION")) {
                            typeNom = "OPTIONNELLE";
                        } else {
                            typeNom = "OPTIONNELLE"; // Valeur par défaut
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ [TONTINE DEBUG] Impossible de récupérer le type, utilisation de 'OPTIONNELLE'");
            }
            
            pstmt.setString(1, typeNom);
            pstmt.setString(2, tontine.getNom());
            pstmt.setBigDecimal(3, java.math.BigDecimal.valueOf(1000.00));
            pstmt.setString(4, "MENSUELLE");
            pstmt.setDate(5, Date.valueOf(tontine.getDateDebut()));
            pstmt.setDate(6, tontine.getDateFin() != null ? Date.valueOf(tontine.getDateFin()) : null);
            // Normaliser le statut pour respecter la contrainte CHECK
            String statutNormalise = tontine.getStatut();
            if (statutNormalise != null) {
                statutNormalise = statutNormalise.toLowerCase()
                    .replace("é", "e")
                    .replace("è", "e")
                    .replace("ê", "e")
                    .replace("à", "a")
                    .replace("â", "a")
                    .replace("ù", "u")
                    .replace("û", "u")
                    .replace("î", "i")
                    .replace("ï", "i")
                    .replace("ô", "o")
                    .replace("ö", "o");
                
                // S'assurer que le statut est l'une des valeurs autorisées
                if (!statutNormalise.equals("active") && 
                    !statutNormalise.equals("terminee") && 
                    !statutNormalise.equals("suspendue")) {
                    statutNormalise = "active"; // Valeur par défaut sécurisée
                }
            }
            
            pstmt.setString(7, statutNormalise);
            pstmt.setInt(8, tontine.getIdType());
            pstmt.setInt(9, tontine.getNombreTours());
            pstmt.setInt(10, tontine.getTourActuel());
            
            System.out.println("🔍 [TONTINE DEBUG] Type normalisé: " + typeNom);
            System.out.println("🔍 [TONTINE DEBUG] Paramètres préparés, exécution de la requête...");
            int affectedRows = pstmt.executeUpdate();
            System.out.println("🔍 [TONTINE DEBUG] Lignes affectées: " + affectedRows);
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    tontine.setIdTontine(generatedKeys.getInt(1));
                    System.out.println("✅ [TONTINE DEBUG] Tontine créée avec ID: " + tontine.getIdTontine());
                }
                return true;
            } else {
                System.out.println("❌ [TONTINE DEBUG] Aucune ligne affectée");
            }
            
        } catch (SQLException ex) {
            System.err.println("❌ [TONTINE ERROR] Erreur SQL: " + ex.getMessage());
            System.err.println("❌ [TONTINE ERROR] Code SQL: " + ex.getSQLState());
            System.err.println("❌ [TONTINE ERROR] Code vendor: " + ex.getErrorCode());
            ex.printStackTrace();
        }
        
        System.out.println("❌ [TONTINE DEBUG] Échec création tontine");
        return false;
    }
    
    /**
     * Met à jour les informations d'une tontine
     */
    public boolean update(Tontine tontine) {
        String sql = "UPDATE Tontine SET nom = ?, date_fin = ?, nombre_tours = ?, " +
                    "tour_actuel = ?, statut = ? WHERE id_tontine = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tontine.getNom());
            pstmt.setDate(2, tontine.getDateFin() != null ? Date.valueOf(tontine.getDateFin()) : null);
            pstmt.setInt(3, tontine.getNombreTours());
            pstmt.setInt(4, tontine.getTourActuel());
            pstmt.setString(5, tontine.getStatut());
            pstmt.setInt(6, tontine.getIdTontine());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la mise à jour de la tontine: " + ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Supprime une tontine de la base de données
     */
    public boolean delete(int idTontine) {
        String sql = "DELETE FROM Tontine WHERE id_tontine = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idTontine);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la suppression de la tontine: " + ex.getMessage());
            if (ex.getSQLState().equals("23000")) {
                System.err.println("Impossible de supprimer cette tontine car elle a des participations ou des séances");
            }
        }
        
        return false;
    }
    
    /**
     * Recherche une tontine par son ID
     */
    public Tontine findById(int idTontine) {
        String sql = "SELECT t.*, tt.nom as type_nom, tt.description as type_description, " +
                    "tt.est_obligatoire, tt.montant_cotisation, tt.frequence " +
                    "FROM Tontine t " +
                    "JOIN typetontine tt ON t.id_type = tt.id_type " +
                    "WHERE t.id_tontine = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idTontine);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTontine(rs);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la recherche de la tontine: " + ex.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère toutes les tontines
     */
    public List<Tontine> findAll() {
        String sql = "SELECT t.*, tt.nom as type_nom, tt.description as type_description, " +
                    "tt.est_obligatoire, tt.montant_cotisation, tt.frequence " +
                    "FROM tontine t " +
                    "JOIN typetontine tt ON t.id_type = tt.id_type " +
                    "ORDER BY t.date_debut DESC";
        
        System.out.println("🔍 [TONTINE DEBUG] Recherche de toutes les tontines...");
        System.out.println("  - SQL: " + sql);
        
        List<Tontine> tontines = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int count = 0;
            while (rs.next()) {
                Tontine tontine = mapResultSetToTontine(rs);
                tontines.add(tontine);
                count++;
                System.out.println("🔍 [TONTINE DEBUG] Tontine trouvée: " + tontine.getNom() + " (ID: " + tontine.getIdTontine() + ")");
            }
            
            System.out.println("🔍 [TONTINE DEBUG] Total tontines trouvées: " + count);
            
        } catch (SQLException ex) {
            System.err.println("❌ [TONTINE ERROR] Erreur lors de la récupération des tontines: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return tontines;
    }
    
    /**
     * Récupère les tontines actives uniquement
     */
    public List<Tontine> findActives() {
        String sql = "SELECT t.*, tt.nom as type_nom, tt.description as type_description, " +
                    "tt.est_obligatoire, tt.montant_cotisation, tt.frequence " +
                    "FROM Tontine t " +
                    "JOIN typetontine tt ON t.id_type = tt.id_type " +
                    "WHERE t.statut = 'active' " +
                    "ORDER BY t.date_debut DESC";
        
        List<Tontine> tontines = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tontines.add(mapResultSetToTontine(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des tontines actives: " + ex.getMessage());
        }
        
        return tontines;
    }
    
    /**
     * Récupère les tontines par statut
     */
    public List<Tontine> findByStatut(String statut) {
        String sql = "SELECT t.*, tt.nom as type_nom, tt.description as type_description, " +
                    "tt.est_obligatoire, tt.montant_cotisation, tt.frequence " +
                    "FROM Tontine t " +
                    "JOIN typetontine tt ON t.id_type = tt.id_type " +
                    "WHERE t.statut = ? " +
                    "ORDER BY t.date_debut DESC";
        
        List<Tontine> tontines = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tontines.add(mapResultSetToTontine(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des tontines par statut: " + ex.getMessage());
        }
        
        return tontines;
    }
    
    /**
     * Récupère les tontines par type
     */
    public List<Tontine> findByType(int idType) {
        String sql = "SELECT t.*, tt.nom as type_nom, tt.description as type_description, " +
                    "tt.est_obligatoire, tt.montant_cotisation, tt.frequence " +
                    "FROM Tontine t " +
                    "JOIN typetontine tt ON t.id_type = tt.id_type " +
                    "WHERE t.id_type = ? " +
                    "ORDER BY t.date_debut DESC";
        
        List<Tontine> tontines = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tontines.add(mapResultSetToTontine(rs));
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des tontines par type: " + ex.getMessage());
        }
        
        return tontines;
    }
    
    /**
     * Compte le nombre total de tontines
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM Tontine";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors du comptage des tontines: " + ex.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Compte le nombre de tontines par statut
     */
    public int countByStatut(String statut) {
        String sql = "SELECT COUNT(*) FROM Tontine WHERE statut = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors du comptage des tontines par statut: " + ex.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Met à jour le tour actuel d'une tontine
     */
    public boolean updateTourActuel(int idTontine, int nouveauTour) {
        String sql = "UPDATE Tontine SET tour_actuel = ? WHERE id_tontine = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nouveauTour);
            pstmt.setInt(2, idTontine);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la mise à jour du tour actuel: " + ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Clôture une tontine (marque comme terminée)
     */
    public boolean cloreTontine(int idTontine) {
        String sql = "UPDATE Tontine SET statut = 'terminee', date_fin = CURDATE() WHERE id_tontine = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idTontine);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la clôture de la tontine: " + ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Convertit un ResultSet en objet Tontine
     */
    private Tontine mapResultSetToTontine(ResultSet rs) throws SQLException {
        Tontine tontine = new Tontine();
        tontine.setIdTontine(rs.getInt("id_tontine"));
        tontine.setIdType(rs.getInt("id_type"));
        tontine.setNom(rs.getString("nom"));
        tontine.setDateDebut(rs.getDate("date_debut").toLocalDate());
        
        Date dateFin = rs.getDate("date_fin");
        if (dateFin != null) {
            tontine.setDateFin(dateFin.toLocalDate());
        }
        
        tontine.setNombreTours(rs.getInt("nombre_tours"));
        tontine.setTourActuel(rs.getInt("tour_actuel"));
        tontine.setStatut(rs.getString("statut"));
        
        // Créer et associer le TypeTontine
        TypeTontine typeTontine = new TypeTontine();
        typeTontine.setIdType(rs.getInt("id_type"));
        typeTontine.setNom(rs.getString("type_nom"));
        typeTontine.setDescription(rs.getString("type_description"));
        typeTontine.setEstObligatoire(rs.getBoolean("est_obligatoire"));
        typeTontine.setMontantCotisation(rs.getBigDecimal("montant_cotisation"));
        typeTontine.setFrequence(rs.getString("frequence"));
        
        tontine.setTypeTontine(typeTontine);
        
        return tontine;
    }
}
