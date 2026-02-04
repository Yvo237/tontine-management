package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;

import database.DatabaseConnection;
import ui.panels.AccueilPanel;
import ui.panels.CreditsPanel;
import ui.panels.MembresPanel;
import ui.panels.PenalitesPanel;
import ui.panels.ProjetsFIACPanel;
import ui.panels.RapportsPanel;
import ui.panels.SeancesPanel;
import ui.panels.TontinesPanel;

public class MainFrame extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Composants principaux
    private JPanel mainPanel;
    private JPanel sidePanel;
    private JPanel contentPanel;
    private JLabel timeLabel;
    private JButton currentActiveButton;
    
    // Panneaux de contenu
    private AccueilPanel accueilPanel;
    private MembresPanel membresPanel;
    private TontinesPanel tontinesPanel;
    private SeancesPanel seancesPanel;
    private CreditsPanel creditsPanel;
    private PenalitesPanel penalitesPanel;
    private ProjetsFIACPanel projetsFIACPanel;
    private RapportsPanel rapportsPanel;
    
    // Base de données
    private DatabaseConnection dbConnection;
    
    // Palette de couleurs cohérente
    private static final Color PRIMARY_PURPLE = new Color(139, 92, 246);
    private static final Color PRIMARY_DARK = new Color(15, 23, 42);
    private static final Color SIDEBAR_BG = new Color(30, 41, 59);
    private static final Color SIDEBAR_HOVER = new Color(51, 65, 85);
    private static final Color SIDEBAR_ACTIVE = new Color(139, 92, 246);
    private static final Color BACKGROUND = new Color(241, 245, 249);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    
    public MainFrame() {
        super("Tontine Management Elite");
        
        setupModernLookAndFeel();
        
        this.dbConnection = DatabaseConnection.getInstance();
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1600, 1000);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1400, 900));
        
        initializeComponents();
        setupEventHandlers();
        
        // Démarrer le timer pour l'heure
        startTimeUpdater();
    }
    
    private void setupModernLookAndFeel() {
        try {
            FlatLightLaf.setup();
            
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
            
        } catch (Exception e) {
            System.err.println("Erreur look and feel: " + e.getMessage());
        }
    }
    
    private void initializeComponents() {
        // Panel principal
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BACKGROUND);
        
        // Sidebar moderne
        sidePanel = createModernSidebar();
        
        // Content panel
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Initialiser les panneaux
        accueilPanel = new AccueilPanel(this);
        membresPanel = new MembresPanel(this);
        tontinesPanel = new TontinesPanel(this);
        seancesPanel = new SeancesPanel(this);
        creditsPanel = new CreditsPanel(this);
        penalitesPanel = new PenalitesPanel(this);
        projetsFIACPanel = new ProjetsFIACPanel(this);
        rapportsPanel = new RapportsPanel(this);
        
        contentPanel.add(accueilPanel, "accueil");
        contentPanel.add(membresPanel, "membres");
        contentPanel.add(tontinesPanel, "tontines");
        contentPanel.add(seancesPanel, "seances");
        contentPanel.add(creditsPanel, "credits");
        contentPanel.add(penalitesPanel, "penalites");
        contentPanel.add(projetsFIACPanel, "projets");
        contentPanel.add(rapportsPanel, "rapports");
        
        mainPanel.add(sidePanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Afficher l'accueil par défaut
        showPanel("accueil");
    }
    
    private JPanel createModernSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(280, 0));
        
        // Container principal
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(SIDEBAR_BG);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Header avec logo
        JPanel headerPanel = createSidebarHeader();
        mainContainer.add(headerPanel);
        
        mainContainer.add(Box.createVerticalStrut(40));
        
        // Navigation
        JPanel navPanel = createNavigationPanel();
        mainContainer.add(navPanel);
        
        mainContainer.add(Box.createVerticalGlue());
        
        // Footer
        JPanel footerPanel = createSidebarFooter();
        mainContainer.add(footerPanel);
        
        sidebar.add(mainContainer, BorderLayout.CENTER);
        
        return sidebar;
    }
    
    private JPanel createSidebarHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(SIDEBAR_BG);
        header.setBorder(BorderFactory.createEmptyBorder(32, 24, 32, 24));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        // Logo - Charger l'image PNG
        JLabel logoLabel;
        try {
            // Utiliser un chemin absolu pour garantir le chargement
            String logoPath = System.getProperty("user.dir") + "/src/ui/logo/logo.png";
            ImageIcon logoIcon = new ImageIcon(logoPath);
            if (logoIcon.getImage() == null || logoIcon.getIconWidth() <= 0) {
                throw new Exception("Image non trouvée ou invalide: " + logoPath);
            }
            // Redimensionner le logo à une taille appropriée (48x48 pixels)
            Image scaledImage = logoIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaledImage));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            System.out.println("Logo chargé avec succès depuis: " + logoPath);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du logo: " + e.getMessage());
            // Fallback : utiliser l'emoji si le logo ne peut pas être chargé
            logoLabel = new JLabel("💎");
            logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        // Titre
        JLabel titleLabel = new JLabel("Tontine Elite");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Sous-titre
        JLabel subtitleLabel = new JLabel("Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        header.add(logoLabel);
        header.add(Box.createVerticalStrut(12));
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitleLabel);
        
        return header;
    }
    
    private JPanel createNavigationPanel() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(SIDEBAR_BG);
        nav.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        nav.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
        
        // Label section
        JLabel navLabel = new JLabel("NAVIGATION");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navLabel.setForeground(new Color(100, 116, 139));
        navLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 0));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nav.add(navLabel);
        
        // Boutons de navigation
        JButton btnAccueil = createNavButton("Accueil", "🏠", "accueil", true);
        JButton btnMembres = createNavButton("Membres", "👥", "membres", false);
        JButton btnTontines = createNavButton("Tontines", "💰", "tontines", false);
        JButton btnSeances = createNavButton("Séances", "📅", "seances", false);
        JButton btnCredits = createNavButton("Crédits", "💳", "credits", false);
        JButton btnPenalites = createNavButton("Pénalités", "⚠️", "penalites", false);
        JButton btnProjets = createNavButton("Projets FIAC", "🏗️", "projets", false);
        JButton btnRapports = createNavButton("Rapports", "📊", "rapports", false);
        
        currentActiveButton = btnAccueil;
        
        nav.add(btnAccueil);
        nav.add(Box.createVerticalStrut(4));
        nav.add(btnMembres);
        nav.add(Box.createVerticalStrut(4));
        nav.add(btnTontines);
        nav.add(Box.createVerticalStrut(4));
        nav.add(btnSeances);
        nav.add(Box.createVerticalStrut(4));
        nav.add(btnCredits);
        nav.add(Box.createVerticalStrut(4));
        nav.add(btnPenalites);
        nav.add(Box.createVerticalStrut(4));
        nav.add(btnProjets);
        nav.add(Box.createVerticalStrut(4));
        nav.add(btnRapports);
        
        return nav;
    }
    
    private JButton createNavButton(String text, String icon, String panel, boolean active) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(12, 0));
        
        // Icône
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        
        // Texte
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        button.add(iconLabel, BorderLayout.WEST);
        button.add(textLabel, BorderLayout.CENTER);
        
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        button.setPreferredSize(new Dimension(248, 48));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Style initial
        if (active) {
            button.setOpaque(true);
            button.setBackground(SIDEBAR_ACTIVE);
            iconLabel.setForeground(Color.WHITE);
            textLabel.setForeground(Color.WHITE);
        } else {
            button.setOpaque(false);
            iconLabel.setForeground(new Color(148, 163, 184));
            textLabel.setForeground(new Color(148, 163, 184));
        }
        
        // Effets hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button != currentActiveButton) {
                    button.setOpaque(true);
                    button.setBackground(SIDEBAR_HOVER);
                    iconLabel.setForeground(Color.WHITE);
                    textLabel.setForeground(Color.WHITE);
                }
            }
            
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (button != currentActiveButton) {
                    button.setOpaque(false);
                    iconLabel.setForeground(new Color(148, 163, 184));
                    textLabel.setForeground(new Color(148, 163, 184));
                }
            }
        });
        
        // Action
        button.addActionListener(e -> {
            // Désactiver l'ancien bouton
            if (currentActiveButton != null) {
                currentActiveButton.setOpaque(false);
                currentActiveButton.setBackground(SIDEBAR_BG);
                Component[] comps = currentActiveButton.getComponents();
                for (Component c : comps) {
                    if (c instanceof JLabel) {
                        ((JLabel) c).setForeground(new Color(148, 163, 184));
                    }
                }
            }
            
            // Activer le nouveau
            button.setOpaque(true);
            button.setBackground(SIDEBAR_ACTIVE);
            iconLabel.setForeground(Color.WHITE);
            textLabel.setForeground(Color.WHITE);
            currentActiveButton = button;
            
            showPanel(panel);
        });
        
        return button;
    }
    
    private JPanel createSidebarFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(SIDEBAR_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        // Séparateur
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(51, 65, 85));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        footer.add(separator);
        footer.add(Box.createVerticalStrut(20));
        
        // Info utilisateur
        JPanel userPanel = new JPanel(new BorderLayout(12, 0));
        userPanel.setBackground(SIDEBAR_BG);
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        // Avatar
        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        avatarLabel.setForeground(PRIMARY_PURPLE);
        
        // Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(SIDEBAR_BG);
        
        JLabel nameLabel = new JLabel("Hello, User");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLabel.setForeground(new Color(148, 163, 184));
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateTime();
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(timeLabel);
        
        userPanel.add(avatarLabel, BorderLayout.WEST);
        userPanel.add(infoPanel, BorderLayout.CENTER);
        
        footer.add(userPanel);
        footer.add(Box.createVerticalStrut(16));
        
        // Version
        JLabel versionLabel = new JLabel("Version 3.0 Elite");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        versionLabel.setForeground(new Color(100, 116, 139));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(versionLabel);
        
        return footer;
    }
    
    private void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panelName);
        
        // Mettre à jour le titre
        switch (panelName) {
            case "accueil":
                setTitle("Tontine Elite - Accueil");
                break;
            case "membres":
                setTitle("Tontine Elite - Membres");
                break;
            case "tontines":
                setTitle("Tontine Elite - Tontines");
                break;
            case "seances":
                setTitle("Tontine Elite - Séances");
                break;
            case "credits":
                setTitle("Tontine Elite - Crédits");
                break;
            case "rapports":
                setTitle("Tontine Elite - Rapports");
                break;
        }
        
        // RÉACTIVÉ : Refresh automatique pour afficher les données BDD
        refreshCurrentPanel();
        
        System.out.println("Panneau affiché: " + panelName + " (avec données BDD)");
    }
    
    private void refreshCurrentPanel() {
        for (Component comp : contentPanel.getComponents()) {
            if (comp.isVisible()) {
                if (comp instanceof AccueilPanel) {
                    ((AccueilPanel) comp).rafraichir();
                } else if (comp instanceof MembresPanel) {
                    ((MembresPanel) comp).rafraichir();
                } else if (comp instanceof TontinesPanel) {
                    ((TontinesPanel) comp).rafraichir();
                } else if (comp instanceof SeancesPanel) {
                    ((SeancesPanel) comp).rafraichir();
                } else if (comp instanceof CreditsPanel) {
                    ((CreditsPanel) comp).rafraichir();
                } else if (comp instanceof RapportsPanel) {
                    ((RapportsPanel) comp).rafraichir();
                }
                break;
            }
        }
    }
    
    
    private void setupEventHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(
                    MainFrame.this,
                    "Êtes-vous sûr de vouloir quitter l'application ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (option == JOptionPane.YES_OPTION) {
                    dbConnection.closeConnection();
                    dispose();
                    System.exit(0);
                }
            }
        });
    }
    
    private void startTimeUpdater() {
        Timer timer = new Timer(1000, e -> updateTime());
        timer.start();
    }
    
    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (timeLabel != null) {
            timeLabel.setText(now.format(formatter));
        }
    }
    
    // Méthodes publiques pour la navigation
    public void afficherMembres() { showPanel("membres"); }
    public void afficherTontines() { showPanel("tontines"); }
    public void afficherCredits() { showPanel("credits"); }
    public void afficherAccueil() { showPanel("accueil"); }
    public void afficherSeances() { showPanel("seances"); }
    public void afficherRapports() { showPanel("rapports"); }
    
    public AccueilPanel getAccueilPanel() {
        return accueilPanel;
    }
    
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
            
        } catch (Exception e) {
            System.err.println("Erreur look and feel: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Erreur lors du démarrage:\n" + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}