package ui.panels;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.CreditDAO;
import dao.MembreDAO;
import dao.TontineDAO;
import models.Credit;
import models.Membre;
import models.Tontine;
import ui.MainFrame;

/**
 * Panneau de gestion des crédits avec design ultra-moderne
 * Design 3.0 - Interface Premium cohérente
 */
public class CreditsPanel extends JPanel {
    private MainFrame mainFrame;
    private CreditDAO creditDAO;
    private MembreDAO membreDAO;
    
    private JTable tableCredits;
    private DefaultTableModel tableModel;
    private JComboBox<Membre> cmbFiltreMembre;
    private JLabel lblTitre;
    private JLabel lblTotalCredits;
    private JLabel lblMontantTotal;
    private JLabel lblCreditsActifs;
    
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
    
    public CreditsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.creditDAO = new CreditDAO();
        this.membreDAO = new MembreDAO();
        initComponents();
        chargerCredits();
    }
    
    /**
     * Initialise tous les composants avec un design ultra-moderne
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
        header.setBorder(new RoundedBorder(20, new Color(226, 232, 240), 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        
        // Section gauche - Titre
        JPanel leftSection = new JPanel();
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));
        leftSection.setBackground(CARD_BG);
        
        JLabel iconLabel = new JLabel("💳");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblTitre = new JLabel("Gestion des Crédits");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitre.setForeground(TEXT_PRIMARY);
        lblTitre.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Suivi des emprunts et remboursements");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftSection.add(iconLabel);
        leftSection.add(Box.createVerticalStrut(8));
        leftSection.add(lblTitre);
        leftSection.add(Box.createVerticalStrut(4));
        leftSection.add(subtitleLabel);
        
        // Section droite - Filtres
        JPanel rightSection = new JPanel();
        rightSection.setLayout(new BoxLayout(rightSection, BoxLayout.Y_AXIS));
        rightSection.setBackground(CARD_BG);
        
        JLabel filterLabel = new JLabel("Filtrer par membre");
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterLabel.setForeground(TEXT_SECONDARY);
        filterLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        cmbFiltreMembre = new JComboBox<>();
        cmbFiltreMembre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFiltreMembre.setPreferredSize(new Dimension(250, 40));
        cmbFiltreMembre.setMaximumSize(new Dimension(250, 40));
        cmbFiltreMembre.setBackground(CARD_BG);
        cmbFiltreMembre.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cmbFiltreMembre.addItem(null);
        
        try {
            List<Membre> membres = membreDAO.findAll();
            for (Membre m : membres) {
                cmbFiltreMembre.addItem(m);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement membres: " + e.getMessage());
        }
        
        cmbFiltreMembre.addActionListener(e -> filtrerCredits());
        
        rightSection.add(filterLabel);
        rightSection.add(Box.createVerticalStrut(8));
        rightSection.add(cmbFiltreMembre);
        
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
        
        // Carte Total Crédits
        JPanel cardTotal = createMiniStatCard("Total Crédits", "0", "📊", PRIMARY_PURPLE);
        lblTotalCredits = (JLabel) ((JPanel)((JPanel)((JPanel)cardTotal.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Montant Total
        JPanel cardMontant = createMiniStatCard("Montant Total", "0 FCFA", "💰", PRIMARY_AMBER);
        lblMontantTotal = (JLabel) ((JPanel)((JPanel)((JPanel)cardMontant.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Crédits Actifs
        JPanel cardActifs = createMiniStatCard("En Cours", "0", "⚡", PRIMARY_EMERALD);
        lblCreditsActifs = (JLabel) ((JPanel)((JPanel)((JPanel)cardActifs.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        panel.add(cardTotal);
        panel.add(cardMontant);
        panel.add(cardActifs);
        
        return panel;
    }
    
    /**
     * Crée une mini carte de statistique
     */
    private JPanel createMiniStatCard(String titre, String valeur, String icone, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new RoundedBorder(12, new Color(226, 232, 240), 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Icône et valeur
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
        panel.setBorder(new RoundedBorder(16, new Color(226, 232, 240), 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Créer le tableau
        createModernTable();
        
        JScrollPane scrollPane = new JScrollPane(tableCredits);
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
        String[] columnNames = {"ID", "Membre", "Montant", "Date Début", "Date Fin", "Statut", "Reste à Payer"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableCredits = new JTable(tableModel);
        tableCredits.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableCredits.setRowHeight(48);
        tableCredits.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableCredits.setShowGrid(false);
        tableCredits.setIntercellSpacing(new Dimension(0, 0));
        tableCredits.setBackground(CARD_BG);
        tableCredits.setSelectionBackground(new Color(237, 233, 254));
        tableCredits.setSelectionForeground(TEXT_PRIMARY);
        
        // Style du header
        JTableHeader header = tableCredits.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        
        // Renderer personnalisé pour les cellules
        tableCredits.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
                    if (statut.equals("en_cours")) {
                        setForeground(PRIMARY_BLUE);
                    } else if (statut.equals("rembourse")) {
                        setForeground(PRIMARY_EMERALD);
                    } else if (statut.equals("en_retard")) {
                        setForeground(PRIMARY_RED);
                    }
                } else if (!isSelected) {
                    setForeground(TEXT_PRIMARY);
                }
                
                return c;
            }
        });
        
        // Largeurs des colonnes
        tableCredits.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableCredits.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableCredits.getColumnModel().getColumn(2).setPreferredWidth(120);
        tableCredits.getColumnModel().getColumn(3).setPreferredWidth(110);
        tableCredits.getColumnModel().getColumn(4).setPreferredWidth(110);
        tableCredits.getColumnModel().getColumn(5).setPreferredWidth(100);
        tableCredits.getColumnModel().getColumn(6).setPreferredWidth(120);
    }
    
    /**
     * Crée le panneau d'actions avec boutons modernes
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setBackground(BACKGROUND);
        
        JButton btnNouveau = createModernButton("➕ Nouveau Crédit", PRIMARY_PURPLE, new Color(237, 233, 254));
        JButton btnModifier = createModernButton("✏️ Modifier", PRIMARY_BLUE, new Color(219, 234, 254));
        JButton btnPaiement = createModernButton("💰 Enregistrer Paiement", PRIMARY_EMERALD, new Color(209, 250, 229));
        JButton btnSupprimer = createModernButton("🗑️ Supprimer", PRIMARY_RED, new Color(254, 226, 226));
        JButton btnRafraichir = createModernButton("🔄 Actualiser", TEXT_SECONDARY, new Color(241, 245, 249));
        
        btnNouveau.addActionListener(e -> nouveauCredit());
        btnModifier.addActionListener(e -> modifierCredit());
        btnPaiement.addActionListener(e -> enregistrerPaiement());
        btnSupprimer.addActionListener(e -> supprimerCredit());
        btnRafraichir.addActionListener(e -> chargerCredits());
        
        panel.add(btnNouveau);
        panel.add(btnModifier);
        panel.add(btnPaiement);
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
        button.setPreferredSize(new Dimension(180, 44));
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
     * Charge tous les crédits depuis la base de données
     */
    private void chargerCredits() {
        try {
            tableModel.setRowCount(0);
            List<Credit> credits = creditDAO.findAll();
            afficherCredits(credits);
            updateStatistics(credits);
        } catch (Exception e) {
            showErrorMessage("Erreur lors du chargement des crédits: " + e.getMessage());
        }
    }
    
    /**
     * Met à jour les statistiques
     */
    private void updateStatistics(List<Credit> credits) {
        int total = credits.size();
        double montantTotal = 0;
        int creditsActifs = 0;
        
        for (Credit c : credits) {
            montantTotal += c.getMontantEmprunte().doubleValue();
            if ("en_cours".equals(c.getStatut())) {
                creditsActifs++;
            }
        }
        
        if (lblTotalCredits != null) {
            lblTotalCredits.setText(String.valueOf(total));
        }
        if (lblMontantTotal != null) {
            lblMontantTotal.setText(String.format("%.0f FCFA", montantTotal));
        }
        if (lblCreditsActifs != null) {
            lblCreditsActifs.setText(String.valueOf(creditsActifs));
        }
    }
    
    /**
     * Filtre les crédits par membre
     */
    private void filtrerCredits() {
        Membre selected = (Membre) cmbFiltreMembre.getSelectedItem();
        if (selected == null) {
            chargerCredits();
        } else {
            try {
                tableModel.setRowCount(0);
                List<Credit> credits = creditDAO.findByMembre(selected.getIdMembre());
                afficherCredits(credits);
                updateStatistics(credits);
            } catch (Exception e) {
                showErrorMessage("Erreur lors du filtrage: " + e.getMessage());
            }
        }
    }
    
    /**
     * Affiche les crédits dans le tableau
     */
    private void afficherCredits(List<Credit> credits) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Credit c : credits) {
            try {
                String statut = c.getStatut();
                String statutDisplay = "";
                switch (statut) {
                    case "en_cours":
                        statutDisplay = "En cours";
                        break;
                    case "rembourse":
                        statutDisplay = "Remboursé";
                        break;
                    case "en_retard":
                        statutDisplay = "En retard";
                        break;
                    default:
                        statutDisplay = statut;
                }
                
                Object[] row = {
                    c.getIdCredit(),
                    c.getMembre() != null ? c.getMembre().getNomComplet() : "Non défini",
                    String.format("%.0f FCFA", c.getMontantEmprunte().doubleValue()),
                    c.getDateEmprunt() != null ? c.getDateEmprunt().format(formatter) : "Non définie",
                    c.getDateEcheance() != null ? c.getDateEcheance().format(formatter) : "Non définie",
                    statutDisplay,
                    String.format("%.0f FCFA", c.getMontantRestant().doubleValue())
                };
                tableModel.addRow(row);
            } catch (Exception e) {
                System.err.println("Erreur affichage crédit: " + e.getMessage());
            }
        }
    }
    
    /**
     * Ouvre le dialogue pour créer un nouveau crédit
     */
    private void nouveauCredit() {
        CreditDialog dialog = new CreditDialog(mainFrame, null, membreDAO);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            chargerCredits();
        }
    }
    
    /**
     * Ouvre le dialogue pour modifier un crédit
     */
    private void modifierCredit() {
        int selectedRow = tableCredits.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Veuillez sélectionner un crédit");
            return;
        }
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Credit credit = creditDAO.findById(id);
            if (credit != null) {
                CreditDialog dialog = new CreditDialog(mainFrame, credit, membreDAO);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    chargerCredits();
                }
            } else {
                showErrorMessage("Crédit introuvable");
            }
        } catch (Exception e) {
            showErrorMessage("Erreur lors de la modification: " + e.getMessage());
        }
    }
    
    /**
     * Supprime le crédit sélectionné
     */
    private void supprimerCredit() {
        int selectedRow = tableCredits.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Veuillez sélectionner un crédit");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer ce crédit ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                if (creditDAO.delete(id)) {
                    showSuccessMessage("Crédit supprimé avec succès");
                    chargerCredits();
                } else {
                    showErrorMessage("Erreur lors de la suppression");
                }
            } catch (Exception e) {
                showErrorMessage("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }
    
    /**
     * Enregistre un paiement pour le crédit sélectionné
     */
    private void enregistrerPaiement() {
        int selectedRow = tableCredits.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Veuillez sélectionner un crédit");
            return;
        }
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String montantStr = JOptionPane.showInputDialog(this, 
                "Montant du paiement (FCFA):", 
                "Enregistrer un paiement", 
                JOptionPane.QUESTION_MESSAGE);
            
            if (montantStr != null && !montantStr.trim().isEmpty()) {
                double montant = Double.parseDouble(montantStr);
                if (creditDAO.enregistrerPaiement(id, BigDecimal.valueOf(montant))) {
                    showSuccessMessage("Paiement enregistré avec succès");
                    chargerCredits();
                }
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Montant invalide");
        } catch (Exception e) {
            showErrorMessage("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Messages d'information
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
        chargerCredits();
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

/**
 * Dialogue pour créer/modifier un crédit - Design moderne
 */
class CreditDialog extends JDialog {
    private Credit credit;
    private boolean saved = false;
    private CreditDAO creditDAO;
    private MembreDAO membreDAO;
    private TontineDAO tontineDAO;
    
    private JComboBox<Membre> cmbMembre;
    private JComboBox<Tontine> cmbTontine;
    private JTextField txtMontant, txtTauxInteret, txtDuree;
    private JComboBox<String> cmbStatut;
    private com.toedter.calendar.JDateChooser dateDebut, dateFin;
    
    // Couleurs
    private static final Color PRIMARY_PURPLE = new Color(139, 92, 246);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BACKGROUND = new Color(248, 250, 252);
    
    public CreditDialog(Frame parent, Credit credit, MembreDAO membreDAO) {
        super(parent, credit == null ? "Nouveau Crédit" : "Modifier Crédit", true);
        this.credit = credit;
        this.membreDAO = membreDAO;
        this.creditDAO = new CreditDAO();
        this.tontineDAO = new TontineDAO();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setSize(600, 680);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(BACKGROUND);
        
        // Titre du dialogue
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Formulaire
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Remplir si modification
        if (credit != null) {
            populateFields();
        }
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));
        
        JLabel iconLabel = new JLabel("💳");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JLabel titleLabel = new JLabel(credit == null ? "Nouveau Crédit" : "Modifier Crédit");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(iconLabel);
        leftPanel.add(titleLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Initialiser les composants
        cmbMembre = createStyledComboBox();
        cmbTontine = createStyledComboBox();
        txtMontant = createStyledTextField();
        txtTauxInteret = createStyledTextField();
        txtDuree = createStyledTextField();
        dateDebut = createStyledDateChooser();
        dateFin = createStyledDateChooser();
        cmbStatut = createStyledComboBox();
        cmbStatut.addItem("en_cours");
        cmbStatut.addItem("rembourse");
        cmbStatut.addItem("en_retard");
        
        // Charger les données
        try {
            List<Membre> membres = membreDAO.findAll();
            for (Membre m : membres) {
                cmbMembre.addItem(m);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement membres: " + e.getMessage());
        }
        
        try {
            List<Tontine> tontines = tontineDAO.findAll();
            for (Tontine t : tontines) {
                cmbTontine.addItem(t);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement tontines: " + e.getMessage());
        }
        
        // Ajouter les champs
        int row = 0;
        addFormField(panel, gbc, row++, "Membre", cmbMembre, true);
        addFormField(panel, gbc, row++, "Tontine", cmbTontine, true);
        addFormField(panel, gbc, row++, "Montant (FCFA)", txtMontant, true);
        addFormField(panel, gbc, row++, "Taux d'intérêt (%)", txtTauxInteret, true);
        addFormField(panel, gbc, row++, "Durée (mois)", txtDuree, true);
        addFormField(panel, gbc, row++, "Date de début", dateDebut, true);
        addFormField(panel, gbc, row++, "Date d'échéance", dateFin, true);
        addFormField(panel, gbc, row++, "Statut", cmbStatut, false);
        
        return panel;
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, 
                              String labelText, JComponent field, boolean required) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        
        JLabel label = new JLabel(labelText + (required ? " *" : ""));
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        panel.add(label, gbc);
        
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(4, 0, 16, 0);
        panel.add(field, gbc);
        gbc.insets = new Insets(10, 0, 10, 0);
    }
    
    private JComboBox createStyledComboBox() {
        JComboBox combo = new JComboBox();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(0, 42));
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return combo;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private com.toedter.calendar.JDateChooser createStyledDateChooser() {
        com.toedter.calendar.JDateChooser chooser = new com.toedter.calendar.JDateChooser();
        chooser.setDateFormatString("dd/MM/yyyy");
        chooser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chooser.setPreferredSize(new Dimension(0, 42));
        chooser.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        return chooser;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JButton btnSave = createDialogButton("💾 Enregistrer", PRIMARY_PURPLE, Color.WHITE);
        JButton btnCancel = createDialogButton("✕ Annuler", new Color(148, 163, 184), Color.WHITE);
        
        btnSave.addActionListener(e -> enregistrer());
        btnCancel.addActionListener(e -> dispose());
        
        panel.add(btnSave);
        panel.add(btnCancel);
        
        return panel;
    }
    
    private JButton createDialogButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 44));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void populateFields() {
        if (credit != null) {
            if (credit.getMembre() != null) {
                for (int i = 0; i < cmbMembre.getItemCount(); i++) {
                    Membre m = cmbMembre.getItemAt(i);
                    if (m.getIdMembre() == credit.getIdMembre()) {
                        cmbMembre.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            if (credit.getTontine() != null) {
                for (int i = 0; i < cmbTontine.getItemCount(); i++) {
                    Tontine t = cmbTontine.getItemAt(i);
                    if (t.getIdTontine() == credit.getIdTontine()) {
                        cmbTontine.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            txtMontant.setText(credit.getMontantEmprunte().toString());
            txtTauxInteret.setText(credit.getTauxInteret().toString());
            txtDuree.setText(String.valueOf(credit.getNombreMois()));
            
            if (credit.getDateEmprunt() != null) {
                dateDebut.setDate(convertToDate(credit.getDateEmprunt()));
            }
            
            if (credit.getDateEcheance() != null) {
                dateFin.setDate(convertToDate(credit.getDateEcheance()));
            }
            
            if (credit.getStatut() != null) {
                cmbStatut.setSelectedItem(credit.getStatut());
            }
        }
    }
    
    private void enregistrer() {
        if (!validateForm()) {
            return;
        }
        
        try {
            if (credit == null) {
                credit = new Credit();
            }
            
            Membre selectedMembre = (Membre) cmbMembre.getSelectedItem();
            if (selectedMembre != null) {
                credit.setIdMembre(selectedMembre.getIdMembre());
                credit.setMembre(selectedMembre);
            }
            
            Tontine selectedTontine = (Tontine) cmbTontine.getSelectedItem();
            if (selectedTontine != null) {
                credit.setIdTontine(selectedTontine.getIdTontine());
                credit.setTontine(selectedTontine);
            }
            
            credit.setMontantEmprunte(new BigDecimal(txtMontant.getText().trim()));
            credit.setTauxInteret(new BigDecimal(txtTauxInteret.getText().trim()));
            credit.setNombreMois(Integer.parseInt(txtDuree.getText().trim()));
            credit.setDateEmprunt(convertToLocalDate(dateDebut.getDate()));
            credit.setDateEcheance(convertToLocalDate(dateFin.getDate()));
            credit.setStatut((String) cmbStatut.getSelectedItem());
            
            if (credit.getMontantRembourse() == null) {
                credit.setMontantRembourse(BigDecimal.ZERO);
            }
            
            boolean success = credit.getIdCredit() == 0 ? 
                creditDAO.create(credit) : creditDAO.update(credit);
                
            if (success) {
                saved = true;
                JOptionPane.showMessageDialog(this, 
                    "Crédit enregistré avec succès", 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'enregistrement", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private boolean validateForm() {
        if (cmbMembre.getSelectedItem() == null) {
            showValidationError("Veuillez sélectionner un membre");
            return false;
        }
        
        if (cmbTontine.getSelectedItem() == null) {
            showValidationError("Veuillez sélectionner une tontine");
            return false;
        }
        
        if (txtMontant.getText().trim().isEmpty()) {
            showValidationError("Le montant est obligatoire");
            txtMontant.requestFocus();
            return false;
        }
        
        try {
            double montant = Double.parseDouble(txtMontant.getText().trim());
            if (montant <= 0) {
                showValidationError("Le montant doit être supérieur à 0");
                txtMontant.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Le montant doit être un nombre valide");
            txtMontant.requestFocus();
            return false;
        }
        
        if (txtTauxInteret.getText().trim().isEmpty()) {
            showValidationError("Le taux d'intérêt est obligatoire");
            txtTauxInteret.requestFocus();
            return false;
        }
        
        if (txtDuree.getText().trim().isEmpty()) {
            showValidationError("La durée est obligatoire");
            txtDuree.requestFocus();
            return false;
        }
        
        if (dateDebut.getDate() == null) {
            showValidationError("La date de début est obligatoire");
            return false;
        }
        
        if (dateFin.getDate() == null) {
            showValidationError("La date d'échéance est obligatoire");
            return false;
        }
        
        if (dateDebut.getDate() != null && dateFin.getDate() != null) {
            if (dateFin.getDate().before(dateDebut.getDate())) {
                showValidationError("La date d'échéance doit être après la date de début");
                return false;
            }
        }
        
        return true;
    }
    
    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation", JOptionPane.WARNING_MESSAGE);
    }
    
    private java.util.Date convertToDate(LocalDate localDate) {
        if (localDate == null) return null;
        return java.util.Date.from(localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
    }
    
    private LocalDate convertToLocalDate(java.util.Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }
    
    public boolean isSaved() {
        return saved;
    }
}