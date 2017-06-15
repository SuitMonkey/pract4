package database;

import modelo.Etiqueta;

/**
 * Created by Francis Cáceres on 14/6/2017.
 */
public class EtiquetaQueries extends Manejador<Etiqueta> {
    private static EtiquetaQueries instancia;

    public EtiquetaQueries() {
        super(Etiqueta.class);
    }

    public static EtiquetaQueries getInstancia() {
        if(instancia==null){
            instancia = new EtiquetaQueries();
        }
        return instancia;
    }
}
