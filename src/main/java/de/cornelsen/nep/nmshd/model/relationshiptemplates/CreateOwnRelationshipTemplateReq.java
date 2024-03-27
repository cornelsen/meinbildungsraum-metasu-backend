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

package de.cornelsen.nep.nmshd.model.relationshiptemplates;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import de.cornelsen.nep.nmshd.model.attribute.CreateAttributeReq;
import de.cornelsen.nep.nmshd.model.attribute.CreateQueryReq;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CreateOwnRelationshipTemplateReq {

	private Integer maxNumberOfAllocations;
	private LocalDateTime expiresAt;
	private NewRelationship content;

	@Data
	@Builder
	public static class NewRelationship {
		@Builder.Default
		@JsonProperty("@type")
		private String type = "RelationshipTemplateContent";
		private NewRelationshipItem onNewRelationship;

		@Data
		@Builder
		public static class NewRelationshipItem {
			List<RequestItemGroup> items;
		}

		@Data
		@Builder
		public static class RequestItemGroup {
			@Builder.Default
			@JsonProperty("@type")
			private String type = "RequestItemGroup";
			private String title;
			private String description;
			private Boolean mustBeAccepted;
			private List<RequestItem> items;
			private Object responseMetadata;

			@Data
			@NoArgsConstructor
			public static class RequestItem {

				public RequestItem(Boolean mustBeAccepted, CreateAttributeReq attribute, String sourceAttributeId) {
					this.type = RequestItemType.SHARE;
					this.mustBeAccepted = mustBeAccepted;
					this.attribute = attribute;
					this.sourceAttributeId = sourceAttributeId;
				}

				public RequestItem(Boolean mustBeAccepted, CreateQueryReq query) {
					this.type = RequestItemType.READ;
					this.mustBeAccepted = mustBeAccepted;
					this.query = query;
				}

				@JsonProperty("@type")
				private RequestItemType type;
				private Boolean mustBeAccepted;

				//Attribute
				private CreateAttributeReq attribute;
				private String sourceAttributeId;

				//Query
				private CreateQueryReq query;

				public enum RequestItemType {
					SHARE("ShareAttributeRequestItem"), READ("ReadAttributeRequestItem");

					private final String value;

					RequestItemType(String value) {
						this.value = value;
					}

					@JsonValue
					public String getValue() {
						return value;
					}
				}
			}
		}
	}
}