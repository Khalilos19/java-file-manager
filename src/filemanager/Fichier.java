package filemanager;

public class Fichier {
    private int id;
    private String chemin;
    private String auteur;
    private String titre;
    private String resume;
    private String commentaires;

    public Fichier(String chemin, String auteur, String titre, String resume, String commentaires) {
        this.chemin = chemin;
        this.auteur = auteur;
        this.titre = titre;
        this.resume = resume;
        this.commentaires = commentaires;
    }

    public Fichier(int id, String chemin, String auteur, String titre, String resume, String commentaires) {
        this(chemin, auteur, titre, resume, commentaires);
        this.id = id;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getChemin() { return chemin; }
    public void setChemin(String chemin) { this.chemin = chemin; }
    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }
    public String getCommentaires() { return commentaires; }
    public void setCommentaires(String commentaires) { this.commentaires = commentaires; }
}