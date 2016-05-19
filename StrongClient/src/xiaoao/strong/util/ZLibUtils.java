package xiaoao.strong.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * ZLib压缩工具
 */
public class ZLibUtils {

//    private static final ByteArrayOutputStream o = new ByteArrayOutputStream(1024);
    /**
     * 压缩
     *
     * @param data 待压缩数据 byte[] 压缩后的数据i
     */
    public static byte[] compress(byte[] data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        compress(data, bos);
        byte[] output = bos.toByteArray();
        try {
            bos.close();
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        return output;
//        byte[] output = new byte[0];
//
//        Deflater compresser = new Deflater();
//
//        compresser.reset();
//        compresser.setInput(data);
//        compresser.finish();
//        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
//        try {
//            byte[] buf = new byte[1024];
//            while (!compresser.finished()) {
//                int i = compresser.deflate(buf);
//                bos.write(buf, 0, i);
//            }
//            output = bos.toByteArray();
//        } catch (Exception e) {
//            output = data;
//            Global.logErrorMessage(e);
//        } finally {
//            try {
//                bos.close();
//            } catch (IOException e) {
//                Global.logErrorMessage(e);
//            }
//        }
//        compresser.end();
//        return output;
    }

    /**
     * 压缩
     *
     * @param data 待压缩数据
     *
     * @param os 输出流
     */
    public static void compress(byte[] data, OutputStream os) {
        DeflaterOutputStream dos = new DeflaterOutputStream(os, new Deflater(Deflater.BEST_SPEED));

        try {
            dos.write(data, 0, data.length);

            dos.finish();

            dos.flush();
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
            try {
                dos.close();
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }

    }

    /**
     * 解压缩
     *
     * @param data 待压缩的数据 byte[] 解压缩后的数据
     */
    public static byte[] decompress(byte[] data) {
        InputStream stream = new ByteArrayInputStream(data);
        return decompress(stream);
//        byte[] output;
//
//        Inflater decompresser = new Inflater();
//        decompresser.reset();
//        decompresser.setInput(data);
//
//        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
//        try {
//            byte[] buf = new byte[1024];
//            while (!decompresser.finished()) {
//                int i = decompresser.inflate(buf);
//                o.write(buf, 0, i);
//            }
//            output = o.toByteArray();
//        } catch (DataFormatException e) {
//            output = data;
//            Global.logErrorMessage(e);
//        } finally {
//            try {
//                o.close();
//            } catch (IOException e) {
//                Global.logErrorMessage(e);
//            }
//        }
//
//        decompresser.end();
//        return output;
    }

    /**
     * 解压缩
     *
     * @param is 输入流 byte[] 解压缩后的数据
     */
    public static byte[] decompress(InputStream is) {
        InflaterInputStream iis = new InflaterInputStream(is);
        ByteArrayOutputStream o = new ByteArrayOutputStream(1024);
        try {
//            o.reset();
            int length = 1024;
            byte[] buf = new byte[length];

            length = iis.read(buf, 0, length);
            while (length > 0) {
                o.write(buf, 0, length);
                length = iis.read(buf, 0, length);
            }

        } catch (IOException e) {
        	e.printStackTrace();
        }
        return o.toByteArray();
    }

    private ZLibUtils() {
    }
}
