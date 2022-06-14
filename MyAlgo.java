package com.example.newoma;

import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;

public class MyAlgo {
    private MyAlgo() {
    }

    public static byte[] desEdeCbcEncrypt(byte[] text, byte[] iv, byte[] myKey) {
        try {
            Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding", "AndroidOpenSSL");
            SecretKey key = new SecretKeySpec(myKey, "DES");
            IvParameterSpec spec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            return cipher.doFinal(text);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException |
                BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException |
                InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] desCbcEncrypt(byte[] text, byte[] myKey) {
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
            //should be "bc"
            Log.d(".MyAlgo", "Provider: " + cipher.getProvider().getName());
            SecretKey key = new SecretKeySpec(myKey, "DES");
            // fix iv = all zero
            byte[] iv = new byte[8];
            IvParameterSpec spec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            return cipher.doFinal(text);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException |
                InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] aes128CbcEncrypt(byte[] text, byte[] iv, byte[] myKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "AndroidOpenSSL");
            SecretKey key = new SecretKeySpec(myKey, "AES");
            IvParameterSpec spec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            return cipher.doFinal(text);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException |
                BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException |
                InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    // below is from bouncy castle
    public static byte[] calc_aes_cbc_mac(byte[] msg, byte[] iv, byte[]  myKey) {
        BlockCipher cipher = new AESEngine();
        KeyParameter key = new KeyParameter(myKey);
        byte[] b = new byte[16];
        CMac mac = new CMac(cipher);
        mac.init(key);
        if (iv != null) {
            mac.update(iv, 0, iv.length);
        }
        mac.update(msg, 0, msg.length);
        mac.doFinal(b, 0);
        return b;
    }
}
