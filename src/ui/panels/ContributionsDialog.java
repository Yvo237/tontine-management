package ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.ContributionFIACDAO;
import dao.MembreDAO;
import dao.ProjetFIACDAO;
import models.ContributionFIAC;
import models.Membre;
import models.ProjetFIAC;
import utils.ThemeColors;
import utils.UIUtils;

public class ContributionsDialog extends JDialog {
    private ProjetFIAC projet;
    private boolean saved = false;
    private ContributionFIACDAO contributionDAO;
    private MembreDAO membreDAO;
    private ProjetFIACDAO projetDAO;
    
    private JTable tableContributions;
    private DefaultTableModel tableModel;
    private JLabel lblTitre;
    private JLabel lblTotalCollecte;
    private JLabel lblNbContributions;
    private JTextField txtMontant;
    private JComboBox<String> cmbMembre;
    
    public ContributionsDialog(Frame parent, ProjetFIAC projet) {
        super(parent, "Gestion des Contributions - " + projet.getNomProjet(), true);
        this.projet = projet;
        this.contributionDAO = new ContributionFIACDAO();
        this.membreDAO = new MembreDAO();
        this.projetDAO = new ProjetFIACDAO();
        initComponents();
        chargerContributions();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(ThemeColors.BACKGROUND);
        
        // Titre du dialogue
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Corps avec statistiques et tableau
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 24));
        bodyPanel.setBackground(ThemeColors.BACKGROUND);
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        
        // Statistiques
        JPanel statsPanel = createStatsPanel();
        bodyPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Panneau contribution
        JPanel contributionPanel = createContributionPanel();
        bodyPanel.add(contributionPanel, BorderLayout.CENTER);
        
        add(bodyPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColors.BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));
        
        JLabel iconLabel = new JLabel("💰");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        lblTitre = new JLabel("Contributions au projet: " + projet.getNomProjet());
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitre.setForeground(ThemeColors.TEXT_PRIMARY);
        lblTitre.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(iconLabel);
        leftPanel.add(lblTitre);
        
        panel.add(leftPanel, BorderLayout.WEST);
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // Carte Total Collecté
        JPanel cardTotal = createStatCard("Total Collecté", "0 FCFA", "💵", ThemeColors.PRIMARY_EMERALD);
        lblTotalCollecte = (JLabel) ((JPanel)((JPanel)((JPanel)cardTotal.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Nombre de Contributions
        JPanel cardNbContrib = createStatCard("Contributions", "0", "👥", ThemeColors.PRIMARY_BLUE);
        lblNbContributions = (JLabel) ((JPanel)((JPanel)((JPanel)cardNbContrib.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        panel.add(cardTotal);
        panel.add(cardNbContrib);
        
        return panel;
    }
    
    private JPanel createStatCard(String titre, String valeur, String icone, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ThemeColors.CARD_BG);
        card.setBorder(new UIUtils.RoundedBorder(12, ThemeColors.BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ThemeColors.CARD_BG);
        
        JLabel iconLabel = new JLabel(icone);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setForeground(color);
        
        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.setBackground(ThemeColors.CARD_BG);
        
        JLabel valueLabel = new JLabel(valeur);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(titre);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        valuePanel.add(valueLabel);
        valuePanel.add(Box.createVerticalStrut(2));
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
    
    private JPanel createContributionPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(ThemeColors.CARD_BG);
        panel.setBorder(new UIUtils.RoundedBorder(12, ThemeColors.BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        // Formulaire d'ajout
        JPanel formPanel = createFormPanel();
        innerPanel.add(formPanel, BorderLayout.NORTH);
        
        // Tableau des contributions
        createContributionTable();
        JScrollPane scrollPane = new JScrollPane(tableContributions);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeColors.CARD_BG);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        innerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(innerPanel);
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setBackground(ThemeColors.CARD_BG);
        
        // Membre
        JLabel lblMembre = new JLabel("Membre:");
        lblMembre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMembre.setForeground(ThemeColors.TEXT_PRIMARY);
        
        cmbMembre = new JComboBox<>();
        cmbMembre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbMembre.setPreferredSize(new Dimension(200, 32));
        chargerMembresDansCombo();
        
        // Montant
        JLabel lblMontant = new JLabel("Montant:");
        lblMontant.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMontant.setForeground(ThemeColors.TEXT_PRIMARY);
        
        txtMontant = new JTextField();
        txtMontant.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtMontant.setPreferredSize(new Dimension(120, 32));
        txtMontant.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        
        // Bouton ajouter
        JButton btnAjouter = new JButton("➕ Ajouter");
        btnAjouter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAjouter.setForeground(ThemeColors.PRIMARY_EMERALD);
        btnAjouter.setBackground(new Color(209, 250, 229));
        btnAjouter.setFocusPainted(false);
        btnAjouter.setBorderPainted(false);
        btnAjouter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAjouter.setPreferredSize(new Dimension(120, 32));
        btnAjouter.addActionListener(e -> ajouterContribution());
        
        panel.add(lblMembre);
        panel.add(cmbMembre);
        panel.add(lblMontant);
        panel.add(txtMontant);
        panel.add(btnAjouter);
        
        return panel;
    }
    
    private void chargerMembresDansCombo() {
        try {
            List<Membre> membres = membreDAO.findAll();
            for (Membre m : membres) {
                if (m.estActif()) {
                    cmbMembre.addItem(m.getIdMembre() + " - " + m.getNomComplet());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des membres: " + e.getMessage());
        }
    }
    
    private void createContributionTable() {
        String[] columnNames = {"ID", "Membre", "Montant", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableContributions = new JTable(tableModel);
        tableContributions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableContributions.setRowHeight(32);
        tableContributions.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableContributions.setShowGrid(false);
        tableContributions.setIntercellSpacing(new Dimension(0, 0));
        tableContributions.setBackground(ThemeColors.CARD_BG);
        tableContributions.setSelectionBackground(new Color(237, 233, 254));
        tableContributions.setSelectionForeground(ThemeColors.TEXT_PRIMARY);
        
        // Style du header
        JTableHeader header = tableContributions.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(ThemeColors.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColors.BORDER_COLOR));
        
        // Renderer personnalisé
        tableContributions.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? ThemeColors.CARD_BG : new Color(248, 250, 252));
                }
                
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                
                if (!isSelected) {
                    setForeground(ThemeColors.TEXT_PRIMARY);
                }
                
                return c;
            }
        });
        
        // Largeurs des colonnes
        tableContributions.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableContributions.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableContributions.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableContributions.getColumnModel().getColumn(3).setPreferredWidth(100);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeColors.BORDER_COLOR),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        
        JButton btnSupprimer = new JButton("🗑️ Supprimer");
        btnSupprimer.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSupprimer.setForeground(ThemeColors.PRIMARY_RED);
        btnSupprimer.setBackground(new Color(254, 226, 226));
        btnSupprimer.setFocusPainted(false);
        btnSupprimer.setBorderPainted(false);
        btnSupprimer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSupprimer.setPreferredSize(new Dimension(120, 36));
        btnSupprimer.addActionListener(e -> supprimerContribution());
        
        JButton btnFermer = new JButton("✕ Fermer");
        btnFermer.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFermer.setForeground(new Color(148, 163, 184));
        btnFermer.setBackground(Color.WHITE);
        btnFermer.setFocusPainted(false);
        btnFermer.setBorderPainted(false);
        btnFermer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFermer.setPreferredSize(new Dimension(120, 36));
        btnFermer.addActionListener(e -> dispose());
        
        panel.add(btnSupprimer);
        panel.add(btnFermer);
        
        return panel;
    }
    
    private void chargerContributions() {
        try {
            tableModel.setRowCount(0);
            List<ContributionFIAC> contributions = contributionDAO.findByProjet(projet.getIdProjet());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            double total = 0;
            
            for (ContributionFIAC c : contributions) {
                Object[] row = {
                    c.getIdContribution(),
                    c.getMembre() != null ? c.getMembre().getNomComplet() : "N/A",
                    String.format("%.0f FCFA", c.getMontant()),
                    c.getDateContribution().format(formatter)
                };
                tableModel.addRow(row);
                total += c.getMontant();
            }
            
            updateStatistics(total, contributions.size());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement: " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStatistics(double total, int nbContributions) {
        if (lblTotalCollecte != null) {
            lblTotalCollecte.setText(String.format("%.0f FCFA", total));
        }
        if (lblNbContributions != null) {
            lblNbContributions.setText(String.valueOf(nbContributions));
        }
    }
    
    private void ajouterContribution() {
        if (cmbMembre.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un membre", 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (txtMontant.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir le montant", 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
            txtMontant.requestFocus();
            return;
        }
        
        try {
            double montant = Double.parseDouble(txtMontant.getText().trim());
            if (montant <= 0) {
                JOptionPane.showMessageDialog(this, "Le montant doit être supérieur à 0", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
                txtMontant.requestFocus();
                return;
            }
            
            // Extraire l'ID du membre
            String membreSelection = (String) cmbMembre.getSelectedItem();
            int idMembre = Integer.parseInt(membreSelection.split(" - ")[0]);
            
            // Créer la contribution
            ContributionFIAC contribution = new ContributionFIAC();
            contribution.setIdProjet(projet.getIdProjet());
            contribution.setIdMembre(idMembre);
            contribution.setMontant(montant);
            contribution.setDateContribution(LocalDate.now());
            
            if (contributionDAO.create(contribution)) {
                // Mettre à jour le montant collecté du projet
                double nouveauTotal = projet.getMontantCollecte() + montant;
                projet.setMontantCollecte(nouveauTotal);
                projetDAO.updateMontantCollecte(projet.getIdProjet(), nouveauTotal);
                
                // Vider les champs
                txtMontant.setText("");
                cmbMembre.setSelectedIndex(0);
                
                // Recharger les données
                chargerContributions();
                
                JOptionPane.showMessageDialog(this, "Contribution ajoutée avec succès", 
                                            "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un montant valide", 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
            txtMontant.requestFocus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerContribution() {
        int selectedRow = tableContributions.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une contribution", 
                                        "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer cette contribution ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                ContributionFIAC contribution = contributionDAO.findById(id);
                
                if (contribution != null && contributionDAO.delete(id)) {
                    // Mettre à jour le montant collecté du projet
                    double nouveauTotal = projet.getMontantCollecte() - contribution.getMontant();
                    projet.setMontantCollecte(Math.max(0, nouveauTotal));
                    projetDAO.updateMontantCollecte(projet.getIdProjet(), projet.getMontantCollecte());
                    
                    chargerContributions();
                    JOptionPane.showMessageDialog(this, "Contribution supprimée avec succès", 
                                                "Succès", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression", 
                                                "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
}
