package database;

import modelo.Articulo;
import modelo.Etiqueta;
import modelo.LikeA;

import javax.persistence.EntityManager;
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

    public List<Articulo> findAllByTagsSorted(String tag){
        List<Articulo> lista = new ArrayList<>();
        List<Articulo> bubble = ArticulosQueries.getInstancia().findAll();

        for (Articulo a : bubble){
            for (Etiqueta e : a.getListaEtiqueta()){
                if(e.getEtiqueta().equals(tag)){
                    lista.add(a);
                }
            }
        }
        return lista;
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
