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
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * The container for all of the core information about an LTI Link.
 */
@Data
public class LTILinkType {
	/**
	 * The human readable title/label for the activity being addressed by the content available through the LTI link. Model Primitive Datatype = NormalizedString.
	 */
	@NotBlank
	@JsonProperty("title")
	private String title;

	/**
	 * A human readable description of the activity addressed by the content supplied via the LTI link. Model Primitive Datatype = String.
	 */
	@JsonProperty("description")
	private String description;

	@JsonProperty("custom")
	private PropertySetType custom;

	@JsonProperty("extensions")
	private PlatformPropertySetType extensions;

	/**
	 * The URL for the LTI launch. One of either the launch_url or the secure_launch_url must be specified. It is acceptable to specify both and if both are specified, the Tool Consumer (TC) decides which to use. Typically, the TC will use a secure_launch_url when embedding the Tool in a secure page and the launch_url when embedding the tool in a non-secure page. So, it is important that the Tool Provider (TP) provides the same functionality whether the launch_url or secure_launch_url is used. Model Primitive Datatype = AnyURI.
	 */
	@JsonProperty("launch_url")
	private String launchUrl;

	/**
	 * A secure URL for the LTI launch. One of either the launch_url or the secure_launch_url must be specified. It is acceptable to specify both and if both are specified, the Tool Consumer (TC) decides which to use. Typically, the TC will use a secure_launch_url when embedding the Tool in a secure page and the launch_url when embedding the tool in a non-secure page. So, it is important that the Tool Provider (TP) provides the same functionality whether the launch_url or secure_launch_url is used. Model Primitive Datatype = AnyURI.
	 */
	@JsonProperty("secure_launch_url")
	private String secureLaunchUrl;

	/**
	 * A URL to an icon for this tool. Model Primitive Datatype = AnyURI.
	 */
	@JsonProperty("icon")
	private String icon;

	/**
	 * A secure URL to an icon for this tool. Model Primitive Datatype = AnyURI.
	 */
	@JsonProperty("secure_icon")
	private String secureIcon;

	@NotNull
	@JsonProperty("vendor")
	private VendorType vendor;

}

