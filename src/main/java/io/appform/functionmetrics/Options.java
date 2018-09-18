package io.appform.functionmetrics;

import com.google.common.base.CaseFormat;

public class Options {
    private boolean enableParameterCapture;
    private CaseFormat caseFormat = CaseFormat.LOWER_CAMEL;

    public boolean isEnableParameterCapture() {
        return enableParameterCapture;
    }

    public void setEnableParameterCapture(final boolean enableParameterCapture) {
        this.enableParameterCapture = enableParameterCapture;
    }

    public CaseFormat getCaseFormat() {
        return caseFormat;
    }

    public void setCaseFormat(final CaseFormat caseFormat) {
        this.caseFormat = caseFormat;
    }

    public Options() {}

    public static class OptionsBuilder {
        private boolean enableParameterCapture;
        private CaseFormat caseFormat;

        public OptionsBuilder enableParameterCapture(final boolean enableParameterCapture) {
            this.enableParameterCapture = enableParameterCapture;
            return this;
        }

        public OptionsBuilder caseFormat(final CaseFormat caseFormat) {
            this.caseFormat = caseFormat;
            return this;
        }

        public Options build() {
            Options options = new Options();
            if (caseFormat != null) {
                options.setCaseFormat(caseFormat);
            }
            options.setEnableParameterCapture(enableParameterCapture);
            return options;
        }
    }
}
