package database;

import main.Main;
import modelo.Articulo;
import modelo.Etiqueta;
import modelo.LikeA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francis Cáceres on 14/6/2017.
 */
public class ArticulosQueries extends Manejador<Articulo> {
    private static ArticulosQueries instancia;

    public ArticulosQueries(){super(Articulo.class);}

    public static ArticulosQueries getInstancia(){
        if(instancia == null){
            instancia = new ArticulosQueries();
        }
        return instancia;
    }

    public List<Articulo> findAllSorted(){
        EntityManager em = getEntityManager();
        Query query = em.createNamedQuery("Articulo.findAllSorted");
        List<Articulo> lista = query.getResultList();
        return lista;
    }

    public List<Articulo> findLimitedSorted(){
        EntityManager em = getEntityManager();
        Query query = em.createQuery("SELECT a FROM Articulo a order by  a.fecha desc")
                .setFirstResult(Main.pa)
                .setMaxResults(5);
        List<Articulo> lista = query.getResultList();
        return lista;
    }

    public List<Articulo> findAllByTagsSorted(String tag){
        List<Articulo> listaA = new ArrayList<>();
        List<Articulo> bubble = ArticulosQueries.getInstancia().findAll();

        for (Articulo a : bubble){
            for (Etiqueta e : a.getListaEtiqueta()){
                if(e.getEtiqueta().equals(tag)){
                    listaA.add(a);
                    break;
                }
            }
        }
        return listaA;
    }

    public void noLike(Long idA, int idL){
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            Articulo ar = em.find(Articulo.class,idA);

            for (LikeA a : ar.getLikes()){
                if(a.getId() == idL){
                    ar.getLikes().clear();
                    LikeAQueries.getInstancia().eliminar(a.getId());
                    System.out.println(ar.getLikes().remove(a));
                    break;
                }
            }
            em.getTransaction().commit();
        }catch (Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally {
            em.close();
        }
    }
}
