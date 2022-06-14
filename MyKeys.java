package com.example.newoma;

import android.util.Log;

// this is for scp02
public class MyKeys {
    private static final String TAG = ".MyKeys";
    private MyKeys() {
    }

    private static final String encKey = "404142434445464748494A4B4C4D4E4F";
    private static final String macKey = "404142434445464748494A4B4C4D4E4F";
    private static final String decKey = "404142434445464748494A4B4C4D4E4F";
    // Host challenge
    private static final String random = "0102030405060708";
    private static final String keyVersion = "20";
    private static final String securityLevel = "01";

    public static byte[] selISD() {
        String str = "00A4040008A00000015100000000";
        return MyUtils.hexStrToByteArray(str);
    }

    public static boolean processSelISD(byte[] resp) {
        if (resp == null || resp.length < 2) {
            Log.d(TAG, "processSelISD: response len wrong");
            return false;
        }
        if (resp[resp.length - 2] == (byte) 0x90 && resp[resp.length - 1] == 0x00) {
            return true;
        }
        Log.d(TAG, "processSelISD: status code wrong");
        return false;
    }

    public static byte[] initUpdate() {
        // fix Host challenge = 1122334455667788
        String str = "8050" + keyVersion + "0008" + random + "00";
        return MyUtils.hexStrToByteArray(str);
    }

    private static final byte[] keyDiversificationData = new byte[10];
    private static final byte[] keyInformation = new byte[2];
    private static final byte[] sequenceCounter = new byte[2];
    private static final byte[] cardChallenge = new byte[6];
    private static final byte[] cardCryptogram = new byte[8];

    public static boolean processInitUpdate(byte[] resp) {
        if (resp == null || resp.length < 30) {
            Log.d(TAG, "processInitUpdate: response len wrong");
            return false;
        }
        if (resp[resp.length - 2] != (byte) 0x90 || resp[resp.length - 1] != 0x00) {
            Log.d(TAG, "processInitUpdate: status code wrong");
            return false;
        }

        System.arraycopy(resp, 0, keyDiversificationData, 0, 10);
        System.arraycopy(resp, 10, keyInformation, 0, 2);
        System.arraycopy(resp, 12, sequenceCounter, 0, 2);
        System.arraycopy(resp, 14, cardChallenge, 0, 6);
        System.arraycopy(resp, 20, cardCryptogram, 0, 8);
        Log.d(TAG, "keyDiversificationData: " + MyUtils.byteArrayToHexStr2(keyDiversificationData));
        Log.d(TAG, "keyInformation: " + MyUtils.byteArrayToHexStr2(keyInformation));
        Log.d(TAG, "sequenceCounter: " + MyUtils.byteArrayToHexStr2(sequenceCounter));
        Log.d(TAG, "cardChallenge: " + MyUtils.byteArrayToHexStr2(cardChallenge));
        Log.d(TAG, "cardCryptogram: " + MyUtils.byteArrayToHexStr2(cardCryptogram));

        Log.d(TAG, "processInitUpdate: success");
        return true;
    }

    public static byte[] extAuthCmd() {
        String str = "8482" + securityLevel + "0010";
        String hostCryptogram = getHostCryptogram();
        Log.d(TAG, "hostCryptogram: " + hostCryptogram);
        if (hostCryptogram == null) {
            return null;
        }
        String cMac = getCmac(str + hostCryptogram + "800000");
        if (cMac == null) {
            return null;
        }
        Log.d(TAG, "getCmac: cMac = " + cMac);
        str += hostCryptogram + cMac;
        Log.d(TAG, "extAuthCmd: " + str);
        return MyUtils.hexStrToByteArray(str);
    }

    private static String getCmac(String text) {
        // calc mac session key
        String str = "0101" + MyUtils.byteArrayToHexStr2(sequenceCounter) + "000000000000000000000000";
        byte[] input2 = MyUtils.hexStrToByteArray(str);
        Log.d(TAG, "getCmac: input2 = " + MyUtils.byteArrayToHexStr2(input2));
        byte[] iv = new byte[8];
        byte[] s_mac = MyAlgo.desEdeCbcEncrypt(input2, iv, MyUtils.hexStrToByteArray(macKey));
        assert s_mac != null;
        Log.d(TAG, "getCmac: s_mac = " + MyUtils.byteArrayToHexStr2(s_mac));

        byte[] firstHalfKey = new byte[8];
        System.arraycopy(s_mac,0, firstHalfKey, 0, 8);
        byte[] leftHalfData = MyUtils.hexStrToByteArray(text.substring(0, 16));
        byte[] rightHalfData = MyUtils.hexStrToByteArray(text.substring(16, 32));

        Log.d(TAG, "getCmac: leftHalfData = " + MyUtils.byteArrayToHexStr2(leftHalfData));
        Log.d(TAG, "getCmac: firstHalfKey = " + MyUtils.byteArrayToHexStr2(firstHalfKey));
        byte[] icv1 = MyAlgo.desCbcEncrypt(leftHalfData, firstHalfKey);
        assert icv1 != null;
        Log.d(TAG, "getCmac: icv1 = " + MyUtils.byteArrayToHexStr2(icv1));

        byte[] cMac = MyAlgo.desEdeCbcEncrypt(rightHalfData, icv1, s_mac);
        if (cMac == null) {
            return null;
        }

        return MyUtils.byteArrayToHexStr2(cMac);
    }

    private static String getHostCryptogram() {
        // calc enc session key
        String str = "0182" + MyUtils.byteArrayToHexStr2(sequenceCounter) + "000000000000000000000000";
        byte[] input1 = MyUtils.hexStrToByteArray(str);
        Log.d(TAG, "getHostCryptogram: input = " + MyUtils.byteArrayToHexStr2(input1));
        byte[] iv = new byte[8];
        byte[] s_enc = MyAlgo.desEdeCbcEncrypt(input1, iv, MyUtils.hexStrToByteArray(encKey));
        assert s_enc != null;
        Log.d(TAG, "getHostCryptogram: s_enc = " + MyUtils.byteArrayToHexStr2(s_enc));

        String input = MyUtils.byteArrayToHexStr2(sequenceCounter) +
                MyUtils.byteArrayToHexStr2(cardChallenge) +
                random +
                "8000000000000000";
        byte[] hostCryptogram = MyAlgo.desEdeCbcEncrypt(MyUtils.hexStrToByteArray(input), iv, s_enc);
        if (hostCryptogram != null && hostCryptogram.length == 24) {
            Log.d(TAG, "getHostCryptogram: " + MyUtils.byteArrayToHexStr2(hostCryptogram));
            return MyUtils.byteArrayToHexStr2(hostCryptogram).substring(32);
        }
        Log.d(TAG, "getHostCryptogram: return null");
        return null;
    }

    public static boolean processExtAuthCmd(byte[] resp) {
        if (resp == null || resp.length < 2) {
            Log.d(TAG, "processExtAuthCmd: response len wrong");
            return false;
        }
        if (resp[resp.length - 2] == (byte) 0x90 && resp[resp.length - 1] == 0x00) {
            return true;
        }
        Log.d(TAG, "processExtAuthCmd: status word wrong");
        return false;
    }
}
