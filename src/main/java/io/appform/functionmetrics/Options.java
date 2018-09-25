package io.appform.functionmetrics;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;

public class Options {
    private boolean enableParameterCapture;
    private Converter<String, String> caseFormatConverter = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_CAMEL);

    public boolean isEnableParameterCapture() {
        return enableParameterCapture;
    }

    public void setEnableParameterCapture(final boolean enableParameterCapture) {
        this.enableParameterCapture = enableParameterCapture;
    }

    public Converter<String, String> getCaseFormatConverter() {
        return caseFormatConverter;
    }

    public void setCaseFormatConverter(final Converter<String, String> caseFormatConverter) {
        this.caseFormatConverter = caseFormatConverter;
    }

    public Options() {}

    public static class OptionsBuilder {
        private boolean enableParameterCapture;
        private Converter<String, String> caseFormatConverter;

        public OptionsBuilder enableParameterCapture(final boolean enableParameterCapture) {
            this.enableParameterCapture = enableParameterCapture;
            return this;
        }

        public OptionsBuilder caseFormatConverter(final Converter<String, String> caseFormatConverter) {
            this.caseFormatConverter = caseFormatConverter;
            return this;
        }

        public Options build() {
            Options options = new Options();
            if (caseFormatConverter != null) {
                options.setCaseFormatConverter(caseFormatConverter);
            }
            options.setEnableParameterCapture(enableParameterCapture);
            return options;
        }
    }
}
