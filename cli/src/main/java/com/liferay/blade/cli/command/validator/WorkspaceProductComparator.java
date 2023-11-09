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
 */
public class WorkspaceProductComparator implements Comparator<Pair<String, ProductInfo>> {

	@Override
	public int compare(Pair<String, ProductInfo> aPair, Pair<String, ProductInfo> bPair) {
		return Comparator.comparing(
			Key::getProductRank
		).thenComparing(
			Key::isQuarterly
		).thenComparingInt(
			Key::getMajorVersion
		).thenComparingInt(
			Key::getMinorVersion
		).thenComparingInt(
			Key::getMicroVersion
		).reversed(
		).compare(
			new Key(aPair.first()), new Key(bPair.first())
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

	private static final List<String> _products = Collections.unmodifiableList(
		Arrays.asList("commerce", "portal", "dxp"));

	private class Key {

		public Key(String key) {
			String[] parts = key.split("-");

			if (ArrayUtil.isEmpty(parts)) {
				throw new IllegalArgumentException();
			}

			product = parts[0];

			productRank = _products.indexOf(product);

			if (Objects.equals(product, "dxp") && (parts.length == 2)) {
				quarterly = true;

				String[] quarterlyParts = parts[1].split("\\.");

				majorVersion = _toInteger(quarterlyParts[0]);
				minorVersion = _toInteger(quarterlyParts[1]);
				microVersion = _toInteger(quarterlyParts[2]);

				return;
			}

			majorVersion = _toInteger(parts[1]);

			if (parts.length > 2) {
				minorVersion = _toInteger(parts[2]);
			}
		}

		public int getMajorVersion() {
			return majorVersion;
		}

		public int getMicroVersion() {
			return microVersion;
		}

		public int getMinorVersion() {
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

		protected final int majorVersion;
		protected int microVersion = 0;
		protected int minorVersion = 0;
		protected final String product;
		protected final int productRank;
		protected boolean quarterly = false;

	}

}