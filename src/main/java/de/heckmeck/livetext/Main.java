package de.heckmeck.livetext;

import org.openqa.selenium.chrome.ChromeDriver;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Main {

  private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
  private static String DEFAULT_URL = "https://zxnet.co.uk/teletext/editor/";

  public static void main(String... args) throws Exception {
    nimbus();
    new MainUI().showInFrame();
  }

  static class MainUI extends JPanel {
    public MainUI() {
      JLabel hostLabel = new JLabel("Host");
      JLabel portLabel = new JLabel("Port");
      JLabel urlLabel = new JLabel("Editor URL");
      JLabel driverLabel = new JLabel("Web driver");
      JTextField host = new JTextField(20);
      JTextField port = new JTextField(6);
      JTextField url = new JTextField(26);
      JComboBox<String> driver = new JComboBox<>("Chrome,Firefox".split(","));
      JButton launchButton = new JButton("<html><b>Launch!");

      SpringLayout layout = new SpringLayout();
      setLayout(layout);
      // add(url);
      layout.putConstraint(SpringLayout.WEST, host, 5, SpringLayout.WEST, this);
      layout.putConstraint(SpringLayout.WEST, port, 5, SpringLayout.EAST, host);
      layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST, port);

      layout.putConstraint(SpringLayout.NORTH, hostLabel, 5, SpringLayout.NORTH, this);
      layout.putConstraint(SpringLayout.NORTH, portLabel, 5, SpringLayout.NORTH, this);

      layout.putConstraint(SpringLayout.WEST, hostLabel, 2, SpringLayout.WEST, host);
      layout.putConstraint(SpringLayout.WEST, portLabel, 2, SpringLayout.WEST, port);
      layout.putConstraint(SpringLayout.NORTH, host, 0, SpringLayout.SOUTH, hostLabel);
      layout.putConstraint(SpringLayout.NORTH, port, 0, SpringLayout.SOUTH, hostLabel);

      layout.putConstraint(SpringLayout.NORTH, urlLabel, 5, SpringLayout.SOUTH, host);
      layout.putConstraint(SpringLayout.NORTH, url, 0, SpringLayout.SOUTH, urlLabel);
      layout.putConstraint(SpringLayout.WEST, urlLabel, 0, SpringLayout.WEST, hostLabel);
      layout.putConstraint(SpringLayout.WEST, url, 0, SpringLayout.WEST, host);
      layout.putConstraint(SpringLayout.EAST, url, 0, SpringLayout.EAST, port);

      layout.putConstraint(SpringLayout.NORTH, driverLabel, 5, SpringLayout.SOUTH, url);
      layout.putConstraint(SpringLayout.NORTH, driver, 0, SpringLayout.SOUTH, driverLabel);
      layout.putConstraint(SpringLayout.NORTH, launchButton, 0, SpringLayout.SOUTH, driverLabel);
      layout.putConstraint(SpringLayout.WEST, driverLabel, 0, SpringLayout.WEST, hostLabel);
      layout.putConstraint(SpringLayout.WEST, driver, 0, SpringLayout.WEST, host);
      layout.putConstraint(SpringLayout.EAST, driver, 0, SpringLayout.EAST, host);
      layout.putConstraint(SpringLayout.WEST, launchButton, 0, SpringLayout.WEST, port);
      layout.putConstraint(SpringLayout.EAST, launchButton, 0, SpringLayout.EAST, port);

      layout.putConstraint(SpringLayout.SOUTH, this, 5, SpringLayout.SOUTH, launchButton);

      add(host);
      add(hostLabel);
      add(port);
      add(portLabel);
      add(urlLabel);
      add(url);
      add(driverLabel);
      add(driver);
      add(launchButton);

      host.setText(getPref("host", "127.0.0.1"));
      port.setText(getPref("port", "2000"));
      url.setText(getPref("url", DEFAULT_URL));

      launchButton.addActionListener(e -> {
        storePref("host", host.getText());
        storePref("port", port.getText());
        storePref("url", url.getText());
        flushPrefs();
        new Thread(() -> launch(host.getText(), port.getText(), url.getText())).start();
      });
    }

    public void showInFrame() {
      JFrame frame = new JFrame("zxnet-teletext-live-edit");
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.setContentPane(this);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    }
  }

  static void nimbus() {
    Arrays.stream(UIManager.getInstalledLookAndFeels()).filter(laf -> "Nimbus".equals(laf.getName())).findFirst().map(UIManager.LookAndFeelInfo::getClassName).ifPresent(className -> {
      try {
        UIManager.setLookAndFeel(className);
      } catch (Exception e) {
        log("Nimbus look and feel unavailable (%s)", e);
      }
    });
  }

  static void log(String message, Object... args) {
    System.out.printf("%s | %s%n", dateTimeFormatter.format(LocalDateTime.now()), String.format(message, args));
  }

  static String getPref(String key, String defaultValue) {
    return Preferences.userRoot().get(key, defaultValue);
  }

  static void storePref(String key, String value) {
    Preferences.userRoot().put(key, value);
  }

  static void flushPrefs() {
    try {
      Preferences.userRoot().flush();
    } catch (BackingStoreException e) {
      log("Error flushing preferences: %s", e);
    }
  }

  static void launch(String host, String port, String editorUrl) {
    int portInt;
    try {
      portInt = Integer.parseInt(port);
    } catch (NumberFormatException e) {
      error("Port is not a number: " + port);
      return;
    }
    Socket socket;
    OutputStream out;
    try {
      socket = new Socket(host, portInt);
      out = socket.getOutputStream();
    } catch (IOException e) {
      error(String.format("Error connecting to %s port %d: %s", host, portInt, e));
      return;
    }
    UrlTranscoder transcoder = new UrlTranscoder();
    System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
    ChromeDriver driver = new ChromeDriver();
    driver.get(editorUrl);
    for (;;) {
      try {
        var url = driver.getCurrentUrl();
        byte[] buf = transcoder.transcodeFromUrl(url);
        for (int i = 0; i < 5; i++) {
          out.write(buf);
          out.flush();
          out.write(transcoder.createFillingHeader(buf));
          out.flush();
        }
      } catch (Exception e) {
        log("Error: %s", e);
        if (e instanceof IOException) {
          try {
            socket.close();
          } catch (Exception e2) {
            log("Additional exception on socket close, ignoring (%s)", e2);
          }
          try {
            socket = new Socket(host, portInt);
            out = socket.getOutputStream();
          } catch (IOException e2) {
            error(String.format("Error re-connecting to %s port %d: %s", host, portInt, e2));
            return;
          }
        }
        try {
          Thread.sleep(1000L);
        } catch (InterruptedException ex) {
          // EMPTY
        }
      }
      try {
        Thread.sleep(500L);
      } catch (InterruptedException e) {
        // EMPTY
      }
    }
  }

  static void error(String message) {
    log("Error: %s", message);
    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
  }
}
