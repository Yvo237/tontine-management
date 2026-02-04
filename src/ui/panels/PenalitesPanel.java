package ui.panels;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.MembreDAO;
import dao.PenaliteDAO;
import dao.SeanceDAO;
import models.Membre;
import models.Penalite;
import models.Seance;
import ui.MainFrame;
import utils.ThemeColors;
import utils.UIUtils;

public class PenalitesPanel extends JPanel {
    
    private MainFrame mainFrame;
    private PenaliteDAO penaliteDAO;
    private MembreDAO membreDAO;
    private SeanceDAO seanceDAO;
    
    private JTable penalitesTable;
    private DefaultTableModel tableModel;
    private JComboBox<Membre> membreComboBox;
    private JComboBox<Seance> seanceComboBox;
    private JTextField motifField;
    private JTextField montantField;
    private JButton ajouterButton;
    private JButton modifierButton;
    private JButton supprimerButton;
    private JButton marquerPayeeButton;
    private JButton rafraichirButton;
    
    // Labels pour les statistiques
    private JLabel lblTotalPenalites;
    private JLabel lblPenalitesPayees;
    private JLabel lblPenalitesImpayees;
    private JLabel lblMontantTotal;
    
    // Références aux cartes pour pouvoir mettre à jour les labels
    private JPanel cardTotal;
    private JPanel cardPayees;
    private JPanel cardImpayees;
    private JPanel cardMontant;
    
    // Utilisation des couleurs du thème
    private static final Color PRIMARY_COLOR = ThemeColors.PRIMARY_PURPLE;
    private static final Color SUCCESS_COLOR = ThemeColors.PRIMARY_EMERALD;
    private static final Color DANGER_COLOR = ThemeColors.PRIMARY_RED;
    private static final Color WARNING_COLOR = ThemeColors.PRIMARY_AMBER;
    
    public PenalitesPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.penaliteDAO = new PenaliteDAO();
        this.membreDAO = new MembreDAO();
        this.seanceDAO = new SeanceDAO();
        
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        loadPenalites();
        updateStatistics();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(ThemeColors.BACKGROUND);
        
        // Container principal
        JPanel mainContainer = new JPanel(new BorderLayout(0, 24));
        mainContainer.setBackground(ThemeColors.BACKGROUND);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        
        // En-tête moderne
        JPanel headerPanel = createModernHeader();
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 24));
        bodyPanel.setBackground(ThemeColors.BACKGROUND);
        
        JPanel statsPanel = createStatsPanel();
        bodyPanel.add(statsPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = createMainContentPanel();
        bodyPanel.add(contentPanel, BorderLayout.CENTER);
        
        mainContainer.add(bodyPanel, BorderLayout.CENTER);
        add(mainContainer);
    }
    
    private JPanel createModernHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeColors.BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        
        // Titre principal
        JLabel titleLabel = new JLabel("Gestion des Pénalités");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        
        // Description
        JLabel descLabel = new JLabel("Gérez les pénalités et suivez leurs statuts de paiement");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 8));
        titlePanel.setBackground(ThemeColors.BACKGROUND);
        titlePanel.add(titleLabel);
        titlePanel.add(descLabel);
        
        header.add(titlePanel, BorderLayout.WEST);
        
        // Bouton rafraîchir à droite
        rafraichirButton = createButton("🔄 Rafraîchir", ThemeColors.TEXT_SECONDARY);
        header.add(rafraichirButton, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setBackground(ThemeColors.BACKGROUND);
        
        // Statistique 1: Total pénalités
        cardTotal = createStatCard("⚠️ Total pénalités", "0", ThemeColors.PRIMARY_RED);
        lblTotalPenalites = (JLabel) cardTotal.getComponent(1); // Le label value est en position CENTER (index 1)
        statsPanel.add(cardTotal);
        
        // Statistique 2: Payées
        cardPayees = createStatCard("✅ Payées", "0", ThemeColors.PRIMARY_EMERALD);
        lblPenalitesPayees = (JLabel) cardPayees.getComponent(1);
        statsPanel.add(cardPayees);
        
        // Statistique 3: Impayées
        cardImpayees = createStatCard("⏳ Impayées", "0", ThemeColors.PRIMARY_AMBER);
        lblPenalitesImpayees = (JLabel) cardImpayees.getComponent(1);
        statsPanel.add(cardImpayees);
        
        // Statistique 4: Montant total
        cardMontant = createStatCard("💰 Montant total", "0 FCFA", ThemeColors.PRIMARY_PURPLE);
        lblMontantTotal = (JLabel) cardMontant.getComponent(1);
        statsPanel.add(cardMontant);
        
        return statsPanel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(8, 4));
        card.setBackground(ThemeColors.CARD_BG);
        card.setBorder(new UIUtils.RoundedBorder(8, ThemeColors.BORDER_COLOR, 1));
        card.setBorder(BorderFactory.createCompoundBorder(
            new UIUtils.RoundedBorder(8, ThemeColors.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    private JPanel createMainContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(0, 24));
        contentPanel.setBackground(ThemeColors.BACKGROUND);
        
        JPanel formPanel = createFormPanel();
        contentPanel.add(formPanel, BorderLayout.WEST);
        
        JPanel tablesPanel = createTablesPanel();
        contentPanel.add(tablesPanel, BorderLayout.CENTER);
        
        JPanel buttonsPanel = createButtonsPanel();
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return contentPanel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 20),
                BorderFactory.createCompoundBorder(
                        new UIUtils.RoundedBorder(10, ThemeColors.BORDER_COLOR, 1),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                )
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 15, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel formTitle = new JLabel("Ajouter une pénalité");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(formTitle, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(createLabel("Membre:"), gbc);
        gbc.gridx = 1;
        membreComboBox = new JComboBox<>();
        membreComboBox.setPreferredSize(new Dimension(200, 30));
        panel.add(membreComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("Séance (optionnel):"), gbc);
        gbc.gridx = 1;
        seanceComboBox = new JComboBox<>();
        seanceComboBox.setPreferredSize(new Dimension(200, 30));
        panel.add(seanceComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createLabel("Motif:"), gbc);
        gbc.gridx = 1;
        motifField = new JTextField();
        motifField.setPreferredSize(new Dimension(200, 30));
        panel.add(motifField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createLabel("Montant (FCFA):"), gbc);
        gbc.gridx = 1;
        montantField = new JTextField();
        montantField.setPreferredSize(new Dimension(200, 30));
        panel.add(montantField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.CENTER;
        ajouterButton = createButton("Ajouter la pénalité", SUCCESS_COLOR);
        panel.add(ajouterButton, gbc);
        
        return panel;
    }
    
    private JPanel createTablesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        
        String[] columns = {"ID", "Membre", "Séance", "Motif", "Montant", "Date", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        penalitesTable = new JTable(tableModel);
        penalitesTable.setRowHeight(30);
        penalitesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        penalitesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        styleTable(penalitesTable);
        
        JScrollPane scrollPane = new JScrollPane(penalitesTable);
        scrollPane.setBorder(new UIUtils.RoundedBorder(10, ThemeColors.BORDER_COLOR, 1));
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setBackground(ThemeColors.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        modifierButton = createButton("✏️ Modifier", WARNING_COLOR);
        supprimerButton = createButton("🗑️ Supprimer", DANGER_COLOR);
        marquerPayeeButton = createButton("✅ Marquer comme payée", SUCCESS_COLOR);
        
        panel.add(modifierButton);
        panel.add(supprimerButton);
        panel.add(marquerPayeeButton);
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(PRIMARY_COLOR);
        return label;
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(color);
        button.setBackground(getLightColor(color));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(170, 44));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(adjustBrightness(getLightColor(color), 0.95f));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(getLightColor(color));
            }
        });
        
        return button;
    }
    
    private Color getLightColor(Color color) {
        // Retourne une version claire de la couleur pour le fond
        if (color.equals(ThemeColors.PRIMARY_PURPLE)) {
            return new Color(237, 233, 254);
        } else if (color.equals(ThemeColors.PRIMARY_BLUE)) {
            return new Color(219, 234, 254);
        } else if (color.equals(ThemeColors.PRIMARY_EMERALD)) {
            return new Color(220, 252, 231);
        } else if (color.equals(ThemeColors.PRIMARY_RED)) {
            return new Color(254, 226, 226);
        } else if (color.equals(ThemeColors.PRIMARY_AMBER)) {
            return new Color(254, 243, 199);
        } else {
            return new Color(241, 245, 249);
        }
    }
    
    private Color adjustBrightness(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * factor));
        int g = Math.min(255, (int)(color.getGreen() * factor));
        int b = Math.min(255, (int)(color.getBlue() * factor));
        return new Color(r, g, b);
    }
    
    private void styleTable(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(ThemeColors.TEXT_SECONDARY);
        header.setReorderingAllowed(false);
        
        // Renderer personnalisé pour le statut
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Boolean) {
                    Boolean payee = (Boolean) value;
                    setText(payee ? "Payée" : "Impayée");
                    setForeground(payee ? SUCCESS_COLOR : DANGER_COLOR);
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                return c;
            }
        });
    }
    
    private void layoutComponents() {
        // Layout déjà géré dans initializeComponents()
    }
    
    private void setupEventHandlers() {
        ajouterButton.addActionListener(e -> ajouterPenalite());
        modifierButton.addActionListener(e -> modifierPenalite());
        supprimerButton.addActionListener(e -> supprimerPenalite());
        marquerPayeeButton.addActionListener(e -> marquerCommePayee());
        rafraichirButton.addActionListener(e -> rafraichir());
    }
    
    private void loadPenalites() {
        // Charger les membres
        membreComboBox.removeAllItems();
        List<Membre> membres = membreDAO.findAll();
        for (Membre membre : membres) {
            membreComboBox.addItem(membre);
        }
        
        // Charger les séances
        seanceComboBox.removeAllItems();
        seanceComboBox.addItem(null); // Option pour aucune séance
        List<Seance> seances = seanceDAO.findAll();
        for (Seance seance : seances) {
            seanceComboBox.addItem(seance);
        }
        
        // Charger les pénalités
        tableModel.setRowCount(0);
        List<Penalite> penalites = penaliteDAO.findAll();
        
        for (Penalite penalite : penalites) {
            Object[] row = {
                penalite.getIdPenalite(),
                penalite.getMembre() != null ? penalite.getMembre().getNom() + " " + penalite.getMembre().getPrenom() : "N/A",
                penalite.getSeance() != null ? "Séance " + penalite.getSeance().getIdSeance() : "Aucune",
                penalite.getMotif(),
                penalite.getMontant() + " FCFA",
                penalite.getDatePenalite().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                penalite.isPayee()
            };
            tableModel.addRow(row);
        }
    }
    
    private void ajouterPenalite() {
        try {
            Membre selectedMembre = (Membre) membreComboBox.getSelectedItem();
            Seance selectedSeance = (Seance) seanceComboBox.getSelectedItem();
            String motif = motifField.getText().trim();
            String montantText = montantField.getText().trim();
            
            if (selectedMembre == null) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un membre", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (motif.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez saisir un motif", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (montantText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez saisir un montant", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            BigDecimal montant = new BigDecimal(montantText);
            if (montant.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Le montant doit être positif", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Penalite penalite = new Penalite(
                selectedMembre.getIdMembre(),
                selectedSeance != null ? selectedSeance.getIdSeance() : null,
                motif,
                montant,
                LocalDate.now()
            );
            
            if (penaliteDAO.create(penalite)) {
                JOptionPane.showMessageDialog(this, "Pénalité ajoutée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadPenalites();
                updateStatistics();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la pénalité", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un montant valide", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void modifierPenalite() {
        int selectedRow = penalitesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une pénalité à modifier", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int idPenalite = (int) tableModel.getValueAt(selectedRow, 0);
        String membreNom = (String) tableModel.getValueAt(selectedRow, 1);
        String motif = (String) tableModel.getValueAt(selectedRow, 3);
        String montantStr = (String) tableModel.getValueAt(selectedRow, 4);
        
        String montantClean = montantStr.replace(" FCFA", "");
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(ThemeColors.BACKGROUND);
        
        panel.add(new JLabel("Membre:"));
        JLabel lblMembre = new JLabel(membreNom);
        lblMembre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lblMembre);
        
        panel.add(new JLabel("Motif:"));
        JTextField txtMotif = new JTextField(motif);
        panel.add(txtMotif);
        
        panel.add(new JLabel("Montant (FCFA):"));
        JTextField txtMontant = new JTextField(montantClean);
        panel.add(txtMontant);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Modifier la pénalité",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String nouveauMotif = txtMotif.getText().trim();
                String nouveauMontantStr = txtMontant.getText().trim();
                
                if (nouveauMotif.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veuillez saisir un motif", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (nouveauMontantStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veuillez saisir un montant", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                BigDecimal nouveauMontant = new BigDecimal(nouveauMontantStr);
                if (nouveauMontant.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "Le montant doit être positif", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Penalite penaliteExistante = penaliteDAO.findById(idPenalite);
                if (penaliteExistante != null) {
                    penaliteExistante.setMotif(nouveauMotif);
                    penaliteExistante.setMontant(nouveauMontant);
                    
                    if (penaliteDAO.update(penaliteExistante)) {
                        JOptionPane.showMessageDialog(this, "Pénalité modifiée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        loadPenalites();
                        updateStatistics();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la modification de la pénalité", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Pénalité introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Veuillez saisir un montant valide", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void supprimerPenalite() {
        int selectedRow = penalitesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une pénalité à supprimer", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int idPenalite = (int) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Êtes-vous sûr de vouloir supprimer cette pénalité ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (penaliteDAO.delete(idPenalite)) {
                JOptionPane.showMessageDialog(this, "Pénalité supprimée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                loadPenalites();
                updateStatistics();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la pénalité", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void marquerCommePayee() {
        int selectedRow = penalitesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une pénalité", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int idPenalite = (int) tableModel.getValueAt(selectedRow, 0);
        boolean isPayee = (Boolean) tableModel.getValueAt(selectedRow, 6);
        
        if (isPayee) {
            JOptionPane.showMessageDialog(this, "Cette pénalité est déjà payée", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (penaliteDAO.marquerCommePayee(idPenalite)) {
            JOptionPane.showMessageDialog(this, "Pénalité marquée comme payée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
            loadPenalites();
            updateStatistics();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors du marquage de la pénalité", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        motifField.setText("");
        montantField.setText("");
        membreComboBox.setSelectedIndex(0);
        seanceComboBox.setSelectedIndex(0);
    }
    
    public void rafraichir() {
        loadPenalites();
        updateStatistics();
    }
    
    private void updateStatistics() {
        try {
            System.out.println("[STATS DEBUG] Début mise à jour statistiques");
            
            // Vérifier si les labels sont initialisés
            if (lblTotalPenalites == null) {
                System.err.println("[STATS DEBUG] lblTotalPenalites est null!");
            }
            if (lblPenalitesPayees == null) {
                System.err.println("[STATS DEBUG] lblPenalitesPayees est null!");
            }
            if (lblPenalitesImpayees == null) {
                System.err.println("[STATS DEBUG] lblPenalitesImpayees est null!");
            }
            if (lblMontantTotal == null) {
                System.err.println("[STATS DEBUG] lblMontantTotal est null!");
            }
            
            List<Penalite> penalites = penaliteDAO.findAll();
            System.out.println("[STATS DEBUG] Nombre de pénalités trouvées: " + penalites.size());
            
            // Calculer les statistiques
            int total = penalites.size();
            int payees = (int) penalites.stream().filter(Penalite::isPayee).count();
            int impayees = total - payees;
            
            BigDecimal montantTotal = penalites.stream()
                .map(Penalite::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            System.out.println("[STATS DEBUG] Total: " + total + ", Payées: " + payees + ", Impayées: " + impayees + ", Montant: " + montantTotal);
            
            // Mettre à jour les labels
            if (lblTotalPenalites != null) {
                lblTotalPenalites.setText(String.valueOf(total));
                System.out.println("[STATS DEBUG] lblTotalPenalites mis à jour: " + total);
            }
            if (lblPenalitesPayees != null) {
                lblPenalitesPayees.setText(String.valueOf(payees));
                System.out.println("[STATS DEBUG] lblPenalitesPayees mis à jour: " + payees);
            }
            if (lblPenalitesImpayees != null) {
                lblPenalitesImpayees.setText(String.valueOf(impayees));
                System.out.println("[STATS DEBUG] lblPenalitesImpayees mis à jour: " + impayees);
            }
            if (lblMontantTotal != null) {
                lblMontantTotal.setText(String.format("%,.0f FCFA", montantTotal));
                System.out.println("[STATS DEBUG] lblMontantTotal mis à jour: " + montantTotal);
            }
            
        } catch (Exception e) {
            System.err.println("Erreur mise à jour statistiques: " + e.getMessage());
            e.printStackTrace();
            // En cas d'erreur, afficher des zéros
            if (lblTotalPenalites != null) lblTotalPenalites.setText("0");
            if (lblPenalitesPayees != null) lblPenalitesPayees.setText("0");
            if (lblPenalitesImpayees != null) lblPenalitesImpayees.setText("0");
            if (lblMontantTotal != null) lblMontantTotal.setText("0 FCFA");
        }
    }
}
