package main;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.text.DecimalFormat;

public class Mahasiswa {
    private final SimpleStringProperty nim;
    private final SimpleStringProperty name;
    private final SimpleDoubleProperty nilaiTugas;
    private final SimpleDoubleProperty nilaiUts;
    private final SimpleDoubleProperty nilaiUas;

    public Mahasiswa(String nim, String name, double nilaiTugas, double nilaiUts, double nilaiUas) {
        this.nim = new SimpleStringProperty(nim);
        this.name = new SimpleStringProperty(name);
        this.nilaiTugas = new SimpleDoubleProperty(nilaiTugas);
        this.nilaiUts = new SimpleDoubleProperty(nilaiUts);
        this.nilaiUas = new SimpleDoubleProperty(nilaiUas);
    }

    public String getNim() {
        return nim.get();
    }

    public String getName() {
        return name.get();
    }
    public double getNilaiTugas() {
        return nilaiTugas.get();
    }

    public double getNilaiUts() {
        return nilaiUts.get();
    }
    public double getNilaiUas() {
        return nilaiUas.get();
    }

    public String getAverageGradeFormatted() {
        double average = getAverageGrade();
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(average).replace(".", ","); // Format dengan koma sebagai desimal
    }

    public double getAverageGrade() {
        double[] grades = {nilaiTugas.get(), nilaiUts.get(), nilaiUas.get()};
        return sumGrades(grades, 0) / grades.length;
    }

    private double sumGrades(double[] grades, int index) {
        if (index >= grades.length) {
            return 0;
        }
        return grades[index] + sumGrades(grades, index + 1);
    }


}
