package main;
/**
 * Created by Francis Cáceres on 3/6/2017.
 */
import freemarker.template.Configuration;
import modelo.Articulo;
import modelo.Comentario;
import modelo.Etiqueta;
import modelo.Usuario;
import spark.ModelAndView;
import spark.Session;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.*;

import static spark.Spark.*;

public class Main {

    public static void main(String [] args)
    {
        staticFileLocation("recursos");

        Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(Main.class, "/templates");
        FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine( configuration );


        //Administradores


        get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            Session session = request.session(true);
            Boolean usuario = session.attribute("sesion");

            attributes.put("user",(session.attribute("currentUser")==null)?new Usuario("","","",false,false):((Usuario) session.attribute("currentUser")));

            Boolean admin =session.attribute("admin");
            //System.out.println(" "+ session.attribute("usuario"));
            attributes.put("sesion","false");

            if(admin!=null) {
                if(admin) {
                    attributes.put("greetings","Saludos Administardor.");
                    attributes.put("sesion","true");
                }
            }
            else
            {
                if(usuario!=null){
                    if(usuario) {
                        attributes.put("greetings","Saludos usuario mortal.");
                        attributes.put("sesion","true");
                    }
                }
                else {
                    attributes.put("greetings","");
                    attributes.put("estado","fuera");
                }
            }

            attributes.put("articulos",bd.getArticulos());


            return new ModelAndView(attributes, "home.ftl");
        }, freeMarkerEngine);

        post("/", (request, response) -> {
            Session sesion = request.session(true);

            Map<String, Object> attributes = new HashMap<>();

            attributes.put("sesion","true");

            attributes.put("user",(sesion.attribute("currentUser")==null)?new Usuario("","","",false,false):((Usuario) sesion.attribute("currentUser")));

            String insertArt = request.queryParams("crearArt");
            String elimArt = request.queryParams("eliminarArt");

            if(insertArt != null) {
                String titulo = request.queryParams("titulo");
                String texto = request.queryParams("area-articulo");
                String etiquetas = request.queryParams("area-etiqueta");
                ArrayList<Etiqueta> etiq = new ArrayList<Etiqueta>();
                for (String eti : etiquetas.split(",")) {
                    etiq.add(new Etiqueta(0, eti));
                    // System.out.println(eti);
                }


                Articulo art = new Articulo(15, titulo, texto, sesion.attribute("currentUser"), null, null, etiq);
                bd.insertarArticulo(art);
            }
            else {
                if (elimArt != null)
                {
                    int elim = Integer.parseInt(request.queryParams("elim"));

                    //System.out.println(elim);
                    bd.eliminarArticulo(elim);


                }

            }
            attributes.put("articulos",bd.getArticulos());
            return new ModelAndView(attributes, "home.ftl");
        }, freeMarkerEngine);

        get("/articulos", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            Session sesion = request.session(true);

            attributes.put("sesion",(sesion.attribute("sesion")==null)?"false":sesion.attribute("sesion").toString());

            attributes.put("user",(sesion.attribute("currentUser")==null)?new Usuario("","","",false,false):((Usuario) sesion.attribute("currentUser")));

            int id = Integer.valueOf(request.queryParams("id"));


            attributes.put("comentarios",bd.getComentariosArt(id));
            attributes.put("articulo",bd.getArticulo(id));
            attributes.put("id",request.queryParams("id"));
            attributes.put("etiquetas",bd.getEtiquetasArt(id));


            return new ModelAndView(attributes, "articulo.ftl");
        }, freeMarkerEngine);

        post("/articulos", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            Session sesion = request.session(true);
            attributes.put("sesion","true");

            attributes.put("user",(sesion.attribute("currentUser")==null)?"false":((Usuario) sesion.attribute("currentUser")));

            String editarArt = null;
            editarArt = (request.queryParams("editarArt")==null)?"null": "nonull";
            String elimC = request.queryParams("eliminarComentario");
            String comen = request.queryParams("comentario");
            int id = Integer.parseInt(request.queryParams("idArticulo"));
            //System.out.println("holaaaerrror");


            if(editarArt.equals("nonull")) {
                String titulo = request.queryParams("titulo");
                String texto = request.queryParams("area-articulo");
                String etiquetas = request.queryParams("area-etiqueta");
                int idArt = Integer.parseInt(request.queryParams("idArticulo"));
                ArrayList<Etiqueta> etiq = new ArrayList<Etiqueta>();
                for (String eti : etiquetas.split(",")) {
                    etiq.add(new Etiqueta(0, eti));
                    //System.out.println(eti);
                }
                Articulo art = new Articulo(idArt, titulo, texto, sesion.attribute("currentUser"), null, null, etiq);
                //System.out.println(art.getId()+ " "+art.getTitulo());
                bd.actualizarArticulo(art);
            }
            else{
                //System.out.println("break");
                if(elimC!=null)
                {
                    bd.eliminarComentario(Integer.valueOf(request.queryParams("eliminarComentarioV")));
                }
                else {
                    if (comen != null || !comen.equals("")) {
                        Comentario com = new Comentario(0, comen, ((Usuario)sesion.attribute("currentUser")), bd.getArticulo(id));
                        bd.insertarComentario(com, id);

                    }
                }
            }


            attributes.put("articulo",bd.getArticulo(id));
            attributes.put("comentarios",bd.getComentariosArt(id));
            attributes.put("id",id);
            attributes.put("etiquetas",bd.getEtiquetasArt(id));

            return new ModelAndView(attributes, "articulo.ftl");
        }, freeMarkerEngine);

        get("/login", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            return new ModelAndView(attributes, "login.ftl");
        }, freeMarkerEngine);

        post("/validacion", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            Session session=request.session(true);

            if(session.attribute("sesion"))
            {
                Usuario u= bd.getUsuario(request.queryParams("user"));

                attributes.put("message","Bienvenido " + u.getNombre());
                attributes.put("redireccionar", "si");
            }
            else
            {
                attributes.put("message", "Username o password incorrectos.");
                attributes.put("redireccionar", "no");

            }

            //response.redirect("/zonaadmin/");
            return new ModelAndView(attributes, "validacion.ftl");
        }, freeMarkerEngine);

        get("/administrarUsuarios", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();


            attributes.put("usuarios",bd.getUsuarios());

            return new ModelAndView(attributes, "administrarUsuarios.ftl");
        }, freeMarkerEngine);

        post("/administrarUsuarios", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            if(request.queryParams("elim")!=null)
            {
                String usernam = request.queryParams("elim");
                //System.out.println(usernam);
                bd.eliminarUsuario(usernam);
            }
            else
            {

                String user = request.queryParams("user");
                String nombre = request.queryParams("nombre");
                String pass = request.queryParams("pass");
                Boolean admin = (request.queryParams("admin") ==null)? false:true;

                //System.out.println(request.queryParams("admin"));
                Usuario usuario = new Usuario(user,nombre,pass,admin,true);
                bd.insertarUsuario(usuario);
            }

            attributes.put("usuarios",bd.getUsuarios());

            return new ModelAndView(attributes, "administrarUsuarios.ftl");
        }, freeMarkerEngine);

        before("/validacion",(request, response) -> {
            Session session=request.session(true);

            String user = request.queryParams("user");
            String pass = request.queryParams("pass");

            if(bd.goodUsernamePassword(user,pass))
            {
                session.attribute("sesion", true);
                session.attribute("currentUser", bd.getUsuario(user));
            }
            else
                session.attribute("sesion", false);
            //response.redirect("/zonaadmin/");



        });

        get("/clear", (request, response) -> {
            request.session().removeAttribute("sesion");
            request.session().removeAttribute("currentUser");
            response.redirect("/");
            return null;
        });

    }
}