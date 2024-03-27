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

package de.cornelsen.nep.mapper;

import de.cornelsen.nep.model.dto.publisher.PublisherCreateReq;
import de.cornelsen.nep.model.dto.publisher.PublisherRsp;
import de.cornelsen.nep.model.dto.publisher.PublisherUpdateReq;
import de.cornelsen.nep.model.entity.Publisher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PublisherMapper extends CommonMapper {

	@Mapping(source = "croppyImageId", target = "croppyImageId", qualifiedByName = "decryptedValue")
	@Mapping(source = "originImageId", target = "originImageId", qualifiedByName = "decryptedValue")
	Publisher toEntity(PublisherCreateReq src);

	List<PublisherRsp> toDto(List<Publisher> publishers);

	@Mapping(source = "id", target = "decryptedId")
	@Mapping(source = "id", target = "id", qualifiedByName = "encryptedValue")
	@Mapping(source = "croppyImageId", target = "croppyImageId", qualifiedByName = "encryptedValue")
	@Mapping(source = "originImageId", target = "originImageId", qualifiedByName = "encryptedValue")
	PublisherRsp toDto(Publisher publisher);

	@Mapping(target = "id", ignore = true)
	@Mapping(source = "croppyImageId", target = "croppyImageId", qualifiedByName = "decryptedValue")
	@Mapping(source = "originImageId", target = "originImageId", qualifiedByName = "decryptedValue")
	Publisher toEntity(PublisherUpdateReq src, @MappingTarget Publisher target);

}
