/*
 * Copyright (c) 2019 Santanu Sinha <santanu.sinha@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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

    public Options() {
        /* Nothing to do here */
    }

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
