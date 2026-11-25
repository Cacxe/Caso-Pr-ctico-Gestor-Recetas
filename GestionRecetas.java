import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class GestionRecetas {

    final static Scanner sc = new Scanner(System.in);

    public static void mostrarTodas() {

        String query = "SELECT * FROM recetas";

        try (Connection con = ConexionBD.getConnection();

             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {

                Receta receta = new Receta(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getInt("tiempo_preparacion"),
                        rs.getString("dificultad"),
                        rs.getInt("raciones"));

                System.out.println(receta);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public static void buscarPorId(int id) {
        String query = "SELECT * FROM recetas WHERE id = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                Receta receta = new Receta(rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getInt("tiempo_preparacion"),
                        rs.getString("dificultad"),
                        rs.getInt("raciones"));

                System.out.println(receta);

            } else {

                System.out.println("No existe ninguna receta con ese id.");
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public static void anadirReceta(Receta r) {
        String query = "INSERT INTO recetas (nombre, categoria, tiempo_preparacion, dificultad, raciones) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, r.getNombre());
            pstmt.setString(2, r.getCategoria());
            pstmt.setInt(3, r.getTiempo());
            pstmt.setString(4, r.getDificultad());
            pstmt.setInt(5, r.getRaciones());

            int filas = pstmt.executeUpdate();

            System.out.println("Recetas añadidas: " + filas);
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public static void modificarReceta(int id, int tiempo, int raciones) {

        String query = "UPDATE recetas SET tiempo_preparacion = ?, raciones = ? WHERE id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, tiempo);
            pstmt.setInt(2, raciones);
            pstmt.setInt(3, id);

            int filas = pstmt.executeUpdate();

            if (filas > 0) {

                System.out.println("Receta modificada.");
            } else {

                System.out.println("No existe ninguna receta con ese id.");
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public static void eliminarReceta(int id) {

        String query = "DELETE FROM recetas WHERE id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, id);

            int filas = pstmt.executeUpdate();

            if (filas > 0) {

                System.out.println("Receta eliminada.");
            } else {

                System.out.println("No existe ninguna receta con ese id.");
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public static void anadirConTransaccion(Receta r) {
        String queryInsertar = "INSERT INTO recetas (nombre, categoria, tiempo_preparacion, dificultad, raciones) VALUES (?, ?, ?, ?, ?)";
        String queryEstadisticas = "UPDATE estadisticas SET total_recetas = (SELECT COUNT(*) FROM recetas) WHERE id = 1";

        Connection con = null;
        PreparedStatement pstmt1;
        PreparedStatement pstmt2;

        try {

            con = ConexionBD.getConnection();

            con.setAutoCommit(false);

            pstmt1 = con.prepareStatement(queryInsertar);
            pstmt1.setString(1, r.getNombre());
            pstmt1.setString(2, r.getCategoria());
            pstmt1.setInt(3, r.getTiempo());
            pstmt1.setString(4, r.getDificultad());
            pstmt1.setInt(5, r.getRaciones());
            pstmt1.executeUpdate();

            pstmt2 = con.prepareStatement(queryEstadisticas);

            int filas = pstmt2.executeUpdate();

            if (filas == 0) {

                throw new SQLException("No existe la fila de estadisticas");
            }

            con.commit();
            System.out.println("Receta añadida y estadísticas actualizadas.");

        } catch (SQLException e) {

            e.printStackTrace();

            try {

                if (con != null) {

                    con.rollback();
                    System.out.println("Se han deshecho los cambios.");
                }
            } catch (SQLException ex) {

                System.out.println("Error en el rollback: " + ex.getMessage());
            }
        }
    }

    public static void buscarConProcedimiento(String nombre) {

        String sql = "{CALL buscarReceta(?)}";

        try (Connection con = ConexionBD.getConnection();
             CallableStatement cstmt = con.prepareCall(sql)) {

            cstmt.setString(1, nombre);

            try (ResultSet rs = cstmt.executeQuery()) {

                boolean existeReceta = false;

                while (rs.next()) {
                    Receta receta = new Receta(rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("categoria"),
                            rs.getInt("tiempo_preparacion"),
                            rs.getString("dificultad"),
                            rs.getInt("raciones"));
                    System.out.println(receta);

                    existeReceta = true;
                }
                if (!existeReceta) {

                    System.out.println("No se ha encontrado esa receta.");
                }
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public static Receta pedirReceta() {

        sc.nextLine();

        System.out.print("Introduce un nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Introduce una categoría: ");
        String categoria = sc.nextLine();

        System.out.print("Introduce el tiempo de preparación: ");
        int tiempo = sc.nextInt();

        sc.nextLine();

        System.out.print("Introduce la dificultad: ");
        String dificultad = sc.nextLine();

        System.out.print("Introduce el número de raciones: ");
        int raciones = sc.nextInt();

        return new Receta(nombre, categoria, tiempo, dificultad, raciones);
    }

    public static void main(String[] args) {
        int opcion = -1;

        while (opcion != 0) {
            System.out.println();
            System.out.println("1. Mostrar todas las recetas");
            System.out.println("2. Buscar receta por id");
            System.out.println("3. Añadir receta");
            System.out.println("4. Modificar tiempo y raciones");
            System.out.println("5. Eliminar receta");
            System.out.println("6. Añadir receta y actualizar estadísticas");
            System.out.println("7. Buscar receta por nombre");
            System.out.println("0. Salir");
            System.out.print("Introduce una opción: ");
            opcion = sc.nextInt();

            switch (opcion) {

                case 1:
                {
                    System.out.println("Elegiste mostrar todas las recetas...");

                    mostrarTodas();

                    break;
                }
                case 2:
                {
                    System.out.println("Elegiste buscar una receta por id...");

                    System.out.print("Introduce un id para buscar: ");
                    int id = sc.nextInt();

                    buscarPorId(id);

                    break;
                }
                case 3:
                {
                    System.out.println("Elegiste añadir una receta...");

                    anadirReceta(pedirReceta());

                    break;
                }
                case 4:
                {
                    System.out.println("Elegiste modificar tiempo y raciones...");

                    System.out.print("Introduce un id: ");
                    int id = sc.nextInt();

                    System.out.print("Introduce el tiempo: ");
                    int tiempo = sc.nextInt();

                    System.out.print("Introduce el número de raciones: ");
                    int raciones = sc.nextInt();

                    modificarReceta(id, tiempo, raciones);

                    break;
                }
                case 5:
                {
                    System.out.println("Elegiste eliminar una receta...");

                    System.out.print("Introduce un id: ");
                    int id = sc.nextInt();

                    eliminarReceta(id);

                    break;
                }
                case 6:
                {
                    System.out.println("Elegiste añadir una receta y actualizar estadísticas...");

                    anadirConTransaccion(pedirReceta());

                    break;
                }
                case 7:
                {
                    System.out.println("Elegiste buscar una receta por nombre...");

                    sc.nextLine();

                    System.out.print("Introduce un nombre: ");
                    String nombre = sc.nextLine();

                    buscarConProcedimiento(nombre);

                    break;
                }
                case 0:
                {
                    System.out.println("Saliendo del programa...");

                    break;
                }
                default:
                {
                    System.out.println("Opción no válida.");

                    break;
                }
            }
        }
    }
}
