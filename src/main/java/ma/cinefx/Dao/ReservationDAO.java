package ma.cinefx.Dao;
import ma.cinefx.Dto.RecetteSeanceDTO;
import java.time.LocalDateTime;
import ma.cinefx.Exception.EntityAlreadyExistsException;
import ma.cinefx.Exception.EntityNotFoundException;
import ma.cinefx.Exception.EntityPersistenceException;
import ma.cinefx.Model.Reservation;
import ma.cinefx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ReservationDAO {

    // Ajouter une réservation
    public void addReservation(Reservation reservation) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Optionnel : vérifier si la réservation existe déjà pour le même client et séance
            Query<Reservation> query = session.createQuery(
                    "FROM Reservation WHERE seance.id = :seanceId AND clientNom = :clientNom", Reservation.class);
            query.setParameter("seanceId", reservation.getSeance().getId());
            query.setParameter("clientNom", reservation.getClientNom());

            if (!query.list().isEmpty()) {
                throw new EntityAlreadyExistsException("Reservation", reservation.getClientNom());
            }

            session.save(reservation);
            transaction.commit();

        } catch (EntityAlreadyExistsException e) {
            if (transaction != null) transaction.rollback();
            throw e;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new EntityPersistenceException("Erreur lors de l'ajout de la réservation", e);
        }
    }

    // Trouver une réservation par ID
    public Reservation getReservationById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Reservation reservation = session.get(Reservation.class, id);

            if (reservation == null) {
                throw new EntityNotFoundException("Reservation", "ID=" + id);
            }

            return reservation;

        } catch (EntityNotFoundException e) {
            throw e;

        } catch (Exception e) {
            throw new EntityPersistenceException("Erreur lors de la récupération de la réservation", e);
        }
    }

    // Lister toutes les réservations
    public List<Reservation> getAllReservations() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Reservation", Reservation.class).list();
        } catch (Exception e) {
            throw new EntityPersistenceException("Erreur lors de la récupération des réservations", e);
        }
    }

    // Mettre à jour une réservation
    public void updateReservation(Reservation reservation) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(reservation);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new EntityPersistenceException("Erreur lors de la mise à jour de la réservation", e);
        }
    }
    public long countSeancesInRange(LocalDateTime start, LocalDateTime end) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select count(s.id) from Seance s where s.dateHeure between :start and :end",
                            Long.class
                    ).setParameter("start", start)
                    .setParameter("end", end)
                    .uniqueResult();
        }
    }

    // Supprimer une réservation
    public void deleteReservation(Long id) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Reservation reservation = session.get(Reservation.class, id);

            if (reservation == null) {
                throw new EntityNotFoundException("Reservation", "ID=" + id);
            }

            session.delete(reservation);
            transaction.commit();

        } catch (EntityNotFoundException e) {
            if (transaction != null) transaction.rollback();
            throw e;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new EntityPersistenceException("Erreur lors de la suppression de la réservation", e);
        }
    }
    public double getRecetteBySeance(Long seanceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Double> q = session.createQuery(
                    "select coalesce(sum(r.nbPlaces * s.tarif.prix), 0) " +
                            "from Reservation r join r.seance s " +
                            "where s.id = :id",
                    Double.class
            );
            q.setParameter("id", seanceId);
            return q.uniqueResult();
        }
    }

    public double getRecetteByPeriode(LocalDateTime start, LocalDateTime end) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Double> q = session.createQuery(
                    "select coalesce(sum(r.nbPlaces * s.tarif.prix), 0) " +
                            "from Reservation r join r.seance s " +
                            "where s.dateHeure >= :start and s.dateHeure <= :end",
                    Double.class
            );
            q.setParameter("start", start);
            q.setParameter("end", end);
            return q.uniqueResult();
        }
    }

    public List<RecetteSeanceDTO> getRecettesParSeance(LocalDateTime start, LocalDateTime end) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<Object[]> rows = session.createQuery(
                            "select " +
                                    " s.id, " +                     // 0
                                    " f.titre, " +                  // 1
                                    " f.genre, " +                  // 2
                                    " sa.nom, " +                   // 3
                                    " s.dateHeure, " +              // 4
                                    " t.prix, " +                   // 5
                                    " coalesce(sum(r.nbPlaces), 0), " +                 // 6
                                    " coalesce(sum(r.nbPlaces * t.prix), 0) " +         // 7
                                    "from Seance s " +
                                    "join s.film f " +
                                    "join s.salle sa " +
                                    "join s.tarif t " +
                                    "left join s.reservations r " +
                                    "where s.dateHeure between :start and :end " +
                                    "group by s.id, f.titre, f.genre, sa.nom, s.dateHeure, t.prix " +
                                    "order by s.dateHeure",
                            Object[].class
                    )
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .list();

            return rows.stream().map(r -> new RecetteSeanceDTO(
                    ((Number) r[0]).longValue(),     // seanceId
                    (String) r[1],                   // filmTitre
                    (String) r[2],                   // genre
                    (String) r[3],                   // salleNom
                    (LocalDateTime) r[4],            // dateHeure
                    ((Number) r[5]).doubleValue(),   // prixTarif
                    ((Number) r[6]).longValue(),     // nbPlacesTotal
                    ((Number) r[7]).doubleValue()    // recette
            )).toList();

        } catch (Exception e) {
            throw new EntityPersistenceException("Erreur calcul recettes par séance", e);
        }
    }
    public List<RecetteSeanceDTO> getRecettesParFilm(Long filmId, LocalDateTime start, LocalDateTime end) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql =
                    "select s.id, f.titre, f.genre, sa.nom, s.dateHeure, t.prix, " +
                            "coalesce(sum(res.nbPlaces), 0), coalesce(sum(res.nbPlaces * t.prix), 0) " +
                            "from Seance s " +
                            "join s.film f " +
                            "join s.salle sa " +
                            "join s.tarif t " +
                            "left join s.reservations res " +
                            "where f.id = :filmId ";

            if (start != null && end != null) {
                hql += "and s.dateHeure between :start and :end ";
            }

            hql += "group by s.id, f.titre, f.genre, sa.nom, s.dateHeure, t.prix " +
                    "order by s.dateHeure";

            Query<Object[]> q = session.createQuery(hql, Object[].class);
            q.setParameter("filmId", filmId);

            if (start != null && end != null) {
                q.setParameter("start", start);
                q.setParameter("end", end);
            }

            List<Object[]> rows = q.list();

            return rows.stream().map(r -> new RecetteSeanceDTO(
                    ((Number) r[0]).longValue(),
                    (String) r[1],
                    (String) r[2],
                    (String) r[3],
                    (LocalDateTime) r[4],
                    ((Number) r[5]).doubleValue(),
                    ((Number) r[6]).longValue(),
                    ((Number) r[7]).doubleValue()
            )).toList();

        } catch (Exception e) {
            throw new EntityPersistenceException("Erreur calcul recettes film", e);
        }
    }

    public RecetteSeanceDTO getRecetteBySeance(long seanceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Object[] r = session.createQuery(
                            "select s.id, f.titre, f.genre, sa.nom, s.dateHeure, t.prix, " +
                                    "coalesce(sum(res.nbPlaces), 0), coalesce(sum(res.nbPlaces * t.prix), 0) " +
                                    "from Seance s " +
                                    "join s.film f " +
                                    "join s.salle sa " +
                                    "join s.tarif t " +
                                    "left join s.reservations res " +
                                    "where s.id = :id " +
                                    "group by s.id, f.titre, f.genre, sa.nom, s.dateHeure, t.prix",
                            Object[].class
                    ).setParameter("id", seanceId)
                    .uniqueResult();

            if (r == null) {
                throw new EntityNotFoundException("Seance", "ID=" + seanceId);
            }

            return new RecetteSeanceDTO(
                    ((Number) r[0]).longValue(),
                    (String) r[1],
                    (String) r[2],
                    (String) r[3],
                    (LocalDateTime) r[4],
                    ((Number) r[5]).doubleValue(),
                    ((Number) r[6]).longValue(),
                    ((Number) r[7]).doubleValue()
            );

        } catch (Exception e) {
            throw new EntityPersistenceException("Erreur calcul recette séance", e);
        }
    }




    // Récupérer toutes les réservations d'une séance donnée
    public List<Reservation> getReservationsBySeance(Long seanceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Reservation> query = session.createQuery(
                    "FROM Reservation r WHERE r.seance.id = :seanceId",
                    Reservation.class
            );
            query.setParameter("seanceId", seanceId);
            return query.list();
        } catch (Exception e) {
            throw new EntityPersistenceException(
                    "Erreur lors de la récupération des réservations pour la séance ID=" + seanceId,
                    e
            );
        }
    }

}
