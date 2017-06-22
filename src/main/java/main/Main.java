package main;
/**
 * Created by Francis Cáceres on 3/6/2017.
 */
import database.*;
import freemarker.template.Configuration;
import modelo.*;
import spark.ModelAndView;
import spark.Session;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.*;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

//TODO: Teminar de arreglar el main
public class Main {
    public static int pa = 0;

    public static void main(String [] args)
    {
        staticFileLocation("recursos");
        enableDebugScreen();

        Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(Main.class, "/templates");
        FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine( configuration );

        //Administradores
        //UsuarioQueries.getInstancia().crear(new Usuario("f", "Francis Cáceres", "1234", true));


        get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            Session session = request.session(true);
            Boolean usuario = session.attribute("sesion");

            attributes.put("user",(session.attribute("currentUser")==null)?new Usuario("","","",false):((Usuario) session.attribute("currentUser")));

            Boolean admin = session.attribute("admin");

            attributes.put("sesion","false");


            if(admin!=null) {
                if(admin) {
                    attributes.put("sesion","true");
                }
            }
            else
            {
                if(usuario!=null){
                    if(usuario) {
                        attributes.put("sesion","true");
                    }
                }
                else {
                    attributes.put("estado","fuera");
                }
            }

            int pagina = 1;
//            paginacion(ArticulosQueries.getInstancia().findAllSorted(),pagina)
            List<Articulo> ar = ArticulosQueries.getInstancia().findLimitedSorted();
            attributes.put("articulos",ar);


            int[] paginas = new int[(int)getCantPag(ArticulosQueries.getInstancia().findAllSorted().size())];
            for (int i = 1; i <= paginas.length; i++ ){
                if(pagina == i){
                    continue;
                }
                paginas[i-1] = i;
            }

            attributes.put("irAdelante","si");
            attributes.put("paginaActual","1");

            attributes.put("paginas",paginas);

            return new ModelAndView(attributes, "home.ftl");
        }, freeMarkerEngine);

        post("/", (request, response) -> {
            Session sesion = request.session(true);
            Map<String, Object> attributes = new HashMap<>();

            String insertArt = request.queryParams("crearArt");
            String elimArt = request.queryParams("eliminarArt");

            String busqueda = request.queryParams("busqueda");
            if(busqueda != null){
                List<Etiqueta> tag = EtiquetaQueries.getInstancia().findAll();
                if(tag != null && tag.size()>0){

                    response.redirect("/tags/"+busqueda+"/page/1");
                }else attributes.put("EtiqNotFound","Etiqueta no encontrada.");

                return new ModelAndView(attributes, "home.ftl");
            }

            if(insertArt != null) {
                String titulo = request.queryParams("titulo");
                String texto = request.queryParams("area-articulo");
                String etiquetas = request.queryParams("area-etiqueta");
                ArrayList<Etiqueta> etiq = new ArrayList<Etiqueta>();
                for (String eti : etiquetas.split(",")) {
                    etiq.add(new Etiqueta(eti));
                    // System.out.println(eti);
                }


                Articulo art = new Articulo( titulo, texto, sesion.attribute("currentUser"), new ArrayList<Comentario>(), etiq,new ArrayList<LikeA>());
                ArticulosQueries.getInstancia().crear(art);
                for (String eti : etiquetas.split(",")) {
                    EtiquetaQueries.getInstancia().crear(new Etiqueta(eti, (Articulo) ArticulosQueries.getInstancia().find(art.getId())));
                }
            }
            else {
                if (elimArt != null)
                {
                    Long elim = Long.parseLong(request.queryParams("elim"));
                    ArticulosQueries.getInstancia().eliminar(elim);
                }

            }
            response.redirect("/");
            return null;
        }, freeMarkerEngine);

        get("/page/:pagina", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            Session session = request.session(true);
            Boolean usuario = session.attribute("sesion");
            attributes.put("user",(session.attribute("currentUser")==null)?new Usuario("","","",false):((Usuario) session.attribute("currentUser")));

            int pagina = Integer.valueOf(request.params("pagina"));

            Boolean admin =session.attribute("admin");
            attributes.put("sesion","false");

            if(admin!=null) {
                if(admin) {
                    attributes.put("greetings","Saludos Administardor.");
                    attributes.put("sesion","true");
                }
            }
            else {
                if(usuario!=null){
                    if(usuario)
                        attributes.put("sesion","true");
                }
                else
                    attributes.put("estado","fuera");
            }

            //List<Articulo> articulos = paginacion(ArticulosQueries.getInstancia().findAllSorted(),pagina);
            pa = 5 * (pagina-1);
            List<Articulo>articulos = ArticulosQueries.getInstancia().findLimitedSorted();
            attributes.put("articulos",articulos);
            pa = 0;

            //paginacion
//            if(pagina == 0 && getCantPag(articulos.size())>1)
//                attributes.put("irAdelante","si");
//            else attributes.put("irAdelante","no");
//
//            if(pagina != 0&& pagina==(int)getCantPag(articulos.size()-1))
//                attributes.put("irAtras","si");
//            else attributes.put("irAtras","no");


            int [] paginas = new int [(int)getCantPag(ArticulosQueries.getInstancia().findAllSorted().size())];
            for(int i = 1 ;i <= paginas.length;i++)
            {
                if(pagina == i)
                    continue;
                paginas[i-1]= i;
            }

//            attributes.put("paginaActual",Integer.toString(pagina));

            attributes.put("paginas",paginas);
            return new ModelAndView(attributes, "page.ftl");
        }, freeMarkerEngine);

        get("tags/:tag/page/:pagina", (request, response) -> {

            Map<String, Object> attributes = new HashMap<>();

            Session session = request.session(true);
            Boolean usuario = session.attribute("sesion");
            attributes.put("user",(session.attribute("currentUser")==null)?new Usuario("","","",false):((Usuario) session.attribute("currentUser")));

            int pagina = Integer.valueOf(request.params("pagina"));

            Boolean admin =session.attribute("admin");
            attributes.put("sesion","false");

            if(admin!=null) {
                if(admin) {
                    attributes.put("sesion","true");
                }
            }
            else {
                if(usuario!=null){
                    if(usuario)
                        attributes.put("sesion","true");
                }
                else
                    attributes.put("estado","fuera");
            }

            String tag = request.params("tag");
            List<Articulo> articulos = paginacion(ArticulosQueries.getInstancia().findAllByTagsSorted(tag),pagina);
            attributes.put("articulos",articulos);

            //paginacion
            if(pagina == 0 && getCantPag(articulos.size())>1)
                attributes.put("irAdelante","si");
            else attributes.put("irAdelante","no");

            if(pagina != 0&& pagina==(int)getCantPag(articulos.size()-1))
                attributes.put("irAtras","si");
            else attributes.put("irAtras","no");


            int [] paginas = new int [(int)getCantPag(articulos.size())];
            for(int i = 1 ;i <= paginas.length;i++)
            {
                if(pagina== i)
                    continue;
                paginas[i-1]= i;
            }

            attributes.put("paginaActual",Integer.toString(pagina));
            attributes.put("tag",tag);

            attributes.put("paginas",paginas);
            return new ModelAndView(attributes, "pageT.ftl");
        }, freeMarkerEngine);

        get("/articulos", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            Session sesion = request.session(true);

            attributes.put("sesion",(sesion.attribute("sesion")==null)?"false":sesion.attribute("sesion").toString());

            attributes.put("user",(sesion.attribute("currentUser")==null)?new Usuario("","","",false):((Usuario) sesion.attribute("currentUser")));

            Long id = Long.valueOf(request.queryParams("id"));

            Articulo ar = ArticulosQueries.getInstancia().find(id);
            attributes.put("comentarios",ar.getListaComentario());
            attributes.put("articulo",ar);
            attributes.put("id",request.queryParams("id"));
            attributes.put("etiquetas",ar.getListaEtiqueta());

            //Parte de Likes
            int totalLA = 0,totalDA = 0;

            for(LikeA l : LikeAQueries.getInstancia().findAll()) {
                if(l.getIsLike() && l.getArticulo().getId() == id) {
                    totalLA++;
                }
                if(!l.getIsLike() && l.getArticulo().getId() == id) {
                    totalDA++;
                }
            }

            String LikeArticulo = null;
            if(sesion.attribute ("currentUser") != null){
                for(LikeA lc : ar.getLikes()) {
                    if(lc.getUsuario().getUsername().equals(((Usuario) sesion.attribute ("currentUser")).getUsername())){
                        if(lc.getIsLike()) {
                            LikeArticulo = "Like";
                            attributes.put("dioLike", "");
                        }
                        else {
                            LikeArticulo = "disLike";
                            attributes.put("dioDisLike","");
                        }
                        break;
                    }
                }
            }

            if(LikeArticulo == null)
            {
                attributes.put("aunNada",totalLA);
                LikeArticulo = "noLike";
            }

            attributes.put("totalLA",totalLA);
            attributes.put("totalDA",totalDA);


            return new ModelAndView(attributes, "articulo.ftl");
        }, freeMarkerEngine);

        post("/articulos", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            Session sesion = request.session(true);


            String editarArt = (request.queryParams("editarArt")==null)?"null": "nonull";
            String elimC = request.queryParams("eliminarComentario");
            String comen = request.queryParams("comentario");
            Long id = Long.valueOf(request.queryParams("idArticulo"));

            if(editarArt.equals("nonull")) {
                Articulo editArt = ArticulosQueries.getInstancia().find(id);
                List<Etiqueta> rep = editArt.getListaEtiqueta();
                limpiaEtiq(rep);
                String titulo = request.queryParams("titulo");
                String texto = request.queryParams("area-articulo");
                String etiquetas = request.queryParams("area-etiqueta");
                ArrayList<Etiqueta> etiq = new ArrayList<>();
                for (String eti : etiquetas.split(",")) {
                    if(eti.equals(" ")) {
                        continue;
                    }
                    etiq.add(new Etiqueta(eti));
                    EtiquetaQueries.getInstancia().crear(new Etiqueta(eti,ArticulosQueries.getInstancia().find(id)));
                }
                Articulo art = new Articulo( titulo, texto, sesion.attribute("currentUser"), new ArrayList<>(), etiq, new ArrayList<>());
                art.setId(id);
                ArticulosQueries.getInstancia().editar(art);
            }
            else{
                if(elimC!=null)
                {
                    int idC =Integer.valueOf(request.queryParams("eliminarComentarioV"));
                    ComentarioQueries.getInstancia().eliminar(idC);
                }
                else {
                    if (comen != null || !comen.equals("")) {
                        Comentario com = new Comentario(comen, sesion.attribute("currentUser"),ArticulosQueries.getInstancia().find(id), new ArrayList<>());
                        ComentarioQueries.getInstancia().crear(com);

                    }
                }
            }

            response.redirect("/articulos?id="+id);

            return null;
        }, freeMarkerEngine);

        get("/articulos/:art/:like", (request, response) -> {
            Session sesion = request.session(true);
            String mode = request.params("like");
            Articulo art = ArticulosQueries.getInstancia().find(Long.valueOf(request.params("art")));

            //elim viejo like
            int idLike = (art.getTHeLike(sesion.attribute("currentUser")));

            if(idLike!=-1){
                ArticulosQueries.getInstancia().noLike(art.getId(),idLike);
            }

            if("likeA".equals(mode)) {
                LikeA like = new LikeA(true,art,sesion.attribute("currentUser"));
                art.getLikes().add(like);
                LikeAQueries.getInstancia().crear(like);
            }
            else if ("dislikeA".equals(mode)) {

                LikeA like = new LikeA(false,art,sesion.attribute("currentUser"));
                art.getLikes().add(like);
                LikeAQueries.getInstancia().crear(like);
            }

            response.redirect("/articulos?id="+art.getId());

            return null;
        }, freeMarkerEngine);

        get("/articulos/:art/:co/:like", (request, response) -> {
            Session sesion = request.session(true);
            String mode = request.params("like");
            Articulo art = ArticulosQueries.getInstancia().find(Long.valueOf(request.params("art")));
            Comentario comentario = ComentarioQueries.getInstancia().find(Integer.valueOf(request.params("co")));

            ComentarioQueries.getInstancia().noLikeC(comentario.getId(), sesion.attribute("currentUser"));
            if("likeC".equals(mode)) {
                LikeC like = new LikeC(true,comentario,sesion.attribute("currentUser"));
                LikeCQueries.getInstancia().crear(like);
            }
            else  if("dislikeC".equals(mode)) {
                LikeC like = new LikeC(false,comentario,sesion.attribute("currentUser"));
                LikeCQueries.getInstancia().crear(like);
            }

            response.redirect("/articulos?id="+art.getId());

            return null;
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
                Usuario u = UsuarioQueries.getInstancia().find(request.queryParams("user"));

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
            attributes.put("usuarios",UsuarioQueries.getInstancia().findAll());

            return new ModelAndView(attributes, "administrarUsuarios.ftl");
        }, freeMarkerEngine);

        post("/administrarUsuarios", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            if(request.queryParams("elim")!=null)
            {
                String usernam = request.queryParams("elim");
                UsuarioQueries.getInstancia().eliminar(new Usuario(usernam,"","",false));
            }
            else
            {

                String user = request.queryParams("user");
                String nombre = request.queryParams("nombre");
                String pass = request.queryParams("pass");
                Boolean admin = (request.queryParams("admin") ==null)? false:true;

                Usuario usuario = new Usuario(user,nombre,pass,admin);
                UsuarioQueries.getInstancia().crear(usuario);
            }

            attributes.put("usuarios",UsuarioQueries.getInstancia().findAll());

            return new ModelAndView(attributes, "administrarUsuarios.ftl");
        }, freeMarkerEngine);

        before("/validacion",(request, response) -> {
            Session session=request.session(true);

            String user = request.queryParams("user");
            String pass = request.queryParams("pass");
            Usuario comprobar = UsuarioQueries.getInstancia().find(user);
            if(comprobar!=null) {
                if (comprobar.getPassword().equals(pass)) {
                    session.attribute("sesion", true);
                    session.attribute("currentUser", comprobar);
                }
            }
            else session.attribute("sesion", false);

        });

        before("/page/:pagina",(request, response) -> {
            int pagina = Integer.valueOf(request.params(":pagina"));
            int max = (int)getCantPag(ArticulosQueries.getInstancia().findAllSorted().size());
            if(pagina<1)
                response.redirect("/");
            else
            if(pagina> max)
                response.redirect("/page/"+max);
        });

        get("/clear", (request, response) -> {
            request.session().removeAttribute("sesion");
            request.session().removeAttribute("currentUser");
            response.redirect("/");
            return null;
        });

    }

    public static List<Articulo> paginacion(List<Articulo> la, int pagina)
    {
        List<Articulo> articulosPagina = new ArrayList<>();

        int rate = 5 *(pagina-1);
        for(int i =  rate; i < rate+5 && i< la.size(); i++ )
        {
            articulosPagina.add(la.get(i));
        }
        return articulosPagina;
    }

    public static double getCantPag(int size)
    {
        return Math.ceil(  ((double)size)/ 5 );
    }

    public static void limpiaEtiq(List<Etiqueta> le) {
        for(Etiqueta e : le) {
            EtiquetaQueries.getInstancia().eliminar(e.getId());
        }
    }


}