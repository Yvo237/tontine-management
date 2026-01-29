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
import java.io.FileWriter;
import java.io.IOException;
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
import dao.SeanceDAO;
import dao.TontineDAO;
import models.Credit;
import models.Membre;
import models.Seance;
import models.Tontine;
import utils.ReportGenerator;
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
                    txtResultat.setText("📊 Rapport de Résumé Général prêt pour export PDF");
                    break;
                case "Membres Actifs":
                    txtResultat.setText("👥 Rapport des Membres Actifs prêt pour export PDF");
                    break;
                case "Tontines Actives":
                    txtResultat.setText("💰 Rapport des Tontines Actives prêt pour export PDF");
                    break;
                case "Crédits en Cours":
                    txtResultat.setText("💳 Rapport des Crédits en Cours prêt pour export PDF");
                    break;
                case "Séances du Mois":
                    txtResultat.setText("📅 Rapport des Séances du Mois prêt pour export PDF");
                    break;
            }
            updateVisualStats();
        } catch (Exception e) {
            showErrorMessage("Erreur lors de la génération: " + e.getMessage());
        }
    }
    
    
    /**
     * Exporte le rapport en PDF ultra-moderne
     */
    private void exporterRapport() {
        if (txtResultat.getText().isEmpty()) {
            showWarningMessage("Veuillez d'abord générer un rapport");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter le rapport PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "PDF Documents", "pdf"));
        fileChooser.setSelectedFile(new File("rapport_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Ajouter .pdf si absent
            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            final File fileToSave = new File(filePath);
            
            // Afficher dialogue de progression
            JDialog progressDialog = createProgressDialog();
            progressDialog.setVisible(true);
            
            // Générer le PDF dans un thread séparé
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    generateModernPDF(fileToSave);
                    return null;
                }
                
                @Override
                protected void done() {
                    progressDialog.dispose();
                    try {
                        get(); // Vérifier les erreurs
                        showSuccessMessage("✅ Rapport PDF exporté avec succès!\n\n" + 
                            "📁 " + fileToSave.getAbsolutePath());
                        
                        // Proposer d'ouvrir le fichier
                        int response = JOptionPane.showConfirmDialog(
                            RapportsPanel.this,
                            "Voulez-vous ouvrir le rapport maintenant?",
                            "Ouvrir le rapport",
                            JOptionPane.YES_NO_OPTION
                        );
                        
                        if (response == JOptionPane.YES_OPTION) {
                            Desktop.getDesktop().open(fileToSave);
                        }
                    } catch (Exception e) {
                        showErrorMessage("❌ Erreur lors de l'export: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            };
            
            worker.execute();
        }
    }
    
    /**
     * Génère un PDF moderne avec le ReportGenerator
     */
    private void generateModernPDF(File outputFile) throws Exception {
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
        
        // KPIs
        try {
            List<Membre> membres = membreDAO.findAll();
            int actifsMembres = (int) membres.stream()
                .filter(m -> "actif".equals(m.getStatut())).count();
            reportData.put("activeMembers", actifsMembres);
            
            List<Credit> credits = creditDAO.findAll();
            double totalRevenue = credits.stream()
                .mapToDouble(c -> c.getMontantEmprunte().doubleValue()).sum();
            reportData.put("totalRevenue", String.format("%.0f", totalRevenue));
            
            double performance = actifsMembres * 100.0 / Math.max(1, membres.size());
            reportData.put("performance", String.format("%.1f", performance));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Données de tableau
        List<String[]> tableData = new ArrayList<>();
        
        // Headers
        String[] headers = new String[tableModel.getColumnCount()];
        for (int i = 0; i < headers.length; i++) {
            headers[i] = tableModel.getColumnName(i);
        }
        tableData.add(headers);
        
        // Lignes (limiter à 20 pour le PDF)
        int maxRows = Math.min(20, tableModel.getRowCount());
        for (int i = 0; i < maxRows; i++) {
            String[] row = new String[tableModel.getColumnCount()];
            for (int j = 0; j < row.length; j++) {
                Object value = tableModel.getValueAt(i, j);
                row[j] = value != null ? value.toString() : "";
            }
            tableData.add(row);
        }
        reportData.put("tableData", tableData);
        
        // Données pour graphiques
        Map<String, Double> chartData = new HashMap<>();
        try {
            List<Tontine> tontines = tontineDAO.findAll();
            int actives = (int) tontines.stream()
                .filter(t -> "active".equals(t.getStatut())).count();
            chartData.put("Tontines Actives", (double) actives);
            chartData.put("Tontines Terminées", (double) (tontines.size() - actives));
            
            List<Credit> credits = creditDAO.findAll();
            int enCours = (int) credits.stream()
                .filter(c -> "en_cours".equals(c.getStatut())).count();
            chartData.put("Crédits Actifs", (double) enCours);
            chartData.put("Crédits Remboursés", (double) (credits.size() - enCours));
            
            reportData.put("chartData", chartData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Générer le rapport
        generator.generateReport(outputFile.getAbsolutePath(), reportType, reportData);
    }
    
    /**
     * Crée un dialogue de progression moderne
     */
    private JDialog createProgressDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "Génération du rapport", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Icône animée
        JLabel iconLabel = new JLabel("📊");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Message
        JLabel messageLabel = new JLabel("Génération du rapport PDF en cours...");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(TEXT_PRIMARY);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 8));
        progressBar.setBackground(BACKGROUND);
        progressBar.setForeground(PRIMARY_PURPLE);
        progressBar.setBorderPainted(false);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);
        
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
    
    /**
     * Imprime le rapport avec un rendu professionnel
     */
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
    
    /**
     * Message de succès amélioré
     */
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(
            this, 
            message, 
            "✅ Succès", 
            JOptionPane.INFORMATION_MESSAGE
        );
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