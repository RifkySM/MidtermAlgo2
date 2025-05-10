package main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
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
import java.util.function.UnaryOperator;

public class Controller {
    private final String dataFilePath = "src/main/resources/datas/data.csv";
    private final Timeline searchDelay = new Timeline();

    @FXML private TextField inputSearch;
    @FXML private TextField inputName;
    @FXML private TextField inputNim;
    @FXML private TextField inputNilaiTugas;
    @FXML private TextField inputNilaiUts;
    @FXML private TextField inputNilaiUas;
    @FXML private ComboBox<String> inputSort;
    @FXML private TableColumn<Mahasiswa, String> columnName;
    @FXML private TableColumn<Mahasiswa, String> columnNim;
    @FXML private TableColumn<Mahasiswa, String> columnNilaiAkhir;
    @FXML private TableColumn<Mahasiswa, Double> columnNilaiTugas;
    @FXML private TableColumn<Mahasiswa, Double> columnNilaiUas;
    @FXML private TableColumn<Mahasiswa, Double> columnNilaiUts;
    @FXML private TableView<Mahasiswa> tableView;

    private final ObservableList<Mahasiswa> students = FXCollections.observableArrayList();
    private final String[] arraySortLists = {
            "NIM (asc)", "NIM (desc)",
            "Nama (asc)", "Nama (desc)",
            "Tugas (asc)", "Tugas (desc)",
            "UTS (asc)", "UTS (desc)",
            "UAS (asc)", "UAS (desc)",
            "Nilai Akhir (asc)", "Nilai Akhir (desc)"
    };
    private final ObservableList<String> sortLists = FXCollections.observableArrayList(arraySortLists);

    @FXML
    public void initialize() {
        columnNim.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNim()));
        columnName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        columnNilaiTugas.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getNilaiTugas()).asObject());
        columnNilaiUts.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getNilaiUts()).asObject());
        columnNilaiUas.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getNilaiUas()).asObject());
        columnNilaiAkhir.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAverageGradeFormatted()));

        tableView.setItems(students);
        reloadTable();

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Mahasiswa selectedStudent = tableView.getSelectionModel().getSelectedItem();
                if (selectedStudent != null) {
                    inputNim.setText(selectedStudent.getNim());
                    inputNim.setDisable(true);
                    inputName.setText(selectedStudent.getName());
                    inputNilaiTugas.setText(String.valueOf(selectedStudent.getNilaiTugas()));
                    inputNilaiUts.setText(String.valueOf(selectedStudent.getNilaiUts()));
                    inputNilaiUas.setText(String.valueOf(selectedStudent.getNilaiUas()));
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
        UnaryOperator<TextFormatter.Change> numberFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("^[0-9]*\\.?[0-9]*$") || newText.isEmpty()) {
                return change;
            }
            return null;
        };

        TextFormatter<String> numberFormatterTugas = new TextFormatter<>(numberFilter);
        TextFormatter<String> numberFormatterUts = new TextFormatter<>(numberFilter);
        TextFormatter<String> numberFormatterUas = new TextFormatter<>(numberFilter);


        inputNilaiTugas.setTextFormatter(numberFormatterTugas);
        inputNilaiUts.setTextFormatter(numberFormatterUts);
        inputNilaiUas.setTextFormatter(numberFormatterUas);
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
        inputNilaiUas.clear();
        inputNilaiTugas.clear();
        inputNilaiUts.clear();
    }

    @FXML
    public void saveData() {
        String nim = inputNim.getText().trim();
        String name = inputName.getText().trim();
        String nilaiTugas = inputNilaiTugas.getText().trim();
        String nilaiUts = inputNilaiUts.getText().trim();
        String nilaiUas = inputNilaiUas.getText().trim();

        if (nim.isEmpty() || name.isEmpty() || nilaiTugas.isEmpty() || nilaiUts.isEmpty() || nilaiUas.isEmpty()) {
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
                if (parts.length != 5) continue;

                String existingNim = parts[0].trim();

                if (existingNim.equals(nim)) {
                    if (isEditing) {
                        updatedRecords.add(new String[]{nim, name, nilaiTugas, nilaiUts, nilaiUas});
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
            updatedRecords.add(new String[]{nim, name, nilaiTugas, nilaiUts, nilaiUas});
        }

        writeCSV(updatedRecords);
        reloadTable();
        clearInputs();
        Helper.showAlert("Success", "Data saved successfully", "info");
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
                if (parts.length != 5) continue;

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
                    result = searchMahasiswa(keyword);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            tableView.setItems(FXCollections.observableArrayList(result));
        }
    }

    private List<Mahasiswa> searchMahasiswa(String keyword) {
        List<Mahasiswa> matched = new ArrayList<>();
        keyword = keyword.toLowerCase();

        try (BufferedReader br = new BufferedReader(new FileReader(dataFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String nim = parts[0].trim().toLowerCase();
                    String name = parts[1].trim().toLowerCase();
                    String email = parts[2].trim().toLowerCase();

                    if (nim.contains(keyword) || name.contains(keyword) || email.contains(keyword)) {
                        matched.add(new Mahasiswa(
                                parts[0].trim(),
                                parts[1].trim(),
                                Double.parseDouble(parts[2].trim()),
                                Double.parseDouble(parts[3].trim()),
                                Double.parseDouble(parts[4].trim())
                        ));
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
                if (parts.length == 5) {
                    students.add(new Mahasiswa(parts[0].trim(),
                            parts[1].trim(),
                            Double.parseDouble(parts[2].trim()),
                            Double.parseDouble(parts[3].trim()),
                            Double.parseDouble(parts[4].trim())));
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
        bubbleSort(list, sortOption);
        tableView.refresh();
    }

    private void bubbleSort(ObservableList<Mahasiswa> list, String sortOption) {
        int n = list.size();
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;

            for (int j = 0; j < n - i - 1; j++) {
                Mahasiswa m1 = list.get(j);
                Mahasiswa m2 = list.get(j + 1);
                boolean shouldSwap = false;

                switch (sortOption) {
                    case "NIM (asc)":
                        shouldSwap = m1.getNim().compareToIgnoreCase(m2.getNim()) > 0;
                        break;
                    case "NIM (desc)":
                        shouldSwap = m1.getNim().compareToIgnoreCase(m2.getNim()) < 0;
                        break;
                    case "Nama (asc)":
                        shouldSwap = m1.getName().compareToIgnoreCase(m2.getName()) > 0;
                        break;
                    case "Nama (desc)":
                        shouldSwap = m1.getName().compareToIgnoreCase(m2.getName()) < 0;
                        break;
                    case "Tugas (asc)":
                        shouldSwap = m1.getNilaiTugas() > m2.getNilaiTugas();
                        break;
                    case "Tugas (desc)":
                        shouldSwap = m1.getNilaiTugas() < m2.getNilaiTugas();
                        break;
                    case "UTS (asc)":
                        shouldSwap = m1.getNilaiUts() > m2.getNilaiUts();
                        break;
                    case "UTS (desc)":
                        shouldSwap = m1.getNilaiUts() < m2.getNilaiUts();
                        break;
                    case "UAS (asc)":
                        shouldSwap = m1.getNilaiUas() > m2.getNilaiUas();
                        break;
                    case "UAS (desc)":
                        shouldSwap = m1.getNilaiUas() < m2.getNilaiUas();
                        break;
                    case "Nilai Akhir (asc)":
                        shouldSwap = m1.getAverageGrade() > m2.getAverageGrade();
                        break;
                    case "Nilai Akhir (desc)":
                        shouldSwap = m1.getAverageGrade() < m2.getAverageGrade();
                        break;
                }

                if (shouldSwap) {
                    list.set(j, m2);
                    list.set(j + 1, m1);
                    swapped = true;
                }
            }

            // Early exit if no swaps occurred in this pass
            if (!swapped) {
                break;
            }
        }
    }

}
