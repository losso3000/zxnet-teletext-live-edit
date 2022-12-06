package de.heckmeck.livetext;

import java.util.Base64;
import java.util.BitSet;

public class UrlTranscoder {
  private static final int ROWS = 25;
  private static final int COLS = 40;

  // Page with a byte for each character
  private BitSet bitSet = new BitSet(ROWS*COLS*8);

  // Data contained in URL, packed in 7 bits per character
  private byte[] urlDataBuffer = new byte[ROWS*COLS*7/8];

  public byte[] transcodeFromUrl(String url) {
    byte[] buf7 = extractDataFromUrl(url, urlDataBuffer);
    if (buf7 == null) return null;
    byte[] buf8 = convertTo8Bit(buf7, bitSet);
    addParity(buf8);
    return buf8;
  }

  static byte[] extractDataFromUrl(String url, byte[] buf) {
    int from = url.indexOf("#0:") + 3;
    int to   = url.indexOf(":PS");
    if (from < 0 || to < 0 || to <= from) {
      return null;
    }
    byte[] urlDataBase64 = url.substring(from, to)
      .replace('_', '/')
      .replace('-', '+')
      .getBytes();
    Base64.getDecoder().decode(urlDataBase64, buf);
    return buf;
  }

  static byte[] convertTo8Bit(byte[] buf7, BitSet bitSet) {
    int sourceBytePos = 0;
    int sourceBitPos = 7;
    int targetBitPos = 0;
    bitSet.clear();
    while (sourceBytePos < buf7.length) {
      boolean set = (buf7[sourceBytePos] & (1 << sourceBitPos)) != 0;
      if (set) {
        bitSet.set(targetBitPos);
      }
      targetBitPos++;
      sourceBitPos--;
      if (sourceBitPos < 0) {
        sourceBitPos = 7;
        sourceBytePos++;
      }
    }
    return bitSet.toByteArray();
  }

  static int byteWithParity(int i) {
    int k = i ^ (i >> 1);
    k ^= (k >> 2);
    k ^= (k >> 4);
    return (k & 1) == 0 ? i : i|0x80;
  }

  static void addParity(byte[] buf) {
    for (int i = 0; i < buf.length; i++) {
      buf[i] = (byte) byteWithParity(buf[i]);
    }
  }

}
