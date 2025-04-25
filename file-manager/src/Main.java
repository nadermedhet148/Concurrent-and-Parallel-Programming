import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Main {
  private static final String header = "Name, Id#, Year Lvl, Gender";
  private static final String filename = "test";



  public static void main(String[] args) throws FileNotFoundException {
    System.out.println("Hello, World!");
    createNewFile(filename, header);
    var fos1 = new FileOutputStream(filename);
    var students = new PrintWriter(fos1);
    var student = new CsvFileManager(students, filename, header);
  }



  private static void createNewFile(String fileName, String header) {
    // Create a new file and write the header
    try (PrintWriter writer = new PrintWriter(new FileOutputStream(fileName))) {
      writer.println(header);
      writer.flush();
    } catch (Exception e) {
      // Handle the exception based on your requirements
      System.out.println("An error occurred:" + e);
    }
  }







}
