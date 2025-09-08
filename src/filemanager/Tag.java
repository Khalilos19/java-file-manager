package filemanager;

public class Tag {
    private int id;
    private String nom;

    public Tag(String nom) {
        this.nom = nom;
    }

    public Tag(int id, String nom) {
        this.nom = nom;
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}