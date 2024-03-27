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

package de.cornelsen.nep.nmshd.model.attribute;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAttributeReq {

	@JsonProperty("@type")
	private AttributeType type;
	private String owner;
	private List<String> tags;
	private LocalDateTime validFrom;
	private LocalDateTime validTo;
	private AttributeValue value;

	public enum AttributeType {
		IDENTITY("IdentityAttribute"), RELATIONSHIP("RelationshipAttribute");

		private final String value;

		AttributeType(String value) {
			this.value = value;
		}

		@JsonValue
		public String getValue() {
			return value;
		}
	}

	@Data
	@Builder
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor
	public static class AttributeValue {
		@JsonProperty("@type")
		private AttributeValueType type;
		private String value;

		public enum AttributeValueType {
			DISPLAY_NAME("DisplayName"), PERSON_NAME("PersonName"), SURNAME("Surname"), GIVEN_NAME("GivenName"), EMAIL("EMailAddress");

			private final String value;

			AttributeValueType(String value) {
				this.value = value;
			}

			@JsonValue
			public String getValue() {
				return value;
			}
		}
	}
}