package io.github.arubaid.common.exception;

public class FeatureNotImplementedException extends RuntimeException {

    public FeatureNotImplementedException(String feature) {

        super(feature + " is coming soon.");
    }
}