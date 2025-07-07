package gmail.dimmka86;

public class StringStatCollector implements StatCollector<String> {
    private long count;
    private int minLength = Integer.MAX_VALUE;
    private int maxLength = 0;

    @Override
    public void addValue(String value) {
        count++;
        int length = value.length();
        if (length < minLength) {
            minLength = length;
        }
        if (length > maxLength) {
            maxLength = length;
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
        return String.format("Count: %d, Min length: %d, Max length: %d",
                count, minLength, maxLength);
    }
}