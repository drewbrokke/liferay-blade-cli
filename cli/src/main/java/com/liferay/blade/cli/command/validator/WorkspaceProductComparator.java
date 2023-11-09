/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.liferay.blade.cli.util.ArrayUtil;
import com.liferay.blade.cli.util.Pair;
import com.liferay.blade.cli.util.ProductInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

//import java.util.*;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 * @author Drew Brokke
 */
public class WorkspaceProductComparator implements Comparator<Pair<String, ProductInfo>> {

	@Override
	public int compare(Pair<String, ProductInfo> aPair, Pair<String, ProductInfo> bPair) {
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
			new KeyInfo(aPair.first()), new KeyInfo(bPair.first())
		);
	}

	private int _toInteger(String s) {
		StringBuilder sb = new StringBuilder();

		for (char c : s.toCharArray()) {
			if (Character.isDigit(c)) {
				sb.append(c);
			}
		}

		return Integer.parseInt(sb.toString());
	}

	private VersionDetails _toVersionDetails(String s) {
		StringBuilder numberStringBuilder = new StringBuilder();
		StringBuilder stringStringBuilder = new StringBuilder();

		for (char c : s.toCharArray()) {
			if (Character.isDigit(c)) {
				numberStringBuilder.append(c);
			}
			else {
				stringStringBuilder.append(c);
			}
		}

		return new VersionDetails(
				Integer.parseInt(numberStringBuilder.toString()),
				stringStringBuilder.toString());
	}

	private static final List<String> _products = Collections.unmodifiableList(
		Arrays.asList("commerce", "portal", "dxp"));

	private class KeyInfo {

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

				majorVersion = _toVersionDetails(quarterlyParts[0]);
				minorVersion = _toVersionDetails(quarterlyParts[1]);
				microVersion = _toVersionDetails(quarterlyParts[2]);

				return;
			}

			majorVersion = _toVersionDetails(parts[1]);

			if (parts.length > 2) {
				minorVersion = _toVersionDetails(parts[2]);
			}
		}

		public VersionDetails getMajorVersion() {
			return majorVersion;
		}

		public VersionDetails getMicroVersion() {
			return microVersion;
		}

		public VersionDetails getMinorVersion() {
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

		protected VersionDetails majorVersion;
		protected VersionDetails microVersion = VersionDetails._BLANK;
		protected VersionDetails minorVersion = VersionDetails._BLANK;
		protected final String product;
		protected final int productRank;
		protected boolean quarterly = false;

	}

	private static class VersionDetails implements Comparable<VersionDetails> {
		private final int _number;

		public int getNumber() {
			return _number;
		}

		public String getString() {
			return _string;
		}

		private final String _string;

		private VersionDetails(int number, String string) {
			_number = number;
			_string = string;
		}

		private static final VersionDetails _BLANK = new VersionDetails(0, "");

		@Override
		public int compareTo(VersionDetails versionDetails) {
			if (versionDetails == null) {
				return 1;
			}

			return Comparator.comparingInt(
					VersionDetails::getNumber
			).thenComparing(
					Comparator.comparing(VersionDetails::getString).reversed()
			).compare(
				this, versionDetails
			);
		}
	}

}