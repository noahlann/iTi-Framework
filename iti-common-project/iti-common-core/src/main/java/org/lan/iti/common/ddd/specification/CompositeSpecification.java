///*
// *
// *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
// *  *
// *  * Licensed under the Apache License, Version 2.0 (the "License");
// *  * you may not use this file except in compliance with the License.
// *  * You may obtain a copy of the License at
// *  *
// *  *     http://www.apache.org/licenses/LICENSE-2.0
// *  *
// *  * Unless required by applicable law or agreed to in writing, software
// *  * distributed under the License is distributed on an "AS IS" BASIS,
// *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  * See the License for the specific language governing permissions and
// *  * limitations under the License.
// *
// */
//
//package org.lan.iti.common.ddd.specification;
//
//import org.lan.iti.common.ddd.model.IDomain;
//
///**
// * 组合业务规约
// *
// * @author NorthLan
// * @date 2021-03-24
// * @url https://noahlan.com
// */
//public abstract class CompositeSpecification<T extends IDomain> implements ISpecification<T> {
//
//    @Override
//    public ISpecification<T> and(ISpecification<T> spec) {
//        return new AndSpecification<>(this, spec);
//    }
//
//    @Override
//    public ISpecification<T> or(ISpecification<T> spec) {
//        return new OrSpecification<>(this, spec);
//    }
//
//    @Override
//    public ISpecification<T> not() {
//        return new NotSpecification<>(this);
//    }
//}
