package weka.core;

/**
 * @author Markus Deutsch
 */
public class FeatureStrippedException extends RuntimeException {
    public FeatureStrippedException() {
        super("Requested feature is not available on Android.");
    }
}
