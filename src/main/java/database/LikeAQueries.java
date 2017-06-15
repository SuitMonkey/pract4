package database;

import modelo.LikeA;

/**
 * Created by Francis Cáceres on 14/6/2017.
 */
public class LikeAQueries extends Manejador<LikeA> {
    private static LikeAQueries instancia;

    public LikeAQueries() {
        super(LikeA.class);
    }

    public static LikeAQueries getInstancia() {
        if(instancia==null){
            instancia = new LikeAQueries();
        }
        return instancia;
    }
}
