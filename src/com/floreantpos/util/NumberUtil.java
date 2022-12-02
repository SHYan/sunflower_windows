/**
 * ************************************************************************
 * * The contents of this file are subject to the MRPL 1.2
 * * (the  "License"),  being   the  Mozilla   Public  License
 * * Version 1.1  with a permitted attribution clause; you may not  use this
 * * file except in compliance with the License. You  may  obtain  a copy of
 * * the License at http://www.floreantpos.org/license.html
 * * Software distributed under the License  is  distributed  on  an "AS IS"
 * * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * * License for the specific  language  governing  rights  and  limitations
 * * under the License.
 * * The Original Code is FLOREANT POS.
 * * The Initial Developer of the Original Code is OROCUBE LLC
 * * All portions are Copyright (C) 2015 OROCUBE LLC
 * * All Rights Reserved.
 * ************************************************************************
 */
package com.floreantpos.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.commons.lang.StringUtils;

import com.floreantpos.config.TerminalConfig;

public class NumberUtil {
	private final static NumberFormat numberFormat = NumberFormat.getNumberInstance();
	private final static NumberFormat numberFormat2 = NumberFormat.getNumberInstance();
	private final static DecimalFormat decimalFormat = new DecimalFormat("###,###"); //$NON-NLS-1$

	
	static {
		numberFormat.setMinimumFractionDigits(0);
		numberFormat.setMaximumFractionDigits(TerminalConfig.getQtyDecimal());

		numberFormat2.setMinimumFractionDigits(0);
		numberFormat2.setMaximumFractionDigits(TerminalConfig.getPriceDecimal());
	}
	
	

	public static double roundToDigitPrice(double value) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(TerminalConfig.getPriceDecimal(), RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	public static double roundToDigitQty(double value) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(TerminalConfig.getQtyDecimal(), RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	public static double roundToTwoDigit(double value) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static double roundToThreeDigit(double value) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(3, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static String format3DigitNumber(Double number) {
		if (number == null) {
			return numberFormat2.format(0);
		}
		String value = numberFormat2.format(number);

		/*if (value.startsWith("-")) { //$NON-NLS-1$
			return numberFormat2.format(0);
		}*/
		return value;
	}

	public static String formatNumber(Double number, boolean isAllowedNegative) {
		if (number == null) {
			return numberFormat.format(0);
		}

		String value = numberFormat.format(number);

		if (!isAllowedNegative) {
			if (value.startsWith("-")) { //$NON-NLS-1$
				return numberFormat.format(0);
			}
		}
		return value;
	}
	
	public static String formatNumber(Double number) {
		if (number == null) {
			return numberFormat.format(0);
		}

		String value = numberFormat.format(number);

		if (value.startsWith("-")) { //$NON-NLS-1$
			return numberFormat.format(0);
		}

		return value;
	}
	
	public static String formatQty(Double qty) {
		if (qty == null) {
			return numberFormat.format(0);
		}

		String value = numberFormat.format(qty);

		if (value.startsWith("-")) { //$NON-NLS-1$
			return numberFormat.format(0);
		}

		return value;
	}
	
	public static String formatPrice(Double price) {
		if (price == null) {
			return numberFormat2.format(0);
		}

		String value = numberFormat2.format(price);

		if (value.startsWith("-")) { //$NON-NLS-1$
			return numberFormat2.format(0);
		}

		return value;
	}

	
	
	public static Number parse(String number) throws ParseException {
		if (StringUtils.isEmpty(number)) {
			return 0;
		}

		return numberFormat.parse(number);
	}
	
	public static Number parseQty(String number) throws ParseException {
		if (StringUtils.isEmpty(number)) {
			return 0;
		}

		return numberFormat.parse(number);
	}
	
	public static Number parsePrice(String number) throws ParseException {
		if (StringUtils.isEmpty(number)) {
			return 0;
		}

		return numberFormat2.parse(number);
	}

	public static String trimDecilamIfNotNeeded(Double number) {
		if (number == null) {
			return decimalFormat.format(0);
		}

		String value = decimalFormat.format(number);

		if (value.startsWith("-")) { //$NON-NLS-1$
			return decimalFormat.format(0);
		}

		return value;
	}

	public static String formatNumber(double value, int decimalPlaces) {
		if (decimalPlaces > 4) {
			decimalPlaces = 4;
		}
		NumberFormat format = DecimalFormat.getInstance();
		format.setMinimumFractionDigits(decimalPlaces);
		format.setMaximumFractionDigits(decimalPlaces);
		format.setRoundingMode(RoundingMode.HALF_UP);
		return format.format(value);
	}
}
