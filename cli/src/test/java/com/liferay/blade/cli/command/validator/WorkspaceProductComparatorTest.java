/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.liferay.blade.cli.util.Pair;
import com.liferay.blade.cli.util.ProductInfo;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class WorkspaceProductComparatorTest {

	@Test
	public void testSortByReleaseDate() throws Exception {
		Assert.assertArrayEquals(
			new String[] {
				"dxp-2023.q3.2", "dxp-2023.q3.1", "dxp-2023.q2.1", "dxp-2022.q3.1", "dxp-7.2-sp1", "dxp-7.2-sp3",
				"dxp-7.2-sp2", "portal-7.3-ga1", "portal-7.1-ga2", "portal-7.1-ga1"
			},
			Arrays.stream(
				new String[][] {
					{"dxp-7.2-sp1", "10/4/2019"}, {"dxp-7.2-sp2", "12/22/2018"}, {"dxp-7.2-sp3", "5/31/2019"},
					{"portal-7.1-ga1", "1/31/2011"}, {"portal-7.1-ga2", "1/31/2012"}, {"portal-7.3-ga1", "6/29/2020"},
					{"dxp-2022.q3.1", "6/29/2022"}, {"dxp-2023.q2.1", "6/29/2023"}, {"dxp-2023.q3.1", "7/29/2023"},
					{"dxp-2023.q3.2", "8/29/2023"}
				}
			).map(
				strings -> new Pair<>(strings[0], new ProductInfo(Collections.singletonMap("releaseDate", strings[1])))
			).sorted(
				new WorkspaceProductComparator()
			).map(
				Pair::first
			).toArray(
				String[]::new
			));
	}

}