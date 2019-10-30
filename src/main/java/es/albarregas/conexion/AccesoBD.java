/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.albarregas.conexion;



import es.albarregas.beans.Usuario;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author franciscoantonio
 */
@WebServlet(name = "AccesoBD", urlPatterns = {"/AccesoBD"})
public class AccesoBD extends HttpServlet {
    
    DataSource dataSource;
    //conexión a la base de dagtos.    
    public void init (ServletConfig config){
        try{
        Context contextoInicial=new InitialContext();
        DataSource dataSource=(DataSource)contextoInicial.lookup("java:comp/env/jdbc/Registro");
        }catch(NamingException ex){
            System.out.println("Se ha producido un error en la conexión a la base de datos.");
            ex.printStackTrace();
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException ontextif a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
         response.setContentType("text/html;charset=UTF-8");

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         processRequest(request, response);
        Connection conexion = null;
        Statement sentencia = null;
        PreparedStatement preparada = null;
        ResultSet resultado = null;
        Usuario usuario = null;
        List<Usuario> listado = null;

        String anilla = request.getParameter("anilla");
        String sql = null;
        String url = null;
        

        
        
        
        try {
            //Se Carga el controlador para acceder a la BD.
            Class.forName("com.mysql.jdbc.Driver");
            
            //Se Realiza la conexión.
            conexion = dataSource.getConnection();
           //sentencia = conexion.createStatement();

            if (request.getParameter("unaAnilla") != null) {

                sql = "select * from aves where anilla = ?";
                preparada = conexion.prepareStatement(sql);
                preparada.setString(1, anilla);
                try {
                    resultado = preparada.executeQuery();

                    resultado.next();
                    url = "unResultado.jsp";
                    request.setAttribute("anilla", resultado.getString("anilla"));
                    request.setAttribute("especie", resultado.getString("especie"));
                    request.setAttribute("lugar", resultado.getString("lugar"));
                    request.setAttribute("fecha", resultado.getString("fecha"));
                } catch (SQLException e) {

                    url = "error.jsp";
                    request.setAttribute("error", "La anilla " + anilla
                            + " no existe en la base de datos");
                }
            } else {
                sql = "select * from aves";
                sentencia = conexion.createStatement();
                resultado = sentencia.executeQuery(sql);
                listado = new ArrayList();
                url = "listaResultado.jsp";

                while (resultado.next()) {
/*
                    usuario = new Usuario();
                    ave.setAnilla(resultado.getString("anilla")); //Saca el resultado de la columna
                    ave.setEspecie(resultado.getString("especie"));
                    ave.setLugar(resultado.getString("lugar"));
                    ave.setFecha(resultado.getString("fecha"));
                    listado.add(ave);*/
                }
                request.setAttribute("lista", listado);
            }

            request.getRequestDispatcher(url).forward(request, response);
        } catch (SQLException ex) {
            System.out.println("Error al crear la conexión");
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AccesoBD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {//Se cierra la sesión.
            try {
                if (resultado != null) {
                    resultado.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                if (preparada != null) {
                    preparada.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }



}
