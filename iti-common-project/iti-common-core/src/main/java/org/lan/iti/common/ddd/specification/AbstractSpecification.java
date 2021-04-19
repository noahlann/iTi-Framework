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

package org.lan.iti.common.ddd.specification;

/**
 * Abstract base implementation of composite {@link ISpecification} with default
 * implementations for {@code and}, {@code or} and {@code not}.
 *
 * @author NorthLan
 * @date 2021-03-24
 * @url https://noahlan.com
 */
public abstract class AbstractSpecification<T> implements ISpecification<T> {
    protected Notification notification;

    @Override
    public Notification getNotification() {
        if (notification == null) {
            notification = Notification.create();
        }
        return notification;
    }

    @Override
    public boolean satisfiedBy(T candidate) {
        return satisfiedBy(candidate, getNotification());
    }

    /**
     * Create a new specification that is the AND operation of {@code this} specification and another specification.
     *
     * @param spec Specification to AND.
     * @return CompositeSpecification
     */
    public AbstractSpecification<T> and(ISpecification<T> spec) {
        return new AndSpecification<>(this, spec);
    }

    /**
     * Create a new specification that is the OR operation of {@code this} specification and another specification.
     *
     * @param spec Specification to OR.
     * @return CompositeSpecification
     */
    public AbstractSpecification<T> or(ISpecification<T> spec) {
        return new OrSpecification<>(this, spec);
    }

    /**
     * Create a new specification that is the NOT operation of {@code this} specification.
     *
     * @param spec Specification to NOT.
     * @return CompositeSpecification
     */
    public AbstractSpecification<T> not(ISpecification<T> spec) {
        return new NotSpecification<>(spec);
    }
}
