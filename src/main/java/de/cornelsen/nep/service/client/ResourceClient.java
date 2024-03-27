/*
 * Copyright (c) 2023-2024 Cornelsen Verlag GmbH
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

package de.cornelsen.nep.service.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.cornelsen.nep.model.dto.publisher.PublisherRsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.function.Function;

public abstract class ResourceClient {

	Logger log = LoggerFactory.getLogger(ResourceClient.class);

	@Value("${application.request-timeout:10}")
	private long requestTimeout;

	public static final String TOTAL_ELEMENTS_PAGINATION_HEADER = "X-Total-Count";

	protected final ObjectMapper mapper = new ObjectMapper()
		.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
		.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		.disable(SerializationFeature.FAIL_ON_SELF_REFERENCES)
		.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
		.registerModule(new JavaTimeModule());

	WebClient webClient = WebClient
		.create()
		.mutate()
		.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) //16MB
		.build();

	protected final <T> Mono<ResponseEntity<T>> callAPI(Function<UriBuilder, URI> uriBuilder, Class<T> responseType, PublisherRsp publisher) {
		return webClient.get()
			.uri(uriBuilder)
			.retrieve()
			.onStatus(HttpStatusCode::isError, response -> {
					log.error("Provider {} responded with status code: {}, headers: {}", publisher.getName(), response.statusCode(), response.headers().asHttpHeaders());
					response.bodyToMono(String.class).subscribe(body -> log.trace("Response body: {}", body));
					return Mono.empty();
			})
			.toEntity(responseType)
			.timeout(Duration.ofSeconds(requestTimeout))
			.doOnError(throwable -> log.error("[{}] skipped due to exception: {}", publisher.getName(), throwable.toString()))
			.onErrorReturn(ResponseEntity.ok(null));
	}

}
