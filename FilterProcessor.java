package gmail.dimmka86;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class FilterProcessor {

    private final Options options;

    // Объекты для сбора статистики
    private final NumberStatCollector integerStats = new NumberStatCollector();
    private final NumberStatCollector floatStats = new NumberStatCollector();
    private final StringStatCollector stringStats = new StringStatCollector();

    // Ведём флаги, были ли в итоге обнаружены целые, вещественные и строки
    private boolean hasIntegers = false;
    private boolean hasFloats = false;
    private boolean hasStrings = false;

    // Потоки на запись (создадим только по мере необходимости)
    private BufferedWriter intWriter = null;
    private BufferedWriter floatWriter = null;
    private BufferedWriter stringWriter = null;

    public FilterProcessor(Options options) {
        this.options = options;
    }

    public void process() {
        // Обработка каждого входного файла
        for (String inputFile : options.getInputFiles()) {
            processSingleFile(inputFile);
        }

        // Закрываем потоки
        closeWriters();

        // Выводим статистику
        printStats();
    }

    private void processSingleFile(String inputFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                classifyAndWrite(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден: " + inputFile + ". Ошибка: " + e.getMessage());
            // Частичная обработка — пропускаем этот файл
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода при чтении " + inputFile + ": " + e.getMessage());
        }
    }

    private void classifyAndWrite(String line) {
        // Пробуем определить, является ли это целым, вещественным или строкой
        // 1) Проверяем, целое ли это
        if (isInteger(line)) {
            writeInteger(line);
        }
        // 2) Если не целое, проверяем вещественное
        else if (isFloat(line)) {
            writeFloat(line);
        }
        // 3) Иначе строка
        else {
            writeString(line);
        }
    }

    private boolean isInteger(String s) {
        try {
            // Можно использовать Long.parseLong, если ожидаются очень большие числа
            // или BigInteger при желании
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isFloat(String s) {
        try {
            // Позволяем форматы с E, например 1.5E-10
            Double.parseDouble(s.replace(",", "."));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void writeInteger(String line) {
        if (!hasIntegers) {
            hasIntegers = true;
            intWriter = createWriter(getTargetFileName("integers.txt"));
        }
        // Записываем
        try {
            intWriter.write(line);
            intWriter.newLine();
            intWriter.flush();
            // Сохраняем статистику
            double value = Double.parseDouble(line);
            integerStats.addValue(value);
        } catch (IOException e) {
            System.err.println("Ошибка записи целого числа: " + e.getMessage());
        }
    }

    private void writeFloat(String line) {
        if (!hasFloats) {
            hasFloats = true;
            floatWriter = createWriter(getTargetFileName("floats.txt"));
        }
        try {
            floatWriter.write(line);
            floatWriter.newLine();
            floatWriter.flush();
            double value = Double.parseDouble(line.replace(",", "."));
            floatStats.addValue(value);
        } catch (IOException e) {
            System.err.println("Ошибка записи вещественного числа: " + e.getMessage());
        }
    }

    private void writeString(String line) {
        if (!hasStrings) {
            hasStrings = true;
            stringWriter = createWriter(getTargetFileName("strings.txt"));
        }
        try {
            stringWriter.write(line);
            stringWriter.newLine();
            stringWriter.flush();
            stringStats.addValue(line);
        } catch (IOException e) {
            System.err.println("Ошибка записи строки: " + e.getMessage());
        }
    }

    private BufferedWriter createWriter(String fileName) {
        try {
            Path path = Paths.get(fileName);
            // Создадим директории, если требуется
            Files.createDirectories(path.getParent());
            return new BufferedWriter(new FileWriter(fileName, options.isAppendMode()));
        } catch (IOException e) {
            System.err.println("Не удалось создать файл для записи: " + fileName + ". " + e.getMessage());
            return null;
        }
    }

    private String getTargetFileName(String defaultName) {
        // Учтём опции -o (путь) и -p (префикс)
        // Например, /some/path и prefix_ => /some/path/prefix_integers.txt
        String dir = options.getOutputPath();
        if (!dir.isEmpty() && !dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        return dir + options.getPrefix() + defaultName;
    }

    private void closeWriters() {
        try {
            if (intWriter != null) {
                intWriter.close();
            }
            if (floatWriter != null) {
                floatWriter.close();
            }
            if (stringWriter != null) {
                stringWriter.close();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии файлов: " + e.getMessage());
        }
    }

    private void printStats() {
        // В зависимости от опций -s (краткая) и -f (полная) выводим статистику по каждому типу
        System.out.println("=== Statistics ===");

        // Integers
        if (hasIntegers) {
            System.out.println("-- Integers --");
            if (options.isFullStats()) {
                System.out.println(integerStats.getFullStats());
            } else if (options.isShortStats()) {
                System.out.println(integerStats.getShortStats());
            } else {
                // Если явно не указано, можно выводить либо ничего, либо что-то базовое
                System.out.println(integerStats.getShortStats());
            }
        }

        // Floats
        if (hasFloats) {
            System.out.println("-- Floats --");
            if (options.isFullStats()) {
                System.out.println(floatStats.getFullStats());
            } else if (options.isShortStats()) {
                System.out.println(floatStats.getShortStats());
            } else {
                System.out.println(floatStats.getShortStats());
            }
        }

        // Strings
        if (hasStrings) {
            System.out.println("-- Strings --");
            if (options.isFullStats()) {
                System.out.println(stringStats.getFullStats());
            } else if (options.isShortStats()) {
                System.out.println(stringStats.getShortStats());
            } else {
                System.out.println(stringStats.getShortStats());
            }
        }
    }
}