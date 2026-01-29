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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
    private JTextField statutField;
    private JTextField observationsField;
    
    public SeanceDialog(java.awt.Frame parent, Seance seance, TontineDAO tontineDAO) {
        super(parent, seance == null ? "Nouvelle Séance" : "Modifier Séance", true);
        this.seance = seance != null ? seance : new Seance(); // Créer une nouvelle séance si null
        this.seanceDAO = new SeanceDAO();
        
        initializeComponents();
        layoutComponents();
        populateFields();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setPreferredSize(new Dimension(200, 30));
        
        lieuField = new JTextField(20);
        statutField = new JTextField(20);
        observationsField = new JTextField(20);
    }
    
    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Date
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(dateChooser, gbc);
        
        // Lieu
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Lieu:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(lieuField, gbc);
        
        // Statut
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(statutField, gbc);
        
        // Observations
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Observations:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(observationsField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        saveButton.addActionListener(e -> saveSeance());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void populateFields() {
        if (seance != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (seance.getDateSeance() != null) {
                dateChooser.setDate(java.sql.Date.valueOf(seance.getDateSeance()));
            }
            lieuField.setText(seance.getLieu() != null ? seance.getLieu() : "");
            statutField.setText(seance.getStatut() != null ? seance.getStatut() : "");
            observationsField.setText(seance.getObservations() != null ? seance.getObservations() : "");
        }
    }
    
    private void saveSeance() {
        try {
            if (seance == null) {
                showValidationError("Aucune séance à enregistrer");
                return;
            }
            
            // Validation des champs
            if (dateChooser.getDate() == null) {
                showValidationError("La date est obligatoire");
                dateChooser.requestFocus();
                return;
            }
            
            if (lieuField.getText().trim().isEmpty()) {
                showValidationError("Le lieu est obligatoire");
                lieuField.requestFocus();
                return;
            }
            
            if (statutField.getText().trim().isEmpty()) {
                showValidationError("Le statut est obligatoire");
                statutField.requestFocus();
                return;
            }
            
            // Parse date
            LocalDate dateSeance;
            try {
                dateSeance = dateChooser.getDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
            } catch (Exception e) {
                showValidationError("Date invalide");
                dateChooser.requestFocus();
                return;
            }
            
            // Mettre à jour l'objet
            seance.setDateSeance(dateSeance);
            seance.setLieu(lieuField.getText().trim());
            seance.setStatut(statutField.getText().trim());
            seance.setObservations(observationsField.getText().trim());
            
            // Déterminer si c'est une création ou une modification
            boolean success;
            if (seance.getIdSeance() == 0) {
                // Création
                success = seanceDAO.create(seance);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Séance créée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // Modification
                success = seanceDAO.update(seance);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Séance mise à jour avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            if (success) {
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation", JOptionPane.WARNING_MESSAGE);
    }
    
    public boolean isSaved() {
        return saved;
    }
}
