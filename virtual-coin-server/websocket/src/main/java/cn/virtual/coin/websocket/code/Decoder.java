package cn.virtual.coin.websocket.code;

import cn.virtual.coin.websocket.exception.WebSocketException;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * @author gdyang
 * @since 2025/2/24 23:01
 */
public interface Decoder {
    /**
     * 解码
     * @param data 数据
     * @param <T> 范型
     * @return 数据
     * @throws WebSocketException 异常
     */
    <T>T decode(byte[] data) throws WebSocketException;

    class Default implements Decoder{

        @Override
        @SuppressWarnings("unchecked")
        public String decode(byte[] data) throws WebSocketException{
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data);ByteArrayOutputStream baos = new ByteArrayOutputStream();){
                decompress(bais, baos);
                baos.flush();
                baos.close();
                bais.close();
                return baos.toString();
            } catch (IOException e) {
                throw new WebSocketException(e);
            }
        }

        void decompress(InputStream is, OutputStream os) throws IOException {
            GZIPInputStream gis = new GZIPInputStream(is);
            int count;
            byte[] data = new byte[1024];
            while ((count = gis.read(data, 0, 1024)) != -1) {
                os.write(data, 0, count);
            }
            gis.close();
        }
    }
}
