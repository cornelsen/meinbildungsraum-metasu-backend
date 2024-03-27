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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * This is the container for the details of a subject that is covered by the content within a LOR.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectType {
	/**
	 * The unique identifier, an integer, of the subject node. For the root node the value will be 'null'.  Model Primitive Datatype = PositiveInteger.
	 */
	@NotNull
	@Min(1)
	@JsonProperty("identifier")
	private Integer identifier;

	/**
	 * The name of the subject node, which may have any character and need not be unique with the returned taxonomy.  Model Primitive Datatype = NormalizedString.
	 */
	@NotBlank
	@JsonProperty("name")
	private String name;

	/**
	 * An integer (the identifier of that subject node) that references the single parent of this node. The returned data must be a true rooted tree, where each node returned has a single parent.  Model Primitive Datatype = PositiveInteger.
	 */
	@NotNull
	@Min(1)
	@JsonProperty("parent")
	private Integer parent;


}

