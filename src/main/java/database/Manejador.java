package database;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

/**
 * Created by Francis Cáceres on 14/6/2017.
 */
public class Manejador<T> {
    private static EntityManagerFactory emf;
    private Class<T> claseEntidad;

    public Manejador(Class<T> claseEntidad){
        if(emf == null){
            emf = Persistence.createEntityManagerFactory("MiUnidadPersistencia");
        }
        this.claseEntidad = claseEntidad;
    }

    public EntityManager getEntityManager(){return emf.createEntityManager();}

    public void crear(T entidad){
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(entidad);
            em.getTransaction().commit();
        }catch (Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally {
            em.close();
        }
    }

    public void editar(T entidad){
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            em.merge(entidad);
            em.getTransaction().commit();
        }catch (Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally {
            em.close();
        }
    }

    public void eliminar(Object entidadId){
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            T entidad = em.find(claseEntidad,entidadId);
            em.remove(entidad);
        }catch (Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally {
            em.close();
        }
    }

    public T find(Object id){
        EntityManager em = getEntityManager();

        try {
            return em.find(claseEntidad,id);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            em.close();
        }
        return null;
    }

    public List<T> findAll(){
        EntityManager em = getEntityManager();
        try{
            javax.persistence.criteria.CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(claseEntidad);
            criteriaQuery.select(criteriaQuery.from(claseEntidad));
            return em.createQuery(criteriaQuery).getResultList();
        } catch (Exception ex){
            ex.printStackTrace();
        }finally {
            em.close();
        }
        return null; //Agregado por que me daba error sin un return
    }

}
