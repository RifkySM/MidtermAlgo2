package main;

import javafx.beans.property.SimpleStringProperty;

public class Mahasiswa {
    private final SimpleStringProperty nim;
    private final SimpleStringProperty name;
    private final SimpleStringProperty email;

    public Mahasiswa(String nim, String name, String email) {
        this.nim = new SimpleStringProperty(nim);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
    }

    public String getNim() {
        return nim.get();
    }

    public String getName() {
        return name.get();
    }

    public String getEmail() {
        return email.get();
    }

    public void setNim(String value) {
        nim.set(value);
    }

    public void setName(String value) {
        name.set(value);
    }

    public void setEmail(String value) {
        email.set(value);
    }
}
