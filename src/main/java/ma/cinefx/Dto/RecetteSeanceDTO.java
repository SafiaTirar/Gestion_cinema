package ma.cinefx.Dto;

import java.time.LocalDateTime;

public class RecetteSeanceDTO {

    private Long seanceId;
    private String filmTitre;
    private String genre;
    private String salleNom;
    private LocalDateTime dateHeure;
    private double prixTarif;
    private long nbPlacesTotal;
    private double recette;

    public RecetteSeanceDTO(Long seanceId, String filmTitre, String genre, String salleNom,
                            LocalDateTime dateHeure, double prixTarif, long nbPlacesTotal, double recette) {
        this.seanceId = seanceId;
        this.filmTitre = filmTitre;
        this.genre = genre;
        this.salleNom = salleNom;
        this.dateHeure = dateHeure;
        this.prixTarif = prixTarif;
        this.nbPlacesTotal = nbPlacesTotal;
        this.recette = recette;
    }

    public Long getSeanceId() { return seanceId; }
    public String getFilmTitre() { return filmTitre; }
    public String getGenre() { return genre; }
    public String getSalleNom() { return salleNom; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public double getPrixTarif() { return prixTarif; }
    public long getNbPlacesTotal() { return nbPlacesTotal; }
    public double getRecette() { return recette; }
}
