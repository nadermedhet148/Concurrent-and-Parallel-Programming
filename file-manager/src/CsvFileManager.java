import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvFileManager {

  final PrintWriter csvFilePath;
  final String fileName;
  public static List<String> lines = new ArrayList<>();
  public String headerTemplate;
  public Map<String, String> data = new HashMap<>();

  public CsvFileManager(PrintWriter filePath, String fileName) {
    this.csvFilePath = filePath;
    this.fileName = fileName;
    this.headerTemplate = getHeader();
    populateLinesList();
  }

  public CsvFileManager(PrintWriter filePath, String fileName, String headerTemplate) {
    this.csvFilePath = filePath;
    this.fileName = fileName;
    this.headerTemplate = headerTemplate;
    lines.add(headerTemplate);
    csvFilePath.println(headerTemplate);
    csvFilePath.flush();
  }

  public void create(String data_name, String data_id, String data_yrLvl, String data_gender, String data_course) {
    String csvLine = String.join(",", data_name, data_id, data_yrLvl, data_gender, data_course);
    csvFilePath.println(csvLine);
    csvFilePath.flush();
    lines.add(csvLine);
  }

  public void create(String data_name, String data_id) {
    String csvLine = String.join(",", data_name, data_id);
    csvFilePath.println(csvLine);
    csvFilePath.flush();
    lines.add(csvLine);
  }

  public void read(int lineNumber) {
    if (lineNumber >= 0 && lineNumber < lines.size()) {
      List<String> result = readLine(lineNumber);
      if (!result.isEmpty()) {
        for (int i = 0; i < result.size(); i++) {
          switch (i) {
            case 0:
              System.out.print("\033[34m" + result.get(i) + "\033[0m, "); // Blue
              break;
            case 1:
              System.out.print("\033[96m" + result.get(i) + "\033[0m, "); // Yellow/Orange
              break;
            case 2:
              System.out.print("\033[32m" + result.get(i) + "\033[0m, "); // Light Green
              break;
            case 3:
              System.out.print("\033[35m" + result.get(i) + "\033[0m, "); // Purple
              break;
            default:
              System.out.print("\033[33m" + result.get(i) + "\033[0m, "); // Yellow/Orange
              break;
          }
        }
      } else {
        System.out.println("Line not found or empty.");
      }
    } else {
      System.out.println("Invalid line number. No record to read.");
    }
  }


  public void update(int lineNumber, String[] newData) {
    List<String> currentData = readLine(lineNumber);

    if (newData.length == currentData.size()) {
      for (int i = 0; i < newData.length; i++) {
        currentData.set(i, newData[i]);
      }
      updateLine(lineNumber, currentData);
      updateFile();
    }
  }


  public void delete(int lineNumber) {
    lines.remove(lineNumber);
    updateFile();
  }

  public void delete(int fromIndex, int toIndex) {
    if (toIndex >= fromIndex) {
      lines.subList(fromIndex, toIndex + 1).clear();
    }
    updateFile();
  }

  public void list() {
    lines.clear();
    populateLinesList();

    for (int i = 1; i < lines.size(); i++) {
      System.out.print(i + ".) ");
      read(i);
      System.out.println();
    }
  }

  private String getHeader() {
    if (!lines.isEmpty()) {
      return lines.getFirst();
    } else {
      return "";
    }
  }

  private void populateLinesList() {
    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (!line.trim().isEmpty()) {
          lines.add(line);
        }
      }
    } catch (IOException e) {
      System.out.println("An error occurred:" + e);
    }
  }

  private List<String> readLine(int lineNumber) {
    List<String> result = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
      String line;
      int currentLineNumber = 0;

      while ((line = reader.readLine()) != null) {
        currentLineNumber++;

        if (currentLineNumber == lineNumber + 1) {
          String[] columns = line.split(",");
          for (String column : columns) {
            result.add(column.trim());
          }
          break;
        }
      }
    } catch (IOException e) {
      System.out.println("An error occurred:" + e);
    }
    return result;
  }

  public String[] getLine(int index) {
    if (index < 0 || index >= lines.size()) {
      // Index out of bounds, return null or throw an exception
      return null;
    }
    String line = lines.get(index);

    return line.split(",");
  }

  private void updateLine(int lineNumber, List<String> newData) {
    try (BufferedReader ignored = new BufferedReader(new FileReader(fileName))) {
      int currentLineNumber = 0;

      for (int i = 0; i < lines.size(); i++) {
        currentLineNumber++;

        if (currentLineNumber == lineNumber + 1) {
          lines.set(i, String.join(",", newData));
          break;
        }
      }
    } catch (IOException e) {
      System.out.println("An error occurred:" + e);
    }
  }

  public void updateFile() {
    try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
      for (String line : lines) {
        writer.println(line);
        csvFilePath.flush();
      }
    } catch (IOException e) {
      System.out.println("An error occurred:" + e);
    }
  }

  public void clearCsvFile() {
    headerTemplate = getHeader();
    lines.clear();
    lines.add(headerTemplate);
    updateFile();
  }

  public List<String> getLinesList() {
    return lines;
  }

  public void setLinesList(List<String> lines) {
    CsvFileManager.lines = lines;
  }
}