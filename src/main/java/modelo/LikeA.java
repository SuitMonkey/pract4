package modelo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Jesus on 6/14/2017.
 */
@Entity
public class LikeA implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private boolean isLike;
    @ManyToOne
    private Articulo articulo;
    @ManyToOne
    private Usuario usuario;

    public LikeA(){}

    public LikeA(Boolean isLike, Articulo articulo, Usuario usuario){
        this.isLike = isLike;
        this.articulo = articulo;
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
        like = like;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
