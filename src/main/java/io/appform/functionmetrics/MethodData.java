/*
 * Copyright (c) 2021 Santanu Sinha <santanu.sinha@gmail.com>
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

/**
 * Stores data about method call
 */
public class MethodData {
    private final String className;
    private final String methodName;
    private final boolean parameterCaptureRequired;

    public MethodData(String className, String methodName, boolean parameterInspectionRequired) {
        this.className = className;
        this.methodName = methodName;
        this.parameterCaptureRequired = parameterInspectionRequired;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isParameterCaptureRequired() {
        return parameterCaptureRequired;
    }
}
