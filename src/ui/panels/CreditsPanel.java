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
import utils.ThemeColors;
import utils.UIUtils;

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
        setBackground(ThemeColors.BACKGROUND);
        
        // Container principal
        JPanel mainContainer = new JPanel(new BorderLayout(0, 24));
        mainContainer.setBackground(ThemeColors.BACKGROUND);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        
        // En-tête moderne
        JPanel headerPanel = createModernHeader();
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // Corps avec statistiques et tableau
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 24));
        bodyPanel.setBackground(ThemeColors.BACKGROUND);
        
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
        header.setBackground(ThemeColors.CARD_BG);
        header.setBorder(new UIUtils.RoundedBorder(20, new Color(226, 232, 240), 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        
        // Section gauche - Titre
        JPanel leftSection = new JPanel();
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));
        leftSection.setBackground(ThemeColors.CARD_BG);
        
        JLabel iconLabel = new JLabel("💳");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblTitre = new JLabel("Gestion des Crédits");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitre.setForeground(ThemeColors.TEXT_PRIMARY);
        lblTitre.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Suivi des emprunts et remboursements");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftSection.add(iconLabel);
        leftSection.add(Box.createVerticalStrut(8));
        leftSection.add(lblTitre);
        leftSection.add(Box.createVerticalStrut(4));
        leftSection.add(subtitleLabel);
        
        // Section droite - Filtres
        JPanel rightSection = new JPanel();
        rightSection.setLayout(new BoxLayout(rightSection, BoxLayout.Y_AXIS));
        rightSection.setBackground(ThemeColors.CARD_BG);
        
        JLabel filterLabel = new JLabel("Filtrer par membre");
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        filterLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        cmbFiltreMembre = new JComboBox<>();
        cmbFiltreMembre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFiltreMembre.setPreferredSize(new Dimension(250, 40));
        cmbFiltreMembre.setMaximumSize(new Dimension(250, 40));
        cmbFiltreMembre.setBackground(ThemeColors.CARD_BG);
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
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // Carte Total Crédits
        JPanel cardTotal = createMiniStatCard("Total Crédits", "0", "📊", ThemeColors.PRIMARY_PURPLE);
        lblTotalCredits = (JLabel) ((JPanel)((JPanel)((JPanel)cardTotal.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Montant Total
        JPanel cardMontant = createMiniStatCard("Montant Total", "0 FCFA", "💰", ThemeColors.PRIMARY_AMBER);
        lblMontantTotal = (JLabel) ((JPanel)((JPanel)((JPanel)cardMontant.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Crédits Actifs
        JPanel cardActifs = createMiniStatCard("En Cours", "0", "⚡", ThemeColors.PRIMARY_EMERALD);
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
        card.setBackground(ThemeColors.CARD_BG);
        card.setBorder(new UIUtils.RoundedBorder(12, new Color(226, 232, 240), 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Icône et valeur
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ThemeColors.CARD_BG);
        
        JLabel iconLabel = new JLabel(icone);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(color);
        
        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.setBackground(ThemeColors.CARD_BG);
        
        JLabel valueLabel = new JLabel(valeur);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(titre);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        valuePanel.add(valueLabel);
        valuePanel.add(Box.createVerticalStrut(4));
        valuePanel.add(titleLabel);
        
        contentPanel.add(iconLabel, BorderLayout.WEST);
        contentPanel.add(Box.createHorizontalStrut(12), BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(ThemeColors.CARD_BG);
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
        panel.setBackground(ThemeColors.CARD_BG);
        panel.setBorder(new UIUtils.RoundedBorder(16, new Color(226, 232, 240), 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Créer le tableau
        createModernTable();
        
        JScrollPane scrollPane = new JScrollPane(tableCredits);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeColors.CARD_BG);
        scrollPane.setBackground(ThemeColors.CARD_BG);
        
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
        tableCredits.setBackground(ThemeColors.CARD_BG);
        tableCredits.setSelectionBackground(new Color(237, 233, 254));
        tableCredits.setSelectionForeground(ThemeColors.TEXT_PRIMARY);
        
        // Style du header
        JTableHeader header = tableCredits.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(ThemeColors.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        
        // Renderer personnalisé pour les cellules
        tableCredits.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? ThemeColors.CARD_BG : new Color(248, 250, 252));
                }
                
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                
                // Coloration du statut
                if (column == 5 && value != null) {
                    String statut = value.toString();
                    if (statut.equals("en_cours")) {
                        setForeground(ThemeColors.PRIMARY_BLUE);
                    } else if (statut.equals("rembourse")) {
                        setForeground(ThemeColors.PRIMARY_EMERALD);
                    } else if (statut.equals("en_retard")) {
                        setForeground(ThemeColors.PRIMARY_RED);
                    }
                } else if (!isSelected) {
                    setForeground(ThemeColors.TEXT_PRIMARY);
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
        panel.setBackground(ThemeColors.BACKGROUND);
        
        JButton btnNouveau = createModernButton("➕ Nouveau Crédit", ThemeColors.PRIMARY_PURPLE, new Color(237, 233, 254));
        JButton btnModifier = createModernButton("✏️ Modifier", ThemeColors.PRIMARY_BLUE, new Color(219, 234, 254));
        JButton btnPaiement = createModernButton("💰 Enregistrer Paiement", ThemeColors.PRIMARY_EMERALD, new Color(209, 250, 229));
        JButton btnSupprimer = createModernButton("🗑️ Supprimer", ThemeColors.PRIMARY_RED, new Color(254, 226, 226));
        JButton btnRafraichir = createModernButton("🔄 Actualiser", ThemeColors.TEXT_SECONDARY, new Color(241, 245, 249));
        
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
            Credit credit = creditDAO.findById(id);
            
            if (credit == null) {
                showErrorMessage("Crédit introuvable");
                return;
            }
            
            BigDecimal resteAPayer = credit.getResteARembourser();
            
            String message = "Montant du paiement (FCFA):\n" +
                           "Reste à payer: " + String.format("%.0f FCFA", resteAPayer.doubleValue()) + "\n" +
                           "Montant maximum autorisé: " + String.format("%.0f FCFA", resteAPayer.doubleValue());
            
            String montantStr = JOptionPane.showInputDialog(this, 
                message, 
                "Enregistrer un paiement", 
                JOptionPane.QUESTION_MESSAGE);
            
            if (montantStr != null && !montantStr.trim().isEmpty()) {
                double montant = Double.parseDouble(montantStr);
                
                if (montant <= 0) {
                    showErrorMessage("Le montant doit être supérieur à 0");
                    return;
                }
                
                BigDecimal paiement = BigDecimal.valueOf(montant);
                
                // Vérifier que le paiement ne dépasse pas le reste à payer
                if (paiement.compareTo(resteAPayer) > 0) {
                    showErrorMessage("Le paiement ne peut pas dépasser le reste à payer de " + 
                                   String.format("%.0f FCFA", resteAPayer.doubleValue()));
                    return;
                }
                
                if (creditDAO.enregistrerPaiement(id, paiement)) {
                    showSuccessMessage("Paiement enregistré avec succès");
                    chargerCredits();
                } else {
                    showErrorMessage("Erreur lors de l'enregistrement du paiement");
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
    
    // Nouveaux composants pour l'UX améliorée
    private JLabel lblMontantTotal, lblMensualite, lblTotalInteret;
    private JPanel previewPanel;
    private JLabel lblValidationMessage;
    
    // Couleurs
    private static final Color DIALOG_PURPLE = new Color(139, 92, 246);
    private static final Color SUCCESS_GREEN = new Color(16, 185, 129);
    private static final Color ERROR_RED = new Color(239, 68, 68);
    private static final Color WARNING_AMBER = new Color(251, 146, 60);
    private static final Color DIALOG_TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color DIALOG_TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color DIALOG_BACKGROUND = new Color(248, 250, 252);
    private static final Color DIALOG_CARD_BG = new Color(255, 255, 255);
    
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
        getContentPane().setBackground(ThemeColors.BACKGROUND);
        
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
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(iconLabel);
        leftPanel.add(titleLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // Panel principal avec formulaire et aperçu
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ThemeColors.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Panel formulaire à gauche
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ThemeColors.BACKGROUND);
        
        // Initialiser les composants
        initializeComponents();
        
        // Ajouter les champs du formulaire
        int row = 0;
        addFormField(formPanel, gbc, row++, "Membre", cmbMembre, true, "Sélectionnez le membre bénéficiaire du crédit");
        addFormField(formPanel, gbc, row++, "Tontine", cmbTontine, true, "Choisissez la tontine concernée");
        addFormField(formPanel, gbc, row++, "Montant (FCFA)", txtMontant, true, "Montant emprunté sans les intérêts");
        addFormField(formPanel, gbc, row++, "Taux d'intérêt (%)", txtTauxInteret, true, "Taux d'intérêt annuel appliqué");
        addFormField(formPanel, gbc, row++, "Durée (mois)", txtDuree, true, "Nombre de mois pour le remboursement");
        addFormField(formPanel, gbc, row++, "Date de début", dateDebut, true, "Date de début du crédit");
        addFormField(formPanel, gbc, row++, "Date d'échéance", dateFin, true, "Date de fin de remboursement");
        addFormField(formPanel, gbc, row++, "Statut", cmbStatut, false, "État actuel du crédit");
        
        // Panel d'aperçu à droite
        previewPanel = createPreviewPanel();
        
        // Assembler le layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(formPanel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(10, 20, 10, 0);
        mainPanel.add(previewPanel, gbc);
        
        panel.add(mainPanel, BorderLayout.CENTER);
        
        // Panel de messages de validation
        lblValidationMessage = new JLabel(" ");
        lblValidationMessage.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblValidationMessage.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        lblValidationMessage.setVisible(false);
        panel.add(lblValidationMessage, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Initialise tous les composants du formulaire
     */
    private void initializeComponents() {
        // Initialiser les composants principaux
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
        loadComboBoxData();
        
        // Ajouter les listeners pour la validation en temps réel
        addRealTimeValidation();
    }
    
    /**
     * Charge les données dans les combobox
     */
    private void loadComboBoxData() {
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
    }
    
    /**
     * Ajoute les listeners pour la validation en temps réel
     */
    private void addRealTimeValidation() {
        // Listener pour le montant
        txtMontant.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        
        // Listener pour le taux d'intérêt
        txtTauxInteret.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        
        // Listener pour la durée
        txtDuree.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        
        // Listener pour la date de début
        dateDebut.getDateEditor().addPropertyChangeListener("date", e -> {
            if (dateDebut.getDate() != null) {
                calculateEndDate();
                updatePreview();
            }
        });
    }
    
    /**
     * Crée le panneau d'aperçu des calculs
     */
    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Titre
        JLabel title = new JLabel("💰 Aperçu du crédit");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(ThemeColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);
        
        // Détails du calcul
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(ThemeColors.CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 8, 0);
        
        // Montant total
        addPreviewRow(detailsPanel, gbc, 0, "Montant emprunté:", "0 FCFA", ThemeColors.TEXT_PRIMARY);
        lblMontantTotal = (JLabel) ((JPanel)detailsPanel.getComponent(0)).getComponent(1);
        
        // Total intérêts
        addPreviewRow(detailsPanel, gbc, 1, "Intérêts totaux:", "0 FCFA", WARNING_AMBER);
        lblTotalInteret = (JLabel) ((JPanel)detailsPanel.getComponent(1)).getComponent(1);
        
        // Montant total avec intérêts
        addPreviewRow(detailsPanel, gbc, 2, "Montant total:", "0 FCFA", ThemeColors.PRIMARY_PURPLE);
        
        // Mensualité
        addPreviewRow(detailsPanel, gbc, 3, "Mensualité:", "0 FCFA", SUCCESS_GREEN);
        lblMensualite = (JLabel) ((JPanel)detailsPanel.getComponent(3)).getComponent(1);
        
        panel.add(detailsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Ajoute une ligne d'aperçu
     */
    private void addPreviewRow(JPanel parent, GridBagConstraints gbc, int row, String label, String value, Color valueColor) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBackground(ThemeColors.CARD_BG);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(ThemeColors.TEXT_SECONDARY);
        
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 14));
        val.setForeground(valueColor);
        val.setHorizontalAlignment(JLabel.RIGHT);
        
        rowPanel.add(lbl, BorderLayout.WEST);
        rowPanel.add(val, BorderLayout.EAST);
        
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        parent.add(rowPanel, gbc);
    }
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, 
                              String labelText, JComponent field, boolean required, String helpText) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        
        // Panel pour le label et l'aide
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setBackground(ThemeColors.BACKGROUND);
        
        JLabel label = new JLabel(labelText + (required ? " *" : ""));
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(ThemeColors.TEXT_PRIMARY);
        labelPanel.add(label, BorderLayout.NORTH);
        
        if (helpText != null && !helpText.isEmpty()) {
            JLabel help = new JLabel(helpText);
            help.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            help.setForeground(ThemeColors.TEXT_SECONDARY);
            help.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
            labelPanel.add(help, BorderLayout.CENTER);
        }
        
        panel.add(labelPanel, gbc);
        
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(4, 0, 16, 0);
        panel.add(field, gbc);
        gbc.insets = new Insets(10, 0, 10, 0);
    }
    
    /**
     * Met à jour l'aperçu des calculs en temps réel
     */
    private void updatePreview() {
        try {
            double montant = getDoubleValue(txtMontant.getText());
            double taux = getDoubleValue(txtTauxInteret.getText());
            int duree = getIntValue(txtDuree.getText());
            
            if (montant > 0 && taux >= 0 && duree > 0) {
                // Calculs
                double totalInterets = montant * (taux / 100) * (duree / 12.0);
                double montantTotal = montant + totalInterets;
                double mensualite = montantTotal / duree;
                
                // Mise à jour des labels
                if (lblMontantTotal != null) {
                    lblMontantTotal.setText(String.format("%.0f FCFA", montant));
                }
                if (lblTotalInteret != null) {
                    lblTotalInteret.setText(String.format("%.0f FCFA", totalInterets));
                }
                if (lblMensualite != null) {
                    lblMensualite.setText(String.format("%.0f FCFA", mensualite));
                }
                
                // Validation en temps réel
                showValidationMessage("Calculs mis à jour automatiquement", SUCCESS_GREEN);
            } else {
                resetPreview();
            }
        } catch (Exception e) {
            showValidationMessage("Vérifiez les valeurs saisies", ERROR_RED);
        }
    }
    
    /**
     * Calcule automatiquement la date d'échéance
     */
    private void calculateEndDate() {
        try {
            int duree = getIntValue(txtDuree.getText());
            if (duree > 0 && dateDebut.getDate() != null) {
                LocalDate debut = convertToLocalDate(dateDebut.getDate());
                LocalDate fin = debut.plusMonths(duree);
                dateFin.setDate(convertToDate(fin));
            }
        } catch (Exception e) {
            // Ignorer les erreurs de calcul
        }
    }
    
    /**
     * Réinitialise l'aperçu
     */
    private void resetPreview() {
        if (lblMontantTotal != null) lblMontantTotal.setText("0 FCFA");
        if (lblTotalInteret != null) lblTotalInteret.setText("0 FCFA");
        if (lblMensualite != null) lblMensualite.setText("0 FCFA");
    }
    
    /**
     * Affiche un message de validation
     */
    private void showValidationMessage(String message, Color color) {
        if (lblValidationMessage != null) {
            lblValidationMessage.setText(message);
            lblValidationMessage.setForeground(color);
            lblValidationMessage.setVisible(true);
            
            // Cacher le message après 3 secondes
            javax.swing.Timer timer = new javax.swing.Timer(3000, e -> {
                lblValidationMessage.setVisible(false);
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    /**
     * Extrait une valeur double d'un champ texte
     */
    private double getDoubleValue(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        try {
            return Double.parseDouble(text.trim().replaceAll("[^0-9.,]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Extrait une valeur entière d'un champ texte
     */
    private int getIntValue(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(text.trim().replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
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
        
        JButton btnSave = createDialogButton("💾 Enregistrer", ThemeColors.PRIMARY_PURPLE, Color.WHITE);
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