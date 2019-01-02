package com.qimeng.huishou.newapp.util;

import java.util.Arrays;

/**
 * Author by Winds on 2016/10/18.
 * Email heardown@163.com.
 */
public class ByteUtil {



    /**
     *
     * <pre>
     * 将4个byte数字组成的数组合并为一个float数.
     * </pre>
     *
     * @param arr
     * @return
     */
    public static float byte4ToFloat(byte[] arr) {
        if (arr == null || arr.length != 4) {
            throw new IllegalArgumentException("byte数组必须不为空,并且是4位!");
        }
        int i = byte4ToInt(arr);
        return Float.intBitsToFloat(i);
    }

    /**
     *
     * <pre>
     * 将一个float数字转换为4个byte数字组成的数组.
     * </pre>
     *
     * @param f
     * @return
     */
    public static byte[] floatToByte4(float f) {
        int i = Float.floatToIntBits(f);
        return intToByte4(i);
    }

    /**
     *
     * <pre>
     * 将八个byte数字组成的数组转换为一个double数字.
     * </pre>
     *
     * @param arr
     * @return
     */
    public static double byte8ToDouble(byte[] arr) {
        if (arr == null || arr.length != 8) {
            throw new IllegalArgumentException("byte数组必须不为空,并且是8位!");
        }
        long l = byte8ToLong(arr);
        return Double.longBitsToDouble(l);
    }

    /**
     *
     * <pre>
     * 将一个double数字转换为8个byte数字组成的数组.
     * </pre>
     *
     * @param i
     * @return
     */
    public static byte[] doubleToByte8(double i) {
        long j = Double.doubleToLongBits(i);
        return longToByte8(j);
    }

    /**
     *
     * <pre>
     * 将一个char字符转换为两个byte数字转换为的数组.
     * </pre>
     *
     * @param c
     * @return
     */
    public static byte[] charToByte2(char c) {
        byte[] arr = new byte[2];
        arr[0] = (byte) (c >> 8);
        arr[1] = (byte) (c & 0xff);
        return arr;
    }

    /**
     *
     * <pre>
     * 将2个byte数字组成的数组转换为一个char字符.
     * </pre>
     *
     * @param arr
     * @return
     */
    public static char byte2ToChar(byte[] arr) {
        if (arr == null || arr.length != 2) {
            throw new IllegalArgumentException("byte数组必须不为空,并且是2位!");
        }
        return (char) (((char) (arr[0] << 8)) | ((char) arr[1]));
    }

    /**
     *
     * <pre>
     * 将一个16位的short转换为长度为2的8位byte数组.
     * </pre>
     *
     * @param s
     * @return
     */
    public static byte[] shortToByte2(Short s) {
        byte[] arr = new byte[2];
        arr[0] = (byte) (s >> 8);
        arr[1] = (byte) (s & 0xff);
        return arr;
    }

    /**
     *
     * <pre>
     * 长度为2的8位byte数组转换为一个16位short数字.
     * </pre>
     *
     * @param arr
     * @return
     */
    public static short byte2ToShort(byte[] arr) {
        if (arr != null && arr.length != 2) {
            throw new IllegalArgumentException("byte数组必须不为空,并且是2位!");
        }
        return (short) (((short) arr[0] << 8) | ((short) arr[1] & 0xff));
    }

    /**
     *
     * <pre>
     * 将short转换为长度为16的byte数组.
     * 实际上每个8位byte只存储了一个0或1的数字
     * 比较浪费.
     * </pre>
     *
     * @param s
     * @return
     */
    public static byte[] shortToByte16(short s) {
        byte[] arr = new byte[16];
        for (int i = 15; i >= 0; i--) {
            arr[i] = (byte) (s & 1);
            s >>= 1;
        }
        return arr;
    }

    public static short byte16ToShort(byte[] arr) {
        if (arr == null || arr.length != 16) {
            throw new IllegalArgumentException("byte数组必须不为空,并且长度为16!");
        }
        short sum = 0;
        for (int i = 0; i < 16; ++i) {
            sum |= (arr[i] << (15 - i));
        }
        return sum;
    }

    /**
     *
     * <pre>
     * 将32位int转换为由四个8位byte数字.
     * </pre>
     *
     * @param sum
     * @return
     */
    public static byte[] intToByte4(int sum) {
        byte[] arr = new byte[4];
        arr[0] = (byte) (sum >> 24);
        arr[1] = (byte) (sum >> 16);
        arr[2] = (byte) (sum >> 8);
        arr[3] = (byte) (sum & 0xff);
        return arr;
    }

    /**
     * <pre>
     * 将长度为4的8位byte数组转换为32位int.
     * </pre>
     *
     * @param arr
     * @return
     */
    public static int byte4ToInt(byte[] arr) {
        if (arr == null || arr.length != 4) {
            throw new IllegalArgumentException("byte数组必须不为空,并且是4位!");
        }
        return (int) (((arr[0] & 0xff) << 24) | ((arr[1] & 0xff) << 16) | ((arr[2] & 0xff) << 8) | ((arr[3] & 0xff)));
    }

    /**
     *
     * <pre>
     * 将长度为8的8位byte数组转换为64位long.
     * </pre>
     *
     * 0xff对应16进制,f代表1111,0xff刚好是8位 byte[]
     * arr,byte[i]&0xff刚好满足一位byte计算,不会导致数据丢失. 如果是int计算. int[] arr,arr[i]&0xffff
     *
     * @param arr
     * @return
     */
    public static long byte8ToLong(byte[] arr) {
        if (arr == null || arr.length != 8) {
            throw new IllegalArgumentException("byte数组必须不为空,并且是8位!");
        }
        return (long) (((long) (arr[0] & 0xff) << 56) | ((long) (arr[1] & 0xff) << 48) | ((long) (arr[2] & 0xff) << 40)
                | ((long) (arr[3] & 0xff) << 32) | ((long) (arr[4] & 0xff) << 24)
                | ((long) (arr[5] & 0xff) << 16) | ((long) (arr[6] & 0xff) << 8) | ((long) (arr[7] & 0xff)));
    }

    /**
     * 将一个long数字转换为8个byte数组组成的数组.
     */
    public static byte[] longToByte8(long sum) {
        byte[] arr = new byte[8];
        arr[0] = (byte) (sum >> 56);
        arr[1] = (byte) (sum >> 48);
        arr[2] = (byte) (sum >> 40);
        arr[3] = (byte) (sum >> 32);
        arr[4] = (byte) (sum >> 24);
        arr[5] = (byte) (sum >> 16);
        arr[6] = (byte) (sum >> 8);
        arr[7] = (byte) (sum & 0xff);
        return arr;
    }

    /**
     *
     * <pre>
     * 将int转换为32位byte.
     * 实际上每个8位byte只存储了一个0或1的数字
     * 比较浪费.
     * </pre>
     *
     * @param num
     * @return
     */
    public static byte[] intToByte32(int num) {
        byte[] arr = new byte[32];
        for (int i = 31; i >= 0; i--) {
            // &1 也可以改为num&0x01,表示取最地位数字.
            arr[i] = (byte) (num & 1);
            // 右移一位.
            num >>= 1;
        }
        return arr;
    }

    /**
     *
     * <pre>
     * 将长度为32的byte数组转换为一个int类型值.
     * 每一个8位byte都只存储了0或1的数字.
     * </pre>
     *
     * @param arr
     * @return
     */
    public static int byte32ToInt(byte[] arr) {
        if (arr == null || arr.length != 32) {
            throw new IllegalArgumentException("byte数组必须不为空,并且长度是32!");
        }
        int sum = 0;
        for (int i = 0; i < 32; ++i) {
            sum |= (arr[i] << (31 - i));
        }
        return sum;
    }

    /**
     *
     * <pre>
     * 将长度为64的byte数组转换为一个long类型值.
     * 每一个8位byte都只存储了0或1的数字.
     * </pre>
     *
     * @param arr
     * @return
     */
    public static long byte64ToLong(byte[] arr) {
        if (arr == null || arr.length != 64) {
            throw new IllegalArgumentException("byte数组必须不为空,并且长度是64!");
        }
        long sum = 0L;
        for (int i = 0; i < 64; ++i) {
            sum |= ((long) arr[i] << (63 - i));
        }
        return sum;
    }

    /**
     *
     * <pre>
     * 将一个long值转换为长度为64的8位byte数组.
     * 每一个8位byte都只存储了0或1的数字.
     * </pre>
     *
     * @param sum
     * @return
     */
    public static byte[] longToByte64(long sum) {
        byte[] arr = new byte[64];
        for (int i = 63; i >= 0; i--) {
            arr[i] = (byte) (sum & 1);
            sum >>= 1;
        }
        return arr;
    }


    /**
     * 字节数组转换成对应的16进制表示的字符串
     *
     * @param src
     * @return
     */
    public static String bytes2HexStr(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return "";
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            builder.append(buffer);
        }
        return builder.toString().toUpperCase();
    }

    /**
     * 十六进制字节数组转字符串
     *
     * @param src    目标数组
     * @param dec    起始位置
     * @param length 长度
     * @return
     */
    public static String bytes2HexStr(byte[] src, int dec, int length) {
        byte[] temp = new byte[length];
        System.arraycopy(src, dec, temp, 0, length);
        return bytes2HexStr(temp);
    }

    /**
     * 16进制字符串转10进制数字
     *
     * @param hex
     * @return
     */
    public static long hexStr2decimal(String hex) {
        return Long.parseLong(hex, 16);
    }

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @return
     */
    public static String decimal2fitHex(long num) {
        String hex = Long.toHexString(num).toUpperCase();
        if (hex.length() % 2 != 0) {
            return "0" + hex;
        }
        return hex.toUpperCase();
    }

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @param strLength 字符串的长度
     * @return
     */
    public static String decimal2fitHex(long num, int strLength) {
        String hexStr = decimal2fitHex(num);
        StringBuilder stringBuilder = new StringBuilder(hexStr);
        while (stringBuilder.length() < strLength) {
            stringBuilder.insert(0, '0');
        }
        return stringBuilder.toString();
    }

    public static String fitDecimalStr(int dicimal, int strLength) {
        StringBuilder builder = new StringBuilder(String.valueOf(dicimal));
        while (builder.length() < strLength) {
            builder.insert(0, "0");
        }
        return builder.toString();
    }

    /**
     * 字符串转十六进制字符串
     *
     * @param str
     * @return
     */
    public static String str2HexString(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        byte[] bs = null;
        try {

            bs = str.getBytes("utf8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }


    /**
     * 整型转换为4位字节数组
     *
     * @param intValue
     * @return
     */
    public static byte[] int2Byte(int intValue) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (intValue >> 8 * (3 - i) & 0xFF);
            //System.out.print(Integer.toBinaryString(b[i])+" ");
            //System.out.print((b[i] & 0xFF) + " ");
        }
        return b;
    }


    /**
     * 2位字节数组转换为整型
     *
     * @param b 78 31
     * @return
     */
    public static int byte2Int(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (1 - i));
        }
        return intValue;
    }

    /**
     * 把十六进制表示的字节数组字符串，转换成十六进制字节数组
     *
     * @param
     * @return byte[]
     */
    public static byte[] hexStr2bytes(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toUpperCase().toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (hexChar2byte(achar[pos]) << 4 | hexChar2byte(achar[pos + 1]));
        }
        return result;
    }


    /**
     * 超找目标数组索引
     *
     * @param bytes 数组
     * @param data  目标数组
     * @return
     */
    public static int findBytes(byte[] bytes, byte[] data) {
        byte[] newData = new byte[data.length];
        int forSize = bytes.length - data.length + 1;
        for (int i = 0; i < forSize; i++) {
            System.arraycopy(bytes, i, newData, 0, newData.length);
            if (Arrays.equals(data, newData)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * 把16进制字符[0123456789abcde]（含大小写）转成字节
     *
     * @param c
     * @return
     */
    private static int hexChar2byte(char c) {
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'a':
            case 'A':
                return 10;
            case 'b':
            case 'B':
                return 11;
            case 'c':
            case 'C':
                return 12;
            case 'd':
            case 'D':
                return 13;
            case 'e':
            case 'E':
                return 14;
            case 'f':
            case 'F':
                return 15;
            default:
                return -1;
        }
    }
}
