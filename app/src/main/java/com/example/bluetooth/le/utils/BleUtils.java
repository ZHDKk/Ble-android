package com.example.bluetooth.le.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.SparseArray;

import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.model.DataInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * 蓝牙工具
 */
public class BleUtils {
    private static String hexString = "0123456789abcdef";

    //字符串转十六进制
    public static String bin2hex(String bin) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer sb = new StringBuffer(digital.length *2);
        byte[] bs = bin.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
        }


        String data = sb.toString().trim();
        if (data.length()!=0 && data.length()>=8) {
            String eight = data.substring(0, 8);
            String surplus = "";
            if (eight.equals(SampleGattAttributes.BLE_RETURNDATA)) {
                surplus = data.substring(8);
            }
            return "55AA" + surplus;
        }else {
            return data;
        }
    }




    public static String bin2hex2(String bin) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer sb = new StringBuffer("");
        byte[] bs = bin.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
        }

            return sb.toString().trim();
    }


    //十六进制转数组
    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        b = null;
        return b2;
    }

    //二进制转十六进制
    public static String binaryString2hexString(String bString)
    {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4)
        {
            iTmp = 0;
            for (int j = 0; j < 4; j++)
            {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    //十六进制转二进制
    public static String hexString2binaryString(String hexString)
    {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++)
        {
            tmp = "0000"
                    + Integer.toBinaryString(Integer.parseInt(hexString
                    .substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    //十进制转二进制
    public static  String tenString2binaryString(int n){
        String result = "";
        boolean minus = false;

        //如果该数字为负数，那么进行该负数+1之后的绝对值的二进制码的对应位取反，然后将它保存在result结果中
        if(n < 0){
            minus = true;
            n = Math.abs(n + 1);
        }

        while(true){
            int remainder = (!minus && n % 2 == 0) || (minus && n % 2 == 1) ? 0 : 1;

            //将余数保存在结果中
            result = remainder + result;
            n /= 2;

            if(n == 0){
                break;
            }
        }

        //判断是否为负数，如果是负数，那么前面所有位补1
        if(minus){
            n = result.length();
            for(int i = 1; i <= 32 - n; i++){
                result = 1 + result;
            }
        }
        return result;
    }
    //累加校验和
    public static String makeChecksum(String data) {
        if (data == null || data.equals("")) {
            return "";
        }
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            System.out.println(s);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        /**
         * 用256求余最大是255，即16进制的FF
         */
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        len = hex.length();
        // 如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    //十六进制转换字符串
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    //bytes转换成十六进制字符串
    public static String byte2HexStr(byte[] b)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=0;n<b.length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    //bytes字符串转换为Byte值
    public static byte[] hexStr2Bytes(String src)
    {
        int m=0,n=0;
        int l=src.length()/2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++)
        {
            m=i*2+1;
            n=m+1;
            ret[i] = Byte.decode("0x" + src.substring(i*2, m) + src.substring(m,n));
        }
        return ret;
    }

    //String的字符串转换成unicode的String
    public static String strToUnicode(String strText)
            throws Exception
    {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++)
        {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128)
                str.append("\\u" + strHex);
            else // 低位在前面补00
                str.append("\\u00" + strHex);
        }
        return str.toString();
    }

    //16进制转10进制
    public static int HexToInt(String strHex){
        int nResult = 0;
        if ( !IsHex(strHex) )
            return nResult;
        String str = strHex.toUpperCase();
        if ( str.length() > 2 ){
            if ( str.charAt(0) == '0' && str.charAt(1) == 'X' ){
                str = str.substring(2);
            }
        }
        int nLen = str.length();
        for ( int i=0; i<nLen; ++i ){
            char ch = str.charAt(nLen-i-1);
            try {
                nResult += (GetHex(ch)*GetPower(16, i));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return nResult;
    }
    //计算16进制对应的数值
    public static int GetHex(char ch) throws Exception{
        if ( ch>='0' && ch<='9' )
            return (int)(ch-'0');
        if ( ch>='a' && ch<='f' )
            return (int)(ch-'a'+10);
        if ( ch>='A' && ch<='F' )
            return (int)(ch-'A'+10);
        throw new Exception("error param");
    }

    //计算幂
    public static int GetPower(int nValue, int nCount) throws Exception{
        if ( nCount <0 )
            throw new Exception("nCount can't small than 1!");
        if ( nCount == 0 )
            return 1;
        int nSum = 1;
        for ( int i=0; i<nCount; ++i ){
            nSum = nSum*nValue;
        }
        return nSum;
    }
    //判断是否是16进制数
    public static boolean IsHex(String strHex){
        int i = 0;
        if ( strHex.length() > 2 ){
            if ( strHex.charAt(0) == '0' && (strHex.charAt(1) == 'X' || strHex.charAt(1) == 'x') ){
                i = 2;
            }
        }
        for ( ; i<strHex.length(); ++i ){
            char ch = strHex.charAt(i);
            if ( (ch>='0' && ch<='9') || (ch>='A' && ch<='F') || (ch>='a' && ch<='f') )
                continue;
            return false;
        }
        return true;
    }

    public static boolean isCMDReadResponse(byte[] data){
        if(data[0] == (byte) 0xB0){
            return true;
        }
        return false;
    }

    public static boolean isCMDReadError(byte[] data, byte payloadLength){
        if(data[3] == (byte) 0xFF || data[3] != payloadLength){
            return true;
        }
        return false;
    }

    public static boolean isCMDWriteResponse(byte[] data){
        if(data[0] == (byte) 0xA0){
            return true;
        }
        return false;
    }

    public static boolean isCMDWriteError(byte[] data){
        if(data[3] == (byte) 0xFF){
            return true;
        }
        return false;
    }
    
    public static boolean isSTMReadResponse(byte[] data){
        if(data[0] == (byte) 0xFC && data[1] == (byte)0xFD){
            return true;
        }
        return false;
    }
    
    public static boolean isSTMReadError(byte[] data, byte payloadLength){
        if(data[4] == (byte)0xFF || data[4] != payloadLength){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isSTMWriteResponse(byte[] data){
        if(data[0] == (byte) 0xFA && data[1] == (byte)0xFB){
            return true;
        }
        return false;
    }

    public static boolean isSTMWriteError(byte[] data){
        if(data[4] == (byte) 0xFF || data[5] != (byte)0){
            return true;
        }
        return false;
    }
    


    public static boolean isSTMWriteHeadDataResponse(byte[] data){
        if(data[0] == (byte) 0xFE && data[1] == (byte)0xFF){
            return true;
        }
        return false;
    }


    public static boolean isSTMWriteDataResponse(byte[] data){
        if(data[0] == (byte) 0xEE && data[1] == (byte)0xEF){
            return true;
        }
        return false;
    }
    
    //判断当前的数据是否属于commandId
    public static boolean isCurrentCommandData(byte[] data, short commandId){
        byte[] b = DataUtils.shortToByteArray(commandId);
        if(data[1] == b[1] && data[2] == b[0]){
            return true;
        }
        return false;
    }


    //STM 判断当前的数据是否属于commandId
    public static boolean isSTMCurrentCommandData(byte[] data, short commandId){
        byte[] b = DataUtils.shortToByteArray(commandId);
        if(data[2] == b[1] && data[3] == b[0]){
            return true;
        }
        return false;
    }
    
    
    

    public static boolean isCMDReadResponse(byte[] data, short commandId){
        byte[] b = DataUtils.shortToByteArray(commandId);
        if(data[0] == (byte) 0xB0 && data[1] == b[1] && data[2] == b[0]){
            return true;
        }
        return false;
    }

    public static boolean isCMDWriteResponse(byte[] data, short commandId){
        byte[] b = DataUtils.shortToByteArray(commandId);
        if(data[0] == (byte) 0xA0 && data[1] == b[1] && data[2] == b[0]){
            return true;
        }
        return false;
    }

    public static boolean isSTM32ReadResponse(byte[] data, short commandId){
        byte[] b = DataUtils.shortToByteArray(commandId);
        if(data[0] == (byte) 0xFC && data[1] == (byte)0xFD && data[2] == b[1] && data[3] == b[0]){
            return true;
        }
        return false;
    }

    public static boolean isSTM32WriteResponse(byte[] data, short commandId){
        byte[] b = DataUtils.shortToByteArray(commandId);
        if(data[0] == (byte) 0xFA && data[1] == (byte)0xFB && data[2] == b[1] && data[3] == b[0]){
            return true;
        }
        return false;
    }

    public static boolean isSTM32WriteHeadResponse(byte[] data, short index){
        byte[] b = DataUtils.shortToByteArray(index);
        if(data[0] == (byte) 0xFE && data[1] == (byte)0xFF && data[2] == b[1] && data[3] == b[0]){
            return true;
        }
        return false;
    }

    public static boolean isSTM32WriteDataResponse(byte[] data, short index){
        byte[] b = DataUtils.shortToByteArray(index);
        if(data[0] == (byte) 0xEE && data[1] == (byte)0xEF && data[2] == b[1] && data[3] == b[0]){
            return true;
        }
        return false;
    }
    
    public static boolean isDfuCMDError(byte[] data){
        if(data[4] == (byte)0xFF){
            return true;
        }else{
            return false;
        }
    }

    //根据SN号生成硬件连接需要的验证码
    public static byte[] generateSNPassWord(byte[] sn) {
        byte[] out = new byte[8];
        byte[] str = "BodyPlus".getBytes();
        
        for (int i = 0; i < 8; i++){
            out[i] = (byte) (sn[i + 2] ^ str[i] ^ sn[i%2]);
        }
        return out;
    }
    
    /**
     * 拆分文件
     *
     * @param fileName 待拆分的完整文件名
     * @param byteSize 按多少字节大小拆分
     * @throws IOException
     */
    public static List<byte[]> splitBySize(String fileName, int byteSize) throws IOException {
        List<byte[]> splitByteArray = new ArrayList<byte[]>();
        File file = new File(fileName);
        int count = (int) Math.ceil(file.length() / (double) byteSize);

        for (int i = 0; i < count; i++) {
            RandomAccessFile rFile;
            try {
                rFile = new RandomAccessFile(file, "r");
                rFile.seek(i * byteSize);// 移动指针到每“段”开头
                int length = byteSize;
                if(rFile.length() - (i* byteSize) < byteSize){
                    length = (int) (rFile.length() - (i* byteSize));
                }
                byte[] b = new byte[length];
                rFile.read(b);
                splitByteArray.add(b);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return splitByteArray;
    }

    //获取ble广播内容 SN码
    public static SparseArray<byte[]> parseFromBytes(byte[] scanRecord) {
        if (scanRecord == null) {
            return null;
        }
        int currentPos = 0;
        SparseArray<byte[]> manufacturerData = new SparseArray<byte[]>();

        try {
            while (currentPos < scanRecord.length) {
                // length is unsigned int.
                int length = scanRecord[currentPos ++] & 0xFF;
                if (length == 0) {
                    break;
                }
                int dataLength = length - 1;
                int fieldType = scanRecord[currentPos++] & 0xFF;
                switch (fieldType) {
                    case 0xFF:
                        int manufacturerId = ((scanRecord[currentPos + 1] & 0xFF) << 8) + (scanRecord[currentPos] & 0xFF);
                        byte[] manufacturerDataBytes = extractBytes(scanRecord, currentPos + 2, dataLength - 2);
                        manufacturerData.put(manufacturerId, manufacturerDataBytes);
                        break;
                    default:
                        break;
                }
                currentPos += dataLength;
            }

            return  manufacturerData;
           
        } catch (Exception e) {
            return manufacturerData;
        }
       
    }

    // Helper method to extract bytes from byte array.
    private static byte[] extractBytes(byte[] scanRecord, int start, int length) {
        byte[] bytes = new byte[length];
        System.arraycopy(scanRecord, start, bytes, 0, length);
        return bytes;
    }

    //过滤出自己的服务UUID
    public static boolean isFilterMyUUID(byte[] scanRecord){
        UUID cmdService = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
        UUID dataService = UUID.fromString("00000005-0000-1000-8000-00805f9b34fb");
        UUID stmService = UUID.fromString("00000009-0000-1000-8000-00805f9b34fb");
        UUID[] bleUUIDs = new UUID[]{cmdService, dataService, stmService};
        int index = 0;
        List<UUID> scanUUID = BleUtils.parseUuids(scanRecord);
        for(int i = 0; i < bleUUIDs.length; i ++){
            UUID uuid = bleUUIDs[i];
            for(int j = 0; j < scanUUID.size(); j ++){
                if(uuid.equals(scanUUID.get(j))){
                    index ++;
                    break;
                }
            }
        }
        if(index == 3){
            return true;
        }
        return false;
    }

    //获取ble广播内容 服务UUID
    public static List<UUID> parseUuids(byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<UUID>();

        ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0) break;

            byte type = buffer.get();
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (length >= 2) {
                        uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                        length -= 2;
                    }
                    break;

                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        uuids.add(new UUID(msb, lsb));
                        length -= 16;
                    }
                    break;

                default:
                    buffer.position(buffer.position() + length - 1);
                    break;
            }
        }

        return uuids;
    }
    
    public static String generateVersion(String versionInfo){
        short w = Short.parseShort(versionInfo);

        byte h = (byte) ((w >> 8) & 0x00FF);
        byte l = (byte) (w & 0x00FF);

        byte a = (byte) ((h >> 4) & 0x0F);
        byte aa = (byte) (h & 0x0F);
        String aaa = String.valueOf(a).equals("0") ? String.valueOf(aa) : String.valueOf(a) + String.valueOf(aa);

        byte b = (byte) ((l >> 4) & 0x0F);
        byte bb = (byte) (l & 0x0F);
        String bbb = String.valueOf(b) + String.valueOf(bb);

        return aaa + "." + bbb;
    }


    public static String byteToChar(byte[] ucPtr){
        final StringBuffer sb = new StringBuffer();
        for(int i = 0; i < ucPtr.length; ++i){
            char c = (char) ucPtr[i];
            sb.append(String.valueOf(c));
        }
        return sb.toString();
    }

    public static String logBytes(byte[] ucPtr){
        String s;
        final StringBuffer sb = new StringBuffer();
        for(int i = 0; i < ucPtr.length; ++i){
            if(0 == i){
                s = "";
            }else{
                s = ", ";
            }
            s += String.valueOf(ucPtr[i]);
            sb.append(s);
        }
        
        return sb.toString();
    }

    public static String dumpBytes(byte[] ucPtr){
        String s;
        final StringBuffer sb = new StringBuffer();
        for(int i = 0; i < ucPtr.length; ++i){
            if(0 == i){
                s = "";
            }else{
                s = ", ";
            }
            s += String.format("0x%02X", ucPtr[i]);//输出十六进制保留两位有效数字
            sb.append(s);
        }
        return sb.toString();
    }

    public static float getFloat(byte[] bytes)
    {
        return Float.intBitsToFloat(getInt(bytes));
    }

    public static int getInt(byte[] bytes)
    {
        return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));
    }

    public static byte[] float2byte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }

        return dest;

    }




    /**
     * 判断手机蓝牙手否打开
     * @param context
     * @return
     */
    public static boolean isOpenBle(Context context){
        BluetoothManager bluetoothManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()){
            return true;
        }
        return false;
    }


    //接收数据分包处理
    public static Queue<byte[]> splitPacketFor20Byte(byte[] data) {
        Queue<byte[]> dataInfoQueue = new LinkedList<>();
        if(data != null) {
            int index = 0;

            do {
                byte[] surplusData = new byte[data.length - index];
                System.arraycopy(data, index, surplusData, 0, data.length - index);
                byte[] currentData;
                if(surplusData.length <= 20) {
                    currentData = new byte[surplusData.length];
                    System.arraycopy(surplusData, 0, currentData, 0, surplusData.length);
                    index += surplusData.length;
                } else {
                    currentData = new byte[20];
                    System.arraycopy(data, index, currentData, 0, 20);
                    index += 20;
                }

                dataInfoQueue.offer(currentData);
            } while(index < data.length);
        }

        return dataInfoQueue;
    }

}
