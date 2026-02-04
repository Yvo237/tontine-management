package dao;

import database.DatabaseConnection;
import models.Penalite;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PenaliteDAO {
    
    private DatabaseConnection dbConnection;
    private MembreDAO membreDAO;
    private SeanceDAO seanceDAO;
    
    public PenaliteDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.membreDAO = new MembreDAO();
        this.seanceDAO = new SeanceDAO();
    }
    
    public boolean create(Penalite penalite) {
        String sql = "INSERT INTO penalite (id_membre, id_seance, motif, montant, date_penalite, payee) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, penalite.getIdMembre());
            if (penalite.getIdSeance() != null) {
                pstmt.setInt(2, penalite.getIdSeance());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, penalite.getMotif());
            pstmt.setBigDecimal(4, penalite.getMontant());
            pstmt.setDate(5, Date.valueOf(penalite.getDatePenalite()));
            pstmt.setBoolean(6, penalite.isPayee());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    penalite.setIdPenalite(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la pénalité: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public Penalite findById(int idPenalite) {
        String sql = "SELECT * FROM penalite WHERE id_penalite = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPenalite);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPenalite(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la pénalité: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Penalite> findAll() {
        List<Penalite> penalites = new ArrayList<>();
        String sql = "SELECT * FROM penalite ORDER BY date_penalite DESC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                penalites.add(mapResultSetToPenalite(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des pénalités: " + e.getMessage());
            e.printStackTrace();
        }
        return penalites;
    }
    
    public List<Penalite> findByMembre(int idMembre) {
        List<Penalite> penalites = new ArrayList<>();
        String sql = "SELECT * FROM penalite WHERE id_membre = ? ORDER BY date_penalite DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idMembre);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                penalites.add(mapResultSetToPenalite(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des pénalités du membre: " + e.getMessage());
            e.printStackTrace();
        }
        return penalites;
    }
    
    public List<Penalite> findImpayees() {
        List<Penalite> penalites = new ArrayList<>();
        String sql = "SELECT * FROM penalite WHERE payee = FALSE ORDER BY date_penalite DESC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                penalites.add(mapResultSetToPenalite(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des pénalités impayées: " + e.getMessage());
            e.printStackTrace();
        }
        return penalites;
    }
    
    public List<Penalite> findBySeance(int idSeance) {
        List<Penalite> penalites = new ArrayList<>();
        String sql = "SELECT * FROM penalite WHERE id_seance = ? ORDER BY date_penalite DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idSeance);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                penalites.add(mapResultSetToPenalite(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des pénalités de la séance: " + e.getMessage());
            e.printStackTrace();
        }
        return penalites;
    }
    
    public boolean update(Penalite penalite) {
        String sql = "UPDATE penalite SET id_membre = ?, id_seance = ?, motif = ?, montant = ?, " +
                    "date_penalite = ?, payee = ? WHERE id_penalite = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, penalite.getIdMembre());
            if (penalite.getIdSeance() != null) {
                pstmt.setInt(2, penalite.getIdSeance());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, penalite.getMotif());
            pstmt.setBigDecimal(4, penalite.getMontant());
            pstmt.setDate(5, Date.valueOf(penalite.getDatePenalite()));
            pstmt.setBoolean(6, penalite.isPayee());
            pstmt.setInt(7, penalite.getIdPenalite());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la pénalité: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean delete(int idPenalite) {
        String sql = "DELETE FROM penalite WHERE id_penalite = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPenalite);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la pénalité: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public BigDecimal getTotalPenalitesByMembre(int idMembre) {
        String sql = "SELECT COALESCE(SUM(montant), 0) as total FROM penalite WHERE id_membre = ? AND payee = FALSE";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idMembre);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du total des pénalités: " + e.getMessage());
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    public boolean marquerCommePayee(int idPenalite) {
        String sql = "UPDATE penalite SET payee = TRUE WHERE id_penalite = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPenalite);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors du marquage de la pénalité comme payée: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private Penalite mapResultSetToPenalite(ResultSet rs) throws SQLException {
        Penalite penalite = new Penalite();
        penalite.setIdPenalite(rs.getInt("id_penalite"));
        penalite.setIdMembre(rs.getInt("id_membre"));
        
        int idSeance = rs.getInt("id_seance");
        if (!rs.wasNull()) {
            penalite.setIdSeance(idSeance);
        }
        
        penalite.setMotif(rs.getString("motif"));
        penalite.setMontant(rs.getBigDecimal("montant"));
        penalite.setDatePenalite(rs.getDate("date_penalite").toLocalDate());
        penalite.setPayee(rs.getBoolean("payee"));
        penalite.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        // Charger les associations
        penalite.setMembre(membreDAO.findById(penalite.getIdMembre()));
        
        if (penalite.getIdSeance() != null) {
            penalite.setSeance(seanceDAO.findById(penalite.getIdSeance()));
        }
        
        return penalite;
    }
}
