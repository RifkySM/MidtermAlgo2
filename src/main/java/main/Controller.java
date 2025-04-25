package main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Controller {
    private final String dataFilePath = "src/main/resources/datas/data.csv";
    private final Timeline searchDelay = new Timeline();

    @FXML private TextField inputSearch;
    @FXML private TextField inputEmail;
    @FXML private TextField inputName;
    @FXML private TextField inputNim;
    @FXML private ComboBox<String> inputSort;
    @FXML private TableColumn<Mahasiswa, String> columnEmail;
    @FXML private TableColumn<Mahasiswa, String> columnName;
    @FXML private TableColumn<Mahasiswa, String> columnNim;
    @FXML private TableView<Mahasiswa> tableView;

    private final ObservableList<Mahasiswa> students = FXCollections.observableArrayList();
    private final String[] arraySortLists = {"NIM (asc)", "NIM (desc)", "Nama (asc)", "Nama (desc)", "Email (asc)", "Email (desc)" };
    private final ObservableList<String> sortLists = FXCollections.observableArrayList(arraySortLists);

    @FXML
    public void initialize() {
        columnNim.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNim()));
        columnName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        columnEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        tableView.setItems(students);
        reloadTable();

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Mahasiswa selectedStudent = tableView.getSelectionModel().getSelectedItem();
                if (selectedStudent != null) {
                    inputNim.setText(selectedStudent.getNim());
                    inputNim.setDisable(true);
                    inputName.setText(selectedStudent.getName());
                    inputEmail.setText(selectedStudent.getEmail());
                }
            }
        });
        inputSearch.setOnKeyReleased(event -> {
            searchDelay.stop();
            searchDelay.getKeyFrames().clear();

            searchDelay.getKeyFrames().add(
                    new KeyFrame(Duration.millis(500), e -> search())
            );
            searchDelay.play();
        });
        inputSort.setItems(sortLists);
        inputSort.setOnAction(event -> {
            String selectedSort = inputSort.getSelectionModel().getSelectedItem();
            if (selectedSort != null) {
                sortTable(selectedSort);
            }
        });
    }
    @FXML
    public void reload(){
        reloadTable();
    }

    @FXML
    public void clearInputs() {
        inputNim.clear();
        inputNim.setDisable(false);
        inputName.clear();
        inputEmail.clear();
    }

    @FXML
    public void saveData() {
        String nim = inputNim.getText().trim();
        String name = inputName.getText().trim();
        String email = inputEmail.getText().trim();

        if (nim.isEmpty() || name.isEmpty() || email.isEmpty()) {
            Helper.showAlert("Validation Error", "Input tidak boleh kosong", "warning");
            return;
        }

        List<String[]> updatedRecords = new ArrayList<>();
        boolean found = false;
        boolean isEditing = inputNim.isDisabled();

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) continue;

                String existingNim = parts[0].trim();

                if (existingNim.equals(nim)) {
                    if (isEditing) {
                        updatedRecords.add(new String[]{nim, name, email});
                        found = true;
                    } else {
                        Helper.showAlert("Validation Error", "NIM sudah digunakan", "warning");
                        return;
                    }
                } else {
                    updatedRecords.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!found) {
            updatedRecords.add(new String[]{nim, name, email});
        }

        writeCSV(updatedRecords);
        reloadTable();
        clearInputs();
        Helper.showAlert("Success", "data saved successfully", "info");
    }

    @FXML
    private void deleteData() {
        Mahasiswa selectedStudent = tableView.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            System.out.println("Pilih data yang ingin dihapus terlebih dahulu.");
            return;
        }

        if (!Helper.showConfirm("Delete Data", "Apakah Anda yakin ingin menghapus data ini?")) {
            return;
        }

        String nimToDelete = selectedStudent.getNim();
        List<String[]> updatedRecords = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) continue;

                if (!parts[0].trim().equals(nimToDelete)) {
                    updatedRecords.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        writeCSV(updatedRecords);
        clearInputs();
        reloadTable();
        Helper.showAlert("Success", "Data dengan NIM " + nimToDelete + " berhasil dihapus.", "info");
    }

    @FXML
    public void search() {
        String keyword = inputSearch.getText().trim();

        if (keyword.isEmpty()) {
            reloadTable();
        } else {
            List<Mahasiswa> result = new ArrayList<>();
            try (InputStream is = getClass().getResourceAsStream("/datas/data.csv")) {
                assert is != null;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                    String firstLine = br.readLine();
                    result = linierSearch(keyword);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            tableView.setItems(FXCollections.observableArrayList(result));
        }
    }

    private List<Mahasiswa> linierSearch(String keyword) {
        List<Mahasiswa> matched = new ArrayList<>();
        keyword = keyword.toLowerCase();

        try (BufferedReader br = new BufferedReader(new FileReader(dataFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String nim = parts[0].trim().toLowerCase();
                    String name = parts[1].trim().toLowerCase();
                    String email = parts[2].trim().toLowerCase();

                    if (nim.contains(keyword) || name.contains(keyword) || email.contains(keyword)) {
                        matched.add(new Mahasiswa(parts[0].trim(), parts[1].trim(), parts[2].trim()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matched;
    }


    private void writeCSV(List<String[]> records) {
        Path path = Paths.get(dataFilePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (String[] record : records) {
                writer.write(String.join(",", record));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadTable() {
        students.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(dataFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    students.add(new Mahasiswa(parts[0].trim(), parts[1].trim(), parts[2].trim()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        tableView.setItems(students);
        inputSort.getSelectionModel().clearSelection();
    }

    private void sortTable(String sortOption) {
        ObservableList<Mahasiswa> list = tableView.getItems();
        bubbleSort(list, sortOption, list.size());
        tableView.refresh();
    }

    private void bubbleSort(ObservableList<Mahasiswa> list, String sortOption, int n) {
        if (n == 1) return;

        for (int i = 0; i < n - 1; i++) {
            Mahasiswa m1 = list.get(i);
            Mahasiswa m2 = list.get(i + 1);
            boolean swap = false;

            switch (sortOption) {
                case "NIM (asc)":
                    if (m1.getNim().compareToIgnoreCase(m2.getNim()) > 0) swap = true;
                    break;
                case "NIM (desc)":
                    if (m1.getNim().compareToIgnoreCase(m2.getNim()) < 0) swap = true;
                    break;
                case "Nama (asc)":
                    if (m1.getName().compareToIgnoreCase(m2.getName()) > 0) swap = true;
                    break;
                case "Nama (desc)":
                    if (m1.getName().compareToIgnoreCase(m2.getName()) < 0) swap = true;
                    break;
                case "Email (asc)":
                    if (m1.getEmail().compareToIgnoreCase(m2.getEmail()) > 0) swap = true;
                    break;
                case "Email (desc)":
                    if (m1.getEmail().compareToIgnoreCase(m2.getEmail()) < 0) swap = true;
                    break;
            }

            if (swap) {
                Mahasiswa temp = list.get(i);
                list.set(i, list.get(i + 1));
                list.set(i + 1, temp);
            }
        }

        bubbleSort(list, sortOption, n - 1);
    }

}
