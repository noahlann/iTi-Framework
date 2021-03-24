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

package org.lan.iti.cloud.enforce;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.ImportOptions;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.belongToAnyOf;

/**
 * @author NorthLan
 * @date 2021-02-07
 * @url https://noahlan.com
 */
public class ITIArchitectureEnforcerTest {

    private JavaClasses classes;

    @BeforeEach
    public void setUp() {
        ImportOptions importOptions = new ImportOptions();
        importOptions.with(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS);
        classes = new ClassFileImporter().importPackages("org.lan.iti.boot.mock");
        classes = classes.that(DescribedPredicate.not(belongToAnyOf(
                // 过滤一些类
        )));
    }

    @Test
    public void requiredRules() {
        for (ArchRule requiredRule : ITIArchitectureEnforcer.REQUIRED_RULES) {
            requiredRule.check(classes);
        }
    }

    @Test
    public void optionalDddLayerRule() {
        ITIArchitectureEnforcer.OPTIONAL_DDD_LAYER_RULE.check(classes);
    }

}
