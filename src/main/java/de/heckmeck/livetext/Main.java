package de.heckmeck.livetext;

import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileOutputStream;
import java.net.Socket;

public class Main {
  public static void main(String... args) throws Exception {
    UrlTranscoder transcoder = new UrlTranscoder();
    if (true) {
      byte[] buf = transcoder.transcodeFromUrl("https://zxnet.co.uk/teletext/editor/#0:QIECBAgQIECBAgQIECBAgQIECBAgQIECBAgQIECBAgQIECAIqKGlCBggQoMCBAgQIEgRUURIMBkj2QNEBFtqQFDWBAEVIChpgkQIFCBAiQKAioooQIECBAiMkUSvsgwEUX9MUNJECBCkCHS4hAgQIECBAgQIECBAgEoECBARQZPfRMo9dXCBAgQIECAIdLiP_9Ag__0CBB____6ASgQIECDhwI62mhuXwcGCBAgQIAh0uI__0CD__QIP_9Ag__xKBAgQIFPL6R__i_hO5QoECBAgCHS4j______9Ag__0CD__EoECBAgQKN-fnv__-yBAgQIECAIdLiP_9Ag__0CD__QHv_9AJQIECDQ_a__bX___sECBAgQIAh0uI__0CD__QIEH____oBKBAgQIES_T25_v__-lQIECBAgCHS6BAgQIECBAgQIECBAgQIEBHR_-l169f___8_RggQIECAoaQoGARUUSGSODge4cC_h50QEUS8mTT_y_7P8____RggCKihpQgQIDJHh8_f__89_L_X_9AEVHyaz8_Tl-2bPv___6RAgCKjRRQZIrl_______7cuiRoUJQ0wQoECIyXVd8mfe_TlDWEogDIC-jv___Pmz58efu_0oaSIECBAgCKkBkvnSIUJQ0hQIChpQZLpl6_9v_r161evzsUJQ0hQJEKBAgRKECBAgCKiihAiCHUBdAg8L1nz5n5u_7r__eIECBAgQIECBAgQIECBAgQIECAIdQF0GLT_9tkv_v_xc3R0or_nhH_-JEf_4n-I___5Q6gQIAh1AX8b-y9f5-q1-f932nSmv-eEf_4kR__iRH_-JEfyh1AgCHUBfx_1P9X_-1X7_7s6UU9P54R______-JEf_4kR_KHUCAIdQF1G1v--8-f7_91_dZ0o6_nhH_-JEf_4kR__iRH8odQIAh1AX0f__Qjs-PC_j8_-nSm_-eEf_4kR__if4j___lDqBAgCHUBf___6__z5-_99_13_d7WCBAgQIECBAgQIECBAgQIECAIgL6P6dD-__2v___bf_7JSEVFDSBCwQJECBQgYIUCgIqKMAioujQINH9-nwP1K9CiXoyhpggUBFRRAiQMAiookQIUCRBgQIECBAgQIECBAgQIECBAgQIECBAgQIECBAgQIECBAgQIECA:PS=0:RE=0:zx=BA0");
      System.err.println("transcoded: " + buf.length);

      try (var socket = new Socket("192.168.178.100", 2000)) {
        try (var out = socket.getOutputStream()) {
          for (int i = 0; i < 10; i++) {
            out.write(buf);
            out.flush();
            System.err.println("wrote buf to " + socket);
            try (var fout = new FileOutputStream("sent.bin")) {
              fout.write(buf);
            }
            Thread.sleep(200);
          }
        }
      }
      return;
    }
    System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
    ChromeDriver driver = new ChromeDriver();
    driver.get("https://zxnet.co.uk/teletext/editor/");
    for (;;) {
      var url = driver.getCurrentUrl();
      System.err.println(url);
      Thread.sleep(500L);
    }
  }

}
