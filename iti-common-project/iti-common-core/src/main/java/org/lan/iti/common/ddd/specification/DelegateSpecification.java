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
 * @author NorthLan
 * @date 2021-04-16
 * @url https://noahlan.com
 */
public class DelegateSpecification<T> extends AbstractSpecification<T> {
    private ISpecification<T> delegate;

    public DelegateSpecification(ISpecification<T> specification) {
        this.delegate = specification;
    }

    public DelegateSpecification() {
    }

    @Override
    public boolean satisfiedBy(T candidate) {
        if (delegate == null) {
            return true;
        }
        return delegate.satisfiedBy(candidate);
    }

    @Override
    public boolean satisfiedBy(T candidate, Notification notification) {
        if (delegate == null) {
            return true;
        }
        return delegate.satisfiedBy(candidate, notification);
    }
}
