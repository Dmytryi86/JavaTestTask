package gmail.dimmka86;

public interface StatCollector<T> {
    /** Добавить новое значение */
    void addValue(T value);

    /** Получить краткую статистику (например, только количество) */
    String getShortStats();

    /** Получить полную статистику */
    String getFullStats();
}