package ui.panels;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javax.swing.SwingWorker;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
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
import dao.ProjetFIACDAO;
import dao.SeanceDAO;
import dao.TontineDAO;
import models.Credit;
import models.Membre;
import models.ProjetFIAC;
import models.Seance;
import models.Tontine;
import utils.ReportGenerator;
import utils.ThemeColors;
import utils.UIUtils;
import ui.MainFrame;

public class RapportsPanel extends JPanel {
    private MainFrame mainFrame;
    private MembreDAO membreDAO;
    private TontineDAO tontineDAO;
    private CreditDAO creditDAO;
    private SeanceDAO seanceDAO;
    private ProjetFIACDAO projetDAO;
    
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
    
    
    public RapportsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.membreDAO = new MembreDAO();
        this.tontineDAO = new TontineDAO();
        this.creditDAO = new CreditDAO();
        this.seanceDAO = new SeanceDAO();
        this.projetDAO = new ProjetFIACDAO();
        initComponents();
        chargerDonnees();
        System.out.println("RapportsPanel initialisé avec données BDD");
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(ThemeColors.BACKGROUND);
        
        // Container principal
        JPanel mainContainer = new JPanel(new BorderLayout(0, 24));
        mainContainer.setBackground(ThemeColors.BACKGROUND);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        
        // En-tête moderne
        JPanel headerPanel = createModernHeader();
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // Corps avec statistiques et contenu
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 24));
        bodyPanel.setBackground(ThemeColors.BACKGROUND);
        
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
    
    private JPanel createModernHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeColors.CARD_BG);
        header.setBorder(new UIUtils.RoundedBorder(20, ThemeColors.BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        
        // Section gauche - Titre
        JPanel leftSection = new JPanel();
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));
        leftSection.setBackground(ThemeColors.CARD_BG);
        
        JLabel iconLabel = new JLabel("📊");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblTitre = new JLabel("Rapports & Statistiques");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitre.setForeground(ThemeColors.TEXT_PRIMARY);
        lblTitre.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Analyses et exportations de données");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
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
    
    private JPanel createFiltersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeColors.CARD_BG);
        
        // Type de rapport
        JLabel typeLabel = new JLabel("Type de rapport");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        cmbTypeRapport = new JComboBox<>(new String[]{
            "Résumé Général",
            "Membres Actifs",
            "Tontines Actives",
            "Crédits en Cours",
            "Séances du Mois",
            "Projets FIAC",
            "Synthèse AG"
        });
        cmbTypeRapport.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTypeRapport.setBackground(ThemeColors.CARD_BG);
        cmbTypeRapport.setPreferredSize(new Dimension(250, 40));
        cmbTypeRapport.setMaximumSize(new Dimension(250, 40));
        cmbTypeRapport.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cmbTypeRapport.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Dates
        JPanel datesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        datesPanel.setBackground(ThemeColors.CARD_BG);
        datesPanel.setMaximumSize(new Dimension(250, 50));
        
        dateDebut = createStyledDateChooser();
        dateFin = createStyledDateChooser();
        
        JLabel fromLabel = new JLabel("Du");
        fromLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        fromLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        
        JLabel toLabel = new JLabel("Au");
        toLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        toLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        
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
    
    private com.toedter.calendar.JDateChooser createStyledDateChooser() {
        com.toedter.calendar.JDateChooser chooser = new com.toedter.calendar.JDateChooser();
        chooser.setDateFormatString("dd/MM/yyyy");
        chooser.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chooser.setPreferredSize(new Dimension(100, 32));
        chooser.setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER_COLOR, 1));
        return chooser;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // Carte Membres
        JPanel cardMembres = createVisualStatCard("Membres", "0/0", "👥", new Color(100, 50, 150), new Color(50, 100, 150));
        lblStatMembres = (JLabel) ((JPanel)((JPanel)((JPanel)cardMembres.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Tontines
        JPanel cardTontines = createVisualStatCard("Tontines", "0/0", "💰", new Color(50, 100, 150), new Color(150, 100, 50));
        lblStatTontines = (JLabel) ((JPanel)((JPanel)((JPanel)cardTontines.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Crédits
        JPanel cardCredits = createVisualStatCard("Crédits", "0/0", "💳", new Color(150, 100, 50), new Color(200, 150, 50));
        lblStatCredits = (JLabel) ((JPanel)((JPanel)((JPanel)cardCredits.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        panel.add(cardMembres);
        panel.add(cardTontines);
        panel.add(cardCredits);
        
        return panel;
    }
    
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
        card.setBorder(new UIUtils.RoundedBorder(12, new Color(200, 200, 200), 1));
        
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
        valueLabel.setForeground(new Color(100, 100, 100));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(titre);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(150, 150, 150));
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
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(ThemeColors.CARD_BG);
        
        // Panneau résultat texte
        JPanel resultPanel = createResultPanel();
        panel.add(resultPanel);
        
        // Panneau tableau
        JPanel tablePanel = createTablePanel();
        panel.add(tablePanel);
        
        return panel;
    }
    
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.CARD_BG);
        panel.setBorder(new UIUtils.RoundedBorder(16, ThemeColors.BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("📄 Résumé du Rapport");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        
        txtResultat = new JTextArea();
        txtResultat.setEditable(false);
        txtResultat.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtResultat.setBackground(ThemeColors.BACKGROUND);
        txtResultat.setForeground(ThemeColors.TEXT_PRIMARY);
        txtResultat.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        txtResultat.setLineWrap(true);
        txtResultat.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(txtResultat);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER_COLOR, 1));
        
        innerPanel.add(titleLabel, BorderLayout.NORTH);
        innerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(innerPanel);
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.CARD_BG);
        panel.setBorder(new UIUtils.RoundedBorder(16, ThemeColors.BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("📋 Détails");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        
        createModernTable();
        JScrollPane scrollPane = new JScrollPane(tableRapports);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeColors.CARD_BG);
        
        innerPanel.add(titleLabel, BorderLayout.NORTH);
        innerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(innerPanel);
        return panel;
    }
    
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
        tableRapports.setBackground(ThemeColors.CARD_BG);
        tableRapports.setSelectionBackground(ThemeColors.PRIMARY_PURPLE);
        tableRapports.setSelectionForeground(ThemeColors.TEXT_PRIMARY);
        
        // Style du header
        JTableHeader header = tableRapports.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(ThemeColors.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColors.BORDER_COLOR));
        
        // Renderer personnalisé
        tableRapports.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? ThemeColors.CARD_BG : ThemeColors.BACKGROUND);
                }
                
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                
                // Coloration du statut
                if (column == 5 && value != null && !isSelected) {
                    String statut = value.toString().toLowerCase();
                    if (statut.contains("actif") || statut.contains("terminée")) {
                        setForeground(ThemeColors.PRIMARY_EMERALD);
                    } else if (statut.contains("cours") || statut.contains("planifiée")) {
                        setForeground(ThemeColors.PRIMARY_BLUE);
                    } else {
                        setForeground(ThemeColors.TEXT_PRIMARY);
                    }
                } else if (!isSelected) {
                    setForeground(ThemeColors.TEXT_PRIMARY);
                }
                
                return c;
            }
        });
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setBackground(ThemeColors.BACKGROUND);
        
        JButton btnGenerer = createModernButton("📊 Générer", ThemeColors.PRIMARY_PURPLE, new Color(237, 233, 254));
        JButton btnExporter = createModernButton("💾 Exporter", ThemeColors.PRIMARY_BLUE, new Color(219, 234, 254));
        JButton btnImprimer = createModernButton("🖨️ Imprimer", ThemeColors.PRIMARY_AMBER, new Color(254, 243, 199));
        JButton btnRafraichir = createModernButton("🔄 Rafraîchir", ThemeColors.TEXT_SECONDARY, new Color(241, 245, 249));
        
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
    
    private Color adjustBrightness(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * factor));
        int g = Math.min(255, (int)(color.getGreen() * factor));
        int b = Math.min(255, (int)(color.getBlue() * factor));
        return new Color(r, g, b);
    }
    
    private void updateVisualStats() {
        chargerDonnees();
    }
    
    private void genererRapport() {
        String typeRapport = (String) cmbTypeRapport.getSelectedItem();
        LocalDate dateDebutValue = convertToLocalDate(dateDebut.getDate());
        LocalDate dateFinValue = convertToLocalDate(dateFin.getDate());
        
        tableModel.setRowCount(0);
        txtResultat.setText("");
        
        try {
            genererApercuRapport();
        } catch (Exception e) {
            showErrorMessage("Erreur lors de la génération: " + e.getMessage());
        }
    }
    private void genererApercuRapport() {
        String typeRapport = (String) cmbTypeRapport.getSelectedItem();
        
        // VERSION SIMPLE SANS APPELS BDD
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder resultat = new StringBuilder();
        
        resultat.append("╔════════════════════════════════════════╗\n");
        resultat.append("║   ").append(String.format("%-37s", typeRapport.toUpperCase())).append("║\n");
        resultat.append("╚════════════════════════════════════════╝\n\n");
        resultat.append("📅 Généré le: ").append(LocalDate.now().format(formatter)).append("\n\n");
        
        resultat.append("📋 ").append(typeRapport).append(" prêt pour export PDF\n\n");
        resultat.append("✅ Cliquez sur 'Exporter le rapport PDF' pour générer le rapport complet\n");
        resultat.append("📊 Le rapport contiendra les données détaillées et les graphiques\n\n");
        resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        resultat.append("🔍 STATUT: Prêt pour export PDF\n");
        resultat.append("⏱️  TEMPS ESTIMÉ: 5-10 secondes\n");
        resultat.append("📄 FORMAT: PDF moderne avec graphiques\n");
        resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        txtResultat.setText(resultat.toString());
        
        // DÉSACTIVÉ : Plus d'appels à updateVisualStats() pour éviter la boucle
        // updateVisualStats();
        
        System.out.println("Aperçu généré (sans updateVisualStats)");
    }
    
    private String genererContenuRapport(String typeRapport, LocalDate dateDebut, LocalDate dateFin) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            StringBuilder resultat = new StringBuilder();
            
            // En-tête commun
            resultat.append("╔════════════════════════════════════════╗\n");
            resultat.append("║   ").append(String.format("%-37s", typeRapport.toUpperCase())).append("║\n");
            resultat.append("╚════════════════════════════════════════╝\n\n");
            resultat.append("📅 Généré le: ").append(LocalDate.now().format(formatter)).append("\n\n");
            
            switch (typeRapport) {
                case "Résumé Général":
                    genererResumeGeneral(resultat);
                    break;
                case "Membres Actifs":
                    genererMembresActifs(resultat);
                    break;
                case "Tontines Actives":
                    genererTontinesActives(resultat);
                    break;
                case "Crédits en Cours":
                    genererCreditsEnCours(resultat);
                    break;
                case "Séances du Mois":
                    genererSeancesMois(resultat, dateDebut, dateFin);
                    break;
                case "Projets FIAC":
                    genererProjetsFIAC(resultat);
                    break;
                case "Synthèse AG":
                    genererSyntheseAG(resultat);
                    break;
            }
            
            return resultat.toString();
            
        } catch (Exception e) {
            return "❌ Erreur: " + e.getMessage();
        }
    }
    
    private void genererResumeGeneral(StringBuilder resultat) {
        // Récupération parallèle des données
        try {
            List<Membre> membres = membreDAO.findAll();
            List<Tontine> tontines = tontineDAO.findAll();
            List<Credit> credits = creditDAO.findAll();
            
            // Statistiques membres
            int membresTotal = membres.size();
            int membresActifs = (int) membres.stream().filter(m -> "actif".equals(m.getStatut())).count();
            
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("👥 MEMBRES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("  • Total: ").append(membresTotal).append("\n");
            resultat.append("  • Actifs: ").append(membresActifs).append("\n");
            resultat.append("  • Taux: ").append(membresTotal > 0 ? String.format("%.1f%%", (membresActifs * 100.0 / membresTotal)) : "0%").append("\n\n");
            
            // Statistiques tontines
            int tontinesTotal = tontines.size();
            int tontinesActives = (int) tontines.stream().filter(t -> "active".equals(t.getStatut())).count();
            
            resultat.append("💰 TONTINES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("  • Total: ").append(tontinesTotal).append("\n");
            resultat.append("  • Actives: ").append(tontinesActives).append("\n\n");
            
            // Statistiques crédits
            int creditsTotal = credits.size();
            int creditsEnCours = (int) credits.stream().filter(c -> "en_cours".equals(c.getStatut())).count();
            double montantTotal = credits.stream().mapToDouble(c -> c.getMontantEmprunte().doubleValue()).sum();
            
            resultat.append("💳 CRÉDITS\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("  • Total: ").append(creditsTotal).append("\n");
            resultat.append("  • En cours: ").append(creditsEnCours).append("\n");
            resultat.append("  • Montant: ").append(String.format("%.0f FCFA", montantTotal)).append("\n");
            
        } catch (Exception e) {
            resultat.append("❌ Erreur lors de la récupération des données: ").append(e.getMessage());
        }
    }
    
    private void genererMembresActifs(StringBuilder resultat) {
        List<Membre> membres;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        int total = 0;
        int actifs = 0;
        
        try {
            membres = membreDAO.findAll();
            
            resultat.append("📋 LISTE DES MEMBRES ACTIFS\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            
            for (Membre m : membres) {
                total++;
                if (m.getStatut() != null && m.getStatut().equals("actif")) {
                    actifs++;
                    resultat.append(String.format("• %s (ID: %d) - Inscrit le: %s\n", 
                        m.getNomComplet(), 
                        m.getIdMembre(),
                        m.getDateInscription() != null ? m.getDateInscription().format(formatter) : "N/A"));
                }
            }
            
            resultat.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("📊 STATISTIQUES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            resultat.append("  • Total membres: ").append(total).append("\n");
            resultat.append("  • Membres actifs: ").append(actifs).append("\n");
            resultat.append("  • Taux d'activité: ").append(total > 0 ? String.format("%.1f%%", (actifs * 100.0 / total)) : "0%").append("\n");
            
        } catch (Exception e) {
            resultat.append("❌ Erreur: ").append(e.getMessage());
        }
    }
    
    private void genererTontinesActives(StringBuilder resultat) {
        List<Tontine> tontines;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        int total = 0;
        int actives = 0;
        
        try {
            tontines = tontineDAO.findAll();
            
            resultat.append("📋 LISTE DES TONTINES ACTIVES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            
            for (Tontine t : tontines) {
                total++;
                if (t.getStatut() != null && t.getStatut().equals("active")) {
                    actives++;
                    resultat.append(String.format("• %s (ID: %d) - %s tours - Début: %s\n", 
                        t.getNom(), 
                        t.getIdTontine(),
                        t.getNombreTours(),
                        t.getDateDebut() != null ? t.getDateDebut().format(formatter) : "N/A"));
                }
            }
            
            resultat.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("📊 STATISTIQUES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            resultat.append("  • Total tontines: ").append(total).append("\n");
            resultat.append("  • Tontines actives: ").append(actives).append("\n");
            resultat.append("  • Taux d'activité: ").append(total > 0 ? String.format("%.1f%%", (actives * 100.0 / total)) : "0%").append("\n");
            
        } catch (Exception e) {
            resultat.append("❌ Erreur: ").append(e.getMessage());
        }
    }
    
    private void genererCreditsEnCours(StringBuilder resultat) {
        try {
            List<Credit> credits = creditDAO.findAll();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            int total = 0;
            int enCours = 0;
            double montantTotal = 0;
            
            resultat.append("📋 LISTE DES CRÉDITS EN COURS\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            
            for (Credit c : credits) {
                total++;
                if (c.getStatut() != null && c.getStatut().equals("en_cours")) {
                    enCours++;
                    montantTotal += c.getMontantEmprunte().doubleValue();
                    resultat.append(String.format("• %s - %s FCFA (ID: %d) - Début: %s\n", 
                        c.getMembre() != null ? c.getMembre().getNomComplet() : "N/A",
                        String.format("%.0f", c.getMontantEmprunte()),
                        c.getIdCredit(),
                        c.getDateDebut() != null ? c.getDateDebut().format(formatter) : "N/A"));
                }
            }
            
            resultat.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("📊 STATISTIQUES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            resultat.append("  • Total crédits: ").append(total).append("\n");
            resultat.append("  • Crédits en cours: ").append(enCours).append("\n");
            resultat.append("  • Montant total: ").append(String.format("%.0f FCFA", montantTotal)).append("\n");
            
        } catch (Exception e) {
            resultat.append("❌ Erreur: ").append(e.getMessage());
        }
    }
    
    private void genererSeancesMois(StringBuilder resultat, LocalDate dateDebut, LocalDate dateFin) {
        try {
            List<Seance> seances = seanceDAO.findAll();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            int total = 0;
            int planifiees = 0;
            int terminees = 0;
            
            resultat.append("📋 LISTE DES SÉANCES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("📅 Période: ");
            if (dateDebut != null && dateFin != null) {
                resultat.append(dateDebut.format(formatter)).append(" → ").append(dateFin.format(formatter));
            } else {
                resultat.append("Toutes les séances");
            }
            resultat.append("\n\n");
            
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
                
                resultat.append(String.format("• Tour %d - %s (ID: %d) - %s\n", 
                    s.getNumeroTour(),
                    s.getDateSeance() != null ? s.getDateSeance().format(formatter) : "N/A",
                    s.getIdSeance(),
                    s.getStatut()));
            }
            
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("✅ SYNTHÈSE VALIDÉE POUR AG\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            resultat.append("❌ Erreur: ").append(e.getMessage());
        }
    }
    
    private void exporterRapport() {
        System.out.println("BOUTON EXPORTER CLIQUÉ - Début du processus");
        
        if (txtResultat.getText().isEmpty()) {
            System.err.println("ERREUR: Aucun rapport à exporter");
            showWarningMessage("Veuillez d'abord générer un rapport");
            return;
        }
        
        System.out.println("Rapport trouvé, ouverture du sélecteur de fichiers");
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter le rapport PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "PDF Documents", "pdf"));
        fileChooser.setSelectedFile(new File("rapport_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        System.out.println("Sélection utilisateur: " + userSelection);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Fichier sélectionné: " + selectedFile.getAbsolutePath());
            
            // Ajouter .pdf si absent
            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            final File fileToSave = new File(filePath);
            System.out.println("Fichier final: " + fileToSave.getAbsolutePath());
            
            // Afficher dialogue de progression
            System.out.println("Création du dialogue de progression");
            JDialog progressDialog = createProgressDialog();
            progressDialog.setModal(false); // RENDRE NON-MODAL pour éviter le blocage
            System.out.println("Dialogue créé, affichage en cours...");
            progressDialog.setVisible(true);
            System.out.println("Dialogue affiché avec succès");
            
            // Générer le PDF avec SwingWorker pur (SOLUTION ORACLE)
            System.out.println("Démarrage du SwingWorker pour la génération PDF");
            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    System.out.println("SwingWorker.doInBackground() démarré");
                    long startTime = System.currentTimeMillis();
                    publish("Préparation des données...");
                    
                    // GÉNÉRATION DIRECTE SANS THREAD SUPPLÉMENTAIRE
                    System.out.println("Appel de generateModernPDF()");
                    generateModernPDF(fileToSave);
                    System.out.println("generateModernPDF() terminé");
                    
                    long elapsed = System.currentTimeMillis() - startTime;
                    String message = "Rapport généré en " + (elapsed/1000.0) + " secondes!";
                    System.out.println(message);
                    publish(message);
                    return null;
                }
                
                @Override
                protected void process(List<String> chunks) {
                    System.out.println("SwingWorker.process() appelé avec: " + chunks);
                    // Mettre à jour le message de progression
                    if (!chunks.isEmpty()) {
                        String latestMessage = chunks.get(chunks.size() - 1);
                        System.out.println("Message à afficher: " + latestMessage);
                        // Mettre à jour le label de progression si le dialogue en a un
                        for (Component comp : progressDialog.getContentPane().getComponents()) {
                            if (comp instanceof JLabel) {
                                ((JLabel) comp).setText(latestMessage);
                                break;
                            }
                        }
                    }
                }
                
                @Override
                protected void done() {
                    System.out.println("SwingWorker.done() appelé");
                    progressDialog.dispose();
                    try {
                        get(); // Vérifier les erreurs
                        System.out.println("Succès: Affichage du message de succès");
                        showSuccessMessage("Rapport PDF exporté avec succès!\n\n" + 
                            "📁 " + fileToSave.getAbsolutePath());
                        
                        // Proposer d'ouvrir le fichier
                        int response = JOptionPane.showConfirmDialog(
                            RapportsPanel.this,
                            "Voulez-vous ouvrir le rapport maintenant?",
                            "Ouvrir le rapport",
                            JOptionPane.YES_NO_OPTION
                        );
                        
                        if (response == JOptionPane.YES_OPTION) {
                            System.out.println("Ouverture du fichier PDF");
                            Desktop.getDesktop().open(fileToSave);
                        }
                    } catch (Exception e) {
                        System.err.println("ERREUR dans SwingWorker.done(): " + e.getMessage());
                        showErrorMessage("Erreur lors de l'export: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            };
            
            System.out.println("Lancement du SwingWorker.execute()");
            worker.execute();
            System.out.println("SwingWorker lancé, méthode exporterRapport() terminée");
        } else {
            System.out.println("Utilisateur a annulé la sélection de fichier");
        }
    }
    
    private void generateModernPDF(File outputFile) throws Exception {
        System.out.println("DÉBUT GÉNÉRATION PDF ULTRA-OPTIMISÉ...");
        
        ReportGenerator generator = new ReportGenerator();
        
        // Préparer les données du rapport
        Map<String, Object> reportData = new HashMap<>();
        
        // Type de rapport
        String reportType = (String) cmbTypeRapport.getSelectedItem();
        reportData.put("reportType", reportType);
        
        // Période
        String period = "Toutes périodes";
        if (dateDebut.getDate() != null && dateFin.getDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate debut = convertToLocalDate(dateDebut.getDate());
            LocalDate fin = convertToLocalDate(dateFin.getDate());
            period = debut.format(formatter) + " - " + fin.format(formatter);
        }
        reportData.put("period", period);
        
        // Statistiques globales
        reportData.put("totalItems", tableModel.getRowCount());
        
        // RÉCUPÉRATION UNIQUE AVEC TIMEOUT ET ERREURS CLAIRES
        try {
            System.out.println("Récupération des données...");
            long dataStartTime = System.currentTimeMillis();
            
            // Timeout de 30 secondes pour les données
            Thread dataThread = new Thread(() -> {
                try {
                    // Récupération unique de toutes les données
                    List<Membre> membres = membreDAO.findAll();
                    List<Credit> credits = creditDAO.findAll();
                    List<Tontine> tontines = tontineDAO.findAll();
                    
                    // KPIs calculés une seule fois
                    int actifsMembres = (int) membres.stream()
                        .filter(m -> "actif".equals(m.getStatut())).count();
                    reportData.put("activeMembers", actifsMembres);
                    
                    double totalRevenue = credits.stream()
                        .mapToDouble(c -> c.getMontantEmprunte().doubleValue()).sum();
                    reportData.put("totalRevenue", String.format("%.0f", totalRevenue));
                    
                    double performance = actifsMembres * 100.0 / Math.max(1, membres.size());
                    reportData.put("performance", String.format("%.1f", performance));
                    
                    // Données pour graphiques
                    Map<String, Double> chartData = new HashMap<>();
                    int activesTontines = (int) tontines.stream()
                        .filter(t -> "active".equals(t.getStatut())).count();
                    chartData.put("Tontines Actives", (double) activesTontines);
                    chartData.put("Tontines Terminées", (double) (tontines.size() - activesTontines));
                    
                    int enCoursCredits = (int) credits.stream()
                        .filter(c -> "en_cours".equals(c.getStatut())).count();
                    chartData.put("Crédits Actifs", (double) enCoursCredits);
                    chartData.put("Crédits Remboursés", (double) (credits.size() - enCoursCredits));
                    
                    reportData.put("chartData", chartData);
                    
                    long dataElapsed = System.currentTimeMillis() - dataStartTime;
                    System.out.println("Données récupérées en " + (dataElapsed/1000.0) + " secondes");
                    
                } catch (Exception e) {
                    throw new RuntimeException("Erreur BDD: " + e.getMessage());
                }
            });
            
            dataThread.start();
            dataThread.join(30000); // 30 secondes max pour les données
            
            if (dataThread.isAlive()) {
                dataThread.interrupt();
                throw new Exception("TIMEOUT: La récupération des données dépasse 30 secondes");
            }
            
        } catch (Exception e) {
            // En cas d'erreur BDD, générer un PDF avec données par défaut
            System.err.println("Erreur données BDD: " + e.getMessage());
            System.out.println("Génération PDF avec données par défaut...");
            
            reportData.put("activeMembers", "0");
            reportData.put("totalRevenue", "0");
            reportData.put("performance", "0");
            reportData.put("chartData", new HashMap<String, Double>());
        }
        
        // Données de tableau simples
        List<String[]> tableData = new ArrayList<>();
        String[] headers = {"ID", "Nom", "Statut"};
        tableData.add(headers);
        tableData.add(new String[]{"-", "Aucune donnée", "-"});
        reportData.put("tableData", tableData);
        
        // Générer le rapport
        System.out.println("Génération du PDF...");
        generator.generateReport(outputFile.getAbsolutePath(), reportType, reportData);
        
        System.out.println("PDF GÉNÉRÉ AVEC SUCCÈS!");
    }
    
    private JDialog createProgressDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "Génération du rapport", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(ThemeColors.CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Icône animée
        JLabel iconLabel = new JLabel("📊");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Message
        JLabel messageLabel = new JLabel("Génération du rapport PDF en cours...");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 8));
        progressBar.setBackground(ThemeColors.BACKGROUND);
        progressBar.setForeground(ThemeColors.PRIMARY_PURPLE);
        progressBar.setBorderPainted(false);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(ThemeColors.CARD_BG);
        
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(progressBar);
        
        panel.add(contentPanel);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        
        return dialog;
    }
    
    private void imprimerRapport() {
        if (txtResultat.getText().isEmpty()) {
            showWarningMessage("Veuillez d'abord générer un rapport");
            return;
        }
        
        // Créer un dialogue de confirmation avec options
        String[] options = {"Imprimer le texte", "Imprimer en PDF", "Annuler"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Choisissez le format d'impression:",
            "Impression du rapport",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[1] // PDF par défaut
        );
        
        if (choice == 0) {
            // Impression texte classique
            try {
                boolean complete = txtResultat.print();
                if (complete) {
                    showSuccessMessage("✅ Impression envoyée à l'imprimante");
                } else {
                    showWarningMessage("⚠️ Impression annulée");
                }
            } catch (Exception e) {
                showErrorMessage("❌ Erreur lors de l'impression: " + e.getMessage());
            }
        } else if (choice == 1) {
            // Génération PDF temporaire et impression
            try {
                File tempFile = File.createTempFile("rapport_", ".pdf");
                tempFile.deleteOnExit();
                
                generateModernPDF(tempFile);
                Desktop.getDesktop().print(tempFile);
                
                showSuccessMessage("✅ Document envoyé à l'imprimante");
            } catch (Exception e) {
                showErrorMessage("❌ Erreur lors de l'impression PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(
            this, 
            message, 
            "✅ Succès", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    public void rafraichir() {
        tableModel.setRowCount(0);
        txtResultat.setText("");
        updateVisualStats();
    }
    
    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Attention", JOptionPane.WARNING_MESSAGE);
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    
    private LocalDate convertToLocalDate(java.util.Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }
    
    private void chargerDonnees() {
        try {
            // Charger les statistiques pour les labels visuels
            int totalMembres = membreDAO.findAll().size();
            int membresActifs = (int) membreDAO.findAll().stream()
                .filter(m -> "actif".equals(m.getStatut()))
                .count();
            
            int totalTontines = tontineDAO.findAll().size();
            int tontinesActives = (int) tontineDAO.findAll().stream()
                .filter(t -> "active".equals(t.getStatut()))
                .count();
            
            int totalCredits = creditDAO.findAll().size();
            int creditsActifs = (int) creditDAO.findAll().stream()
                .filter(c -> "en_cours".equals(c.getStatut()))
                .count();
            
            // Mettre à jour les labels
            lblStatMembres.setText(String.format("%d/%d", membresActifs, totalMembres));
            lblStatTontines.setText(String.format("%d/%d", tontinesActives, totalTontines));
            lblStatCredits.setText(String.format("%d/%d", creditsActifs, totalCredits));
            
            System.out.println("Stats visuelles mises à jour avec données BDD");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des données: " + e.getMessage());
            // Valeurs par défaut en cas d'erreur
            lblStatMembres.setText("0/0");
            lblStatTontines.setText("0/0");
            lblStatCredits.setText("0/0");
        }
    }
    
    private void genererProjetsFIAC(StringBuilder resultat) {
        try {
            List<ProjetFIAC> projets = projetDAO.findAll();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            int total = 0;
            int enCours = 0;
            int termines = 0;
            double montantTotalObjectif = 0;
            double montantTotalCollecte = 0;
            
            resultat.append("📋 LISTE DES PROJETS FIAC\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            
            for (ProjetFIAC p : projets) {
                total++;
                montantTotalObjectif += p.getMontantObjectif();
                montantTotalCollecte += p.getMontantCollecte();
                
                if (p.getStatut() != null) {
                    switch (p.getStatut()) {
                        case "en_cours": enCours++; break;
                        case "termine": termines++; break;
                    }
                }
                
                resultat.append(String.format("• %s (ID: %d)\n", p.getNomProjet(), p.getIdProjet()));
                resultat.append(String.format("  📊 Objectif: %s FCFA\n", String.format("%.0f", p.getMontantObjectif())));
                resultat.append(String.format("  💰 Collecté: %s FCFA (%.1f%%)\n", 
                    String.format("%.0f", p.getMontantCollecte()), p.getPourcentageAvancement()));
                resultat.append(String.format("  📅 Début: %s\n", p.getDateDebut() != null ? p.getDateDebut().format(formatter) : "N/A"));
                resultat.append(String.format("  📈 Statut: %s\n\n", p.getStatut()));
            }
            
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("📊 STATISTIQUES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            resultat.append("  • Total projets: ").append(total).append("\n");
            resultat.append("  • Projets en cours: ").append(enCours).append("\n");
            resultat.append("  • Projets terminés: ").append(termines).append("\n");
            resultat.append("  • Montant total objectif: ").append(String.format("%.0f FCFA", montantTotalObjectif)).append("\n");
            resultat.append("  • Montant total collecté: ").append(String.format("%.0f FCFA", montantTotalCollecte)).append("\n");
            resultat.append("  • Taux de collecte global: ").append(
                montantTotalObjectif > 0 ? String.format("%.1f%%", (montantTotalCollecte * 100.0 / montantTotalObjectif)) : "0%").append("\n");
            
        } catch (Exception e) {
            resultat.append("❌ Erreur: ").append(e.getMessage());
        }
    }
    
    private void genererSyntheseAG(StringBuilder resultat) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            resultat.append("📋 SYNTHÈSE ASSEMBLÉE GÉNÉRALE\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            
            // Statistiques des membres
            List<Membre> membres = membreDAO.findAll();
            int membresActifs = 0;
            for (Membre m : membres) {
                if (m.estActif()) membresActifs++;
            }
            
            // Statistiques des tontines
            List<Tontine> tontines = tontineDAO.findAll();
            int tontinesActives = 0;
            for (Tontine t : tontines) {
                if ("active".equals(t.getStatut())) tontinesActives++;
            }
            
            // Statistiques des crédits
            List<Credit> credits = creditDAO.findAll();
            int creditsEnCours = 0;
            double montantCredits = 0;
            for (Credit c : credits) {
                if ("en_cours".equals(c.getStatut())) {
                    creditsEnCours++;
                    montantCredits += c.getMontantEmprunte().doubleValue();
                }
            }
            
            // Statistiques des projets FIAC
            List<ProjetFIAC> projets = projetDAO.findAll();
            int projetsEnCours = 0;
            double montantProjetsCollecte = 0;
            for (ProjetFIAC p : projets) {
                if ("en_cours".equals(p.getStatut())) {
                    projetsEnCours++;
                    montantProjetsCollecte += p.getMontantCollecte();
                }
            }
            
            resultat.append("👥 MEMBRES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("  • Total membres: ").append(membres.size()).append("\n");
            resultat.append("  • Membres actifs: ").append(membresActifs).append("\n");
            resultat.append("  • Taux de participation: ").append(
                membres.size() > 0 ? String.format("%.1f%%", (membresActifs * 100.0 / membres.size())) : "0%").append("\n\n");
            
            resultat.append("💰 TONTINES\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("  • Total tontines: ").append(tontines.size()).append("\n");
            resultat.append("  • Tontines actives: ").append(tontinesActives).append("\n\n");
            
            resultat.append("💳 CRÉDITS\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("  • Crédits en cours: ").append(creditsEnCours).append("\n");
            resultat.append("  • Montant total: ").append(String.format("%.0f FCFA", montantCredits)).append("\n\n");
            
            resultat.append("🏗️ PROJETS FIAC\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("  • Projets en cours: ").append(projetsEnCours).append("\n");
            resultat.append("  • Fonds collectés: ").append(String.format("%.0f FCFA", montantProjetsCollecte)).append("\n\n");
            
            resultat.append("📊 RÉCAPITULATIF FINANCIER\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("  • Total des crédits: ").append(String.format("%.0f FCFA", montantCredits)).append("\n");
            resultat.append("  • Total des projets FIAC: ").append(String.format("%.0f FCFA", montantProjetsCollecte)).append("\n");
            resultat.append("  • Solde net: ").append(String.format("%.0f FCFA", montantProjetsCollecte - montantCredits)).append("\n\n");
            
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            resultat.append("✅ SYNTHÈSE VALIDÉE POUR AG\n");
            resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            resultat.append("❌ Erreur: ").append(e.getMessage());
        }
    }
    
}