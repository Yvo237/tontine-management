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
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.CotisationDAO;
import dao.SeanceDAO;
import dao.TontineDAO;
import models.Seance;
import models.Tontine;
import ui.MainFrame;
import ui.panels.CotisationsDialog;

/**
 * Panneau de gestion des séances avec design ultra-moderne
 * Design 3.0 - Interface Premium cohérente
 */
public class SeancesPanel extends JPanel {
    private MainFrame mainFrame;
    private SeanceDAO seanceDAO;
    private TontineDAO tontineDAO;
    private CotisationDAO cotisationDAO;
    
    private JTable tableSeances;
    private DefaultTableModel tableModel;
    private JComboBox<Tontine> cmbFiltreTontine;
    private JLabel lblTitre;
    private JLabel lblStatTotal;
    private JLabel lblStatPlanifiees;
    private JLabel lblStatTerminees;
    
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
    
    public SeancesPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.seanceDAO = new SeanceDAO();
        this.tontineDAO = new TontineDAO();
        this.cotisationDAO = new CotisationDAO();
        initComponents();
        chargerSeances();
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
        
        // Corps avec statistiques et tableau
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 24));
        bodyPanel.setBackground(BACKGROUND);
        
        // Statistiques rapides
        JPanel statsPanel = createStatsPanel();
        bodyPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Panneau tableau
        JPanel tablePanel = createTablePanel();
        bodyPanel.add(tablePanel, BorderLayout.CENTER);
        
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
        
        JLabel iconLabel = new JLabel("📅");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblTitre = new JLabel("Gestion des Séances");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitre.setForeground(TEXT_PRIMARY);
        lblTitre.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Planification et suivi des rencontres");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftSection.add(iconLabel);
        leftSection.add(Box.createVerticalStrut(8));
        leftSection.add(lblTitre);
        leftSection.add(Box.createVerticalStrut(4));
        leftSection.add(subtitleLabel);
        
        // Section droite - Filtre
        JPanel rightSection = new JPanel();
        rightSection.setLayout(new BoxLayout(rightSection, BoxLayout.Y_AXIS));
        rightSection.setBackground(CARD_BG);
        
        JLabel filterLabel = new JLabel("Filtrer par tontine");
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterLabel.setForeground(TEXT_SECONDARY);
        filterLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        cmbFiltreTontine = new JComboBox<>();
        cmbFiltreTontine.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFiltreTontine.setPreferredSize(new Dimension(250, 40));
        cmbFiltreTontine.setMaximumSize(new Dimension(250, 40));
        cmbFiltreTontine.setBackground(CARD_BG);
        cmbFiltreTontine.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cmbFiltreTontine.addItem(null);
        
        try {
            List<Tontine> tontines = tontineDAO.findAll();
            for (Tontine t : tontines) {
                cmbFiltreTontine.addItem(t);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement tontines: " + e.getMessage());
        }
        
        cmbFiltreTontine.addActionListener(e -> filtrerSeances());
        
        rightSection.add(filterLabel);
        rightSection.add(Box.createVerticalStrut(8));
        rightSection.add(cmbFiltreTontine);
        
        innerPanel.add(leftSection, BorderLayout.WEST);
        innerPanel.add(rightSection, BorderLayout.EAST);
        
        header.add(innerPanel);
        return header;
    }
    
    /**
     * Crée le panneau de statistiques rapides
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(BACKGROUND);
        
        // Carte Total Séances
        JPanel cardTotal = createMiniStatCard("Total Séances", "0", "📊", PRIMARY_PURPLE);
        lblStatTotal = (JLabel) ((JPanel)((JPanel)((JPanel)cardTotal.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Planifiées
        JPanel cardPlanifiees = createMiniStatCard("Planifiées", "0", "📋", PRIMARY_BLUE);
        lblStatPlanifiees = (JLabel) ((JPanel)((JPanel)((JPanel)cardPlanifiees.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Terminées
        JPanel cardTerminees = createMiniStatCard("Terminées", "0", "✅", PRIMARY_EMERALD);
        lblStatTerminees = (JLabel) ((JPanel)((JPanel)((JPanel)cardTerminees.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        panel.add(cardTotal);
        panel.add(cardPlanifiees);
        panel.add(cardTerminees);
        
        return panel;
    }
    
    /**
     * Crée une mini carte de statistique
     */
    private JPanel createMiniStatCard(String titre, String valeur, String icone, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new RoundedBorder(12, BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CARD_BG);
        
        JLabel iconLabel = new JLabel(icone);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(color);
        
        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.setBackground(CARD_BG);
        
        JLabel valueLabel = new JLabel(valeur);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(titre);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        valuePanel.add(valueLabel);
        valuePanel.add(Box.createVerticalStrut(4));
        valuePanel.add(titleLabel);
        
        contentPanel.add(iconLabel, BorderLayout.WEST);
        contentPanel.add(Box.createHorizontalStrut(12), BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(CARD_BG);
        rightPanel.add(valuePanel, BorderLayout.CENTER);
        
        innerPanel.add(contentPanel, BorderLayout.WEST);
        innerPanel.add(rightPanel, BorderLayout.CENTER);
        
        card.add(innerPanel);
        return card;
    }
    
    /**
     * Crée le panneau du tableau moderne
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new RoundedBorder(16, BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Créer le tableau
        createModernTable();
        
        JScrollPane scrollPane = new JScrollPane(tableSeances);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_BG);
        scrollPane.setBackground(CARD_BG);
        
        innerPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(innerPanel);
        
        return panel;
    }
    
    /**
     * Crée le tableau moderne avec style premium
     */
    private void createModernTable() {
        String[] columnNames = {"ID", "Tontine", "Tour N°", "Date", "Lieu", "Statut"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableSeances = new JTable(tableModel);
        tableSeances.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSeances.setRowHeight(48);
        tableSeances.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableSeances.setShowGrid(false);
        tableSeances.setIntercellSpacing(new Dimension(0, 0));
        tableSeances.setBackground(CARD_BG);
        tableSeances.setSelectionBackground(new Color(237, 233, 254));
        tableSeances.setSelectionForeground(TEXT_PRIMARY);
        
        // Style du header
        JTableHeader header = tableSeances.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        
        // Renderer personnalisé
        tableSeances.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : new Color(248, 250, 252));
                }
                
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                
                // Coloration du statut
                if (column == 5 && value != null) {
                    String statut = value.toString();
                    switch (statut.toLowerCase()) {
                        case "planifiée":
                            setForeground(PRIMARY_BLUE);
                            setText("📋 Planifiée");
                            break;
                        case "en_cours":
                            setForeground(PRIMARY_AMBER);
                            setText("⏳ En cours");
                            break;
                        case "terminée":
                            setForeground(PRIMARY_EMERALD);
                            setText("✅ Terminée");
                            break;
                        default:
                            setForeground(TEXT_PRIMARY);
                    }
                } else if (!isSelected) {
                    setForeground(TEXT_PRIMARY);
                }
                
                return c;
            }
        });
        
        // Largeurs des colonnes
        tableSeances.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableSeances.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableSeances.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableSeances.getColumnModel().getColumn(3).setPreferredWidth(110);
        tableSeances.getColumnModel().getColumn(4).setPreferredWidth(150);
        tableSeances.getColumnModel().getColumn(5).setPreferredWidth(120);
    }
    
    /**
     * Crée le panneau d'actions avec boutons modernes
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setBackground(BACKGROUND);
        
        JButton btnNouvelle = createModernButton("➕ Nouvelle Séance", PRIMARY_PURPLE, new Color(237, 233, 254));
        JButton btnCotisations = createModernButton("💰 Cotisations", PRIMARY_AMBER, new Color(254, 243, 199));
        JButton btnModifier = createModernButton("✏️ Modifier", PRIMARY_BLUE, new Color(219, 234, 254));
        JButton btnSupprimer = createModernButton("🗑️ Supprimer", PRIMARY_RED, new Color(254, 226, 226));
        JButton btnRafraichir = createModernButton("🔄 Actualiser", TEXT_SECONDARY, new Color(241, 245, 249));
        
        btnNouvelle.addActionListener(e -> nouvelleSeance());
        btnCotisations.addActionListener(e -> gererCotisations());
        btnModifier.addActionListener(e -> modifierSeance());
        btnSupprimer.addActionListener(e -> supprimerSeance());
        btnRafraichir.addActionListener(e -> chargerSeances());
        
        panel.add(btnNouvelle);
        panel.add(btnCotisations);
        panel.add(btnModifier);
        panel.add(btnSupprimer);
        panel.add(btnRafraichir);
        
        return panel;
    }
    
    /**
     * Crée un bouton moderne avec effet hover
     */
    private JButton createModernButton(String text, Color textColor, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(170, 44));
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
     * Ajuste la luminosité d'une couleur
     */
    private Color adjustBrightness(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * factor));
        int g = Math.min(255, (int)(color.getGreen() * factor));
        int b = Math.min(255, (int)(color.getBlue() * factor));
        return new Color(r, g, b);
    }
    
    /**
     * Charge toutes les séances
     */
    private void chargerSeances() {
        try {
            tableModel.setRowCount(0);
            List<Seance> seances = seanceDAO.findAll();
            afficherSeances(seances);
            updateStatistics(seances);
        } catch (Exception e) {
            showErrorMessage("Erreur lors du chargement des séances: " + e.getMessage());
        }
    }
    
    /**
     * Met à jour les statistiques
     */
    private void updateStatistics(List<Seance> seances) {
        int total = seances.size();
        int planifiees = 0;
        int terminees = 0;
        
        for (Seance s : seances) {
            String statut = s.getStatut();
            if ("planifiée".equals(statut)) planifiees++;
            else if ("terminée".equals(statut)) terminees++;
        }
        
        if (lblStatTotal != null) lblStatTotal.setText(String.valueOf(total));
        if (lblStatPlanifiees != null) lblStatPlanifiees.setText(String.valueOf(planifiees));
        if (lblStatTerminees != null) lblStatTerminees.setText(String.valueOf(terminees));
    }
    
    /**
     * Filtre les séances par tontine
     */
    private void filtrerSeances() {
        Tontine selected = (Tontine) cmbFiltreTontine.getSelectedItem();
        if (selected == null) {
            chargerSeances();
        } else {
            try {
                tableModel.setRowCount(0);
                List<Seance> seances = seanceDAO.findByTontine(selected.getIdTontine());
                afficherSeances(seances);
                updateStatistics(seances);
            } catch (Exception e) {
                showErrorMessage("Erreur lors du filtrage: " + e.getMessage());
            }
        }
    }
    
    /**
     * Affiche les séances dans le tableau
     */
    private void afficherSeances(List<Seance> seances) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Seance s : seances) {
            try {
                Tontine t = tontineDAO.findById(s.getIdTontine());
                Object[] row = {
                    s.getIdSeance(),
                    t != null ? t.getNom() : "Non définie",
                    s.getNumeroTour(),
                    s.getDateSeance() != null ? s.getDateSeance().format(formatter) : "Non définie",
                    s.getLieu(),
                    s.getStatut()
                };
                tableModel.addRow(row);
            } catch (Exception e) {
                System.err.println("Erreur affichage séance: " + e.getMessage());
            }
        }
    }
    
    /**
     * Ouvre le dialogue pour créer une nouvelle séance
     */
    private void nouvelleSeance() {
        SeanceDialog dialog = new SeanceDialog(mainFrame, null, tontineDAO);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            chargerSeances();
        }
    }
    
    /**
     * Ouvre le dialogue pour modifier une séance
     */
    private void modifierSeance() {
        int selectedRow = tableSeances.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Veuillez sélectionner une séance");
            return;
        }
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Seance seance = seanceDAO.findById(id);
            if (seance != null) {
                SeanceDialog dialog = new SeanceDialog(mainFrame, seance, tontineDAO);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    chargerSeances();
                }
            } else {
                showErrorMessage("Séance introuvable");
            }
        } catch (Exception e) {
            showErrorMessage("Erreur lors de la modification: " + e.getMessage());
        }
    }
    
    /**
     * Supprime la séance sélectionnée
     */
    private void supprimerSeance() {
        int selectedRow = tableSeances.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Veuillez sélectionner une séance");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "⚠️ ATTENTION !\n\n" +
            "Êtes-vous sûr de vouloir supprimer cette séance ?\n\n" +
            "Cela supprimera également TOUTES les cotisations associées.\n" +
            "Cette action est IRRÉVERSIBLE !",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                if (seanceDAO.delete(id)) {
                    showSuccessMessage("Séance et ses cotisations supprimées avec succès");
                    chargerSeances();
                } else {
                    showErrorMessage("Erreur lors de la suppression");
                }
            } catch (Exception e) {
                showErrorMessage("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }
    
    /**
     * Ouvre le dialogue pour gérer les cotisations
     */
    private void gererCotisations() {
        int selectedRow = tableSeances.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Veuillez sélectionner une séance");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Seance seance = seanceDAO.findById(id);
        
        if (seance != null) {
            CotisationsDialog dialog = new CotisationsDialog(mainFrame, seance, cotisationDAO);
            dialog.setVisible(true);
        } else {
            showErrorMessage("Séance introuvable");
        }
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
    
    /**
     * Rafraîchit le panneau
     */
    public void rafraichir() {
        chargerSeances();
    }
    
    /**
     * Classe pour créer des bordures arrondies
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