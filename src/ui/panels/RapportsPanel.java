package ui.panels;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.CreditDAO;
import dao.MembreDAO;
import dao.SeanceDAO;
import dao.TontineDAO;
import models.Credit;
import models.Membre;
import models.Seance;
import models.Tontine;
import ui.MainFrame;

/**
 * Panneau de rapports avec design ultra-moderne
 * Design 3.0 - Interface Premium cohérente avec visualisations
 */
public class RapportsPanel extends JPanel {
    private MainFrame mainFrame;
    private MembreDAO membreDAO;
    private TontineDAO tontineDAO;
    private CreditDAO creditDAO;
    private SeanceDAO seanceDAO;
    
    private JTable tableRapports;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbTypeRapport;
    private com.toedter.calendar.JDateChooser dateDebut, dateFin;
    private JLabel lblTitre;
    private JTextArea txtResultat;
    
    // Labels pour statistiques visuelles
    private JLabel lblStatMembres;
    private JLabel lblStatTontines;
    private JLabel lblStatCredits;
    
    // Palette de couleurs cohérente
    private static final Color PRIMARY_DARK = new Color(15, 23, 42);
    private static final Color PRIMARY_PURPLE = new Color(139, 92, 246);
    private static final Color PRIMARY_BLUE = new Color(59, 130, 246);
    private static final Color PRIMARY_EMERALD = new Color(16, 185, 129);
    private static final Color PRIMARY_RED = new Color(239, 68, 68);
    private static final Color PRIMARY_AMBER = new Color(251, 146, 60);
    private static final Color BACKGROUND = new Color(241, 245, 249);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    
    public RapportsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.membreDAO = new MembreDAO();
        this.tontineDAO = new TontineDAO();
        this.creditDAO = new CreditDAO();
        this.seanceDAO = new SeanceDAO();
        initComponents();
    }
    
    /**
     * Initialise tous les composants avec design ultra-moderne
     */
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND);
        
        // Container principal
        JPanel mainContainer = new JPanel(new BorderLayout(0, 24));
        mainContainer.setBackground(BACKGROUND);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        
        // En-tête moderne
        JPanel headerPanel = createModernHeader();
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // Corps avec statistiques et contenu
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 24));
        bodyPanel.setBackground(BACKGROUND);
        
        // Statistiques rapides
        JPanel statsPanel = createStatsPanel();
        bodyPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Contenu principal
        JPanel contentPanel = createContentPanel();
        bodyPanel.add(contentPanel, BorderLayout.CENTER);
        
        mainContainer.add(bodyPanel, BorderLayout.CENTER);
        
        // Panneau actions
        JPanel actionPanel = createActionPanel();
        mainContainer.add(actionPanel, BorderLayout.SOUTH);
        
        add(mainContainer, BorderLayout.CENTER);
    }
    
    /**
     * Crée l'en-tête moderne avec filtres
     */
    private JPanel createModernHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(new RoundedBorder(20, BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        
        // Section gauche - Titre
        JPanel leftSection = new JPanel();
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));
        leftSection.setBackground(CARD_BG);
        
        JLabel iconLabel = new JLabel("📊");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblTitre = new JLabel("Rapports & Statistiques");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitre.setForeground(TEXT_PRIMARY);
        lblTitre.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Analyses et exportations de données");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftSection.add(iconLabel);
        leftSection.add(Box.createVerticalStrut(8));
        leftSection.add(lblTitre);
        leftSection.add(Box.createVerticalStrut(4));
        leftSection.add(subtitleLabel);
        
        // Section droite - Filtres
        JPanel rightSection = createFiltersPanel();
        
        innerPanel.add(leftSection, BorderLayout.WEST);
        innerPanel.add(rightSection, BorderLayout.EAST);
        
        header.add(innerPanel);
        return header;
    }
    
    /**
     * Crée le panneau de filtres
     */
    private JPanel createFiltersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        
        // Type de rapport
        JLabel typeLabel = new JLabel("Type de rapport");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeLabel.setForeground(TEXT_SECONDARY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        cmbTypeRapport = new JComboBox<>(new String[]{
            "Résumé Général",
            "Membres Actifs",
            "Tontines Actives",
            "Crédits en Cours",
            "Séances du Mois"
        });
        cmbTypeRapport.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTypeRapport.setBackground(CARD_BG);
        cmbTypeRapport.setPreferredSize(new Dimension(250, 40));
        cmbTypeRapport.setMaximumSize(new Dimension(250, 40));
        cmbTypeRapport.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cmbTypeRapport.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Dates
        JPanel datesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        datesPanel.setBackground(CARD_BG);
        datesPanel.setMaximumSize(new Dimension(250, 50));
        
        dateDebut = createStyledDateChooser();
        dateFin = createStyledDateChooser();
        
        JLabel fromLabel = new JLabel("Du");
        fromLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        fromLabel.setForeground(TEXT_SECONDARY);
        
        JLabel toLabel = new JLabel("Au");
        toLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        toLabel.setForeground(TEXT_SECONDARY);
        
        datesPanel.add(fromLabel);
        datesPanel.add(dateDebut);
        datesPanel.add(toLabel);
        datesPanel.add(dateFin);
        
        panel.add(typeLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(cmbTypeRapport);
        panel.add(Box.createVerticalStrut(12));
        panel.add(datesPanel);
        
        return panel;
    }
    
    /**
     * Crée un DateChooser stylisé
     */
    private com.toedter.calendar.JDateChooser createStyledDateChooser() {
        com.toedter.calendar.JDateChooser chooser = new com.toedter.calendar.JDateChooser();
        chooser.setDateFormatString("dd/MM/yyyy");
        chooser.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chooser.setPreferredSize(new Dimension(100, 32));
        chooser.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return chooser;
    }
    
    /**
     * Crée le panneau de statistiques visuelles
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(BACKGROUND);
        
        // Carte Membres
        JPanel cardMembres = createVisualStatCard("Membres", "0/0", "👥", PRIMARY_PURPLE, PRIMARY_BLUE);
        lblStatMembres = (JLabel) ((JPanel)((JPanel)((JPanel)cardMembres.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Tontines
        JPanel cardTontines = createVisualStatCard("Tontines", "0/0", "💰", PRIMARY_BLUE, PRIMARY_EMERALD);
        lblStatTontines = (JLabel) ((JPanel)((JPanel)((JPanel)cardTontines.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Crédits
        JPanel cardCredits = createVisualStatCard("Crédits", "0/0", "💳", PRIMARY_EMERALD, PRIMARY_AMBER);
        lblStatCredits = (JLabel) ((JPanel)((JPanel)((JPanel)cardCredits.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        panel.add(cardMembres);
        panel.add(cardTontines);
        panel.add(cardCredits);
        
        return panel;
    }
    
    /**
     * Crée une carte de statistique visuelle avec gradient
     */
    private JPanel createVisualStatCard(String titre, String valeur, String icone, Color color1, Color color2) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient de fond
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), 10),
                    getWidth(), getHeight(), new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), 10)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        card.setOpaque(false);
        card.setBorder(new RoundedBorder(12, BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setOpaque(false);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icone);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.setOpaque(false);
        
        JLabel valueLabel = new JLabel(valeur);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(titre);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        valuePanel.add(valueLabel);
        valuePanel.add(Box.createVerticalStrut(4));
        valuePanel.add(titleLabel);
        
        contentPanel.add(iconLabel, BorderLayout.WEST);
        contentPanel.add(Box.createHorizontalStrut(16), BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(valuePanel, BorderLayout.CENTER);
        
        innerPanel.add(contentPanel, BorderLayout.WEST);
        innerPanel.add(rightPanel, BorderLayout.CENTER);
        
        card.add(innerPanel);
        return card;
    }
    
    /**
     * Crée le panneau de contenu principal
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(BACKGROUND);
        
        // Panneau résultat texte
        JPanel resultPanel = createResultPanel();
        panel.add(resultPanel);
        
        // Panneau tableau
        JPanel tablePanel = createTablePanel();
        panel.add(tablePanel);
        
        return panel;
    }
    
    /**
     * Crée le panneau de résultat
     */
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new RoundedBorder(16, BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("📄 Résumé du Rapport");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        
        txtResultat = new JTextArea();
        txtResultat.setEditable(false);
        txtResultat.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtResultat.setBackground(new Color(248, 250, 252));
        txtResultat.setForeground(TEXT_PRIMARY);
        txtResultat.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        txtResultat.setLineWrap(true);
        txtResultat.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(txtResultat);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        innerPanel.add(titleLabel, BorderLayout.NORTH);
        innerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(innerPanel);
        return panel;
    }
    
    /**
     * Crée le panneau du tableau
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new RoundedBorder(16, BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("📋 Détails");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        
        createModernTable();
        JScrollPane scrollPane = new JScrollPane(tableRapports);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_BG);
        
        innerPanel.add(titleLabel, BorderLayout.NORTH);
        innerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(innerPanel);
        return panel;
    }
    
    /**
     * Crée le tableau moderne
     */
    private void createModernTable() {
        String[] columnNames = {"ID", "Nom", "Type", "Montant", "Date", "Statut"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableRapports = new JTable(tableModel);
        tableRapports.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableRapports.setRowHeight(40);
        tableRapports.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableRapports.setShowGrid(false);
        tableRapports.setIntercellSpacing(new Dimension(0, 0));
        tableRapports.setBackground(CARD_BG);
        tableRapports.setSelectionBackground(new Color(237, 233, 254));
        tableRapports.setSelectionForeground(TEXT_PRIMARY);
        
        // Style du header
        JTableHeader header = tableRapports.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        
        // Renderer personnalisé
        tableRapports.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : new Color(248, 250, 252));
                }
                
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                
                // Coloration du statut
                if (column == 5 && value != null && !isSelected) {
                    String statut = value.toString().toLowerCase();
                    if (statut.contains("actif") || statut.contains("terminée")) {
                        setForeground(PRIMARY_EMERALD);
                    } else if (statut.contains("cours") || statut.contains("planifiée")) {
                        setForeground(PRIMARY_BLUE);
                    } else {
                        setForeground(TEXT_PRIMARY);
                    }
                } else if (!isSelected) {
                    setForeground(TEXT_PRIMARY);
                }
                
                return c;
            }
        });
    }
    
    /**
     * Crée le panneau d'actions
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setBackground(BACKGROUND);
        
        JButton btnGenerer = createModernButton("📊 Générer", PRIMARY_PURPLE, new Color(237, 233, 254));
        JButton btnExporter = createModernButton("💾 Exporter", PRIMARY_BLUE, new Color(219, 234, 254));
        JButton btnImprimer = createModernButton("🖨️ Imprimer", PRIMARY_AMBER, new Color(254, 243, 199));
        JButton btnRafraichir = createModernButton("🔄 Rafraîchir", TEXT_SECONDARY, new Color(241, 245, 249));
        
        btnGenerer.addActionListener(e -> genererRapport());
        btnExporter.addActionListener(e -> exporterRapport());
        btnImprimer.addActionListener(e -> imprimerRapport());
        btnRafraichir.addActionListener(e -> rafraichir());
        
        panel.add(btnGenerer);
        panel.add(btnExporter);
        panel.add(btnImprimer);
        panel.add(btnRafraichir);
        
        return panel;
    }
    
    /**
     * Crée un bouton moderne
     */
    private JButton createModernButton(String text, Color textColor, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 44));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(adjustBrightness(bgColor, 0.95f));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * Ajuste la luminosité
     */
    private Color adjustBrightness(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * factor));
        int g = Math.min(255, (int)(color.getGreen() * factor));
        int b = Math.min(255, (int)(color.getBlue() * factor));
        return new Color(r, g, b);
    }
    
    /**
     * Met à jour les statistiques visuelles
     */
    private void updateVisualStats() {
        try {
            // Membres
            List<Membre> membres = membreDAO.findAll();
            int totalMembres = membres.size();
            int actifsMembres = (int) membres.stream().filter(m -> "actif".equals(m.getStatut())).count();
            if (lblStatMembres != null) {
                lblStatMembres.setText(actifsMembres + "/" + totalMembres);
            }
            
            // Tontines
            List<Tontine> tontines = tontineDAO.findAll();
            int totalTontines = tontines.size();
            int activesTontines = (int) tontines.stream().filter(t -> "active".equals(t.getStatut())).count();
            if (lblStatTontines != null) {
                lblStatTontines.setText(activesTontines + "/" + totalTontines);
            }
            
            // Crédits
            List<Credit> credits = creditDAO.findAll();
            int totalCredits = credits.size();
            int coursCredits = (int) credits.stream().filter(c -> "en_cours".equals(c.getStatut())).count();
            if (lblStatCredits != null) {
                lblStatCredits.setText(coursCredits + "/" + totalCredits);
            }
        } catch (Exception e) {
            System.err.println("Erreur mise à jour stats: " + e.getMessage());
        }
    }
    
    /**
     * Génère le rapport
     */
    private void genererRapport() {
        String typeRapport = (String) cmbTypeRapport.getSelectedItem();
        LocalDate dateDebutValue = convertToLocalDate(dateDebut.getDate());
        LocalDate dateFinValue = convertToLocalDate(dateFin.getDate());
        
        tableModel.setRowCount(0);
        txtResultat.setText("");
        
        try {
            switch (typeRapport) {
                case "Résumé Général":
                    genererRapportResumeGeneral();
                    break;
                case "Membres Actifs":
                    genererRapportMembresActifs();
                    break;
                case "Tontines Actives":
                    genererRapportTontinesActives();
                    break;
                case "Crédits en Cours":
                    genererRapportCreditsEnCours();
                    break;
                case "Séances du Mois":
                    genererRapportSeancesMois(dateDebutValue, dateFinValue);
                    break;
            }
            updateVisualStats();
        } catch (Exception e) {
            showErrorMessage("Erreur lors de la génération: " + e.getMessage());
        }
    }
    
    /**
     * Génère le rapport des membres actifs
     */
    private void genererRapportMembresActifs() {
        try {
            List<Membre> membres = membreDAO.findAll();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            StringBuilder resultat = new StringBuilder();
            resultat.append("╔════════════════════════════════════════╗\n");
            resultat.append("║   RAPPORT DES MEMBRES ACTIFS           ║\n");
            resultat.append("╚════════════════════════════════════════╝\n\n");
            resultat.append("📅 Généré le: ").append(LocalDate.now().format(formatter)).append("\n\n");
            
            int total = 0;
            int actifs = 0;
            
            for (Membre m : membres) {
                total++;
                if (m.getStatut() != null && m.getStatut().equals("actif")) {
                    actifs++;
                    
                    Object[] row = {
                        m.getIdMembre(),
                        m.getNomComplet(),
                        "Membre",
                        "N/A",
                        m.getDateInscription() != null ? m.getDateInscription().format(formatter) : "N/A",
                        m.getStatut()
                    };
                    tableModel.addRow(row);
                }
            }
            
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("📊 STATISTIQUES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            resultat.append("  • Total membres: ").append(total).append("\n");
            resultat.append("  • Membres actifs: ").append(actifs).append("\n");
            resultat.append("  • Taux d'activité: ").append(total > 0 ? String.format("%.1f%%", (actifs * 100.0 / total)) : "0%").append("\n");
            
            txtResultat.setText(resultat.toString());
            
        } catch (Exception e) {
            showErrorMessage("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Génère le rapport des tontines actives
     */
    private void genererRapportTontinesActives() {
        try {
            List<Tontine> tontines = tontineDAO.findAll();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            StringBuilder resultat = new StringBuilder();
            resultat.append("╔════════════════════════════════════════╗\n");
            resultat.append("║   RAPPORT DES TONTINES ACTIVES         ║\n");
            resultat.append("╚════════════════════════════════════════╝\n\n");
            resultat.append("📅 Généré le: ").append(LocalDate.now().format(formatter)).append("\n\n");
            
            int total = 0;
            int actives = 0;
            
            for (Tontine t : tontines) {
                total++;
                if (t.getStatut() != null && t.getStatut().equals("active")) {
                    actives++;
                    
                    Object[] row = {
                        t.getIdTontine(),
                        t.getNom(),
                        "Tontine",
                        t.getNombreTours() + " tours",
                        t.getDateDebut() != null ? t.getDateDebut().format(formatter) : "N/A",
                        t.getStatut()
                    };
                    tableModel.addRow(row);
                }
            }
            
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("📊 STATISTIQUES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            resultat.append("  • Total tontines: ").append(total).append("\n");
            resultat.append("  • Tontines actives: ").append(actives).append("\n");
            resultat.append("  • Taux d'activité: ").append(total > 0 ? String.format("%.1f%%", (actives * 100.0 / total)) : "0%").append("\n");
            
            txtResultat.setText(resultat.toString());
            
        } catch (Exception e) {
            showErrorMessage("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Génère le rapport des crédits en cours
     */
    private void genererRapportCreditsEnCours() {
        try {
            List<Credit> credits = creditDAO.findAll();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            StringBuilder resultat = new StringBuilder();
            resultat.append("╔════════════════════════════════════════╗\n");
            resultat.append("║   RAPPORT DES CRÉDITS EN COURS         ║\n");
            resultat.append("╚════════════════════════════════════════╝\n\n");
            resultat.append("📅 Généré le: ").append(LocalDate.now().format(formatter)).append("\n\n");
            
            int total = 0;
            int enCours = 0;
            double montantTotal = 0;
            
            for (Credit c : credits) {
                total++;
                if (c.getStatut() != null && c.getStatut().equals("en_cours")) {
                    enCours++;
                    montantTotal += c.getMontantEmprunte().doubleValue();
                    
                    Object[] row = {
                        c.getIdCredit(),
                        c.getMembre() != null ? c.getMembre().getNomComplet() : "N/A",
                        "Crédit",
                        String.format("%.0f FCFA", c.getMontantEmprunte()),
                        c.getDateDebut() != null ? c.getDateDebut().format(formatter) : "N/A",
                        c.getStatut()
                    };
                    tableModel.addRow(row);
                }
            }
            
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("📊 STATISTIQUES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            resultat.append("  • Total crédits: ").append(total).append("\n");
            resultat.append("  • Crédits en cours: ").append(enCours).append("\n");
            resultat.append("  • Montant total: ").append(String.format("%.0f FCFA", montantTotal)).append("\n");
            
            txtResultat.setText(resultat.toString());
            
        } catch (Exception e) {
            showErrorMessage("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Génère le rapport des séances
     */
    private void genererRapportSeancesMois(LocalDate dateDebut, LocalDate dateFin) {
        try {
            List<Seance> seances = seanceDAO.findAll();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            StringBuilder resultat = new StringBuilder();
            resultat.append("╔════════════════════════════════════════╗\n");
            resultat.append("║   RAPPORT DES SÉANCES                  ║\n");
            resultat.append("╚════════════════════════════════════════╝\n\n");
            resultat.append("📅 Période: ");
            if (dateDebut != null && dateFin != null) {
                resultat.append(dateDebut.format(formatter)).append(" → ").append(dateFin.format(formatter));
            } else {
                resultat.append("Toutes les séances");
            }
            resultat.append("\n\n");
            
            int total = 0;
            int planifiees = 0;
            int terminees = 0;
            
            for (Seance s : seances) {
                if (dateDebut != null && s.getDateSeance().isBefore(dateDebut)) continue;
                if (dateFin != null && s.getDateSeance().isAfter(dateFin)) continue;
                
                total++;
                
                if (s.getStatut() != null) {
                    switch (s.getStatut()) {
                        case "planifiée": planifiees++; break;
                        case "terminée": terminees++; break;
                    }
                }
                
                Object[] row = {
                    s.getIdSeance(),
                    "Tour " + s.getNumeroTour(),
                    "Séance",
                    "N/A",
                    s.getDateSeance() != null ? s.getDateSeance().format(formatter) : "N/A",
                    s.getStatut()
                };
                tableModel.addRow(row);
            }
            
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("📊 STATISTIQUES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            resultat.append("  • Total séances: ").append(total).append("\n");
            resultat.append("  • Planifiées: ").append(planifiees).append("\n");
            resultat.append("  • Terminées: ").append(terminees).append("\n");
            
            txtResultat.setText(resultat.toString());
            
        } catch (Exception e) {
            showErrorMessage("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Génère le résumé général
     */
    private void genererRapportResumeGeneral() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            StringBuilder resultat = new StringBuilder();
            resultat.append("╔════════════════════════════════════════╗\n");
            resultat.append("║   RÉSUMÉ GÉNÉRAL                       ║\n");
            resultat.append("╚════════════════════════════════════════╝\n\n");
            resultat.append("📅 Généré le: ").append(LocalDate.now().format(formatter)).append("\n\n");
            
            // Membres
            List<Membre> membres = membreDAO.findAll();
            int membresTotal = membres.size();
            int membresActifs = (int) membres.stream().filter(m -> "actif".equals(m.getStatut())).count();
            
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("👥 MEMBRES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("  • Total: ").append(membresTotal).append("\n");
            resultat.append("  • Actifs: ").append(membresActifs).append("\n");
            resultat.append("  • Taux: ").append(membresTotal > 0 ? String.format("%.1f%%", (membresActifs * 100.0 / membresTotal)) : "0%").append("\n\n");
            
            // Tontines
            List<Tontine> tontines = tontineDAO.findAll();
            int tontinesTotal = tontines.size();
            int tontinesActives = (int) tontines.stream().filter(t -> "active".equals(t.getStatut())).count();
            
            resultat.append("💰 TONTINES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("  • Total: ").append(tontinesTotal).append("\n");
            resultat.append("  • Actives: ").append(tontinesActives).append("\n\n");
            
            // Crédits
            List<Credit> credits = creditDAO.findAll();
            int creditsTotal = credits.size();
            int creditsEnCours = (int) credits.stream().filter(c -> "en_cours".equals(c.getStatut())).count();
            double montantTotal = credits.stream().mapToDouble(c -> c.getMontantEmprunte().doubleValue()).sum();
            
            resultat.append("💳 CRÉDITS\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("  • Total: ").append(creditsTotal).append("\n");
            resultat.append("  • En cours: ").append(creditsEnCours).append("\n");
            resultat.append("  • Montant: ").append(String.format("%.0f FCFA", montantTotal)).append("\n");
            
            txtResultat.setText(resultat.toString());
            
        } catch (Exception e) {
            showErrorMessage("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Exporte le rapport
     */
    private void exporterRapport() {
        if (txtResultat.getText().isEmpty()) {
            showWarningMessage("Veuillez d'abord générer un rapport");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter le rapport");
        fileChooser.setSelectedFile(new File("rapport_" + LocalDate.now() + ".txt"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(txtResultat.getText());
                showSuccessMessage("Rapport exporté avec succès vers:\n" + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                showErrorMessage("Erreur lors de l'export: " + e.getMessage());
            }
        }
    }
    
    /**
     * Imprime le rapport
     */
    private void imprimerRapport() {
        if (txtResultat.getText().isEmpty()) {
            showWarningMessage("Veuillez d'abord générer un rapport");
            return;
        }
        
        try {
            boolean complete = txtResultat.print();
            if (complete) {
                showSuccessMessage("Impression envoyée à l'imprimante");
            } else {
                showWarningMessage("Impression annulée");
            }
        } catch (Exception e) {
            showErrorMessage("Erreur lors de l'impression: " + e.getMessage());
        }
    }
    
    /**
     * Rafraîchit le panneau
     */
    public void rafraichir() {
        tableModel.setRowCount(0);
        txtResultat.setText("");
        updateVisualStats();
    }
    
    /**
     * Messages
     */
    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Attention", JOptionPane.WARNING_MESSAGE);
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private LocalDate convertToLocalDate(java.util.Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }
    
    /**
     * Classe pour bordures arrondies
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
    }
}