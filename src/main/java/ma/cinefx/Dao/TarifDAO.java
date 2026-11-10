package ma.cinefx.Dao;

import ma.cinefx.Exception.EntityAlreadyExistsException;
import ma.cinefx.Exception.EntityNotFoundException;
import ma.cinefx.Exception.EntityPersistenceException;
import ma.cinefx.Model.Tarif;
import ma.cinefx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class TarifDAO {

    // Ajouter un tarif
    public void addTarif(Tarif tarif) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Vérifier l'unicité du libellé
            Query<Tarif> query = session.createQuery("FROM Tarif WHERE libelle = :libelle", Tarif.class);
            query.setParameter("libelle", tarif.getLibelle());

            if (!query.list().isEmpty()) {
                throw new EntityAlreadyExistsException("Tarif", tarif.getLibelle());
            }

            session.save(tarif);
            transaction.commit();

        } catch (EntityAlreadyExistsException e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new EntityPersistenceException("Erreur lors de l'ajout du tarif", e);
        }
    }

    // Récupérer un tarif par ID
    public Tarif getTarifById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Tarif tarif = session.get(Tarif.class, id);
            if (tarif == null) {
                throw new EntityNotFoundException("Tarif", "ID=" + id);
            }
            return tarif;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new EntityPersistenceException("Erreur lors de la récupération du tarif", e);
        }
    }

    // Lister tous les tarifs
    public List<Tarif> getAllTarifs() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Tarif", Tarif.class).list();
        } catch (Exception e) {
            throw new EntityPersistenceException("Erreur lors de la récupération des tarifs", e);
        }
    }

    // Mettre à jour un tarif
    public void updateTarif(Tarif tarif) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(tarif);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new EntityPersistenceException("Erreur lors de la mise à jour du tarif", e);
        }
    }

    // Supprimer un tarif
    public void deleteTarif(Long id) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Tarif tarif = session.get(Tarif.class, id);
            if (tarif == null) {
                throw new EntityNotFoundException("Tarif", "ID=" + id);
            }
            session.delete(tarif);
            transaction.commit();
        } catch (EntityNotFoundException e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new EntityPersistenceException("Erreur lors de la suppression du tarif", e);
        }
    }
}
