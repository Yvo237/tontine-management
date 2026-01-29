package database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Classe de gestion de la connexion à la base de données
 * Projet INF2212 - Université de Yaoundé I
 */
public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Connection connection;
    private Properties properties;
    
    /**
     * Constructeur privé pour le pattern Singleton
     */
    private DatabaseConnection() {
        loadProperties();
    }
    
    /**
     * Point d'accès unique à l'instance (Singleton)
     * @return instance de DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Charge les propriétés depuis le fichier database.properties
     */
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("Fichier database.properties non trouvé dans le classpath");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Erreur lors du chargement du fichier de configuration: " + ex.getMessage());
        }
    }
    
    /**
     * Établit la connexion à la base de données
     * @return Connection objet de connexion
     * @throws SQLException en cas d'erreur de connexion
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                System.out.println("🔍 [DEBUG] Tentative de connexion à la base de données...");
                
                // Chargement du driver
                String driver = properties.getProperty("db.driver");
                System.out.println("🔍 [DEBUG] Driver: " + driver);
                if (driver != null) {
                    Class.forName(driver);
                    System.out.println("🔍 [DEBUG] Driver chargé avec succès");
                }
                
                // Création de la connexion
                String url = properties.getProperty("db.url");
                String username = properties.getProperty("db.username");
                String password = properties.getProperty("db.password");
                
                System.out.println("🔍 [DEBUG] URL: " + url);
                System.out.println("🔍 [DEBUG] Username: " + username);
                System.out.println("🔍 [DEBUG] Password: " + (password != null ? "[MASQUÉ]" : "NULL"));
                
                connection = DriverManager.getConnection(url, username, password);
                
                System.out.println("✅ Connexion à la base de données établie avec succès");
                System.out.println("🔍 [DEBUG] Connexion valide: " + connection.isValid(5));
                System.out.println("🔍 [DEBUG] Auto-commit: " + connection.getAutoCommit());
                
            } catch (ClassNotFoundException ex) {
                System.err.println("❌ [ERROR] Driver de base de données non trouvé: " + ex.getMessage());
                throw new SQLException("Driver de base de données non trouvé: " + ex.getMessage());
            } catch (SQLException ex) {
                System.err.println("❌ [ERROR] Erreur SQL lors de la connexion: " + ex.getMessage());
                System.err.println("❌ [ERROR] SQL State: " + ex.getSQLState());
                System.err.println("❌ [ERROR] Error Code: " + ex.getErrorCode());
                throw ex;
            }
        } else {
            System.out.println("🔍 [DEBUG] Connexion existante réutilisée");
        }
        return connection;
    }
    
    /**
     * Teste la connexion à la base de données
     * @return true si la connexion est réussie, false sinon
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed() && conn.isValid(5);
        } catch (SQLException ex) {
            System.err.println("❌ Erreur de connexion: " + ex.getMessage());
            return false;
        }
    }
    
    /**
     * Ferme la connexion à la base de données
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("🔌 Connexion à la base de données fermée");
                }
            } catch (SQLException ex) {
                System.err.println("❌ Erreur lors de la fermeture de la connexion: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Affiche un message d'erreur convivial pour les problèmes de connexion
     * @param ex l'exception SQL
     */
    public static void afficherErreurConnexion(SQLException ex) {
        String message = "Erreur de connexion à la base de données:\n\n";
        
        if (ex.getSQLState().equals("08001")) {
            message += "❌ Impossible de se connecter au serveur MySQL\n";
            message += "• Vérifiez que MySQL est démarré\n";
            message += "• Vérifiez l'URL de connexion dans database.properties\n";
            message += "• Vérifiez que le port 3306 est disponible";
        } else if (ex.getSQLState().equals("28000")) {
            message += "❌ Identifiants de connexion incorrects\n";
            message += "• Vérifiez le nom d'utilisateur dans database.properties\n";
            message += "• Vérifiez le mot de passe dans database.properties";
        } else if (ex.getSQLState().equals("42000")) {
            message += "❌ Base de données non trouvée\n";
            message += "• Exécutez d'abord le script schema.sql\n";
            message += "• Vérifiez que la base 'gestion_tontine' existe";
        } else {
            message += "❌ " + ex.getMessage() + "\n";
            message += "• Code SQL: " + ex.getSQLState() + "\n";
            message += "• Vérifiez votre configuration MySQL";
        }
        
        message += "\n\nDétails techniques:\n" + ex.getMessage();
        
        JOptionPane.showMessageDialog(null, message, "Erreur de Connexion", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Obtient une propriété de configuration
     * @param key nom de la propriété
     * @return valeur de la propriété
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Vérifie si la base de données est accessible et contient les tables requises
     * @return true si la base est prête, false sinon
     */
    public boolean isDatabaseReady() {
        try {
            Connection conn = getConnection();
            if (conn == null || conn.isClosed()) {
                return false;
            }
            
            // Vérification de l'existence des tables principales
            java.sql.DatabaseMetaData meta = conn.getMetaData();
            java.sql.ResultSet tables = meta.getTables(null, null, "Membre", null);
            boolean membreTableExists = tables.next();
            tables.close();
            
            if (!membreTableExists) {
                JOptionPane.showMessageDialog(null, 
                    "La base de données existe mais les tables ne sont pas créées.\n" +
                    "Veuillez exécuter le script schema.sql pour créer les tables.",
                    "Base de Données Incomplète", 
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            return true;
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la vérification de la base: " + ex.getMessage());
            return false;
        }
    }
}
