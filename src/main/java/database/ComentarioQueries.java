package database;

import modelo.Comentario;
import modelo.LikeC;
import modelo.Usuario;

import javax.persistence.EntityManager;

/**
 * Created by Francis Cáceres on 14/6/2017.
 */
public class ComentarioQueries extends Manejador<Comentario> {
    private static ComentarioQueries instancia;

    public ComentarioQueries() {
        super(Comentario.class);
    }

    public static ComentarioQueries getInstancia() {
        if(instancia==null){
            instancia = new ComentarioQueries();
        }
        return instancia;
    }

    public void noLikeC(int idCom, Usuario us){
        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        try {
            int idLike = 0;
            Comentario co = em.find(Comentario.class,idCom);
            for (LikeC c : co.getLikes()){
                if(c.getUsuario().getUsername().equals(us.getUsername())){
                    LikeCQueries.getInstancia().eliminar(c.getId());
                    us.getLikesC().remove(c);
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
