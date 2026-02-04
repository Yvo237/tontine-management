package ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dao.CreditDAO;
import dao.MembreDAO;
import dao.TontineDAO;
import models.Credit;
import models.Membre;
import models.Tontine;
import ui.MainFrame;
import utils.ThemeColors;
import utils.UIUtils;

public class AccueilPanel extends JPanel {
    private MainFrame mainFrame;
    private MembreDAO membreDAO;
    private TontineDAO tontineDAO;
    private CreditDAO creditDAO;
    
    private JLabel lblNombreMembres;
    private JLabel lblMembresActifs;
    private JLabel lblTontinesActives;
    private JLabel lblCreditsEnCours;
    
    
    public AccueilPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.membreDAO = new MembreDAO();
        this.tontineDAO = new TontineDAO();
        this.creditDAO = new CreditDAO();
        initComponents();
        chargerStatistiques();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(ThemeColors.BACKGROUND);
        
        // Container principal avec padding
        JPanel mainContainer = new JPanel(new BorderLayout(0, 24));
        mainContainer.setBackground(ThemeColors.BACKGROUND);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        
        // En-tête sophistiqué avec gradient visuel
        JPanel headerPanel = createUltraModernHeader();
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // Corps principal avec grille de cartes
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 24));
        bodyPanel.setBackground(ThemeColors.BACKGROUND);
        
        // Grille des statistiques premium
        JPanel statsGrid = createPremiumStatsGrid();
        bodyPanel.add(statsGrid, BorderLayout.CENTER);
        
        // Section d'actions rapides
        JPanel quickActionsPanel = createQuickActionsPanel();
        bodyPanel.add(quickActionsPanel, BorderLayout.SOUTH);
        
        mainContainer.add(bodyPanel, BorderLayout.CENTER);
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private JPanel createUltraModernHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeColors.CARD_BG);
        header.setBorder(new UIUtils.RoundedBorder(20, ThemeColors.BORDER_COLOR, 1));
        
        // Panneau interne avec padding
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        
        // Section gauche - Titre et sous-titre
        JPanel leftSection = new JPanel();
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));
        leftSection.setBackground(ThemeColors.CARD_BG);
        
        JLabel welcomeLabel = new JLabel("Bonjour, 👋");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Tableau de Bord");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Gestion Financière & Tontines");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(ThemeColors.PRIMARY_PURPLE);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftSection.add(welcomeLabel);
        leftSection.add(Box.createVerticalStrut(4));
        leftSection.add(titleLabel);
        leftSection.add(Box.createVerticalStrut(4));
        leftSection.add(subtitleLabel);
        
        // Section droite - Date et heure
        JPanel rightSection = new JPanel();
        rightSection.setLayout(new BoxLayout(rightSection, BoxLayout.Y_AXIS));
        rightSection.setBackground(ThemeColors.CARD_BG);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        
        JLabel dateLabel = new JLabel(dateFormat.format(new Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        dateLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        JLabel timeLabel = new JLabel(timeFormat.format(new Date()));
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        timeLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        timeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        rightSection.add(dateLabel);
        rightSection.add(Box.createVerticalStrut(4));
        rightSection.add(timeLabel);
        
        innerPanel.add(leftSection, BorderLayout.WEST);
        innerPanel.add(rightSection, BorderLayout.EAST);
        
        header.add(innerPanel);
        return header;
    }
    

        private JPanel createPremiumStatsGrid() {
    JPanel grid = new JPanel(new GridLayout(2, 2, 24, 24));
    grid.setBackground(ThemeColors.BACKGROUND);
    
    // Cartes avec couleurs distinctives
    JPanel cardMembres = createPremiumStatCard(
        "Total Membres", "0", "👥", 
        ThemeColors.PRIMARY_PURPLE, new Color(250, 245, 255)
    );
    
    JPanel cardActifs = createPremiumStatCard(
        "Membres Actifs", "0", "⚡", 
        ThemeColors.PRIMARY_BLUE, new Color(235, 248, 255)
    );
    
    JPanel cardTontines = createPremiumStatCard(
        "Tontines Actives", "0", "💎", 
        ThemeColors.PRIMARY_EMERALD, new Color(240, 255, 250)
    );
    
    JPanel cardCredits = createPremiumStatCard(
        "Crédits en Cours", "0", "💰", 
        ThemeColors.PRIMARY_AMBER, new Color(255, 252, 235)
    );
    
    grid.add(cardMembres);
    grid.add(cardActifs);
    grid.add(cardTontines);
    grid.add(cardCredits);
    
    return grid;
}


    
    private JPanel createPremiumStatCard(String titre, String valeur, String icone, 
                                         Color accentColor, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ThemeColors.CARD_BG);
        card.setBorder(new UIUtils.RoundedBorder(16, new Color(226, 232, 240), 1));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Container interne
        JPanel innerContainer = new JPanel(new BorderLayout());
        innerContainer.setBackground(ThemeColors.CARD_BG);
        innerContainer.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        
        // En-tête de la carte
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeColors.CARD_BG);
        
        // Badge icône avec fond coloré
        JPanel iconBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconBadge.setPreferredSize(new Dimension(56, 56));
        iconBadge.setBackground(bgColor);
        iconBadge.setBorder(new UIUtils.RoundedBorder(12, bgColor, 0));
        
        JLabel iconLabel = new JLabel(icone);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconBadge.add(iconLabel);
        
        // Panneau titre
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(ThemeColors.CARD_BG);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        
        JLabel titleLabel = new JLabel(titre);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(Box.createVerticalGlue());
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalGlue());
        
        headerPanel.add(iconBadge, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Valeur principale
        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setBackground(ThemeColors.CARD_BG);
        valuePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JLabel valueLabel = new JLabel(valeur);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        valueLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        
        // Footer avec trend indicator
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footerPanel.setBackground(ThemeColors.CARD_BG);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        
        JLabel trendLabel = new JLabel("↗ +12% ce mois");
        trendLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        trendLabel.setForeground(ThemeColors.PRIMARY_EMERALD);
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
            private Color originalBg = ThemeColors.CARD_BG;
            
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(249, 250, 251));
                innerContainer.setBackground(new Color(249, 250, 251));
                headerPanel.setBackground(new Color(249, 250, 251));
                titlePanel.setBackground(new Color(249, 250, 251));
                valuePanel.setBackground(new Color(249, 250, 251));
                footerPanel.setBackground(new Color(249, 250, 251));
                card.setBorder(new UIUtils.RoundedBorder(16, accentColor, 2));
            }
            
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(originalBg);
                innerContainer.setBackground(originalBg);
                headerPanel.setBackground(originalBg);
                titlePanel.setBackground(originalBg);
                valuePanel.setBackground(originalBg);
                footerPanel.setBackground(originalBg);
                card.setBorder(new UIUtils.RoundedBorder(16, new Color(226, 232, 240), 1));
            }
        });
        
        return card;
    }
    
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.CARD_BG);
        panel.setBorder(new UIUtils.RoundedBorder(16, new Color(226, 232, 240), 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        
        // Titre de la section
        JLabel titleLabel = new JLabel("🚀 Actions Rapides");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        
        // Grille de boutons
        JPanel buttonsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        buttonsGrid.setBackground(ThemeColors.CARD_BG);
        
        buttonsGrid.add(createActionButton("➕ Nouveau Membre", ThemeColors.PRIMARY_PURPLE));
        buttonsGrid.add(createActionButton("💰 Créer Tontine", ThemeColors.PRIMARY_BLUE));
        buttonsGrid.add(createActionButton("📅 Planifier Séance", ThemeColors.PRIMARY_EMERALD));
        buttonsGrid.add(createActionButton("📊 Voir Rapports", ThemeColors.PRIMARY_AMBER));
        
        innerPanel.add(titleLabel, BorderLayout.NORTH);
        innerPanel.add(buttonsGrid, BorderLayout.CENTER);
        
        panel.add(innerPanel);
        return panel;
    }
    
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
    
    private void chargerStatistiques() {
        try {
            System.out.println("Chargement statistiques AccueilPanel...");
            
            // Récupération unique des données pour éviter les appels multiples
            List<Membre> membres = null;
            List<Tontine> tontines = null;
            List<Credit> credits = null;
            
            try {
                membres = membreDAO.findAll();
                System.out.println("Membres chargés: " + membres.size());
            } catch (Exception e) {
                System.err.println("Erreur chargement membres: " + e.getMessage());
                membres = new ArrayList<>();
            }
            
            try {
                tontines = tontineDAO.findAll();
                System.out.println("Tontines chargées: " + tontines.size());
            } catch (Exception e) {
                System.err.println("Erreur chargement tontines: " + e.getMessage());
                tontines = new ArrayList<>();
            }
            
            try {
                credits = creditDAO.findAll();
                System.out.println("Crédits chargés: " + credits.size());
            } catch (Exception e) {
                System.err.println("Erreur chargement crédits: " + e.getMessage());
                credits = new ArrayList<>();
            }
            
            // Statistiques des membres (calculées sur la liste déjà récupérée)
            int nombreMembres = membres.size();
            int membresActifs = (int) membres.stream()
                .filter(m -> "actif".equalsIgnoreCase(m.getStatut()))
                .count();
            
            if (lblNombreMembres != null) {
                lblNombreMembres.setText(String.valueOf(nombreMembres));
            }
            
            if (lblMembresActifs != null) {
                lblMembresActifs.setText(String.valueOf(membresActifs));
            }
            
            // Statistiques des tontines (calculées sur la liste déjà récupérée)
            int tontinesActives = (int) tontines.stream()
                .filter(t -> "active".equalsIgnoreCase(t.getStatut()))
                .count();
            
            if (lblTontinesActives != null) {
                lblTontinesActives.setText(String.valueOf(tontinesActives));
            }
            
            // Statistiques des crédits (calculées sur la liste déjà récupérée)
            int creditsEnCours = (int) credits.stream()
                .filter(c -> "en_cours".equalsIgnoreCase(c.getStatut()))
                .count();
            
            if (lblCreditsEnCours != null) {
                lblCreditsEnCours.setText(String.valueOf(creditsEnCours));
            }
            
            System.out.println("Statistiques AccueilPanel mises à jour avec succès");
            
        } catch (Exception e) {
            System.err.println("Erreur générale chargement statistiques: " + e.getMessage());
        }
    }
    
    public void rafraichir() {
        chargerStatistiques();
    }
    
}