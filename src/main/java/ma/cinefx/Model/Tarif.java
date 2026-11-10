package ma.cinefx.Model;


import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Tarif")
public class Tarif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle;
    private double prix;

    @OneToMany(mappedBy = "tarif", cascade = CascadeType.ALL)
    private List<Seance> seances;

    public Tarif() {}

    public Tarif(String libelle, double prix) {
        this.libelle = libelle;
        this.prix = prix;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public List<Seance> getSeances() { return seances; }
    public void setSeances(List<Seance> seances) { this.seances = seances; }
}
