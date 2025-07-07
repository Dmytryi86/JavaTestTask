package gmail.dimmka86;

import java.util.ArrayList;
import java.util.List;

public class Options {
    private boolean appendMode = false;       // -a
    private boolean shortStats = false;       // -s
    private boolean fullStats = false;        // -f

    private String outputPath = "";           // -o
    private String prefix = "";               // -p

    private List<String> inputFiles = new ArrayList<>();

    public boolean isAppendMode() {
        return appendMode;
    }

    public void setAppendMode(boolean appendMode) {
        this.appendMode = appendMode;
    }

    public boolean isShortStats() {
        return shortStats;
    }

    public void setShortStats(boolean shortStats) {
        this.shortStats = shortStats;
    }

    public boolean isFullStats() {
        return fullStats;
    }

    public void setFullStats(boolean fullStats) {
        this.fullStats = fullStats;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<String> getInputFiles() {
        return inputFiles;
    }
}