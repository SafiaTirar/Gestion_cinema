package ma.cinefx.Model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "seance_id", nullable = false)
    private Seance seance;

    @Column(nullable = false)
    private String clientNom;

    @Column(nullable = false)
    private int nbPlaces;

    public Reservation() {}

    public Reservation(Seance seance, String clientNom, int nbPlaces) {
        this.seance = seance;
        this.clientNom = clientNom;
        this.nbPlaces = nbPlaces;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public Seance getSeance() { return seance; }
    public void setSeance(Seance seance) { this.seance = seance; }
    public String getClientNom() { return clientNom; }
    public void setClientNom(String clientNom) { this.clientNom = clientNom; }
    public int getNbPlaces() { return nbPlaces; }
    public void setNbPlaces(int nbPlaces) { this.nbPlaces = nbPlaces; }

    // toString pour debug facile
    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", clientNom='" + clientNom + '\'' +
                ", nbPlaces=" + nbPlaces +
                ", seanceId=" + (seance != null ? seance.getId() : null) +
                '}';
    }

    // equals & hashCode basés sur l'ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
