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
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the is the container for the set of curriculum standards metadata. Each member of the set contains the curriculum standards metadata for a specific source of the GUIDs.
 */
@Data
public class CSMSetType {
	/**
	 * This is a human readable label used to identify the type of resource, or part of resource, to which the enclosed metadata refers. Model Primitive Datatype &#x3D; NormalizedString.
	 */
	@JsonProperty("resourceLabel")
	private String resourceLabel;

	/**
	 * This is used to contain the appropriate identifier that is used to identify the resource part. Model Primitive Datatype &#x3D; NormalizedString.
	 */
	@JsonProperty("resourcePartId")
	private String resourcePartId;

	/**
	 * The curriculum standards associated from a single source of the curriculum standards definition.
	 */
	@NotEmpty
	@JsonProperty("curriculumStandardsMetadata")
	private List<CurriculumStandardsMetadataType> curriculumStandardsMetadata = new ArrayList<>();

}

