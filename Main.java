package gmail.dimmka86;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        Options options = parseArgs(args);

        // Если после разбора аргументов нет входных файлов — выходим
        if (options.getInputFiles().isEmpty()) {
            System.err.println("Не заданы входные файлы для обработки.");
            return;
        }

        FilterProcessor processor = new FilterProcessor(options);
        processor.process();
    }

    private static Options parseArgs(String[] args) {
        Options options = new Options();

        // Будем читать флаги до тех пор, пока не встретим что-то, что не начинается с "-"
        // Всё остальное считаем именами файлов
        int i = 0;
        while (i < args.length) {
            String arg = args[i];
            if (!arg.startsWith("-")) {
                // Считаем остальное входными файлами
                break;
            }

            switch (arg) {
                case "-a":
                    options.setAppendMode(true);
                    i++;
                    break;
                case "-s":
                    options.setShortStats(true);
                    i++;
                    break;
                case "-f":
                    options.setFullStats(true);
                    i++;
                    break;
                case "-o":
                    i++;
                    if (i < args.length) {
                        options.setOutputPath(args[i]);
                        i++;
                    } else {
                        System.err.println("Указан флаг -o без пути. Игнорируем.");
                    }
                    break;
                case "-p":
                    i++;
                    if (i < args.length) {
                        options.setPrefix(args[i]);
                        i++;
                    } else {
                        System.err.println("Указан флаг -p без префикса. Игнорируем.");
                    }
                    break;
                default:
                    System.err.println("Неизвестная опция: " + arg);
                    i++;
                    break;
            }
        }

        // Остальные аргументы — это входные файлы
        while (i < args.length) {
            options.getInputFiles().add(args[i]);
            i++;
        }

        return options;
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar content-filter-util.jar [options] file1 [file2 ...]");
        System.out.println("Options:");
        System.out.println("  -a             Append mode (default is overwrite)");
        System.out.println("  -s             Short statistics mode");
        System.out.println("  -f             Full statistics mode");
        System.out.println("  -o <path>      Output directory");
        System.out.println("  -p <prefix>    Prefix for output file names");
    }
}