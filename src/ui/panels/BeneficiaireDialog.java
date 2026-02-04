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

import dao.BeneficiaireDAO;
import dao.ParticipationDAO;
import models.Beneficiaire;
import models.Participation;
import models.Seance;
import models.Membre;
import ui.MainFrame;
import utils.ThemeColors;
import utils.UIUtils;

public class BeneficiaireDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    
    private MainFrame mainFrame;
    private Seance seance;
    private boolean saved = false;
    private BeneficiaireDAO beneficiaireDAO;
    private ParticipationDAO participationDAO;
    
    private JTable tableBeneficiaires;
    private DefaultTableModel tableModel;
    private JLabel lblTitre;
    private JLabel lblTotalAttributions;
    private JLabel lblNbBeneficiaires;
    private JTextField txtMontantGain;
    private JComboBox<String> cmbParticipation;
    private JComboBox<String> cmbModePaiement;
    
    public BeneficiaireDialog(MainFrame parent, Seance seance) {
        super(parent, "Gestion des Bénéficiaires - " + seance.getDateSeance(), true);
        this.mainFrame = parent;
        this.seance = seance;
        this.beneficiaireDAO = new BeneficiaireDAO();
        this.participationDAO = new ParticipationDAO();
        initializeComponents();
        chargerBeneficiaires();
        chargerParticipations();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout(0, 0));
        setSize(900, 700);
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
        
        // Panneau attribution
        JPanel attributionPanel = createAttributionPanel();
        bodyPanel.add(attributionPanel, BorderLayout.CENTER);
        
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
        
        JLabel iconLabel = new JLabel("🎁");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        lblTitre = new JLabel("Bénéficiaires de la séance: " + seance.getTitreComplet());
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
        
        // Carte Total des attributions
        JPanel cardTotal = createStatCard("Total des attributions", "0 FCFA", "💰", ThemeColors.PRIMARY_EMERALD);
        lblTotalAttributions = (JLabel) ((JPanel)((JPanel)((JPanel)cardTotal.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        // Carte Nombre de bénéficiaires
        JPanel cardNbBenef = createStatCard("Bénéficiaires", "0", "👥", ThemeColors.PRIMARY_BLUE);
        lblNbBeneficiaires = (JLabel) ((JPanel)((JPanel)((JPanel)cardNbBenef.getComponent(0)).getComponent(1)).getComponent(0)).getComponent(0);
        
        panel.add(cardTotal);
        panel.add(cardNbBenef);
        
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
    
    private JPanel createAttributionPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(ThemeColors.CARD_BG);
        panel.setBorder(new UIUtils.RoundedBorder(12, ThemeColors.BORDER_COLOR, 1));
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(ThemeColors.CARD_BG);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        // Formulaire d'attribution
        JPanel formPanel = createFormPanel();
        innerPanel.add(formPanel, BorderLayout.NORTH);
        
        // Tableau des bénéficiaires
        createBeneficiaireTable();
        JScrollPane scrollPane = new JScrollPane(tableBeneficiaires);
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
        
        // Participation
        JLabel lblParticipation = new JLabel("Participant:");
        lblParticipation.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblParticipation.setForeground(ThemeColors.TEXT_PRIMARY);
        
        cmbParticipation = new JComboBox<>();
        cmbParticipation.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbParticipation.setPreferredSize(new Dimension(250, 32));
        
        // Montant
        JLabel lblMontant = new JLabel("Montant:");
        lblMontant.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMontant.setForeground(ThemeColors.TEXT_PRIMARY);
        
        txtMontantGain = new JTextField();
        txtMontantGain.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtMontantGain.setPreferredSize(new Dimension(120, 32));
        txtMontantGain.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        
        // Mode de paiement
        JLabel lblModePaiement = new JLabel("Mode paiement:");
        lblModePaiement.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblModePaiement.setForeground(ThemeColors.TEXT_PRIMARY);
        
        cmbModePaiement = new JComboBox<>(new String[]{"especes", "virement", "cheque", "mobile_money"});
        cmbModePaiement.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbModePaiement.setPreferredSize(new Dimension(150, 32));
        
        // Bouton ajouter
        JButton btnAjouter = new JButton("➕ Attribuer");
        btnAjouter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAjouter.setForeground(ThemeColors.PRIMARY_EMERALD);
        btnAjouter.setBackground(new Color(209, 250, 229));
        btnAjouter.setFocusPainted(false);
        btnAjouter.setBorderPainted(false);
        btnAjouter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAjouter.setPreferredSize(new Dimension(120, 32));
        btnAjouter.addActionListener(e -> ajouterBeneficiaire());
        
        panel.add(lblParticipation);
        panel.add(cmbParticipation);
        panel.add(lblMontant);
        panel.add(txtMontantGain);
        panel.add(lblModePaiement);
        panel.add(cmbModePaiement);
        panel.add(btnAjouter);
        
        return panel;
    }
    
    private void chargerParticipations() {
        try {
            List<Participation> participations = participationDAO.findByTontine(seance.getIdTontine());
            cmbParticipation.removeAllItems();
            
            for (Participation p : participations) {
                if (p.isActive()) {
                    String display = p.getIdParticipation() + " - " + p.getNomMembre() + " (" + p.getNombreParts() + " part(s))";
                    cmbParticipation.addItem(display);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des participations: " + e.getMessage());
        }
    }
    
    private void createBeneficiaireTable() {
        String[] columnNames = {"ID", "Participant", "Montant", "Date attribution", "Mode paiement", "Statut"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableBeneficiaires = new JTable(tableModel);
        tableBeneficiaires.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableBeneficiaires.setRowHeight(32);
        tableBeneficiaires.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableBeneficiaires.setShowGrid(false);
        tableBeneficiaires.setIntercellSpacing(new Dimension(0, 0));
        tableBeneficiaires.setBackground(ThemeColors.CARD_BG);
        tableBeneficiaires.setSelectionBackground(new Color(237, 233, 254));
        tableBeneficiaires.setSelectionForeground(ThemeColors.TEXT_PRIMARY);
        
        // Style du header
        JTableHeader header = tableBeneficiaires.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(ThemeColors.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColors.BORDER_COLOR));
        
        // Renderer personnalisé
        tableBeneficiaires.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        tableBeneficiaires.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableBeneficiaires.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableBeneficiaires.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableBeneficiaires.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableBeneficiaires.getColumnModel().getColumn(4).setPreferredWidth(120);
        tableBeneficiaires.getColumnModel().getColumn(5).setPreferredWidth(100);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeColors.BORDER_COLOR),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        
        JButton btnMarquerPaye = new JButton("✅ Marquer payé");
        btnMarquerPaye.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMarquerPaye.setForeground(ThemeColors.PRIMARY_BLUE);
        btnMarquerPaye.setBackground(new Color(219, 234, 254));
        btnMarquerPaye.setFocusPainted(false);
        btnMarquerPaye.setBorderPainted(false);
        btnMarquerPaye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMarquerPaye.setPreferredSize(new Dimension(140, 36));
        btnMarquerPaye.addActionListener(e -> marquerCommePaye());
        
        JButton btnSupprimer = new JButton("🗑️ Supprimer");
        btnSupprimer.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSupprimer.setForeground(ThemeColors.PRIMARY_RED);
        btnSupprimer.setBackground(new Color(254, 226, 226));
        btnSupprimer.setFocusPainted(false);
        btnSupprimer.setBorderPainted(false);
        btnSupprimer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSupprimer.setPreferredSize(new Dimension(120, 36));
        btnSupprimer.addActionListener(e -> supprimerBeneficiaire());
        
        JButton btnFermer = new JButton("✕ Fermer");
        btnFermer.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFermer.setForeground(new Color(148, 163, 184));
        btnFermer.setBackground(Color.WHITE);
        btnFermer.setFocusPainted(false);
        btnFermer.setBorderPainted(false);
        btnFermer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFermer.setPreferredSize(new Dimension(120, 36));
        btnFermer.addActionListener(e -> dispose());
        
        panel.add(btnMarquerPaye);
        panel.add(btnSupprimer);
        panel.add(btnFermer);
        
        return panel;
    }
    
    private void chargerBeneficiaires() {
        try {
            tableModel.setRowCount(0);
            List<Beneficiaire> beneficiaires = beneficiaireDAO.findBySeance(seance.getIdSeance());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            double total = 0;
            
            for (Beneficiaire b : beneficiaires) {
                Object[] row = {
                    b.getIdBeneficiaire(),
                    b.getNomBeneficiaire(),
                    String.format("%.0f FCFA", b.getMontantGain()),
                    b.getDateAttribution().format(formatter),
                    b.getModePaiementAffichage(),
                    b.getStatutAffichage()
                };
                tableModel.addRow(row);
                total += b.getMontantGain();
            }
            
            updateStatistics(total, beneficiaires.size());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement: " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStatistics(double total, int nbBeneficiaires) {
        if (lblTotalAttributions != null) {
            lblTotalAttributions.setText(String.format("%.0f FCFA", total));
        }
        if (lblNbBeneficiaires != null) {
            lblNbBeneficiaires.setText(String.valueOf(nbBeneficiaires));
        }
    }
    
    private void ajouterBeneficiaire() {
        if (cmbParticipation.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un participant", 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (txtMontantGain.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir le montant", 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
            txtMontantGain.requestFocus();
            return;
        }
        
        try {
            double montant = Double.parseDouble(txtMontantGain.getText().trim());
            if (montant <= 0) {
                JOptionPane.showMessageDialog(this, "Le montant doit être supérieur à 0", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
                txtMontantGain.requestFocus();
                return;
            }
            
            // Extraire l'ID de la participation
            String participationSelection = (String) cmbParticipation.getSelectedItem();
            int idParticipation = Integer.parseInt(participationSelection.split(" - ")[0]);
            
            // Créer le bénéficiaire
            Beneficiaire beneficiaire = new Beneficiaire();
            beneficiaire.setIdSeance(seance.getIdSeance());
            beneficiaire.setIdParticipation(idParticipation);
            beneficiaire.setMontantGain(montant);
            beneficiaire.setDateAttribution(LocalDate.now());
            beneficiaire.setModePaiement((String) cmbModePaiement.getSelectedItem());
            beneficiaire.setStatut("attribue");
            
            if (beneficiaireDAO.create(beneficiaire)) {
                // Vider les champs
                txtMontantGain.setText("");
                cmbParticipation.setSelectedIndex(0);
                cmbModePaiement.setSelectedIndex(0);
                
                // Recharger les données
                chargerBeneficiaires();
                
                JOptionPane.showMessageDialog(this, "Bénéficiaire attribué avec succès", 
                                            "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'attribution", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un montant valide", 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
            txtMontantGain.requestFocus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void marquerCommePaye() {
        int selectedRow = tableBeneficiaires.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un bénéficiaire", 
                                        "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String statut = (String) tableModel.getValueAt(selectedRow, 5);
            
            if ("Payé".equals(statut)) {
                JOptionPane.showMessageDialog(this, "Ce bénéficiaire est déjà marqué comme payé", 
                                            "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            if (beneficiaireDAO.marquerCommePaye(id)) {
                chargerBeneficiaires();
                JOptionPane.showMessageDialog(this, "Bénéficiaire marqué comme payé avec succès", 
                                            "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors du marquage comme payé", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerBeneficiaire() {
        int selectedRow = tableBeneficiaires.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un bénéficiaire", 
                                        "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer cette attribution ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                
                if (beneficiaireDAO.delete(id)) {
                    chargerBeneficiaires();
                    JOptionPane.showMessageDialog(this, "Attribution supprimée avec succès", 
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
