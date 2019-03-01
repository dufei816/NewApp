package com.qimeng.huishou.newapp.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Base64;

/**
 * 加密/解密工具
 *
 * @author ershuai
 * @date 2017年4月18日 上午11:27:36
 */
@RequiresApi(api = Build.VERSION_CODES.FROYO)
public class EncryptUtil {

    private final byte[] DESIV = new byte[]{0x12, 0x34, 0x56, 120, (byte) 0x90, (byte) 0xab, (byte) 0xcd, (byte) 0xef};// 向量

    private AlgorithmParameterSpec iv = null;// 加密算法的参数接口
    private Key key = null;

    private String charset = "utf-8";

    /**
     * 初始化
     *
     * @param deSkey 密钥
     * @throws Exception
     */
    public EncryptUtil(String deSkey, String charset) throws Exception {
        if (!TextUtils.isEmpty(charset)) {
            this.charset = charset;
        }
        DESKeySpec keySpec = new DESKeySpec(deSkey.getBytes(this.charset));// 设置密钥参数
        iv = new IvParameterSpec(DESIV);// 设置向量
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
        key = keyFactory.generateSecret(keySpec);// 得到密钥对象
    }

    /**
     * 加密
     *
     * @param data
     * @return
     * @throws Exception
     * @author ershuai
     * @date 2017年4月19日 上午9:40:53
     */

    public String encode(String data) {
        Cipher enCipher = null;// 得到加密对象Cipher
        try {
            enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// 设置工作模式为加密模式，给出密钥和向量
            byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));
            return Base64.encodeToString(pasByte, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
//		Base64 base64Encoder = new Base64();
        return "";
    }

    /**
     * 解密
     *
     * @param data
     * @return
     * @throws Exception
     * @author ershuai
     * @date 2017年4月19日 上午9:41:01
     */
    public String decode(String data) throws Exception {
        Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        deCipher.init(Cipher.DECRYPT_MODE, key, iv);
//        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] pasByte = deCipher.doFinal(Base64.decode(data, Base64.DEFAULT));
        return new String(pasByte, "UTF-8");
    }

    public static void main(String[] args) {
        try {
            String test = "ershuai";
            String key = "9ba45bfd500642328ec03ad8ef1b6e75";// 自定义密钥
            EncryptUtil des = new EncryptUtil(key, "utf-8");
            System.out.println("加密前的字符：" + test);
            System.out.println("加密后的字符：" + des.encode(test));
            System.out.println("解密后的字符：" + des.decode(des.encode(test)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
