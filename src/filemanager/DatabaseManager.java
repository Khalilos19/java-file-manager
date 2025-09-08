package filemanager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String URL = "jdbc:oracle:thin:@localhost:1522/FILEMGR";
    private static final String USER = "filemanager";
    private static final String PASSWORD = "filemanager123";

    private Connection connection;

    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion Oracle établie.");
        }
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Déconnecté d'Oracle.");
        }
    }

    // ---------------------- GESTION DES FICHIERS --------------------------

    public int insertFichier(Fichier fichier) throws SQLException {
        String sql = "INSERT INTO FICHIERS (CHEMIN, NOM, TITRE, AUTEUR, RESUME, COMMENTAIRES, DATE_AJOUT) " +
                     "VALUES (?, ?, ?, ?, ?, ?, SYSDATE)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"ID"})) {
            pstmt.setString(1, fichier.getChemin());
            pstmt.setString(2, fichier.getTitre());
            pstmt.setString(3, fichier.getTitre());
            pstmt.setString(4, fichier.getAuteur());
            pstmt.setString(5, fichier.getResume());
            pstmt.setString(6, fichier.getCommentaires());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void updateFichier(Fichier fichier) throws SQLException {
        String sql = "UPDATE FICHIERS SET CHEMIN = ?, NOM = ?, TITRE = ?, AUTEUR = ?, RESUME = ?, COMMENTAIRES = ? WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, fichier.getChemin());
            pstmt.setString(2, fichier.getTitre());
            pstmt.setString(3, fichier.getTitre());
            pstmt.setString(4, fichier.getAuteur());
            pstmt.setString(5, fichier.getResume());
            pstmt.setString(6, fichier.getCommentaires());
            pstmt.setInt(7, fichier.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteFichier(int fichierId) throws SQLException {
        String deleteLinks = "DELETE FROM FICHIER_TAGS WHERE FICHIER_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteLinks)) {
            pstmt.setInt(1, fichierId);
            pstmt.executeUpdate();
        }

        String deleteFile = "DELETE FROM FICHIERS WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteFile)) {
            pstmt.setInt(1, fichierId);
            pstmt.executeUpdate();
        }
    }

    public List<Fichier> listAllFichiers() throws SQLException {
        List<Fichier> fichiers = new ArrayList<>();
        String sql = "SELECT ID, CHEMIN, AUTEUR, TITRE, RESUME, COMMENTAIRES FROM FICHIERS";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Fichier fichier = new Fichier(
                        rs.getInt("ID"),
                        rs.getString("CHEMIN"),
                        rs.getString("AUTEUR"),
                        rs.getString("TITRE"),
                        rs.getString("RESUME"),
                        rs.getString("COMMENTAIRES")
                );
                fichiers.add(fichier);
            }
        }
        return fichiers;
    }

    public List<Fichier> searchFichiersByTitle(String searchTerm) throws SQLException {
        List<Fichier> fichiers = new ArrayList<>();
        String sql = "SELECT ID, CHEMIN, AUTEUR, TITRE, RESUME, COMMENTAIRES FROM FICHIERS WHERE LOWER(TITRE) LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTerm.toLowerCase() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Fichier fichier = new Fichier(
                            rs.getInt("ID"),
                            rs.getString("CHEMIN"),
                            rs.getString("AUTEUR"),
                            rs.getString("TITRE"),
                            rs.getString("RESUME"),
                            rs.getString("COMMENTAIRES")
                    );
                    fichiers.add(fichier);
                }
            }
        }
        return fichiers;
    }

    public List<Fichier> searchFichiersByAuthor(String author) throws SQLException {
        List<Fichier> fichiers = new ArrayList<>();
        String sql = "SELECT ID, CHEMIN, AUTEUR, TITRE, RESUME, COMMENTAIRES FROM FICHIERS WHERE LOWER(AUTEUR) LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + author.toLowerCase() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Fichier fichier = new Fichier(
                            rs.getInt("ID"),
                            rs.getString("CHEMIN"),
                            rs.getString("AUTEUR"),
                            rs.getString("TITRE"),
                            rs.getString("RESUME"),
                            rs.getString("COMMENTAIRES")
                    );
                    fichiers.add(fichier);
                }
            }
        }
        return fichiers;
    }

    public List<Fichier> searchFichiersByTag(String tagName) throws SQLException {
        List<Fichier> fichiers = new ArrayList<>();
        String sql = """
            SELECT f.ID, f.CHEMIN, f.AUTEUR, f.TITRE, f.RESUME, f.COMMENTAIRES
            FROM FICHIERS f
            JOIN FICHIER_TAGS ft ON f.ID = ft.FICHIER_ID
            JOIN TAGS t ON t.ID = ft.TAG_ID
            WHERE LOWER(t.NOM) LIKE ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + tagName.toLowerCase() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Fichier fichier = new Fichier(
                            rs.getInt("ID"),
                            rs.getString("CHEMIN"),
                            rs.getString("AUTEUR"),
                            rs.getString("TITRE"),
                            rs.getString("RESUME"),
                            rs.getString("COMMENTAIRES")
                    );
                    fichiers.add(fichier);
                }
            }
        }
        return fichiers;
    }

    // ---------------------- GESTION DES TAGS --------------------------

    public int insertTag(String nom) throws SQLException {
        String sql = "INSERT INTO TAGS (NOM) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"ID"})) {
            pstmt.setString(1, nom);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public Integer findTagByName(String nom) throws SQLException {
        String sql = "SELECT ID FROM TAGS WHERE NOM = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        }
        return null;
    }

    // ---------------------- GESTION FICHIER-TAGS --------------------------

    public void linkFichierToTag(int fichierId, int tagId) throws SQLException {
        String sql = "INSERT INTO FICHIER_TAGS (FICHIER_ID, TAG_ID) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, fichierId);
            pstmt.setInt(2, tagId);
            pstmt.executeUpdate();
        }
    }

    public String findTagsForFichier(int fichierId) throws SQLException {
        StringBuilder tags = new StringBuilder();
        String sql = """
            SELECT t.NOM
            FROM TAGS t
            JOIN FICHIER_TAGS ft ON t.ID = ft.TAG_ID
            WHERE ft.FICHIER_ID = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, fichierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (tags.length() > 0) {
                        tags.append(", ");
                    }
                    tags.append(rs.getString("NOM"));
                }
            }
        }
        return tags.toString();
    }

    public void deleteTagsForFichier(int fichierId) throws SQLException {
        String sql = "DELETE FROM FICHIER_TAGS WHERE FICHIER_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, fichierId);
            pstmt.executeUpdate();
        }
    }

    public void insertFichierTags(int fichierId, String[] tags) throws SQLException {
        for (String tag : tags) {
            tag = tag.trim();
            if (!tag.isEmpty()) {
                Integer tagId = findTagByName(tag);
                if (tagId == null) {
                    tagId = insertTag(tag);
                }
                linkFichierToTag(fichierId, tagId);
            }
        }
    }

    // ---------------------- LISTAGE DE PROPRIÉTÉS --------------------------

    public int getTotalFichiers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM FICHIERS";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<String> getAuthors() throws SQLException {
        List<String> authors = new ArrayList<>();
        String sql = "SELECT DISTINCT AUTEUR FROM FICHIERS WHERE AUTEUR IS NOT NULL";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                authors.add(rs.getString("AUTEUR"));
            }
        }
        return authors;
    }

    public List<String> getDistinctTags() throws SQLException {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT DISTINCT NOM FROM TAGS";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tags.add(rs.getString("NOM"));
            }
        }
        return tags;
    }

    public List<String> getFilesPerTag() throws SQLException {
        List<String> filesPerTag = new ArrayList<>();
        String sql = """
            SELECT t.NOM, COUNT(ft.FICHIER_ID) as count
            FROM TAGS t
            LEFT JOIN FICHIER_TAGS ft ON t.ID = ft.TAG_ID
            GROUP BY t.NOM
            """;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String tag = rs.getString("NOM");
                int count = rs.getInt("count");
                filesPerTag.add(tag + ": " + count);
            }
        }
        return filesPerTag;
    }
} 