package ma.cinefx.Dao;

import ma.cinefx.Exception.EntityAlreadyExistsException;
import ma.cinefx.Exception.EntityNotFoundException;
import ma.cinefx.Exception.EntityPersistenceException;
import ma.cinefx.Model.Salle;
import ma.cinefx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class SalleDAO {

    // Ajouter une salle
    public void addSalle(Salle salle) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Vérifier l'unicité du nom
            Query<Salle> query = session.createQuery("FROM Salle WHERE nom = :nom", Salle.class);
            query.setParameter("nom", salle.getNom());

            if (!query.list().isEmpty()) {
                throw new EntityAlreadyExistsException("Salle", salle.getNom());
            }

            session.save(salle);
            transaction.commit();

        } catch (EntityAlreadyExistsException e) {
            if (transaction != null) transaction.rollback();
            throw e;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new EntityPersistenceException("Erreur lors de l'ajout de la salle", e);
        }
    }

    // Trouver une salle par ID
    public Salle getSalleById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Salle salle = session.get(Salle.class, id);

            if (salle == null) {
                throw new EntityNotFoundException("Salle", "ID=" + id);
            }

            return salle;

        } catch (EntityNotFoundException e) {
            throw e;

        } catch (Exception e) {
            throw new EntityPersistenceException("Erreur lors de la récupération de la salle", e);
        }
    }

    // Lister toutes les salles
    public List<Salle> getAllSalles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Salle", Salle.class).list();
        } catch (Exception e) {
            throw new EntityPersistenceException("Erreur lors de la récupération de la liste des salles", e);
        }
    }

    // Mettre à jour une salle
    public void updateSalle(Salle salle) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(salle);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new EntityPersistenceException("Erreur lors de la mise à jour de la salle", e);
        }
    }

    // Supprimer une salle
    public void deleteSalle(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Salle salle = session.get(Salle.class, id);

            if (salle == null) {
                throw new EntityNotFoundException("Salle", "ID=" + id);
            }

            session.delete(salle);
            transaction.commit();

        } catch (EntityNotFoundException e) {
            if (transaction != null) transaction.rollback();
            throw e;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new EntityPersistenceException("Erreur lors de la suppression de la salle", e);
        }
    }
}
