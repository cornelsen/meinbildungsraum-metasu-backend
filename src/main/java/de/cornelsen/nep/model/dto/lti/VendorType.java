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

package de.cornelsen.nep.model.dto.lti;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * The container for the information about the vendor who created the resource.
 */
@Data
public class VendorType {
	/**
	 * An identification code for the vendor. Model Primitive Datatype = NormalizedString.
	 */
	@NotBlank
	@JsonProperty("code")
	private String code;

	/**
	 * The name of the vendor. Model Primitive Datatype = NormalizedString.
	 */
	@NotBlank
	@JsonProperty("name")
	private String name;

	/**
	 * A human readable description of the vendor. Model Primitive Datatype = String.
	 */
	@JsonProperty("description")
	private String description;

	/**
	 * A URL for the vendor. Model Primitive Datatype = AnyURI.
	 */
	@JsonProperty("url")
	private String url;

	/**
	 * Contact email for the vendor. Model Primitive Datatype = NormalizedString.
	 */
	@JsonProperty("emailContact")
	private String emailContact;

}

