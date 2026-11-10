package ma.cinefx.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Seance")
public class Seance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film;

    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    // ✅ Cascade PERSIST (Jakarta) + colonne claire
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "tarif_id")
    private Tarif tarif;

    private LocalDateTime dateHeure;

    @OneToMany(mappedBy = "seance", cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    public Seance() {}

    public Seance(Film film, Salle salle, Tarif tarif, LocalDateTime dateHeure) {
        this.film = film;
        this.salle = salle;
        this.tarif = tarif;
        this.dateHeure = dateHeure;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public Film getFilm() { return film; }
    public void setFilm(Film film) { this.film = film; }

    public Salle getSalle() { return salle; }
    public void setSalle(Salle salle) { this.salle = salle; }

    public Tarif getTarif() { return tarif; }
    public void setTarif(Tarif tarif) { this.tarif = tarif; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public void setId(Long id) { this.id = id; }
}
