/*
 *
 *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.lan.iti.common.ddd.step;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 框架提供的默认{@link IReviseStepsException}实现.
 *
 * @author NorthLan
 * @date 2021-02-07
 * @url https://noahlan.com
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Deprecated
public class ReviseStepsException extends RuntimeException implements IReviseStepsException {
    private static final long serialVersionUID = -5673564807243684991L;

    private List<String> subsequentSteps = new ArrayList<>();

    public static ReviseStepsException withSubsequentSteps(List<String> subsequentSteps) {
        return new ReviseStepsException(subsequentSteps);
    }

    public static ReviseStepsException withSubsequentSteps(String... steps) {
        return new ReviseStepsException(Arrays.asList(steps));
    }

    public ReviseStepsException subsequentSteps(List<String> subsequentSteps) {
        this.subsequentSteps = subsequentSteps;
        return this;
    }

    @Override
    public @NotNull List<String> subsequentSteps() {
        return this.subsequentSteps;
    }
}
