package ma.cinefx.Dao;

import ma.cinefx.Exception.EntityAlreadyExistsException;
import ma.cinefx.Exception.EntityNotFoundException;
import ma.cinefx.Exception.EntityPersistenceException;
import ma.cinefx.Model.Seance;
import ma.cinefx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class SeanceDAO {

    // ✅ Ajouter une séance (corrigé)
    public void addSeance(Seance seance) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                // Vérifier si la salle a déjà une séance à la même dateHeure
                Query<Seance> query = session.createQuery(
                        "FROM Seance s WHERE s.salle.id = :salleId AND s.dateHeure = :dateHeure",
                        Seance.class
                );
                query.setParameter("salleId", seance.getSalle().getId());
                query.setParameter("dateHeure", seance.getDateHeure());

                if (!query.list().isEmpty()) {
                    throw new EntityAlreadyExistsException("Seance",
                            "Salle ID=" + seance.getSalle().getId() + " à " + seance.getDateHeure());
                }

                // (Optionnel mais utile si jamais Film/Salle/Tarif sont "detached")
                seance.setFilm(session.merge(seance.getFilm()));
                seance.setSalle(session.merge(seance.getSalle()));
                seance.setTarif(session.merge(seance.getTarif()));

                session.save(seance);
                tx.commit();

            } catch (EntityAlreadyExistsException e) {
                tx.rollback(); // ✅ rollback pendant session ouverte
                throw e;
            } catch (Exception e) {
                tx.rollback(); // ✅ rollback pendant session ouverte
                throw new EntityPersistenceException("Erreur lors de l'ajout de la séance", e);
            }
        }
    }

    // ✅ Recherche séances (film, date, genre)
    public List<Seance> searchSeances(String filmTitre, LocalDate date, String genre) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            StringBuilder hql = new StringBuilder(
                    "select distinct s from Seance s " +
                            "join fetch s.film f " +
                            "join fetch s.salle sa " +
                            "join fetch s.tarif t " +
                            "where 1=1 "
            );

            if (filmTitre != null && !filmTitre.trim().isEmpty()) {
                hql.append(" and lower(f.titre) like :titre ");
            }
            if (genre != null && !genre.trim().isEmpty()) {
                // ✅ tu peux aussi mettre like si tu veux genre partiel
                hql.append(" and lower(f.genre) = :genre ");
            }
            if (date != null) {
                hql.append(" and s.dateHeure >= :d1 and s.dateHeure < :d2 ");
            }

            hql.append(" order by s.dateHeure ");

            Query<Seance> q = session.createQuery(hql.toString(), Seance.class);

            if (filmTitre != null && !filmTitre.trim().isEmpty()) {
                q.setParameter("titre", "%" + filmTitre.trim().toLowerCase() + "%");
            }
            if (genre != null && !genre.trim().isEmpty()) {
                q.setParameter("genre", genre.trim().toLowerCase());
            }
            if (date != null) {
                LocalDateTime d1 = date.atStartOfDay();
                LocalDateTime d2 = date.plusDays(1).atStartOfDay();
                q.setParameter("d1", d1);
                q.setParameter("d2", d2);
            }

            return q.list();

        } catch (Exception e) {
            throw new EntityPersistenceException("Erreur lors de la recherche des séances", e);
        }
    }

    // ✅ Récupérer une séance par ID
    public Seance getSeanceById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Seance seance = session.get(Seance.class, id);
            if (seance == null) {
                throw new EntityNotFoundException("Seance", "ID=" + id);
            }
            return seance;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new EntityPersistenceException("Erreur lors de la récupération de la séance", e);
        }
    }

    // ✅ Lister toutes les séances
    public List<Seance> getAllSeances() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Seance", Seance.class).list();
        } catch (Exception e) {
            throw new EntityPersistenceException("Erreur lors de la récupération des séances", e);
        }
    }

    // ✅ Mettre à jour une séance (corrigé pareil)
    public void updateSeance(Seance seance) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                session.update(seance);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw new EntityPersistenceException("Erreur lors de la mise à jour de la séance", e);
            }
        }
    }

    // ✅ Supprimer une séance (corrigé pareil)
    public void deleteSeance(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                Seance seance = session.get(Seance.class, id);
                if (seance == null) {
                    throw new EntityNotFoundException("Seance", "ID=" + id);
                }

                session.delete(seance);
                tx.commit();

            } catch (EntityNotFoundException e) {
                tx.rollback();
                throw e;
            } catch (Exception e) {
                tx.rollback();
                throw new EntityPersistenceException("Erreur lors de la suppression de la séance", e);
            }
        }
    }
}
