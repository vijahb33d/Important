/*
 * Created on 7/07/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.panconversion.activity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.accenture.services.security.SymmetricEncryptDecrypt;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;
import com.telstra.olb.tegcbm.job.migration.panconversion.dao.SFTPConnectionException;

/**
 * @author arun.balasubramanian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PANConversionHelper {	
	private static String ALOGRITHM_USED = "AES";
	// IMPORTANT: THE KEY MUST NOT BE MODIFIED!!
	private static String strKey = "#^2005schedule-0";
	private static SecretKeySpec keySpec;
	private static Key key = null;

	private static boolean initialize = false;

	public static String decrypt(byte[] encryptedBytes) throws GeneralSecurityException {
		keySpec = new SecretKeySpec(strKey.getBytes(), ALOGRITHM_USED);
		Cipher decryptCipher = Cipher.getInstance(ALOGRITHM_USED);
		decryptCipher.init(Cipher.DECRYPT_MODE, keySpec);

		byte[] recoveredBytes = decryptCipher.doFinal(encryptedBytes);
		return new String(recoveredBytes);
	}
	
	public static void transferStreamSFTP(InputStream iStream, String host, String user, final String pwd,int port, String location, String filename) throws SFTPConnectionException{
		JSch jsch = new JSch();
		Session session = null;
		ChannelSftp sftpChannel = null;
		UserInfo ui = new UserInfo() {
			public String getPassword() {
				return pwd;
			}
			public boolean promptYesNo(String str) {
				return true;
			}
			public String getPassphrase() {
				return null;
			}
			public boolean promptPassphrase(String message) {
				return true;
			}
			public boolean promptPassword(String message) {
				return true;
			}
			public void showMessage(String message) {
			}
		};
		try {
			session = jsch.getSession(user, host, port);
			session.setUserInfo(ui);
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(location);
			sftpChannel.put(iStream, filename);
		} catch (JSchException e) {
			throw new SFTPConnectionException("Error occured while trying to SFTP connect to Host:"+host,e);
		} catch (SftpException e) {
			throw new SFTPConnectionException("Error occured while trying to SFTP connect to Host:"+host,e);
		} finally{
			sftpChannel.disconnect();
			session.disconnect();
		}
	}
	
	public static boolean isFileExists(String host, String user, final String pwd, int port, String location, String filename) throws SFTPConnectionException{
		JSch jsch = new JSch();
		Session session = null;
		ChannelSftp sftpChannel = null;
		UserInfo ui = new UserInfo() {
			public String getPassword() {
				return pwd;
			}
			public boolean promptYesNo(String str) {
				return true;
			}
			public String getPassphrase() {
				return null;
			}
			public boolean promptPassphrase(String message) {
				return true;
			}
			public boolean promptPassword(String message) {
				return true;
			}
			public void showMessage(String message) {
			}
		};
		try {
			session = jsch.getSession(user, host, port);
			session.setUserInfo(ui);
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(location);
			Vector files = sftpChannel.ls(filename);
			if(files.size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (JSchException e) {
			throw new SFTPConnectionException("Error occured while trying to SFTP connect to Host:"+host,e);
		} catch (SftpException e) {
			throw new SFTPConnectionException("Error occured while trying to SFTP connect to Host:"+host,e);
		} finally {
			sftpChannel.disconnect();
			session.disconnect();
		}
	}
	
	public static ByteArrayOutputStream getContents(String host, String user, final String pwd, int port, String location, String filename) throws SFTPConnectionException {
		JSch jsch = new JSch();
		Session session = null;
		ChannelSftp sftpChannel = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		UserInfo ui = new UserInfo() {
			public String getPassword() {
				return pwd;
			}
			public boolean promptYesNo(String str) {
				return true;
			}
			public String getPassphrase() {
				return null;
			}
			public boolean promptPassphrase(String message) {
				return true;
			}
			public boolean promptPassword(String message) {
				return true;
			}
			public void showMessage(String message) {
			}
		};
		try {
			session = jsch.getSession(user, host, port);
			session.setUserInfo(ui);
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(location);
			sftpChannel.get(filename, outputStream);
			return outputStream;	
		} catch (JSchException e) {
			throw new SFTPConnectionException("Error occured while trying to SFTP connect to Host:"+host,e);
		} catch (SftpException e) {
			throw new SFTPConnectionException("Error occured while trying to SFTP connect to Host:"+host,e);
		} finally {
			sftpChannel.disconnect();
			session.disconnect();
		}
	}
	
	
}
