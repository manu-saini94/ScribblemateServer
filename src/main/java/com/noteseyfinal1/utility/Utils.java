package com.noteseyfinal1.utility;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Utils {

	private static final String ERR_STRING_FORMAT = "Error in formating string, possibly due to mismatched number of placeholders and objects";

	public enum Role {
		OWNER, COLLABORATOR;

		public static Role findByName(String name) {
			for (Role role : values()) {
				if (role.name().equalsIgnoreCase(name)) {
					return role;
				}
			}
			return null;
		}
	}

	public enum Status {
		ACTIVE, INACTIVE;

		public static Status findByName(String name) {
			for (Status status : values()) {
				if (status.name().equalsIgnoreCase(name)) {
					return status;
				}
			}
			return null;
		}
	}

	public static String formatSafe(String format, Object... args) {
		try {
			return String.format(format, args);
		} catch (Exception ex) {
			log.error(ERR_STRING_FORMAT, ex);
		}
		return null;
	}

}
