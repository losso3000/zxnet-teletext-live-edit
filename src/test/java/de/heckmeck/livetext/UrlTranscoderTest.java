package de.heckmeck.livetext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlTranscoderTest {

  @Test
  void parity() {
    assertEquals(0b00000000, UrlTranscoder.byteWithParity(0b0000000));
    assertEquals(0b10000001, UrlTranscoder.byteWithParity(0b0000001));
    assertEquals(0b00000011, UrlTranscoder.byteWithParity(0b0000011));
    assertEquals(0b10000111, UrlTranscoder.byteWithParity(0b0000111));
    assertEquals(0b00001111, UrlTranscoder.byteWithParity(0b0001111));
    assertEquals(0b10011111, UrlTranscoder.byteWithParity(0b0011111));
    assertEquals(0b00111111, UrlTranscoder.byteWithParity(0b0111111));
    assertEquals(0b11111111, UrlTranscoder.byteWithParity(0b1111111));
    // one bit
    assertEquals(0b10000001, UrlTranscoder.byteWithParity(0b0000001));
    assertEquals(0b10000010, UrlTranscoder.byteWithParity(0b0000010));
    assertEquals(0b10000100, UrlTranscoder.byteWithParity(0b0000100));
    assertEquals(0b10001000, UrlTranscoder.byteWithParity(0b0001000));
    assertEquals(0b10010000, UrlTranscoder.byteWithParity(0b0010000));
    assertEquals(0b10100000, UrlTranscoder.byteWithParity(0b0100000));
    assertEquals(0b11000000, UrlTranscoder.byteWithParity(0b1000000));
    // two bits
    assertEquals(0b01000001, UrlTranscoder.byteWithParity(0b1000001));
    assertEquals(0b00000011, UrlTranscoder.byteWithParity(0b0000011));
    assertEquals(0b00000110, UrlTranscoder.byteWithParity(0b0000110));
    assertEquals(0b00001100, UrlTranscoder.byteWithParity(0b0001100));
    assertEquals(0b00011000, UrlTranscoder.byteWithParity(0b0011000));
    assertEquals(0b00110000, UrlTranscoder.byteWithParity(0b0110000));
    assertEquals(0b01100000, UrlTranscoder.byteWithParity(0b1100000));
  }
}