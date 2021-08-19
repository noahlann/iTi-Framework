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

package org.lan.iti.iha.security.pipeline;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author NorthLan
 * @date 2021/7/29
 * @url https://blog.noahlan.com
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class PipelineManager {
    private List<Pipeline> pipelines;

    public void registerPipeline(Pipeline... pipelines) {
        registerPipeline(Arrays.asList(pipelines));
    }

    public void registerPipeline(List<Pipeline> pipelines) {
        if (this.pipelines == null) {
            this.pipelines = new LinkedList<>();
        }
        this.pipelines.addAll(pipelines);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private <T extends Pipeline> void process(Class<T> clazz, ThrowableConsumer<T> consumer) {
        if (this.pipelines != null) {
            for (Pipeline pipeline : this.pipelines) {
                if (clazz.isAssignableFrom(pipeline.getClass())) {
                    consumer.accept((T) pipeline);
                }
            }
        }
    }

    public void onAuthenticationFailure(RequestParameter parameter, AuthenticationException exception) throws AuthenticationException {
        process(AuthenticationFailureHandler.class, pipeline -> pipeline.onAuthenticationFailure(parameter, exception));
    }

    public void onAuthenticationSuccess(RequestParameter parameter, Authentication authentication) throws AuthenticationException {
        process(AuthenticationSuccessHandler.class, pipeline -> pipeline.onAuthenticationSuccess(parameter, authentication));
    }

    public void preAuthentication(RequestParameter parameter) throws AuthenticationException {
        process(PreAuthenticationHandler.class, pipeline -> pipeline.preAuthentication(parameter));
    }

    @FunctionalInterface
    private interface ThrowableConsumer<T extends Pipeline> {
        void accept(T t) throws IOException, ServletException;
    }
}
