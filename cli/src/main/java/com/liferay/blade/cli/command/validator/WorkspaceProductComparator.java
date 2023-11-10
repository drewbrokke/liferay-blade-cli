/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import java.util.Comparator;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 * @author Drew Brokke
 */
public class WorkspaceProductComparator implements Comparator<String> {

	@Override
	public int compare(String key1, String key2) {
		return ProductKeyUtil.compare(key1, key2);
	}

}