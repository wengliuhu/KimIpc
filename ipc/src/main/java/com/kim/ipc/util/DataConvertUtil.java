package com.kim.ipc.util;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2020/12/25
 * Describe：进制转换等工具类
 */
public class DataConvertUtil
{
    /**
     * description 将byte数组转化成String,为了支持中文，转化时用GBK编码方式
     * param
     * return
     *
     * @Time 2021/1/8 0008 10:52
     * @version 1.0.0
     */
    public String byteArray2String(byte[] valArr, int maxLen)
    {
        String result = null;
        int index = 0;
        while (index < valArr.length && index < maxLen)
        {
            if (valArr[index] == 0)
            {
                break;
            }
            index++;
        }
        byte[] temp = new byte[index];
        System.arraycopy(valArr, 0, temp, 0, index);
        try
        {
            result = new String(temp, "GBK");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * description 十六进制转为十进制
     * param
     * return
     *
     * @Time 2021/1/18 0018 16:20
     * @version 1.0.0
     */
    public static String getHexToTen(String hex)
    {
        return String.valueOf(Integer.parseInt(hex, 16));
    }


    /**
     * description 将十六进制字符串转为字符串类型ASCII码
     * param
     * return
     *
     * @Time 2021/1/18 0018 16:21
     * @version 1.0.0
     */
    public static String getHexToAscllString(String hex)
    {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2)
        {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }


        return getFileAddSpace(sb.toString());
    }

    /**
     * description 每两位之间插入空格
     * param
     * return
     *
     * @Time 2021/1/18 0018 16:22
     * @version 1.0.0
     */
    public static String getFileAddSpace(String replace)
    {
        String regex = "(.{2})";
        replace = replace.replaceAll(regex, "$1 ");
        return replace;
    }


    /**
     * description 字符串转换为Ascii
     * param
     * return
     *
     * @Time 2021/1/18 0018 16:22
     * @version 1.0.0
     */
    public static String stringToAscii(String value)
    {
        StringBuffer strBuffer = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            if (i != chars.length - 1)
            {
                strBuffer.append((int) chars[i]).append(",");
            } else
            {
                strBuffer.append((int) chars[i]);
            }
        }
        return strBuffer.toString();
    }


    /**
     * description 十六进制转字符串
     * param  hexString   十六进制字符串
     * param  encodeType  编码类型4：Unicode，2：普通编码
     * return
     *
     * @Time 2021/1/18 0018 16:24
     * @version 1.0.0
     */
    public static String hexStringToString(String hexString, int encodeType)
    {
        String result = "";
        int max = hexString.length() / encodeType;
        for (int i = 0; i < max; i++)
        {
            char c = (char) hexStringToAlgorism(hexString.substring(i * encodeType, (i + 1) * encodeType));
            result += c;
        }
        return result;
    }


    /**
     * description 十六进制字符串装十进制
     * param    hex  十六进制字符串
     * return
     *
     * @Time 2021/1/18 0018 16:25
     * @version 1.0.0
     */
    public static int hexStringToAlgorism(String hex)
    {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--)
        {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9')
            {
                algorism = c - '0';
            } else
            {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }


    /**
     * description 16进制转换成为string类型字符串
     * param
     * return
     *
     * @Time 2021/1/18 0018 16:26
     * @version 1.0.0
     */
    public static String hexStringToString(String s)
    {
        if (s == null || "".equals(s))
        {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++)
        {
            try
            {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            s = new String(baKeyword, "UTF-8");
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
        return s;
    }


    /**
     * description 十六进制数转byte数组
     * param
     * return
     *
     * @Time 2021/1/18 0018 16:28
     * @version 1.0.0
     */
    public static byte[] hex2byte(String hex)
    {
        String digital = "0123456789ABCDEF";
        String hex1 = hex.replace(" ", "");
        char[] hex2char = hex1.toCharArray();
        byte[] bytes = new byte[hex1.length() / 2];
        byte temp;
        for (int p = 0; p < bytes.length; p++)
        {
            temp = (byte) (digital.indexOf(hex2char[2 * p]) * 16);
            temp += digital.indexOf(hex2char[2 * p + 1]);
            bytes[p] = (byte) (temp & 0xff);
        }
        return bytes;
    }


    /**
     * description bytes数组转16进制
     * param
     * return
     *
     * @Time 2021/1/18 0018 16:28
     * @version 1.0.0
     */
    public static String bytesToHexString(byte[] src)
    {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0)
        {
            return null;
        }
        for (byte b : src)
        {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2)
            {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 包含0x
     * @param dataByte
     * @return
     */
    public static String byteToHexString(byte dataByte){
        StringBuffer sb = new StringBuffer();
        sb.append("0x");
        if ((dataByte & 240) == 0) {
            sb.append("0");
        }

        sb.append(Integer.toHexString(dataByte & 255));
        return sb.toString();
    }


    /**
     * description 字符串转byte数组
     * param
     * return
     *
     * @Time 2021/1/18 0018 16:30
     * @version 1.0.0
     */
    public static byte[] strToByteArray(String str)
    {
        if (str == null)
        {
            return null;
        }
        return str.getBytes();
    }

    /**
     * 包含0x
     * @param bytes
     * @return
     */
    public static String bytesToHexStr(byte[] bytes){
        if (null != bytes && bytes.length >= 1) {
            StringBuffer sb = new StringBuffer();
            byte[] var2 = bytes;
            int var3 = bytes.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                byte t = var2[var4];
                sb.append("0X");
                if ((t & 240) == 0) {
                    sb.append("0");
                }

                sb.append(Integer.toHexString(t & 255));
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * bytes转为int 如c4 00 大端模式转化为:00C4,12 * 16 + 4 = 196
     *
     * @param bytes
     * @param isBig 是否大端模式
     * @return
     */
    public static int bytesToInt(byte[] bytes, boolean isBig)
    {
        String numStr;
        if (isBig)
        {
            numStr = bytesToHexStr(bytes);
        } else
        {
            StringBuilder stringBuilder = new StringBuilder(); int length = bytes.length;
            for (int i = length - 1; i >= 0; i--)
            {
                stringBuilder.append(byteToHexString(bytes[i]));
            }
            numStr = stringBuilder.toString();
        }
        numStr = numStr.toUpperCase();
        // split 之后会包含第一个"", 要把这个空字符串移除；
        String[] hexStrs1 = numStr.split("0X");
        String[] hexStrs = new String[hexStrs1.length - 1];
        System.arraycopy(hexStrs1, 1, hexStrs, 0, hexStrs.length);
        //        char[] chars = numStr.toCharArray();
        int sum = 0;
        if (hexStrs == null || hexStrs.length <= 0) return sum;
        try
        {
            for (int i = 0; i < hexStrs.length; i ++)
            {
                String itemChar = hexStrs[i];
                if (itemChar.length() != 2) continue;
                char[] chars = itemChar.toCharArray();
                char char1 = chars[0];
                int itemNUm1 = getNumByStartPosition(char1, 2 * i + 1);
                char char2 = chars[1];
                int itemNUm2 = getNumByStartPosition(char2, 2 * i);

                sum = sum + itemNUm1 + itemNUm2;
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        return sum;
    }

    private static int getNumByStartPosition(char itemChar, int position){
        int itemNUm = 0;
        switch (itemChar){
            case 'A': itemNUm = 10; break;
            case 'B': itemNUm = 11; break;
            case 'C': itemNUm = 12; break;
            case 'D': itemNUm = 13; break;
            case 'E': itemNUm = 14; break;
            case 'F': itemNUm = 15; break;
            default:itemNUm = Integer.valueOf(String.valueOf(itemChar));
        }

        return itemNUm * (int) Math.pow(16, position);
    }

    /**
     * byte转int类型
     * 如果byte是负数，则转出的int型是正数
     *
     * @param b
     * @return
     */
    public static int byteToInt(byte b)
    {
        return b & 0xff;
    }

    //b为传入的字节，i为第几位（范围0-7），如要获取bit0，则i=0
    public static int getBit(byte b,int i) {
        int bit = (int)((b>>i) & 0x1);
        return bit;
    }

    /**
     * 验证校验码
     * @param headAndBodyBytes 需要异或的数据
     * @param validCode 校验位
     * @return 校验结果
     */
    public static boolean checXorCode(byte[] headAndBodyBytes, byte validCode)
    {
//        int length = headAndBodyBytes.length;
//        /**从消息头开始到校验码前一位进行异或运算**/
//        byte xor = headAndBodyBytes[0];
//        for (int i = 1; i < length; i++)
//        {
//            xor = (byte) (xor ^ headAndBodyBytes[i]);
//        }

//        if (validCode == xor)
//        {
//            return true;
//        }
//        return false;
        return getXorCode(headAndBodyBytes) == validCode;
    }

    public static byte getXorCode(byte[] headAndBodyBytes){
        int length = headAndBodyBytes.length;
        /**从消息头开始到校验码前一位进行异或运算**/
        byte xor = headAndBodyBytes[0];
        for (int i = 1; i < length; i++)
        {
            xor = (byte) (xor ^ headAndBodyBytes[i]);
        }

        return xor;
    }

    /**
     * 取反码
     * @param datas
     */
    public static void negationCode(byte[] datas){
        byte temp;
        for(int i=0;i<datas.length;i++){
            temp = datas[i];
            datas[i] = (byte) (~temp);
        }
    }


    /**
     * @功能: BCD码转为10进制串(阿拉伯数据)
     * @参数: BCD码
     * @结果: 10进制串
     */
    public static int bcd2Int(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }

        String tempStr = temp.toString();
        while (!TextUtils.isEmpty(tempStr) && tempStr.substring(0, 1).equalsIgnoreCase("0")){
            tempStr = tempStr.substring(1);
        }
        if (TextUtils.isEmpty(tempStr))
        {
            return 0;
        }

        int result = 0;
        try
        {
            result = Integer.parseInt(tempStr);
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return result;
    }



}
