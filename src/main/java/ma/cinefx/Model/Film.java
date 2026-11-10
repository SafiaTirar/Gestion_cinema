package ma.cinefx.Model;


import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Film")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String genre;
    private int duree;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    private List<Seance> seances;

    public Film() {}

    public Film(String titre, String genre, int duree) {
        this.titre = titre;
        this.genre = genre;
        this.duree = duree;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }
    public List<Seance> getSeances() { return seances; }
    public void setSeances(List<Seance> seances) { this.seances = seances; }
    public void setId(Long id) {
        this.id = id;
    }
}
