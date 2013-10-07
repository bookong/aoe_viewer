package net.bookong.game.aoeviewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jiangxu
 *
 */
public class Settings extends Properties {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(getClass());;
	private static final File SETTINGS_FILE = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "aoeViewer.properties");

	public static final String LAST_SELECT_FILE = "last.select.file";

	public Settings() {
		FileInputStream is = null;
		try {
			is = new FileInputStream(SETTINGS_FILE);
			load(is);
		} catch (FileNotFoundException e) {
			logger.warn("settings file not found, make new one.");
			try {
				SETTINGS_FILE.createNewFile();
			} catch (IOException e1) {
				logger.error("Fail to create settings file.", e);
			}
		} catch (Exception e) {
			logger.error("Fail to load settings file.", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// do not care!
				}
			}
		}
	}

	public Object setProperty(String key, String value) {
		Object obj = super.setProperty(key, value);
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(SETTINGS_FILE);
			store(os, "");
		} catch (Exception e) {
			logger.error("Fail to save settings file.", e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// do not care!
				}
			}
		}
		return obj;
	}
}
