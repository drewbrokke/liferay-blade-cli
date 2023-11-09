/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.liferay.blade.cli.util.ArrayUtil;
import com.liferay.blade.cli.util.Pair;
import com.liferay.blade.cli.util.ProductInfo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

//import java.util.*;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 */
public class WorkspaceProductComparator implements Comparator<Pair<String, ProductInfo>> {

	@Override
	public int compare(Pair<String, ProductInfo> aPair, Pair<String, ProductInfo> bPair) {
		return _productKeyComparator.thenComparing(
			_productInfoComparator
		).thenComparing(
			(pair1, pair2) -> _keyVersionComparator.compare(new Key(pair1.first()), new Key(pair2.first()))
		).reversed(
		).compare(
			aPair, bPair
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

	private final Comparator<Key> _keyProductComparator = Comparator.comparingInt(
		key -> _products.indexOf(key.getProduct()));
	private final Comparator<Key> _keyVersionComparator = Comparator.comparingInt(
		Key::getMajorVersion
	).thenComparingInt(
		Key::getMinorVersion
	).thenComparingInt(
		Key::getMicroVersion
	);

	private final Comparator<Key> _keyVersionConditionalComparator = (key1, key2) -> {
		if (!Objects.equals(key1.getProduct(), key2.getProduct())) {
			return 0;
		}

		if (Objects.equals(key1.getProduct(), "dxp")) {
			return 0;
		}

		return _keyVersionComparator.compare(key1, key2);
	};

	private final Comparator<Pair<String, ProductInfo>> _productInfoComparator = (pair1, pair2) -> {
		ProductInfo aProductInfo = pair1.second();
		ProductInfo bProductInfo = pair2.second();

		try {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH);

			LocalDate aDate = LocalDate.parse(aProductInfo.getReleaseDate(), dateTimeFormatter);
			LocalDate bDate = LocalDate.parse(bProductInfo.getReleaseDate(), dateTimeFormatter);

			return aDate.compareTo(bDate);
		}
		catch (Exception exception) {
		}

		return 0;
	};

	private final Comparator<Pair<String, ProductInfo>> _productKeyComparator =
		(pair1, pair2) -> _keyProductComparator.thenComparing(
			_keyVersionConditionalComparator
		).compare(
			new Key(pair1.first()), new Key(pair2.first())
		);

	private class Key {

		public Key(String key) {
			String[] parts = key.split("-");

			if (ArrayUtil.isEmpty(parts)) {
				throw new IllegalArgumentException();
			}

			product = parts[0];

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

		protected final int majorVersion;
		protected int microVersion = 0;
		protected int minorVersion = 0;
		protected final String product;
		protected boolean quarterly = false;

	}

}