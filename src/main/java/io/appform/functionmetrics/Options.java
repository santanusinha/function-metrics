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
import lombok.Getter;

@Getter

public class Options {
    private boolean enableParameterCapture;
    private Converter<String, String> caseFormatConverter = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_CAMEL);
    private boolean disableCacheOptimisation;

    public static class OptionsBuilder {
        private boolean enableParameterCapture;
        private Converter<String, String> caseFormatConverter;
        private boolean disableCacheOptimisation;

        public OptionsBuilder enableParameterCapture(final boolean enableParameterCapture) {
            this.enableParameterCapture = enableParameterCapture;
            return this;
        }

        public OptionsBuilder caseFormatConverter(final Converter<String, String> caseFormatConverter) {
            this.caseFormatConverter = caseFormatConverter;
            return this;
        }

        public OptionsBuilder disableCacheOptimisation() {
            return disableCacheOptimisation(true);
        }

        public OptionsBuilder disableCacheOptimisation(final boolean disableCacheOptimisation) {
            this.disableCacheOptimisation = disableCacheOptimisation;
            return this;
        }

        public Options build() {
            Options options = new Options();
            if (caseFormatConverter != null) {
                options.caseFormatConverter = caseFormatConverter;
            }
            options.enableParameterCapture = enableParameterCapture;
            options.disableCacheOptimisation = disableCacheOptimisation;
            return options;
        }
    }
}
