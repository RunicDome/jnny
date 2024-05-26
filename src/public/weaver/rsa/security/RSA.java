package weaver.rsa.security;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;

@SuppressWarnings({ "unused", "restriction", "rawtypes", "unchecked" })
public class RSA {
	private static final String PUB_KEY_NAME = "rsa_2048_pub.key";
	private static final String PRI_KEY_NAME = "rsa_2048_priv.key";
	private static String RSA_PUB = null;

	private static String RSA_PUB_FILE = null;
	private static PrivateKey PRI_KEY = null;
	private static Cipher DECRYPT_CIPHER = null;

	private static final String RSA_FLAG = "``RSA``";

	private String salt = "";

	private int saltLen = 0;

	private static boolean initRsASuccess = false;

	private String message = "0";

	public static String getProjectPath() {
		URL url = RSA.class.getProtectionDomain().getCodeSource().getLocation();
		String realPath = null;
		try {
			realPath = URLDecoder.decode(url.getPath(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (realPath.endsWith(".jar"))
			realPath = realPath.substring(0, realPath.lastIndexOf("/") + 1);
		System.out.println("before skip real path:::::" + realPath);
		if (realPath.startsWith("file:")) {
			realPath = realPath.substring(5);
		}
		System.out.println("skip file:::::" + realPath);
		int pos = realPath.indexOf("RSA");
		System.out.println("RSA POS:::" + pos);
		if (pos != -1) {
			realPath = realPath.substring(0, pos);
		}
		if (!realPath.endsWith("/")) {
			realPath = realPath + "/";
		}
		return realPath;
	}

	static {
		try {
			String rootPath = getProjectPath();
			System.out.println("============rootPath:::" + rootPath);
			try {
				RSA_PUB_FILE = rootPath + "keys" + File.separator
						+ "rsa_2048_pub.key";
				File rpf = new File(RSA_PUB_FILE);
				RSA_PUB = FileUtils.readFileToString(rpf, Const.UTF8_CHARSET);
				if (RSA_PUB == null) {
					genarateKeyFiles(rootPath, Const.UTF8_CHARSET);
					RSA_PUB = FileUtils.readFileToString(rpf,
							Const.UTF8_CHARSET);
				}
			} catch (Exception e) {
				e.printStackTrace();

				genarateKeyFiles(rootPath, Const.UTF8_CHARSET);
				RSA_PUB = FileUtils.readFileToString(new File(rootPath + "keys"
						+ File.separator + "rsa_2048_pub.key"),
						Const.UTF8_CHARSET);
			}
			System.out.println("RSA_PUB:::" + RSA_PUB);

			PRI_KEY = PrivateKeyReader.getFromBase64(rootPath + "keys"
					+ File.separator + "rsa_2048_priv.key");

			DECRYPT_CIPHER = Cipher.getInstance("RSA");
			DECRYPT_CIPHER.init(2, PRI_KEY);
			initRsASuccess = true;
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public String getMessage() {
		return this.message;
	}

	public static String getRsaFlag() {
		return "``RSA``";
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getSalt() {
		return this.salt;
	}

	public String encrypt(String value) {
		return encrypt(null, value);
	}

	public String encrypt(HttpServletRequest req, String value) {
		return encrypt(req, value, generateSalt(), "UTF-8");
	}

	public String encrypt(HttpServletRequest req, String value, String salt) {
		return encrypt(req, value, salt, "UTF-8");
	}

	public String encrypt(HttpServletRequest req, String value, String salt,
			String charset) {
		return encrypt(req, value, salt, charset, RSA_PUB_FILE, true);
	}

	public String encrypt(HttpServletRequest req, String value, String salt,
			String charset, String rsaPublicFile, boolean isFile) {
		if (!initRsASuccess) {
			this.message = "-3";
			return value;
		}
		if ((value == null) || ("".equals(value))) {
			this.message = "-6";
		}
		if (rsaPublicFile == null) {
			rsaPublicFile = RSA_PUB_FILE;
		}
		String result = "";
		try {
			if ((salt != null) && (!salt.equals(""))) {
				value = value + salt;
				this.salt = salt;
			}
			byte[] msgBytes = value.getBytes(charset);
			if (msgBytes.length > 245) {
				this.message = "-4";
			} else {
				PublicKey publicKey = null;
				if (isFile) {
					publicKey = PublicKeyReader.getFromBase64(rsaPublicFile);
				} else {
					publicKey = PublicKeyReader
							.getFromBase64String(rsaPublicFile);
				}
				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(1, publicKey);

				byte[] results = cipher.doFinal(msgBytes);
				if (req != null) {
					req.getSession().setAttribute("rsa_code", salt);
				}
				return Base64.encodeBase64String(results);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.message = "-2";
		}
		return result;
	}

	public String encryptCommon(String value) {
		return encryptCommon(null, value);
	}

	public String encryptCommon(HttpServletRequest req, String value) {
		return encryptCommon(req, value, generateSalt(), "UTF-8");
	}

	public String encryptCommon(HttpServletRequest req, String value,
			String salt) {
		return encryptCommon(req, value, salt, "UTF-8");
	}

	public String encryptCommon(HttpServletRequest req, String value,
			String salt, String charset) {
		return encryptCommon(req, value, salt, charset, RSA_PUB_FILE, true);
	}

	public String encryptCommon(HttpServletRequest req, String value,
			String salt, String charset, String rsaPublicFile, boolean isFile) {
		if (!initRsASuccess) {
			this.message = "-3";
			return value;
		}
		if ((value == null) || ("".equals(value))) {
			this.message = "-6";
		}
		if (rsaPublicFile == null) {
			rsaPublicFile = RSA_PUB_FILE;
		}
		String result = "";

		try {
			byte[] msgBytes = value.getBytes(charset);
			if (msgBytes.length > 245) {
				this.message = "-4";
			} else {
				PublicKey publicKey = null;
				if (isFile) {
					publicKey = PublicKeyReader.getFromBase64(rsaPublicFile);
				} else {
					publicKey = PublicKeyReader
							.getFromBase64String(rsaPublicFile);
				}
				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(1, publicKey);

				byte[] results = cipher.doFinal(msgBytes);

				return Base64.encodeBase64String(results);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.message = "-2";
		}
		return result;
	}

	public String generateSalt() {
		return generateSalt(null);
	}

	public String generateSalt(HttpServletRequest req) {
		Random ranGen = new java.security.SecureRandom();
		byte[] aesKey = new byte[4];
		ranGen.nextBytes(aesKey);
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < aesKey.length; i++) {
			String hex = Integer.toHexString(0xFF & aesKey[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		if (req != null) {
			req.getSession().setAttribute("rsa_code", hexString.toString());
		}
		return hexString.toString();
	}

	public List<String> decryptList(List<String> value) {
		return decryptList(null, value);
	}

	public List<String> decryptList(HttpServletRequest req, List<String> value) {
		return decryptList(req, value, false);
	}

	public List<String> decryptList(HttpServletRequest req, List<String> value,
			boolean forceDecrypt) {
		return decryptList(req, value, forceDecrypt, "UTF-8");
	}

	public List<String> decryptList(HttpServletRequest req, List<String> value,
			boolean forceDecrypt, String charset) {
		if ((value == null) || (value.size() == 0)) {
			this.message = "-6";
			return value;
		}
		List<String> decryptValue = new ArrayList();
		try {
			for (int i = 0; i < value.size(); i++) {
				String v = (String) value.get(i);
				if ((v == null) || ("".equals(v))) {
					decryptValue.add(v);
				} else {
					decryptValue.add(decrypt(req, v, forceDecrypt, charset,
							true));
				}
			}
		} finally {
			removeRsaCode(req);
		}
		return decryptValue;
	}

	public String decrypt(String value) {
		return decrypt(null, value, false);
	}

	public String decrypt(HttpServletRequest req, String value) {
		return decrypt(req, value, false);
	}

	public String decrypt(HttpServletRequest req, String value,
			boolean forceDecrypt) {
		return decrypt(req, value, forceDecrypt, "UTF-8");
	}

	public String decrypt(HttpServletRequest req, String value,
			boolean forceDecrypt, String charset) {
		return decrypt(req, value, forceDecrypt, charset, false);
	}

	public String decrypt(HttpServletRequest req, String value,
			boolean forceDecrypt, String charset, boolean keepSession) {
		if (!initRsASuccess) {
			this.message = "-3";
			return value;
		}
		String result = "";
		try {
			String str1;
			if ((value == null) || ("".equals(value))) {
				this.message = "-6";
				return value;
			}
			if ((forceDecrypt) || (value.endsWith("``RSA``"))) {
				if (value.endsWith("``RSA``")) {
					value = value.substring(0, value.indexOf("``RSA``"));
				}
				result = new String(DECRYPT_CIPHER.doFinal(Base64
						.decodeBase64(value)), charset);
				if (req != null) {
					String rsa_code = (String) req.getSession().getAttribute(
							"rsa_code");

					if (this.saltLen == 0) {
						if ((this.salt != null) && (!this.salt.equals(""))) {
							this.saltLen = this.salt.length();
						} else {
							this.saltLen = 8;
						}
					}
					String salt = result.substring(result.length()
							- this.saltLen);

					result = result
							.substring(0, result.length() - this.saltLen);

					if ((rsa_code == null) || ("".equals(rsa_code))
							|| (!rsa_code.equals(salt))) {
						this.message = "-1";
						result = "";
					}
				}
			} else {
				return value;
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.message = "-2";
		} finally {
			if ((req != null) && (!keepSession)) {
				removeRsaCode(req);
			}
		}
		if ((req != null) && (!keepSession)) {
			removeRsaCode(req);
		}

		return result;
	}

	public List<String> decryptCommonList(List<String> value) {
		return decryptCommonList(null, value);
	}

	public List<String> decryptCommonList(HttpServletRequest req,
			List<String> value) {
		return decryptCommonList(req, value, true);
	}

	public List<String> decryptCommonList(HttpServletRequest req,
			List<String> value, boolean forceDecrypt) {
		return decryptCommonList(req, value, forceDecrypt, "UTF-8");
	}

	public List<String> decryptCommonList(HttpServletRequest req,
			List<String> value, boolean forceDecrypt, String charset) {
		if ((value == null) || (value.size() == 0)) {
			this.message = "-6";
			return value;
		}
		List<String> decryptValue = new ArrayList();
		for (int i = 0; i < value.size(); i++) {
			String v = (String) value.get(i);
			if ((v == null) || ("".equals(v))) {
				decryptValue.add(v);
			} else {
				decryptValue.add(decryptCommon(req, v, forceDecrypt, charset,
						true));
			}
		}
		return decryptValue;
	}

	public String decryptCommon(String value) {
		return decryptCommon(null, value, true);
	}

	public String decryptCommon(HttpServletRequest req, String value) {
		return decryptCommon(req, value, true);
	}

	public String decryptCommon(HttpServletRequest req, String value,
			boolean forceDecrypt) {
		return decryptCommon(req, value, forceDecrypt, "UTF-8");
	}

	public String decryptCommon(HttpServletRequest req, String value,
			boolean forceDecrypt, String charset) {
		return decryptCommon(req, value, forceDecrypt, charset, true);
	}

	public String decryptCommon(HttpServletRequest req, String value,
			boolean forceDecrypt, String charset, boolean keepSession) {
		if (!initRsASuccess) {
			this.message = "-3";
			return value;
		}
		String result = "";
		try {
			if ((value == null) || ("".equals(value))) {
				this.message = "-6";
				return value;
			}
			if ((forceDecrypt) || (value.endsWith("``RSA``"))) {
				if (value.endsWith("``RSA``")) {
					value = value.substring(0, value.indexOf("``RSA``"));
				}
				return new String(DECRYPT_CIPHER.doFinal(Base64
						.decodeBase64(value)), charset);
			}

			return value;
		} catch (Exception e) {
			e.printStackTrace();
			this.message = "-2";
		}
		return result;
	}

	public void removeRsaCode(HttpServletRequest req) {
		if (req != null) {
			req.getSession().removeAttribute("rsa_code");
		}
	}

	private static void genarateKeyFiles(String path, Charset charset)
			throws Throwable {
		path = path + "keys" + File.separator;

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);

		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		String publicKey64 = Base64.encodeBase64String(publicKey.getEncoded());
		FileUtils.writeStringToFile(new File(path + "rsa_2048_pub.key"),
				publicKey64, charset);
		String privateKey64 = Base64
				.encodeBase64String(privateKey.getEncoded());
		FileUtils.writeStringToFile(new File(path + "rsa_2048_priv.key"),
				privateKey64, charset);
	}

	public static String getRSA_PUB() {
		return RSA_PUB;
	}

	public int getSaltLen() {
		return this.saltLen;
	}

	public void setSaltLen(int saltLen) {
		this.saltLen = saltLen;
	}

	public RSA() {
	}
}
