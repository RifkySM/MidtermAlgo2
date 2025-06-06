
**Nama : Rifky Saputra Maylandra**
<br>
**NIM : 24552011091**
<br>
**Kelas : TIF K 24A**

Link Video: https://1drv.ms/v/c/e2493cb5db2a9b7d/EZEAsIMCyIVCkwixp1C9P0cBCbpuIBuIdKpo-HgaKGV7Nw?e=L70xYm

This project is a simple **JavaFX desktop application** designed to manage a list of student data. The data is stored in a **CSV file** and consists of three fields:
-   **NIM** 
-   **Name**
-   **Nilai Tugas**
-   **Nilai UTS**
-   **Nilai UAS**
    

The app includes features for:
-   Adding, updating, and deleting data
-   Real-time search with a 500ms delay for optimized performance
-   Sorting data using a **bubble sort algorithm**
-   Reading and writing to a CSV file for data persistence


### Explanation of Files:

1. **App.java**: The main entry point for the application. This class typically contains the `main` method.
2. **Helper.java**: A utility class to help with common tasks such as file operations or UI actions.
3. **Controller.java**: The controller for managing the UI interactions, such as saving, deleting, and sorting student data.
4. **Mahasiswa.java**: A model class that represents a student's data (NIM, Name, and Grades).
5. **index.fxml**: The layout file for the JavaFX application UI.
6. **style.css**: The CSS file for styling the UI components.
7. **data.csv**: A CSV file to store the student data.

This structure ensures that your files are well-organized and easy to maintain, especially with the separation of UI components, logic, and resources.



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

## TableView Display

The app uses a **TableView** with three columns:
-   **NIM**
-   **Name**
-   **Nilai Tugas**
-   **Nilai Uts**
-   **Nilai Uas**
-   **Nilai Akhir**

```mermaid
flowchart TD
    A([Start App]) --> B[Load data from CSV]
    B --> C{Input Nim, Name, Nilai Tugas, Nilai Uts, Nilai Uas}
    C --> D[Validate inputs]
    D --> E{Validation passed?}
    E -- No --> F[Show dialog with validation message]
    F --> C
    E -- Yes --> G{Nim exists in CSV?}
    G -- No --> I[Create new record]
    G -- Yes --> H{isUpdate true?}
    H -- Yes --> J[Update existing record]
    H -- No --> M[Show NIM already taken dialog]
    M --> C
    I --> K[Save to CSV]
    J --> K
    K --> L[Reload table display]
    L --> N([End])
```
