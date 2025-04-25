
- **Nama : Rifky Saputra Maylandra**
- **NIM : 24552011091**
- **Kelas : TIF K 24A**

This project is a simple **JavaFX desktop application** designed to manage a list of student data. The data is stored in a **CSV file** and consists of three fields:
-   **NIM** 
-   **Name**
-   **Email**
    

The app includes features for:
-   Adding, updating, and deleting data
-   Real-time search with a 500ms delay for optimized performance
-   Sorting data using a **bubble sort algorithm**
-   Reading and writing to a CSV file for data persistence

src/
├── main/
│   ├── java/
│   │   └── main/
│   │       ├── App.java
│   │       ├── Helper.java
│   │       ├── Controller.java
│   │       └── Mahasiswa.java
│   └── resources/
│       ├── App/
│       │   ├── index.fxml
│       │   └── style.css
│       └── datas/
│           └── data.csv


#  Features

### Add & Update Data
-   Users input **NIM**, **Name**, and **Email**.
-   If the NIM exists:
    -   It will update the existing data.
-   If it’s a new NIM:
    -   It will be appended to the CSV.
-   Data is then saved and the TableView is reloaded.
    

###  Delete Data
-   Double-clicking a table row loads its data into input fields.
-   Selecting a record and clicking delete removes it from the CSV file.
-   Table reloads to reflect changes.
    

### Search Feature
-   Users can type keywords in a search field.
-   A **500ms delay** is implemented using `Timeline` to optimize performance and avoid lag while typing.
-   The search is case-insensitive and checks NIM, Name, and Email.

###  Sorting with Bubble Sort (Recursive)
-   The app can sort data based on:
    -   NIM (ascending & descending)
    -   Name (ascending & descending)
    -   Email (ascending & descending)
-   Sorting is done using a **recursive bubble sort algorithm**.

## 📈 TableView Display

The app uses a **TableView** with three columns:
-   **NIM**
-   **Name**
-   **Email**
