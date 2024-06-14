package server.filters;

public interface Filter<I,O> {
    O process(I input);
}
