package ma.cinefx.Model;


import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Salle")
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private int capacite;

    @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL)
    private List<Seance> seances;

    public Salle() {}

    public Salle(String nom, int capacite) {
        this.nom = nom;
        this.capacite = capacite;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public List<Seance> getSeances() { return seances; }
    public void setSeances(List<Seance> seances) { this.seances = seances; }
    public void setId(Long id) {
        this.id = id;
    }
}
