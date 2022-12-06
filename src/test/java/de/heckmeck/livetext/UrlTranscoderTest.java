package de.heckmeck.livetext;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UrlTranscoderTest {

  @Test
  void parity() {
    assertBits(0b10000000, UrlTranscoder.byteWithParity(0b0000000));
    assertBits(0b00000001, UrlTranscoder.byteWithParity(0b0000001));
    assertBits(0b10000011, UrlTranscoder.byteWithParity(0b0000011));
    assertBits(0b00000111, UrlTranscoder.byteWithParity(0b0000111));
    assertBits(0b10001111, UrlTranscoder.byteWithParity(0b0001111));
    assertBits(0b00011111, UrlTranscoder.byteWithParity(0b0011111));
    assertBits(0b10111111, UrlTranscoder.byteWithParity(0b0111111));
    assertBits(0b01111111, UrlTranscoder.byteWithParity(0b1111111));
    // one bit
    assertBits(0b00000001, UrlTranscoder.byteWithParity(0b0000001));
    assertBits(0b00000010, UrlTranscoder.byteWithParity(0b0000010));
    assertBits(0b00000100, UrlTranscoder.byteWithParity(0b0000100));
    assertBits(0b00001000, UrlTranscoder.byteWithParity(0b0001000));
    assertBits(0b00010000, UrlTranscoder.byteWithParity(0b0010000));
    assertBits(0b00100000, UrlTranscoder.byteWithParity(0b0100000));
    assertBits(0b01000000, UrlTranscoder.byteWithParity(0b1000000));
    // two bits
    assertBits(0b11000001, UrlTranscoder.byteWithParity(0b1000001));
    assertBits(0b10000011, UrlTranscoder.byteWithParity(0b0000011));
    assertBits(0b10000110, UrlTranscoder.byteWithParity(0b0000110));
    assertBits(0b10001100, UrlTranscoder.byteWithParity(0b0001100));
    assertBits(0b10011000, UrlTranscoder.byteWithParity(0b0011000));
    assertBits(0b10110000, UrlTranscoder.byteWithParity(0b0110000));
    assertBits(0b11100000, UrlTranscoder.byteWithParity(0b1100000));
  }

  @Test
  void convertTo8Bit() {
    assertConvert(
        //   12345678 12345678 12345678 12345678 12345678 12345678 12345678 12345678
            "01000001 00000000 01111111 00000000 01111111 00000000 01111111 00000000",
        //   12345671 23456712 34567123 45671234 56712345 67123456 71234567
            "10000010 00000011 11111000 00001111 11100000 00111111 10000000"
    );
  }

  private void assertConvert(String expectedFormatted, String bits7) {
    var bytes7 = toByteArray(bits7);
    assertEquals(bits7.split(" ").length, bytes7.length, "internal test setup: toByteArray result");
    assertEquals(expectedFormatted, toBinaryString(toByteArray(expectedFormatted)), "internal test setup: toByteArray/toBinaryString mapping");
    var destBuf = new byte[bytes7.length*8/7];
    var bytes8 = UrlTranscoder.convertTo8Bit(bytes7, destBuf);
    var actualFormatted = toBinaryString(bytes8);
    assertEquals(expectedFormatted, actualFormatted);
  }

  private String toBinaryString(byte[] bytes) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      if (buf.length() > 0) buf.append(' ');
      var s = "0000000" + Integer.toBinaryString(bytes[i]);
      buf.append(s.substring(s.length()-8));
    }
    return buf.toString();
  }

  private byte[] toByteArray(String spaceSeparatedBits) {
    var objs = Stream.of(spaceSeparatedBits.split(" ")).map(s -> Integer.parseInt(s, 2)).collect(Collectors.toList());
    var bytes = new byte[objs.size()];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = (byte) ((int) objs.get(i));
    }
    return bytes;
  }

  private void assertBits(int a, int b) {
    assertEquals(String.format("%02x", a & 0xff), String.format("%02x", b & 0xff));
  }
}