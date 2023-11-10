/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import java.util.Comparator;

/**
 * @author Drew Brokke
 */
public class ProductKeyInfo implements Comparable<ProductKeyInfo> {

	@Override
	public int compareTo(final ProductKeyInfo keyInfo) {
		return Comparator.comparing(
			ProductKeyInfo::getProductRank
		).thenComparing(
			ProductKeyInfo::isQuarterly
		).thenComparing(
			ProductKeyInfo::getMajorProductKeyVersion
		).thenComparing(
			ProductKeyInfo::getMinorProductKeyVersion
		).thenComparing(
			ProductKeyInfo::getMicroProductKeyVersion
		).reversed(
		).compare(
			this, keyInfo
		);
	}

	public ProductKeyVersion getMajorProductKeyVersion() {
		return majorProductKeyVersion;
	}

	public ProductKeyVersion getMicroProductKeyVersion() {
		return microProductKeyVersion;
	}

	public ProductKeyVersion getMinorProductKeyVersion() {
		return minorProductKeyVersion;
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

	public void setMajorProductKeyVersion(ProductKeyVersion majorProductKeyVersion) {
		this.majorProductKeyVersion = majorProductKeyVersion;
	}

	public void setMicroProductKeyVersion(ProductKeyVersion microProductKeyVersion) {
		this.microProductKeyVersion = microProductKeyVersion;
	}

	public void setMinorProductKeyVersion(ProductKeyVersion minorProductKeyVersion) {
		this.minorProductKeyVersion = minorProductKeyVersion;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public void setProductRank(int productRank) {
		this.productRank = productRank;
	}

	public void setQuarterly(boolean quarterly) {
		this.quarterly = quarterly;
	}

	protected ProductKeyVersion majorProductKeyVersion = ProductKeyVersion.BLANK;
	protected ProductKeyVersion microProductKeyVersion = ProductKeyVersion.BLANK;
	protected ProductKeyVersion minorProductKeyVersion = ProductKeyVersion.BLANK;
	protected String product;
	protected int productRank;
	protected boolean quarterly = false;

}