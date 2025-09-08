package filemanager;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

public class FileManagerView {

    private BorderPane mainLayout;
    private VBox formLayout;
    private HBox buttonLayout;

    private TextField authorField;
    private TextField titleField;
    private TextField tagsField;
    private TextArea summaryArea;
    private TextArea commentsArea;
    private Label selectedFileLabel;
    private Button selectFileButton;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private Button searchByTitleButton;
    private Button searchByTagButton;
    private Button searchByAuthorButton;
    private Button propertiesButton;
    private TableView<Fichier> filesTable;

    private File selectedFile;

    public FileManagerView() {
        initializeLayout();
    }

    private void initializeLayout() {
        mainLayout = new BorderPane();
        formLayout = new VBox(10);
        formLayout.setPadding(new Insets(15));

        authorField = new TextField();
        authorField.setPromptText("Auteur *");

        titleField = new TextField();
        titleField.setPromptText("Titre *");

        tagsField = new TextField();
        tagsField.setPromptText("Tags (séparés par virgule) *");

        summaryArea = new TextArea();
        summaryArea.setPromptText("Résumé");

        commentsArea = new TextArea();
        commentsArea.setPromptText("Commentaires");

        selectedFileLabel = new Label("Aucun fichier sélectionné.");
        selectFileButton = new Button("Sélectionner Fichier");
        selectFileButton.setOnAction(e -> selectFile());

        addButton = new Button("Ajouter");
        updateButton = new Button("Modifier");
        deleteButton = new Button("Supprimer");
        searchByTitleButton = new Button("Rechercher par Titre");
        searchByTagButton = new Button("Rechercher par Tag");
        searchByAuthorButton = new Button("Rechercher par Auteur");
        propertiesButton = new Button("Afficher Propriétés");

        buttonLayout = new HBox(10, addButton, updateButton, deleteButton, searchByTitleButton, searchByTagButton, searchByAuthorButton, propertiesButton);

        filesTable = new TableView<>();
        filesTable.setPrefHeight(300);

        TableColumn<Fichier, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Fichier, String> titleCol = new TableColumn<>("Titre");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("titre"));

        TableColumn<Fichier, String> authorCol = new TableColumn<>("Auteur");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("auteur"));

        TableColumn<Fichier, String> cheminCol = new TableColumn<>("Chemin");
        cheminCol.setCellValueFactory(new PropertyValueFactory<>("chemin"));

        filesTable.getColumns().addAll(idCol, titleCol, authorCol, cheminCol);

        formLayout.getChildren().addAll(
                selectedFileLabel, selectFileButton,
                authorField, titleField, tagsField,
                summaryArea, commentsArea,
                buttonLayout
        );

        mainLayout.setTop(formLayout);
        mainLayout.setCenter(filesTable);

        addButton.setOnAction(e -> ajouterFichier());
        updateButton.setOnAction(e -> modifierFichier());
        deleteButton.setOnAction(e -> supprimerFichier());
        searchByTitleButton.setOnAction(e -> rechercherFichierParTitre());
        searchByTagButton.setOnAction(e -> rechercherFichierParTag());
        searchByAuthorButton.setOnAction(e -> rechercherFichierParAuteur());
        propertiesButton.setOnAction(e -> showProperties());

        filesTable.setOnMouseClicked(event -> {
            Fichier selected = filesTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                authorField.setText(selected.getAuteur());
                titleField.setText(selected.getTitre());
                summaryArea.setText(selected.getResume());
                commentsArea.setText(selected.getCommentaires());
                selectedFile = new File(selected.getChemin());
                selectedFileLabel.setText("Fichier sélectionné: " + selected.getChemin());

                try {
                    DatabaseManager db = new DatabaseManager();
                    db.connect();
                    String tags = db.findTagsForFichier(selected.getId());
                    tagsField.setText(tags);
                    db.disconnect();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                if (event.getClickCount() == 2) {
                    try {
                        java.awt.Desktop.getDesktop().open(selectedFile);
                    } catch (Exception ex) {
                        System.out.println("Impossible d'ouvrir le fichier.");
                    }
                }
            }
        });

        refreshTable();
    }
    private void selectFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Sélectionner un fichier");
    selectedFile = fileChooser.showOpenDialog(null);

    if (selectedFile != null) {
        selectedFileLabel.setText("Fichier sélectionné: " + selectedFile.getName());
    } else {
        selectedFileLabel.setText("Aucun fichier sélectionné.");
    }
}


    private void ajouterFichier() {
        String titre = titleField.getText().trim();
        String auteur = authorField.getText().trim();
        String tagsInput = tagsField.getText().trim();

         if (titre.isEmpty()) {
        showAlert(Alert.AlertType.ERROR, "Validation", "Le champ 'Titre' est obligatoire.");
        return;
    }

    if (auteur.isEmpty()) {
        showAlert(Alert.AlertType.ERROR, "Validation", "Le champ 'Auteur' est obligatoire.");
        return;
    }

    if (tagsInput.isEmpty()) {
        showAlert(Alert.AlertType.ERROR, "Validation", "Le champ 'Tags' est obligatoire.");
        return;
    }

    if (selectedFile == null) {
        showAlert(Alert.AlertType.ERROR, "Validation", "Veuillez sélectionner un fichier.");
        return;
    }
        try {
            DatabaseManager db = new DatabaseManager();
            db.connect();

            Fichier fichier = new Fichier(
                    selectedFile.getAbsolutePath(),
                    auteur,
                    titre,
                    summaryArea.getText(),
                    commentsArea.getText()
            );
            int fichierId = db.insertFichier(fichier);

            if (tagsInput != null && !tagsInput.isEmpty()) {
                String[] tags = tagsInput.split(",");
                db.insertFichierTags(fichierId, tags);
            }

            System.out.println("Fichier et tags ajoutés avec succès !");
            clearForm();
            refreshTable();

            db.disconnect();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void modifierFichier() {
        Fichier selected = filesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                DatabaseManager db = new DatabaseManager();
                db.connect();

                selected.setAuteur(authorField.getText());
                selected.setTitre(titleField.getText());
                selected.setResume(summaryArea.getText());
                selected.setCommentaires(commentsArea.getText());
                selected.setChemin(selectedFile != null ? selectedFile.getAbsolutePath() : selected.getChemin());

                db.updateFichier(selected);

                db.deleteTagsForFichier(selected.getId());
                String[] newTags = tagsField.getText().split(",");
                db.insertFichierTags(selected.getId(), newTags);

                System.out.println("Fichier et tags modifiés avec succès !");
                clearForm();
                refreshTable();

                db.disconnect();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un fichier à modifier.");
        }
    }

    private void supprimerFichier() {
        Fichier selected = filesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                DatabaseManager db = new DatabaseManager();
                db.connect();

                db.deleteFichier(selected.getId());
                System.out.println("Fichier supprimé avec succès !");

                clearForm();
                refreshTable();

                db.disconnect();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un fichier à supprimer.");
        }
    }

    private void rechercherFichierParTitre() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Recherche de fichiers par titre");
        dialog.setHeaderText(null);
        dialog.setContentText("Mot-clé dans le titre :");
        dialog.showAndWait().ifPresent(searchTerm -> {
            try {
                DatabaseManager db = new DatabaseManager();
                db.connect();

                var foundFiles = db.searchFichiersByTitle(searchTerm);
                filesTable.getItems().setAll(foundFiles);

                db.disconnect();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void rechercherFichierParTag() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Recherche de fichiers par tag");
        dialog.setHeaderText(null);
        dialog.setContentText("Entrez le tag :");
        dialog.showAndWait().ifPresent(tagName -> {
            try {
                DatabaseManager db = new DatabaseManager();
                db.connect();

                var foundFiles = db.searchFichiersByTag(tagName);
                filesTable.getItems().setAll(foundFiles);

                db.disconnect();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void rechercherFichierParAuteur() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Recherche de fichiers par auteur");
        dialog.setHeaderText(null);
        dialog.setContentText("Entrez l'auteur :");
        dialog.showAndWait().ifPresent(author -> {
            try {
                DatabaseManager db = new DatabaseManager();
                db.connect();

                var foundFiles = db.searchFichiersByAuthor(author);
                filesTable.getItems().setAll(foundFiles);

                db.disconnect();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void refreshTable() {
        try {
            DatabaseManager db = new DatabaseManager();
            db.connect();

            var allFiles = db.listAllFichiers();
            filesTable.getItems().setAll(allFiles);

            db.disconnect();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        authorField.clear();
        titleField.clear();
        tagsField.clear();
        summaryArea.clear();
        commentsArea.clear();
        selectedFile = null;
        selectedFileLabel.setText("Aucun fichier sélectionné.");
    }

    private void showProperties() {
        try {
            DatabaseManager db = new DatabaseManager();
            db.connect();

            int totalFiles = db.getTotalFichiers();
            List<String> authors = db.getAuthors();
            List<String> tags = db.getDistinctTags();
            List<String> filesPerTag = db.getFilesPerTag();

            db.disconnect();

            Stage propertiesStage = new Stage();
            propertiesStage.setTitle("Propriétés des Fichiers Favoris");

            VBox propertiesLayout = new VBox(10);
            propertiesLayout.setPadding(new Insets(15));

            Label totalLabel = new Label("Nombre total de fichiers favoris: " + totalFiles);
            Label authorsLabel = new Label("Auteurs: " + String.join(", ", authors));
            Label tagsLabel = new Label("Tags: " + String.join(", ", tags));
            Label filesPerTagLabel = new Label("Fichiers par tag: " + String.join(", ", filesPerTag));

            Button saveToFileButton = new Button("Sauvegarder dans un fichier");
            saveToFileButton.setOnAction(event -> savePropertiesToFile(totalFiles, authors, tags, filesPerTag));

            propertiesLayout.getChildren().addAll(totalLabel, authorsLabel, tagsLabel, filesPerTagLabel, saveToFileButton);

            Scene propertiesScene = new Scene(propertiesLayout, 400, 300);
            propertiesStage.setScene(propertiesScene);
            propertiesStage.show();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void savePropertiesToFile(int totalFiles, List<String> authors, List<String> tags, List<String> filesPerTag) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder les propriétés");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Nombre total de fichiers favoris: " + totalFiles);
                writer.println("Auteurs: " + String.join(", ", authors));
                writer.println("Tags: " + String.join(", ", tags));
                writer.println("Fichiers par tag: " + String.join(", ", filesPerTag));
                System.out.println("Propriétés sauvegardées dans " + file.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getMainLayout() {
        return mainLayout;
    }
}
