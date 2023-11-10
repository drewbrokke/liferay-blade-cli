/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import java.util.Comparator;

/**
 * @author Drew Brokke
 */
public class ProductKeyVersion implements Comparable<ProductKeyVersion> {

	public static final ProductKeyVersion BLANK = new ProductKeyVersion();

	public ProductKeyVersion(String versionString) {
		StringBuilder numberStringBuilder = new StringBuilder();
		StringBuilder typeStringBuilder = new StringBuilder();

		for (char c : versionString.toCharArray()) {
			if (Character.isDigit(c)) {
				numberStringBuilder.append(c);
			}
			else if (Character.isAlphabetic(c)) {
				typeStringBuilder.append(c);
			}
		}

		if (numberStringBuilder.length() > 0) {
			_number = Integer.parseInt(numberStringBuilder.toString());
		}

		_type = typeStringBuilder.toString();
	}

	@Override
	public int compareTo(final ProductKeyVersion version) {
		return Comparator.comparingInt(
			ProductKeyVersion::getNumber
		).thenComparing(
			ProductKeyVersion::getType
		).compare(
			this, version
		);
	}

	public int getNumber() {
		return _number;
	}

	public String getType() {
		return _type;
	}

	private ProductKeyVersion() {
	}

	private int _number = 0;
	private String _type;

}