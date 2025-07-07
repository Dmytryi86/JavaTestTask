package gmail.dimmka86;

public class NumberStatCollector implements StatCollector<Double> {
    private long count;
    private double sum;

    private double min = Double.POSITIVE_INFINITY;
    private double max = Double.NEGATIVE_INFINITY;

    @Override
    public void addValue(Double value) {
        count++;
        sum += value;
        if (value < min) {
            min = value;
        }
        if (value > max) {
            max = value;
        }
    }

    @Override
    public String getShortStats() {
        return String.format("Count: %d", count);
    }

    @Override
    public String getFullStats() {
        if (count == 0) {
            return "Count: 0";
        }
        double avg = sum / count;
        return String.format("Count: %d, Min: %.5g, Max: %.5g, Sum: %.5g, Avg: %.5g",
                count, min, max, sum, avg);
    }
}