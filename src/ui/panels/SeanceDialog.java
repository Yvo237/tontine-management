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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.toedter.calendar.JDateChooser;

import dao.SeanceDAO;
import dao.TontineDAO;
import models.Seance;
import models.Tontine;

public class SeanceDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private Seance seance;
    private SeanceDAO seanceDAO;
    private boolean saved = false;
    
    private JDateChooser dateChooser;
    private JTextField lieuField;
    private JComboBox<String> statutCombo;
    private JTextArea observationsArea;
    private JComboBox<Tontine> tontineCombo;
    private JSpinner numeroTourSpinner;
    private JLabel dateErrorLabel;
    private JLabel lieuErrorLabel;
    private JLabel tontineErrorLabel;
    
    // Palette de couleurs moderne
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    
    public SeanceDialog(java.awt.Frame parent, Seance seance, TontineDAO tontineDAO) {
        super(parent, seance == null ? "Nouvelle Séance" : "Modifier Séance", true);
        this.seance = seance != null ? seance : new Seance(); // Créer une nouvelle séance si null
        this.seanceDAO = new SeanceDAO();
        
        initializeComponents(tontineDAO);
        layoutComponents();
        populateFields();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(550, 450);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents(TontineDAO tontineDAO) {
        // Date chooser avec style moderne
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setPreferredSize(new Dimension(250, 35));
        dateChooser.setToolTipText("Sélectionnez la date de la séance");
        
        // Champ lieu avec validation en temps réel
        lieuField = new JTextField(25);
        lieuField.setToolTipText("Entrez le lieu où se déroulera la séance");
        lieuField.getDocument().addDocumentListener(new FieldValidationListener());
        
        // ComboBox pour les tontines
        tontineCombo = new JComboBox<>();
        tontineCombo.setToolTipText("Sélectionnez la tontine concernée");
        chargerTontines(tontineDAO);
        
        // Spinner pour le numéro de tour
        numeroTourSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(1, 1, 999, 1));
        numeroTourSpinner.setPreferredSize(new Dimension(100, 35));
        numeroTourSpinner.setToolTipText("Numéro du tour de la séance");
        
        // ComboBox pour le statut (valeurs normalisées)
        statutCombo = new JComboBox<>();
        statutCombo.addItem("planifiée");
        statutCombo.addItem("en cours");
        statutCombo.addItem("terminée");
        statutCombo.setToolTipText("Sélectionnez le statut actuel de la séance");
        
        // TextArea pour observations avec scrollbar
        observationsArea = new JTextArea(4, 25);
        observationsArea.setLineWrap(true);
        observationsArea.setWrapStyleWord(true);
        observationsArea.setToolTipText("Ajoutez des observations ou notes supplémentaires (optionnel)");
        JScrollPane observationsScrollPane = new JScrollPane(observationsArea);
        observationsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        observationsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Labels d'erreur
        dateErrorLabel = createErrorLabel();
        lieuErrorLabel = createErrorLabel();
        tontineErrorLabel = createErrorLabel();
    }
    
    private void chargerTontines(TontineDAO tontineDAO) {
        try {
            List<Tontine> tontines = tontineDAO.findAll();
            for (Tontine tontine : tontines) {
                tontineCombo.addItem(tontine);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des tontines: " + e.getMessage());
        }
    }
    
    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setForeground(ERROR_COLOR);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 11f));
        return label;
    }
    
    // Listener pour validation en temps réel
    private class FieldValidationListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) { validateField(); }
        @Override
        public void removeUpdate(DocumentEvent e) { validateField(); }
        @Override
        public void changedUpdate(DocumentEvent e) { validateField(); }
        
        private void validateField() {
            if (lieuField.getText().trim().isEmpty()) {
                lieuErrorLabel.setText("Le lieu est obligatoire");
                lieuField.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 2));
            } else {
                lieuErrorLabel.setText(" ");
                lieuField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            }
        }
    }
    
    private void layoutComponents() {
        // Panel principal avec fond moderne
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Titre
        JLabel titleLabel = new JLabel(seance == null ? "Nouvelle Séance" : "Modifier Séance");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // Séparateur
        gbc.insets = new Insets(15, 8, 15, 8);
        gbc.gridy = 1;
        mainPanel.add(createSeparator(), gbc);
        
        // Réinitialiser les insets pour les champs
        gbc.insets = new Insets(8, 8, 2, 8);
        gbc.gridwidth = 1;
        
        // Tontine
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(createStyledLabel("Tontine *"), gbc);
        gbc.gridx = 1;
        mainPanel.add(tontineCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(tontineErrorLabel, gbc);
        
        // Numéro de tour
        gbc.insets = new Insets(8, 8, 2, 8);
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(createStyledLabel("Numéro de tour *"), gbc);
        gbc.gridx = 1;
        mainPanel.add(numeroTourSpinner, gbc);
        
        // Date de séance
        gbc.insets = new Insets(8, 8, 2, 8);
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(createStyledLabel("Date de la séance *"), gbc);
        gbc.gridx = 1;
        mainPanel.add(dateChooser, gbc);
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(dateErrorLabel, gbc);
        
        // Lieu
        gbc.insets = new Insets(8, 8, 2, 8);
        gbc.gridx = 0; gbc.gridy = 7;
        mainPanel.add(createStyledLabel("Lieu *"), gbc);
        gbc.gridx = 1;
        mainPanel.add(lieuField, gbc);
        gbc.gridx = 0; gbc.gridy = 8;
        mainPanel.add(lieuErrorLabel, gbc);
        
        // Statut
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0; gbc.gridy = 9;
        mainPanel.add(createStyledLabel("Statut *"), gbc);
        gbc.gridx = 1;
        mainPanel.add(statutCombo, gbc);
        
        // Observations
        gbc.gridx = 0; gbc.gridy = 10;
        mainPanel.add(createStyledLabel("Observations"), gbc);
        gbc.gridx = 1; gbc.gridy = 10;
        gbc.gridwidth = 1;
        mainPanel.add(new JScrollPane(observationsArea), gbc);
        
        // Panel boutons avec style moderne
        JPanel buttonPanel = createButtonPanel();
        
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Style de la fenêtre
        getContentPane().setBackground(BACKGROUND_COLOR);
        setResizable(false);
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(51, 65, 85));
        return label;
    }
    
    private JPanel createSeparator() {
        JPanel separator = new JPanel();
        separator.setPreferredSize(new Dimension(400, 1));
        separator.setBackground(BORDER_COLOR);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return separator;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton saveButton = createStyledButton("Enregistrer", PRIMARY_COLOR);
        JButton cancelButton = createStyledButton("Annuler", new Color(107, 114, 128));
        
        saveButton.addActionListener(e -> saveSeance());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        return buttonPanel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void populateFields() {
        if (seance != null && seance.getIdSeance() != 0) {
            // Sélectionner la tontine
            if (seance.getIdTontine() > 0) {
                for (int i = 0; i < tontineCombo.getItemCount(); i++) {
                    Tontine t = tontineCombo.getItemAt(i);
                    if (t.getIdTontine() == seance.getIdTontine()) {
                        tontineCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            // Numéro de tour
            numeroTourSpinner.setValue(seance.getNumeroTour());
            
            // Date
            if (seance.getDateSeance() != null) {
                dateChooser.setDate(java.sql.Date.valueOf(seance.getDateSeance()));
            }
            
            // Lieu
            lieuField.setText(seance.getLieu() != null ? seance.getLieu() : "");
            
            // Sélectionner le statut dans la combo
            String statut = seance.getStatut();
            if (statut != null) {
                // Normaliser le statut pour correspondre aux valeurs de la combo
                switch (statut.toLowerCase()) {
                    case "planifiee":
                    case "planifiée":
                        statutCombo.setSelectedItem("planifiée");
                        break;
                    case "en_cours":
                    case "en cours":
                        statutCombo.setSelectedItem("en cours");
                        break;
                    case "terminee":
                    case "terminée":
                        statutCombo.setSelectedItem("terminée");
                        break;
                    default:
                        statutCombo.setSelectedItem("planifiée");
                }
            }
            
            observationsArea.setText(seance.getObservations() != null ? seance.getObservations() : "");
        } else {
            // Nouvelle séance : sélectionner la première tontine par défaut
            if (tontineCombo.getItemCount() > 0) {
                tontineCombo.setSelectedIndex(0);
                // Suggérer le prochain numéro de tour
                Tontine premiereTontine = (Tontine) tontineCombo.getSelectedItem();
                if (premiereTontine != null) {
                    int prochainTour = seanceDAO.getLastNumeroTour(premiereTontine.getIdTontine()) + 1;
                    numeroTourSpinner.setValue(prochainTour);
                }
            }
        }
    }
    
    private void saveSeance() {
        try {
            if (seance == null) {
                showValidationError("Aucune séance à enregistrer");
                return;
            }
            
            // Validation des champs
            boolean isValid = true;
            
            // Validation tontine
            Tontine tontineSelectionnee = (Tontine) tontineCombo.getSelectedItem();
            if (tontineSelectionnee == null) {
                tontineErrorLabel.setText("La tontine est obligatoire");
                isValid = false;
            } else {
                tontineErrorLabel.setText(" ");
            }
            
            // Validation numéro de tour
            int numeroTour = (Integer) numeroTourSpinner.getValue();
            if (numeroTour < 1) {
                showValidationError("Le numéro de tour doit être supérieur à 0");
                isValid = false;
            }
            
            // Validation date
            if (dateChooser.getDate() == null) {
                dateErrorLabel.setText("La date est obligatoire");
                isValid = false;
            } else {
                dateErrorLabel.setText(" ");
            }
            
            // Validation lieu
            if (lieuField.getText().trim().isEmpty()) {
                lieuErrorLabel.setText("Le lieu est obligatoire");
                lieuField.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 2));
                isValid = false;
            } else {
                lieuErrorLabel.setText(" ");
                lieuField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            }
            
            if (!isValid) {
                showValidationError("Veuillez corriger les erreurs indiquées");
                return;
            }
            
            // Parse date
            LocalDate dateSeance;
            try {
                dateSeance = dateChooser.getDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
            } catch (Exception e) {
                dateErrorLabel.setText("Date invalide");
                showValidationError("Date invalide");
                dateChooser.requestFocus();
                return;
            }
            
            // Normaliser le statut pour la base de données
            String statutNormalise = normalizeStatut((String) statutCombo.getSelectedItem());
            
            // Mettre à jour l'objet
            seance.setIdTontine(tontineSelectionnee.getIdTontine());
            seance.setNumeroTour(numeroTour);
            seance.setDateSeance(dateSeance);
            seance.setLieu(lieuField.getText().trim());
            seance.setStatut(statutNormalise);
            seance.setObservations(observationsArea.getText().trim());
            
            // Déterminer si c'est une création ou une modification
            boolean success;
            if (seance.getIdSeance() == 0) {
                // Création
                success = seanceDAO.create(seance);
                if (success) {
                    showSuccessMessage("Séance créée avec succès");
                }
            } else {
                // Modification
                success = seanceDAO.update(seance);
                if (success) {
                    showSuccessMessage("Séance mise à jour avec succès");
                }
            }
            
            if (success) {
                saved = true;
                dispose();
            } else {
                showErrorMessage("Erreur lors de l'enregistrement");
            }
            
        } catch (Exception e) {
            showErrorMessage("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String normalizeStatut(String statut) {
        if (statut == null) return "planifiee";
        
        switch (statut.toLowerCase()) {
            case "planifiée": return "planifiee";
            case "en cours": return "en_cours";
            case "terminée": return "terminee";
            default: return "planifiee";
        }
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation", JOptionPane.WARNING_MESSAGE);
    }
    
    public boolean isSaved() {
        return saved;
    }
}
