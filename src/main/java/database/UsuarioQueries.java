package database;

import modelo.Usuario;

/**
 * Created by Francis Cáceres on 15/6/2017.
 */
public class UsuarioQueries extends Manejador<Usuario> {
    private static UsuarioQueries instancia;

    public UsuarioQueries() {
        super(Usuario.class);
    }

    public static UsuarioQueries getInstancia() {
        if(instancia==null){
            instancia = new UsuarioQueries ();
        }
        return instancia;
    }
}
