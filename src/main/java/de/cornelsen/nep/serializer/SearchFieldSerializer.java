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

package de.cornelsen.nep.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.cornelsen.nep.model.dto.request.SearchReq;
import de.cornelsen.nep.model.dto.search.ProviderType;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SearchFieldSerializer extends JsonSerializer<String> {

	public static final String DELIMITER = " ";
	public static final String DUF_FIELD_NAME = "name";
	public static final String JOINING_DELIMITER = ",";


	@Override
	public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		String result = value.matches("\".*\"") ?
			Arrays.stream(value.split("\"")).skip(1).collect(Collectors.joining(JOINING_DELIMITER))
			: String.join(JOINING_DELIMITER, value.split(DELIMITER));

		jsonGenerator.writeString(result);
		SearchReq searchReq = (SearchReq) jsonGenerator.getCurrentValue();
		if (ProviderType.DUF.equals(searchReq.getProviderType())) {
			jsonGenerator.writeStringField(DUF_FIELD_NAME, result);
		}
	}
}