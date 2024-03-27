/*
 * Copyright (c) 2023 Cornelsen Verlag GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.cornelsen.nep.nmshd;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;


@Configuration
@ConditionalOnProperty(prefix = "application.nmshd", name = "enabled")
@EnableFeignClients(basePackages = "de.cornelsen.nep.nmshd", defaultConfiguration = FeignConfig.FeignConfiguration.class)
public class FeignConfig {

	private final String apiKey;

	public FeignConfig(@Value("${application.nmshd.connector-api-key}") String apiKey) {
		this.apiKey = apiKey;
		if (this.apiKey.isBlank()) {
			throw new IllegalArgumentException("Enmeshed API Key must be set");
		}
	}

	@Configuration
	@AllArgsConstructor
	public class FeignConfiguration {

		@Bean
		public RequestInterceptor authRequestInterceptor() {
			return requestTemplate -> requestTemplate.header("X-API-KEY", apiKey);
		}

		@Bean("feignObjectMapper")
		public ObjectMapper feignObjectMapper() {
			final ObjectMapper om = new ObjectMapper();
			om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			om.registerModule(new JavaTimeModule());
			om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			return om;
		}

		@Bean
		public Encoder feignEncoder(@Qualifier("feignObjectMapper") ObjectMapper objectMapper) {
			return new SpringEncoder(() -> new HttpMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper)));
		}

		@Bean
		Logger.Level feignLoggerLevel() {
			return Logger.Level.BASIC;
		}
	}
}
