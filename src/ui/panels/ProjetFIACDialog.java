package ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.AbstractButton;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;

import dao.ProjetFIACDAO;
import dao.TontineDAO;
import models.ProjetFIAC;
import models.Tontine;
import utils.ThemeColors;
import utils.UIUtils;

public class ProjetFIACDialog extends JDialog {
    private ProjetFIAC projet;
    private boolean saved = false;
    private ProjetFIACDAO projetDAO;
    private TontineDAO tontineDAO;
    
    private JTextField txtNomProjet;
    private JTextArea txtDescription;
    private JTextField txtMontantObjectif;
    private JComboBox<String> cmbTontine;
    private JComboBox<String> cmbStatut;
    private com.toedter.calendar.JDateChooser dateDebut;
    private com.toedter.calendar.JDateChooser dateFinPrevue;
    
    private static final Color DIALOG_PURPLE = new Color(139, 92, 246);
    private static final Color DIALOG_TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color DIALOG_TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color DIALOG_BACKGROUND = new Color(248, 250, 252);
    private static final Color DIALOG_BORDER_COLOR = new Color(226, 232, 240);
    
    public ProjetFIACDialog(Frame parent, ProjetFIAC projet) {
        super(parent, projet == null ? "Nouveau Projet FIAC" : "Modifier Projet FIAC", true);
        this.projet = projet;
        this.projetDAO = new ProjetFIACDAO();
        this.tontineDAO = new TontineDAO();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setSize(700, 700);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(ThemeColors.BACKGROUND);
        
        // Titre du dialogue
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Formulaire
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        if (projet != null) {
            populateFields();
        }
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColors.BORDER_COLOR),
            BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));
        
        JLabel iconLabel = new JLabel("🏗️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JLabel titleLabel = new JLabel(projet == null ? "Nouveau Projet FIAC" : "Modifier Projet FIAC");
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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeColors.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        
        // Créer les champs
        txtNomProjet = createStyledTextField();
        txtDescription = createStyledTextArea();
        txtMontantObjectif = createStyledTextField();
        cmbTontine = createStyledComboBox();
        chargerTontinesDansCombo();
        
        dateDebut = createStyledDateChooser();
        dateFinPrevue = createStyledDateChooser();
        
        cmbStatut = createStyledComboBox();
        cmbStatut.addItem("planifié");
        cmbStatut.addItem("en cours");
        cmbStatut.addItem("terminé");
        cmbStatut.addItem("annulé");
        
        // Ajouter les champs avec des espacements
        addFormField(panel, "Nom du projet *", "Nom du projet de développement", txtNomProjet);
        addFormField(panel, "Description", "Description détaillée du projet", txtDescription);
        addFormField(panel, "Montant objectif *", "Montant à collecter (FCFA)", txtMontantObjectif);
        addFormField(panel, "Tontine source *", "Tontine qui finance le projet", cmbTontine);
        addFormField(panel, "Date de début *", "Date de début du projet", dateDebut);
        addFormField(panel, "Date de fin prévue", "Date prévue de fin du projet", dateFinPrevue);
        addFormField(panel, "Statut", "Statut actuel du projet", cmbStatut);
        
        return panel;
    }
    
    private void addFormField(JPanel panel, String label, String description, JComponent field) {
        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(field);
        panel.add(Box.createVerticalStrut(16));
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(400, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea(4, 30);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(400, 100));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        return area;
    }
    
    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(400, 42));
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return combo;
    }
    
    private com.toedter.calendar.JDateChooser createStyledDateChooser() {
        com.toedter.calendar.JDateChooser chooser = new com.toedter.calendar.JDateChooser();
        chooser.setDateFormatString("dd/MM/yyyy");
        chooser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chooser.setPreferredSize(new Dimension(400, 42));
        chooser.setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER_COLOR, 1));
        return chooser;
    }
    
    private void chargerTontinesDansCombo() {
        try {
            List<Tontine> tontines = tontineDAO.findAll();
            for (Tontine t : tontines) {
                cmbTontine.addItem(t.getIdTontine() + " - " + t.getNom());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des tontines: " + e.getMessage());
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeColors.BORDER_COLOR),
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
        if (projet != null) {
            txtNomProjet.setText(projet.getNomProjet());
            txtDescription.setText(projet.getDescription());
            txtMontantObjectif.setText(String.format("%.0f", projet.getMontantObjectif()));
            
            if (projet.getDateDebut() != null) {
                dateDebut.setDate(java.util.Date.from(projet.getDateDebut()
                    .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
            }
            
            if (projet.getDateFinPrevue() != null) {
                dateFinPrevue.setDate(java.util.Date.from(projet.getDateFinPrevue()
                    .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
            }
            
            if (projet.getStatut() != null) {
                cmbStatut.setSelectedItem(projet.getStatut());
            }
            
            // Sélectionner la tontine
            for (int i = 0; i < cmbTontine.getItemCount(); i++) {
                String item = cmbTontine.getItemAt(i);
                int idTontine = Integer.parseInt(item.split(" - ")[0]);
                if (idTontine == projet.getIdTontine()) {
                    cmbTontine.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void enregistrer() {
        if (!validateForm()) return;
        
        try {
            if (projet == null) {
                projet = new ProjetFIAC();
            }
            
            projet.setNomProjet(txtNomProjet.getText().trim());
            projet.setDescription(txtDescription.getText().trim());
            
            try {
                projet.setMontantObjectif(Double.parseDouble(txtMontantObjectif.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un montant valide", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Extraire l'ID de la tontine
            String tontineSelection = (String) cmbTontine.getSelectedItem();
            int idTontine = Integer.parseInt(tontineSelection.split(" - ")[0]);
            projet.setIdTontine(idTontine);
            
            projet.setDateDebut(dateDebut.getDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            
            if (dateFinPrevue.getDate() != null) {
                projet.setDateFinPrevue(dateFinPrevue.getDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            }
            
            projet.setStatut((String) cmbStatut.getSelectedItem());
            
            boolean success = projet.getIdProjet() == 0 ? 
                projetDAO.create(projet) : projetDAO.update(projet);
                
            if (success) {
                saved = true;
                JOptionPane.showMessageDialog(this, 
                    "Projet FIAC enregistré avec succès", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'enregistrement", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateForm() {
        if (txtNomProjet.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir le nom du projet", "Erreur", JOptionPane.ERROR_MESSAGE);
            txtNomProjet.requestFocus();
            return false;
        }
        
        if (txtMontantObjectif.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir le montant objectif", "Erreur", JOptionPane.ERROR_MESSAGE);
            txtMontantObjectif.requestFocus();
            return false;
        }
        
        try {
            double montant = Double.parseDouble(txtMontantObjectif.getText().trim());
            if (montant <= 0) {
                JOptionPane.showMessageDialog(this, "Le montant doit être supérieur à 0", "Erreur", JOptionPane.ERROR_MESSAGE);
                txtMontantObjectif.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un montant valide", "Erreur", JOptionPane.ERROR_MESSAGE);
            txtMontantObjectif.requestFocus();
            return false;
        }
        
        if (dateDebut.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner la date de début", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (dateFinPrevue.getDate() != null && dateDebut.getDate().after(dateFinPrevue.getDate())) {
            JOptionPane.showMessageDialog(this, "La date de début doit être antérieure à la date de fin", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public boolean isSaved() {
        return saved;
    }
}
