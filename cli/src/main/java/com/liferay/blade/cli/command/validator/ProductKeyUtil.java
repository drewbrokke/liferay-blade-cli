package com.liferay.blade.cli.command.validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Drew Brokke
 */
public class ProductKeyUtil {

	public static final Pattern productKeyCommercePattern = Pattern.compile(
		"^(?<product>commerce)-(?<major>[1-9]\\.\\d\\.\\d)(?:-(?<minor>[1-9]\\.\\d))?$");
	public static final Pattern productKeyDXPNonQuarterlyPattern = Pattern.compile(
		"^(?<product>dxp)-(?<major>[1-9]\\.\\d)-(?<minor>(?:de|ep|fp|ga|sp|u)\\d+)$");
	public static final Pattern productKeyDXPQuarterlyPattern = Pattern.compile(
		"^(?<product>dxp)-(?<major>2\\d{3})\\.(?<minor>q[1234])\\.(?<micro>\\d+)$");
	public static final Pattern productKeyPortalPattern = Pattern.compile(
		"^(?<product>portal)-(?<major>[1-9]\\.\\d)-(?<minor>ga\\d+)$");
	public static final List<String> products = Collections.unmodifiableList(
		Arrays.asList("commerce", "portal", "dxp"));

	public static int compare(String productKey1, String productKey2) {
		ProductKeyInfo keyInfo1 = createProductKeyInfo(productKey1);

		return keyInfo1.compareTo(createProductKeyInfo(productKey2));
	}

	public static ProductKeyInfo createProductKeyInfo(String productKey) {
		Matcher matcher = _firstMatches(
			productKey, productKeyDXPQuarterlyPattern, productKeyDXPNonQuarterlyPattern, productKeyPortalPattern,
			productKeyCommercePattern);

		if (matcher == null) {
			throw new IllegalArgumentException(String.format("%s is not a valid Liferay product key\n", productKey));
		}

		ProductKeyInfo productKeyInfo = new ProductKeyInfo();

		_withGroup(
			matcher, "product",
			group -> {
				productKeyInfo.setProduct(group);

				productKeyInfo.setProductRank(ProductKeyUtil.products.indexOf(group));
			});
		_withGroup(
			matcher, "major",
			group -> productKeyInfo.setMajorProductKeyVersion(new ProductKeyVersion(group)));
		_withGroup(
			matcher, "minor",
			group -> {
				ProductKeyVersion minorProductKeyVersion = new ProductKeyVersion(group);

				productKeyInfo.setMinorProductKeyVersion(minorProductKeyVersion);

				if (Objects.equals(productKeyInfo.getProduct(), "dxp") &&
					Objects.equals(minorProductKeyVersion.getType(), "q")) {

					productKeyInfo.setQuarterly(true);
				}
			});
		_withGroup(
			matcher, "micro",
			group -> productKeyInfo.setMicroProductKeyVersion(new ProductKeyVersion(group)));

		return productKeyInfo;
	}

	private static void _withGroup(Matcher matcher, String groupName, Consumer<String> consumer) {
		try {
			 consumer.accept(matcher.group(groupName));
		} catch (Exception ignored) {
		}
	}

	public static boolean verifyCommerceWorkspaceProduct(String product) {
		return _matchesAny(product, productKeyCommercePattern);
	}

	public static boolean verifyPortalDxpWorkspaceProduct(String product) {
		return _matchesAny(
			product, productKeyDXPQuarterlyPattern, productKeyDXPNonQuarterlyPattern, productKeyPortalPattern);
	}

	private static Matcher _firstMatches(String s, Pattern... patterns) {
		for (Pattern pattern : patterns) {
			Matcher matcher = pattern.matcher(s);

			if (matcher.matches()) {
				return matcher;
			}
		}

		return null;
	}

	private static boolean _matchesAny(String s, Pattern... patterns) {
		Matcher matcher = _firstMatches(s, patterns);

		if (matcher != null) {
			return true;
		}

		return false;
	}

}