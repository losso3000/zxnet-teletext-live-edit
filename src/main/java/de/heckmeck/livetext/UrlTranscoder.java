package de.heckmeck.livetext;

import java.util.Base64;

public class UrlTranscoder {
  private static final int EDITOR_COLS = 40;
  private static final int EDITOR_ROWS = 25;

  private static final int SEND_COLS = 42;
  private static final int SEND_ROWS = 24;
  private static final int FILLING_HEADER_BYTES = SEND_COLS;

  // Data contained in URL, packed in 7 bits per character
  private byte[] urlDataBuffer = new byte[EDITOR_ROWS * EDITOR_COLS *7/8];

  // Page with a byte for each character
  private byte[] pageBuffer = new byte[EDITOR_ROWS * EDITOR_COLS];

  // Only 24 rows, but with 2 MPAG bytes before each row
  private byte[] sendBuffer = new byte[SEND_ROWS * SEND_COLS + FILLING_HEADER_BYTES];

  private static int[] mpags = {
      0x02, 0x15, // magazine 1 row  0
      0xc7, 0x15, // magazine 1 row  1
      0x02, 0x02, // magazine 1 row  2
      0xc7, 0x02, // magazine 1 row  3
      0x02, 0x49, // magazine 1 row  4
      0xc7, 0x49, // magazine 1 row  5
      0x02, 0x5e, // magazine 1 row  6
      0xc7, 0x5e, // magazine 1 row  7
      0x02, 0x64, // magazine 1 row  8
      0xc7, 0x64, // magazine 1 row  9
      0x02, 0x73, // magazine 1 row 10
      0xc7, 0x73, // magazine 1 row 11
      0x02, 0x38, // magazine 1 row 12
      0xc7, 0x38, // magazine 1 row 13
      0x02, 0x2f, // magazine 1 row 14
      0xc7, 0x2f, // magazine 1 row 15
      0x02, 0xd0, // magazine 1 row 16
      0xc7, 0xd0, // magazine 1 row 17
      0x02, 0xc7, // magazine 1 row 18
      0xc7, 0xc7, // magazine 1 row 19
      0x02, 0x8c, // magazine 1 row 20
      0xc7, 0x8c, // magazine 1 row 21
      0x02, 0x9b, // magazine 1 row 22
      0xc7, 0x9b, // magazine 1 row 23
  };

  private static int[] fillingHeader = {
    0x02, 0x15, 0xea, 0xea, 0x15, 0x15, 0x15, 0x15, 0x02, 0x15
  };

  public byte[] transcodeFromUrl(String url) {
    byte[] buf7 = extractDataFromUrl(url, urlDataBuffer);
    if (buf7 == null) return null;
    byte[] buf8 = convertTo8Bit(buf7, pageBuffer);
    for (int i = 0; i < buf8.length; i++) {
      buf8[i] = 'e';
    }
    byte[] sendBuf = convertToSendBuffer(buf8, sendBuffer);
    return sendBuf;
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

  static byte[] convertTo8Bit(byte[] buf7, byte[] buf8) {
    int targetByte = 0;
    int targetBytePos = 0;
    int sourceBytePos = 0;
    int sourceBitPos = 7;
    while (sourceBytePos < buf7.length) {
      boolean set = (buf7[sourceBytePos] & (1 << sourceBitPos)) != 0;
      if (set) {
        targetByte |= 1 << sourceBitPos;
      }
      if (--sourceBitPos < 0) {
        sourceBitPos = 7;
        sourceBytePos++;
        buf8[targetBytePos++] = (byte) targetByte;
        targetByte = 0;
      }
    }
    return buf8;
  }

  static int byteWithParity(int i) {
    int k = i ^ (i >> 1);
    k ^= (k >> 2);
    k ^= (k >> 4);
    return (k & 1) == 0 ? i : i|0x80;
  }

  static byte[] convertToSendBuffer(byte[] src, byte[] dst) {
    // Copy characters
    for (int y = 0; y < SEND_ROWS; y++) {
      for (int x = 0; x < EDITOR_COLS; x++) {
        dst[y * SEND_COLS + x + 2] = (byte) byteWithParity(src[y * EDITOR_COLS + x]);
      }
    }
    // Page header for page 0x100 sub-page 0000, default flags
    for (int i = 0; i < 8; i++) {
      dst[2 + i] = 0x15;
    }
    // MPAGs for each line
    for (int y = 0; y < SEND_ROWS; y++) {
      dst[y * SEND_COLS + 0] = (byte) mpags[y * 2 + 0];
      dst[y * SEND_COLS + 1] = (byte) mpags[y * 2 + 1];
    }
    // Filling header (page 0x1FF, content as in first row)
    for (int x = 8; x < EDITOR_COLS; x++) {
      dst[SEND_ROWS * SEND_COLS + x + 2] = src[x];
    }
    for (int i = 0; i < fillingHeader.length; i++) {
      dst[SEND_ROWS * SEND_COLS + i] = (byte) fillingHeader[i];
    }
    return dst;
  }

}
