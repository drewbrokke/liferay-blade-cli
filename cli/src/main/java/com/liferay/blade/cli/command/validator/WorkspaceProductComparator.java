/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.liferay.blade.cli.util.ArrayUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 * @author Drew Brokke
 */
public class WorkspaceProductComparator implements Comparator<String> {

	@Override
	public int compare(String key1, String key2) {
		KeyInfo keyInfo1 = new KeyInfo(key1);

		return keyInfo1.compareTo(new KeyInfo(key2));
	}

	private static final List<String> _products = Collections.unmodifiableList(
		Arrays.asList("commerce", "portal", "dxp"));

	private static class KeyInfo implements Comparable<KeyInfo> {

		public KeyInfo(String key) {
			String[] parts = key.split("-");

			if (ArrayUtil.isEmpty(parts)) {
				throw new IllegalArgumentException();
			}

			product = parts[0];

			productRank = _products.indexOf(product);

			if (Objects.equals(product, "dxp") && (parts.length == 2)) {
				quarterly = true;

				String[] quarterlyParts = parts[1].split("\\.");

				majorVersion = new Version(quarterlyParts[0]);
				minorVersion = new Version(quarterlyParts[1]);
				microVersion = new Version(quarterlyParts[2]);

				return;
			}

			majorVersion = new Version(parts[1]);

			if (parts.length > 2) {
				minorVersion = new Version(parts[2]);
			}
		}

		@Override
		public int compareTo(final KeyInfo keyInfo) {
			return Comparator.comparing(
				KeyInfo::getProductRank
			).thenComparing(
				KeyInfo::isQuarterly
			).thenComparing(
				KeyInfo::getMajorVersion
			).thenComparing(
				KeyInfo::getMinorVersion
			).thenComparing(
				KeyInfo::getMicroVersion
			).reversed(
			).compare(
				this, keyInfo
			);
		}

		public Version getMajorVersion() {
			return majorVersion;
		}

		public Version getMicroVersion() {
			return microVersion;
		}

		public Version getMinorVersion() {
			return minorVersion;
		}

		public String getProduct() {
			return product;
		}

		public int getProductRank() {
			return productRank;
		}

		public boolean isQuarterly() {
			return quarterly;
		}

		protected Version majorVersion;
		protected Version microVersion = Version.BLANK;
		protected Version minorVersion = Version.BLANK;
		protected final String product;
		protected final int productRank;
		protected boolean quarterly = false;

	}

	private static class Version implements Comparable<Version> {

		public static final Version BLANK = new Version();

		public Version(String versionString) {
			StringBuilder numberStringBuilder = new StringBuilder();
			StringBuilder stringStringBuilder = new StringBuilder();

			for (char c : versionString.toCharArray()) {
				if (Character.isDigit(c)) {
					numberStringBuilder.append(c);
				}
				else {
					stringStringBuilder.append(c);
				}
			}

			if (numberStringBuilder.length() > 0) {
				_number = Integer.parseInt(numberStringBuilder.toString());
			}

			_string = stringStringBuilder.toString();
		}

		@Override
		public int compareTo(final Version version) {
			return Comparator.comparingInt(
				Version::getNumber
			).thenComparing(
				Version::getString
			).compare(
				this, version
			);
		}

		public int getNumber() {
			return _number;
		}

		public String getString() {
			return _string;
		}

		private Version() {
		}

		private int _number = 0;
		private String _string;

	}

}