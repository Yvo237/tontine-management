package ui.panels;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;

import dao.MembreDAO;
import dao.TontineDAO;
import dao.CreditDAO;
import ui.MainFrame;

/**
 * Panneau d'accueil avec tableau de bord ultra-moderne
 * Design 3.0 - Interface Premium avec gradients et animations subtiles
 */
public class AccueilPanel extends JPanel {
    private MainFrame mainFrame;
    private MembreDAO membreDAO;
    private TontineDAO tontineDAO;
    private CreditDAO creditDAO;
    
    private JLabel lblNombreMembres;
    private JLabel lblMembresActifs;
    private JLabel lblTontinesActives;
    private JLabel lblCreditsEnCours;
    
    // Palette de couleurs moderne et sophistiquée
    private static final Color PRIMARY_DARK = new Color(15, 23, 42);      // Slate 900
    private static final Color PRIMARY_PURPLE = new Color(139, 92, 246);  // Violet 500
    private static final Color PRIMARY_BLUE = new Color(59, 130, 246);    // Blue 500
    private static final Color PRIMARY_EMERALD = new Color(16, 185, 129); // Emerald 500
    private static final Color PRIMARY_AMBER = new Color(251, 146, 60);   // Orange 400
    private static final Color BACKGROUND = new Color(241, 245, 249);     // Slate 100
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    
    public AccueilPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.membreDAO = new MembreDAO();
        this.tontineDAO = new TontineDAO();
        this.creditDAO = new CreditDAO();
        initComponents();
        chargerStatistiques();
    }
    
    /**
     * Initialise tous les composants avec un design ultra-moderne
     */
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND);
        
        // Container principal avec padding
        JPanel mainContainer = new JPanel(new BorderLayout(0, 24));
        mainContainer.setBackground(BACKGROUND);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        
        // En-tête sophistiqué avec gradient visuel
        JPanel headerPanel = createUltraModernHeader();
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // Corps principal avec grille de cartes
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 24));
        bodyPanel.setBackground(BACKGROUND);
        
        // Grille des statistiques premium
        JPanel statsGrid = createPremiumStatsGrid();
        bodyPanel.add(statsGrid, BorderLayout.CENTER);
        
        // Section d'actions rapides
        JPanel quickActionsPanel = createQuickActionsPanel();
        bodyPanel.add(quickActionsPanel, BorderLayout.SOUTH);
        
        mainContainer.add(bodyPanel, BorderLayout.CENTER);
        add(mainContainer, BorderLayout.CENTER);
    }
    
    /**
     * Crée l'en-tête ultra-moderne avec gradient et informations de session
     */
    private JPanel createUltraModernHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(new RoundedBorder(20, new Color(226, 232, 240), 1));
        
        // Panneau interne avec padding
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        
        // Section gauche - Titre et sous-titre
        JPanel leftSection = new JPanel();
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));
        leftSection.setBackground(CARD_BG);
        
        JLabel welcomeLabel = new JLabel("Bonjour, Administrateur 👋");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setForeground(TEXT_SECONDARY);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Tableau de Bord");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Gestion Financière & Tontines");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(PRIMARY_PURPLE);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftSection.add(welcomeLabel);
        leftSection.add(Box.createVerticalStrut(4));
        leftSection.add(titleLabel);
        leftSection.add(Box.createVerticalStrut(4));
        leftSection.add(subtitleLabel);
        
        // Section droite - Date et heure
        JPanel rightSection = new JPanel();
        rightSection.setLayout(new BoxLayout(rightSection, BoxLayout.Y_AXIS));
        rightSection.setBackground(CARD_BG);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        
        JLabel dateLabel = new JLabel(dateFormat.format(new Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(TEXT_SECONDARY);
        dateLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        JLabel timeLabel = new JLabel(timeFormat.format(new Date()));
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        timeLabel.setForeground(TEXT_PRIMARY);
        timeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        rightSection.add(dateLabel);
        rightSection.add(Box.createVerticalStrut(4));
        rightSection.add(timeLabel);
        
        innerPanel.add(leftSection, BorderLayout.WEST);
        innerPanel.add(rightSection, BorderLayout.EAST);
        
        header.add(innerPanel);
        return header;
    }
    
    /**
     * Crée la grille premium des cartes statistiques
     */
    private JPanel createPremiumStatsGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 24, 24));
        grid.setBackground(BACKGROUND);
        
        // Cartes avec couleurs distinctives
        JPanel cardMembres = createPremiumStatCard(
            "Total Membres", "0", "👥", 
            PRIMARY_PURPLE, new Color(237, 233, 254)
        );
        
        JPanel cardActifs = createPremiumStatCard(
            "Membres Actifs", "0", "⚡", 
            PRIMARY_BLUE, new Color(219, 234, 254)
        );
        
        JPanel cardTontines = createPremiumStatCard(
            "Tontines Actives", "0", "💎", 
            PRIMARY_EMERALD, new Color(209, 250, 229)
        );
        
        JPanel cardCredits = createPremiumStatCard(
            "Crédits en Cours", "0", "💰", 
            PRIMARY_AMBER, new Color(254, 243, 199)
        );
        
        grid.add(cardMembres);
        grid.add(cardActifs);
        grid.add(cardTontines);
        grid.add(cardCredits);
        
        return grid;
    }
    
    /**
     * Crée une carte statistique premium avec design moderne
     */
    private JPanel createPremiumStatCard(String titre, String valeur, String icone, 
                                         Color accentColor, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new RoundedBorder(16, new Color(226, 232, 240), 1));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Container interne
        JPanel innerContainer = new JPanel(new BorderLayout());
        innerContainer.setBackground(CARD_BG);
        innerContainer.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        
        // En-tête de la carte
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BG);
        
        // Badge icône avec fond coloré
        JPanel iconBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconBadge.setPreferredSize(new Dimension(56, 56));
        iconBadge.setBackground(bgColor);
        iconBadge.setBorder(new RoundedBorder(12, bgColor, 0));
        
        JLabel iconLabel = new JLabel(icone);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconBadge.add(iconLabel);
        
        // Panneau titre
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(CARD_BG);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        
        JLabel titleLabel = new JLabel(titre);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(Box.createVerticalGlue());
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalGlue());
        
        headerPanel.add(iconBadge, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Valeur principale
        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setBackground(CARD_BG);
        valuePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JLabel valueLabel = new JLabel(valeur);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        valueLabel.setForeground(TEXT_PRIMARY);
        
        // Footer avec trend indicator
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footerPanel.setBackground(CARD_BG);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        
        JLabel trendLabel = new JLabel("↗ +12% ce mois");
        trendLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        trendLabel.setForeground(PRIMARY_EMERALD);
        footerPanel.add(trendLabel);
        
        valuePanel.add(valueLabel, BorderLayout.CENTER);
        valuePanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Stocker les références
        if (titre.equals("Total Membres")) {
            lblNombreMembres = valueLabel;
        } else if (titre.equals("Membres Actifs")) {
            lblMembresActifs = valueLabel;
        } else if (titre.equals("Tontines Actives")) {
            lblTontinesActives = valueLabel;
        } else if (titre.equals("Crédits en Cours")) {
            lblCreditsEnCours = valueLabel;
        }
        
        innerContainer.add(headerPanel, BorderLayout.NORTH);
        innerContainer.add(valuePanel, BorderLayout.CENTER);
        card.add(innerContainer);
        
        // Effets hover sophistiqués
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color originalBg = CARD_BG;
            
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(249, 250, 251));
                innerContainer.setBackground(new Color(249, 250, 251));
                headerPanel.setBackground(new Color(249, 250, 251));
                titlePanel.setBackground(new Color(249, 250, 251));
                valuePanel.setBackground(new Color(249, 250, 251));
                footerPanel.setBackground(new Color(249, 250, 251));
                card.setBorder(new RoundedBorder(16, accentColor, 2));
            }
            
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(originalBg);
                innerContainer.setBackground(originalBg);
                headerPanel.setBackground(originalBg);
                titlePanel.setBackground(originalBg);
                valuePanel.setBackground(originalBg);
                footerPanel.setBackground(originalBg);
                card.setBorder(new RoundedBorder(16, new Color(226, 232, 240), 1));
            }
        });
        
        return card;
    }
    
    /**
     * Crée le panneau d'actions rapides
     */
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new RoundedBorder(16, new Color(226, 232, 240), 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        
        // Titre de la section
        JLabel titleLabel = new JLabel("🚀 Actions Rapides");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        
        // Grille de boutons
        JPanel buttonsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        buttonsGrid.setBackground(CARD_BG);
        
        buttonsGrid.add(createActionButton("➕ Nouveau Membre", PRIMARY_PURPLE));
        buttonsGrid.add(createActionButton("💰 Créer Tontine", PRIMARY_BLUE));
        buttonsGrid.add(createActionButton("📅 Planifier Séance", PRIMARY_EMERALD));
        buttonsGrid.add(createActionButton("📊 Voir Rapports", PRIMARY_AMBER));
        
        innerPanel.add(titleLabel, BorderLayout.NORTH);
        innerPanel.add(buttonsGrid, BorderLayout.CENTER);
        
        panel.add(innerPanel);
        return panel;
    }
    
    /**
     * Crée un bouton d'action moderne
     */
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        
        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    /**
     * Charge les statistiques depuis la base de données
     */
    private void chargerStatistiques() {
        try {
            // Statistiques des membres
            int nombreMembres = 0;
            int membresActifs = 0;
            try {
                nombreMembres = membreDAO.findAll().size();
                // Compter les membres actifs
                membresActifs = (int) membreDAO.findAll().stream()
                    .filter(m -> "actif".equalsIgnoreCase(m.getStatut()))
                    .count();
            } catch (Exception e) {
                System.err.println("Erreur chargement membres: " + e.getMessage());
            }
            
            if (lblNombreMembres != null) {
                lblNombreMembres.setText(String.valueOf(nombreMembres));
            }
            
            if (lblMembresActifs != null) {
                lblMembresActifs.setText(String.valueOf(membresActifs));
            }
            
            // Statistiques des tontines
            int tontinesActives = 0;
            try {
                tontinesActives = (int) tontineDAO.findAll().stream()
                    .filter(t -> "active".equalsIgnoreCase(t.getStatut()))
                    .count();
            } catch (Exception e) {
                System.err.println("Erreur chargement tontines: " + e.getMessage());
                tontinesActives = 0;
            }
            
            if (lblTontinesActives != null) {
                lblTontinesActives.setText(String.valueOf(tontinesActives));
            }
            
            // Statistiques des crédits
            int creditsEnCours = 0;
            try {
                creditsEnCours = (int) creditDAO.findAll().stream()
                    .filter(c -> "en_cours".equalsIgnoreCase(c.getStatut()))
                    .count();
            } catch (Exception e) {
                System.err.println("Erreur chargement crédits: " + e.getMessage());
                creditsEnCours = 0;
            }
            
            if (lblCreditsEnCours != null) {
                lblCreditsEnCours.setText(String.valueOf(creditsEnCours));
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des statistiques: " + e.getMessage());
        }
    }
    
    /**
     * Rafraîchit le panneau
     */
    public void rafraichir() {
        chargerStatistiques();
    }
    
    /**
     * Classe pour créer des bordures arrondies personnalisées
     */
    private static class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color borderColor;
        private int thickness;
        
        public RoundedBorder(int radius, Color borderColor, int thickness) {
            this.radius = radius;
            this.borderColor = borderColor;
            this.thickness = thickness;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (thickness > 0) {
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(thickness));
                g2d.draw(new RoundRectangle2D.Double(
                    x + thickness/2.0, 
                    y + thickness/2.0, 
                    width - thickness, 
                    height - thickness, 
                    radius, 
                    radius
                ));
            }
            
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = thickness;
            return insets;
        }
    }
}