package modelo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Jesus on 6/14/2017.
 */
@Entity
public class LikeC implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Boolean isLike;
    @ManyToOne
    private Comentario comentario;
    @ManyToOne
    private Usuario usuario;

    public LikeC(){}

    public LikeC(Boolean isLike, Comentario Comentario, Usuario usuario){
        this.isLike = isLike;
        this.comentario = Comentario;
        this.usuario = usuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getIsLike() {
        return isLike;
    }

    public void setIsLike(Boolean like) {
        this.isLike = like;
    }

    public Comentario getComentario() {
        return comentario;
    }

    public void setComentario(Comentario comentario) {
        this.comentario = comentario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
