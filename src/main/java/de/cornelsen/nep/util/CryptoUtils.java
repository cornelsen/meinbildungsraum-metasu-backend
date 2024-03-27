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

package de.cornelsen.nep.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.codec.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.NoSuchElementException;

@Slf4j
public class CryptoUtils {
	private static final String ALGORITHM = "DESede";
	private static final String ENCODING = "ISO-8859-2";
	private static final String TRANSFORMATION = "DESede/CBC/PKCS5Padding";
	private static final String HEX_KEY = "a8eb47c6194a8111ec1b90910242ac12000210242ac12001";

	private CryptoUtils() {
	}

	public static String encrypt(final Long value) throws RuntimeException {

		if (value == null) {
			return null;
		}
		try {
			byte[] keyVal = Hex.decode(HEX_KEY);
			DESedeKeySpec keySpec = new DESedeKeySpec(keyVal);
			SecretKey key = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(keySpec);
			IvParameterSpec iv = new IvParameterSpec(new byte[8]);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			byte[] textTab = value.toString().getBytes(ENCODING);
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			byte[] doFinal = cipher.doFinal(textTab);
			return String.valueOf(Hex.encode(doFinal));
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new IllegalArgumentException("errors.wrongValue");
		}
	}

	public static Long decrypt(final String value) throws RuntimeException {
		if (value == null) {
			return null;
		}
		try {
			byte[] keyVal = Hex.decode(HEX_KEY);
			DESedeKeySpec keySpec = new DESedeKeySpec(keyVal);
			IvParameterSpec iv = new IvParameterSpec(new byte[8]);
			SecretKey key = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(keySpec);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			byte[] encryptedTab = Hex.decode(value);
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
			byte[] doFinal = cipher.doFinal(encryptedTab);
			return Long.parseLong(new String(doFinal, ENCODING));
		} catch (Exception e) {
			log.error("Unable to decrypt value: {}, due to: {}", value, e.getMessage());
			throw new NoSuchElementException();
		}
	}
}
