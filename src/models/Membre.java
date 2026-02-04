package models;

import java.time.LocalDate;

public class Membre {
    
    private int idMembre;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String adresse;
    private LocalDate dateAdhesion;
    private String statut;
    
    public Membre() {}
    
    public Membre(String nom, String prenom, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.statut = "actif";
        this.dateAdhesion = LocalDate.now();
    }
    
    public Membre(String nom, String prenom, String telephone, String email, String adresse) {
        this(nom, prenom, telephone);
        this.email = email;
        this.adresse = adresse;
    }
    
    public int getIdMembre() {
        return idMembre;
    }
    
    public void setIdMembre(int idMembre) {
        this.idMembre = idMembre;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public LocalDate getDateAdhesion() {
        return dateAdhesion;
    }
    
    public void setDateAdhesion(LocalDate dateAdhesion) {
        this.dateAdhesion = dateAdhesion;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public LocalDate getDateInscription() {
        return dateAdhesion;
    }
    
    public String getNomComplet() {
        return nom + " " + prenom;
    }
    
    public boolean estActif() {
        return "actif".equals(statut);
    }
    
    public boolean estSuspendu() {
        return "suspendu".equals(statut);
    }
    
    public boolean estInactif() {
        return "inactif".equals(statut);
    }
    
    @Override
    public String toString() {
        return getNomComplet() + " (" + telephone + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Membre membre = (Membre) obj;
        return idMembre == membre.idMembre;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(idMembre);
    }
}
