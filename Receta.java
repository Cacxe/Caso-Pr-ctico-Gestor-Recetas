public class Receta {

    private int id;
    private String nombre;
    private String categoria;
    private int tiempo;
    private String dificultad;
    private int raciones;

    public Receta(int id, String nombre, String categoria, int tiempo, String dificultad, int raciones) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.tiempo = tiempo;
        this.dificultad = dificultad;
        this.raciones = raciones;
    }

    public Receta(String nombre, String categoria, int tiempo, String dificultad, int raciones) {

        this(0, nombre, categoria, tiempo, dificultad, raciones);
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getTiempo() {
        return tiempo;
    }

    public String getDificultad() {
        return dificultad;
    }

    public int getRaciones() {
        return raciones;
    }

    public String toString() {
        return id + " - " + nombre + " - " + categoria + " - " + tiempo + " min - " + dificultad + " - " + raciones + " raciones";
    }
}
