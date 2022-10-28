package com.matthey.brimjava.util;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;

public class JIHandle {
	public static Object asObject(JIVariant input) {
		Object output = null;
		try {
			switch (input.getObject().getClass().getSimpleName()) {
				case "JIUnsignedShort":
					output = input.getObjectAsUnsigned().getValue().shortValue();
					break;
				case "JIUnsignedInteger":
					output = input.getObjectAsUnsigned().getValue().intValue();
					break;
				case "Integer":
					output = input.getObjectAsInt();
					break;
				case "Short":
					output = input.getObjectAsShort();
					break;
				case "Boolean":
					output = input.getObjectAsBoolean();
					break;
				case "Float":
					output = input.getObjectAsFloat();
					break;
				default:
					output = input.getObject().getClass().getSimpleName();
					break;
			}
		} catch (JIException e) {
			e.printStackTrace();
		}
		return output;
	}
	public static JIVariant asVariant(Object input) {
		return JIVariant.makeVariant(input);
	}
	public static String name(JIVariant input) {
		String output = null;
		try {
			output = input.getObject().getClass().getSimpleName();
		} catch (JIException e) {
			output = "Unknown Type";
		}
		return output;
	}
}
