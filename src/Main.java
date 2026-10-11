import com.workintech.library.data.SampleData;
import com.workintech.library.model.person.Librarian;
import com.workintech.library.service.Library;
import com.workintech.library.ui.ConsoleMenu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Librarian librarian = new Librarian("Kütüphane Görevlisi", "1234");
        Library library = new Library("Workintech Kütüphanesi", librarian);
        SampleData.load(library);

        Scanner scanner = new Scanner(System.in);
        new ConsoleMenu(library, scanner).start();
        scanner.close();
    }
}
