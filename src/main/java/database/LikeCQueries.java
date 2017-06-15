package database;

import modelo.LikeC;

/**
 * Created by Francis Cáceres on 14/6/2017.
 */
public class LikeCQueries extends Manejador<LikeC> {
    private static LikeCQueries instancia;

    public LikeCQueries() {
        super(LikeC.class);
    }

    public static LikeCQueries getInstancia() {
        if(instancia==null){
            instancia = new LikeCQueries();
        }
        return instancia;
    }
}
