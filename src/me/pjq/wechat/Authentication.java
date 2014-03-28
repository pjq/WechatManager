package me.pjq.wechat;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cjc.weixinmp.bean.SignatureInfo;

import me.pjq.base.BaseHttpServlet;
import me.pjq.base.CommonParamString;

/**
 * <pre>
 * signature	 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
 * timestamp	 时间戳
 * nonce	 随机数
 * echostr	 随机字符串
 * </pre>
 * 
 * @author pengjianqing@gmail.comm
 */
public class Authentication extends BaseHttpServlet {
	public static final String token = "pjq_me";
	/**
     * 
     */
	private static final long serialVersionUID = 5407965005777694140L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);

		String echostr = req.getParameter("echostr");
		String signature = req.getParameter("signature");
		String timestamp = req.getParameter("timestamp");
		String nonce = req.getParameter("nonce");
		SignatureInfo signatureInfo = new SignatureInfo();
		signatureInfo.echostr = echostr;
		signatureInfo.signature = signature;
		signatureInfo.timestamp = timestamp;
		signatureInfo.nonce = nonce;

		boolean match = isSignatureMatch(signatureInfo);
		match = true;
		if (match) {
			// check the signature,check the request is trusted
			out.println(echostr);
		} else {
			
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);

		String echostr = req.getParameter("echostr");
		String signature = req.getParameter("signature");
		String timestamp = req.getParameter("timestamp");
		String nonce = req.getParameter("nonce");
		SignatureInfo signatureInfo = new SignatureInfo();
		signatureInfo.echostr = echostr;
		signatureInfo.signature = signature;
		signatureInfo.timestamp = timestamp;
		signatureInfo.nonce = nonce;

		boolean match = isSignatureMatch(signatureInfo);
		if (match) {
			// check the signature,check the request is trusted
			out.println("Got your message, please waiting...I am try my best to develop it.");
		} else {
			out.println("Check the signature failed. Got your message, please waiting...I am try my best to develop it.");
		}
	}

	public static boolean isSignatureMatch(SignatureInfo signatureInfo) {
		signatureInfo.token = token;
		String[] arrTmp = { signatureInfo.token, signatureInfo.timestamp, signatureInfo.nonce };
		Arrays.sort(arrTmp);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arrTmp.length; i++) {
			sb.append(arrTmp[i]);
		}

		String pwd = encrypt(sb.toString());

		boolean match = signatureInfo.signature.equalsIgnoreCase(pwd);
		if (match) {

		} else {
			//out.println("The signature check failed.signature = " + signatureInfo.signature+ ",local = " + pwd);
		}

		return match;
	}

	public static String encrypt(String strSrc) {
		MessageDigest md = null;
		String strDes = null;
		byte[] bt = strSrc.getBytes();
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(bt);
			strDes = bytes2Hex(md.digest()); // to HexString
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Invalid algorithm.");
			return null;
		}
		return strDes;
	}

	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

	public boolean isChinese(String str) {
		boolean result = false;
		for (int i = 0; i < str.length(); i++) {
			int chr1 = (char) str.charAt(i);
			if (chr1 >= 19968 && chr1 <= 171941) {// 汉字范围 \u4e00-\u9fa5 (中文)
				result = true;
			}
		}
		return result;
	}

	/**
	 * Send the register html.
	 */
	private void sendRegisterHtml() {
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Register(It will NOT REMEMBER your Twitter UserName and Password)</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h3>Register(It will NOT REMEMBER your Twitter UserName and Password.)</h3>");

		out.println("<P>");
		out.print("<form action=\"");
		out.print("Register\" ");
		out.println("method=POST>");
		out.println("User Name:");
		out.println("<input type=text size=20 name="
				+ CommonParamString.PARAM_USERNAME + ">");
		// out.println("<br>");
		out.println("Password:");
		out.println("<input type=text size=20 name="
				+ CommonParamString.PARAM_PASSWORD + ">");
		// out.println("<br>");
		out.println("Email:");
		out.println("<input type=text size=20 name="
				+ CommonParamString.PARAM_EMAIL + ">");
		out.println("<br>");
		out.println("Your Twitter User Name:");
		out.println("<input type=text size=20 name="
				+ CommonParamString.PARAM_TWITTER_USER_NAME + ">");
		// out.println("<br>");
		out.println("Your Twitter Password:");
		out.println("<input type=text size=20 name="
				+ CommonParamString.PARAM_TWITTER_USER_PASSWORD + ">");
		out.println("<br>");
		out.println("<input type=submit>");
		out.println("</form>");
		out.println("</body>");
		out.println("</html>");
	}
}
