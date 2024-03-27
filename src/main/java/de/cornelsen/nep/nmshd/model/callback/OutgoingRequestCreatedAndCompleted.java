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

package de.cornelsen.nep.nmshd.model.callback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutgoingRequestCreatedAndCompleted {

	private ResponseData data;
	private String trigger;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ResponseData {
		private String id;
		@JsonProperty("isOwn")
		private Boolean isOwn;
		private String peer;
		private LocalDateTime createdAt;
		private RequestContent content;
		private Source source;
		private Response response;
		private String status;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Response {
		private LocalDateTime createdAt;
		private ResponseContent content;
		private Source source;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RequestContent {
		@JsonProperty("@type")
		private String type;
		private List<RequestItemGroup> items;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ResponseContent {
		@JsonProperty("@type")
		private String type;
		private List<ResponseItemGroup> items;
		private String requestId;
		private String result;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RequestItemGroup {
		@JsonProperty("@type")
		private String type;
		private List<AttributeRequestItem> items;
		@JsonProperty("mustBeAccepted")
		private Boolean mustBeAccepted;
		private String title;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ResponseItemGroup {
		@JsonProperty("@type")
		private String type;
		private List<AttributeAcceptResponseItem> items;
	}

	//ShareAttributeRequestItem or ReadAttributeRequestItem
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class AttributeRequestItem {
		@JsonProperty("@type")
		private String type;
		private Query query;
		private Attribute attribute;
		@JsonProperty("mustBeAccepted")
		private Boolean mustBeAccepted;
		private String sourceAttributeId;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class AttributeAcceptResponseItem {
		@JsonProperty("@type")
		private String type;
		private Attribute attribute;
		private String attributeId;
		private String result;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Attribute {
		@JsonProperty("@type")
		private String type;
		private String owner;
		private Values value;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Query {
		@JsonProperty("@type")
		private String type;
		private String valueType;
	}

	@Data
	public static class Values {
		@JsonProperty("@type")
		private String type;
		private String value;
	}

	@Data
	public static class Source {
		private String type;
		private String reference;
	}

}
