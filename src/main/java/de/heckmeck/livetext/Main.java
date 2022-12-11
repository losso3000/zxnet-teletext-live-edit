package de.heckmeck.livetext;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Main {

  private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
  private static String DEFAULT_URL = "https://zxnet.co.uk/teletext/editor/#0:QIEDFgwQIAPrxuy9FuzT2yrcuTT038kDFcwQBGLN0zZumbcmdUBECBAgQIKmXZl6ZfHRBM09sqCLk09N_JAgQIEBNoeInCKJGjRo0aNGjRo0aNGjRo0aNGjRo0aNGjRo0aNGjRo0aNGhLoECBAgQIECBAgQIECAkgwMECDggQIC6BAgLoECBAgQIECAug1f_____9-_aBAgQICSDf26dvyVAgLoECAvoeLFizY8WNC6DUgQaGCBAg1E0CBggIoHXF3xdsCaBAgYIC-pA4SIEHfm1LoNSBBq_pECDUTULNyQiqar-LtK_JoFCzckL6kCBAgQPVjUug1IECJEtQINRNAgQoCKD86_v9TQmgQIEKAvq6cOHLx14NS6BUsWLFixYsXIECBAgIoESFYkRoECAugQIC6BAgQIECBAgQIEELlv788vJAgQIECClh58MWXly8oECBAgQIECCpWQIECBAgQIECBAgQIECBAgQIECBAgQIECBAgQIECBAgQIECBAgQIAnrxuy9F2Peu661_TLsy9Mvjovy5NPTfyDQLViP1y8-eXkgCTMPXdj0ZeSxBVpTEHTlh3c8e_Jp3Z0CANA2b-fPezYMGCBAgQIECBAgQIECBAgQIECBAgQIECBAgQIECBAgQIECBAgQICZ1QEQIECBAgQIECBBR66cetBT6YeXRAgQIECBAgQE2h4icIokaNGjRo0aNGjRo0aNGjRo0aNGjRo0aNGjRo0aNGjRo0aFAtQU8vRB00ZUGHhw5b-HLTh6ZUGvL5xb8PLIg2YfO_r0QIEC1BF3YcWzKgRc9G_ugx793Tlv2IMe_Jl5okGHdkQIuejf3QIEGflpyIkHTeg2b9-tB13ZMvJB00ZUGjfvyIECBAgQIECBAtQS8vnFvw8siDHv27cO7JzfoKeXKgRY9-7py37EGvL55okCBBww7suxCgQRacNYg6aMu5AxQIECBAgQPUHLLkQdMvjogQIECBAgQIECBBFpw1iDpoy7kFORJjVFrFA9QcsuRBnzeECBAgQIECBAgQIEGXpjXIECBAgQIECBAgQIECBAgQIECBAgQIEC1BZ39UGPDuQYdnfD55oOvPKgh1KUxbaQZt_JB13ZN6FAgQIECBAgQIECBAgQIECBAgQIECBAgQIECBAgQIECBAgQIECA:PS=0:RE=0:zx=BI0";

  public static void main(String... args) throws Exception {
    nimbus();
    new MainUI().showInFrame();
  }

  static enum DriverType {
    Chrome,
    Firefox,
    Safari,
    Edge,
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
      JComboBox<DriverType> driver = new JComboBox<>(DriverType.values());
      JButton launchButton = new JButton("<html><b>Launch!");

      setLayout(new BorderLayout());
      SpringLayout layout = new SpringLayout();
      JPanel configPanel = new JPanel(layout);

      URL logoUrl = getClass().getResource("/logo.png");
      if (logoUrl != null) {
        add(new JLabel(new ImageIcon(logoUrl)), BorderLayout.NORTH);
      }

      layout.putConstraint(SpringLayout.WEST, host, 5, SpringLayout.WEST, this);
      layout.putConstraint(SpringLayout.WEST, port, 5, SpringLayout.EAST, host);
      layout.putConstraint(SpringLayout.EAST, configPanel, 5, SpringLayout.EAST, port);

      layout.putConstraint(SpringLayout.NORTH, hostLabel, 5, SpringLayout.NORTH, configPanel);
      layout.putConstraint(SpringLayout.NORTH, portLabel, 5, SpringLayout.NORTH, configPanel);

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

      layout.putConstraint(SpringLayout.SOUTH, configPanel, 5, SpringLayout.SOUTH, launchButton);

      configPanel.add(host);
      configPanel.add(hostLabel);
      configPanel.add(port);
      configPanel.add(portLabel);
      configPanel.add(urlLabel);
      configPanel.add(url);
      configPanel.add(driverLabel);
      configPanel.add(driver);
      configPanel.add(launchButton);
      add(configPanel, BorderLayout.CENTER);

      host.setText(getPref("host", "127.0.0.1"));
      port.setText(getPref("port", "2000"));
      url.setText(getPref("editor-url", DEFAULT_URL));

      launchButton.addActionListener(e -> {
        storePref("host", host.getText());
        storePref("port", port.getText());
        storePref("editor-url", url.getText());
        flushPrefs();
        new Thread(() -> launch(host.getText(), port.getText(), url.getText(), (DriverType) driver.getSelectedItem())).start();
      });
    }

    public void showInFrame() {
      JFrame frame = new JFrame("zxnet-teletext-live-edit");
      Optional.ofNullable(getClass().getResource("/icon.png")).ifPresent(url -> {
        try {
          frame.setIconImage(new ImageIcon(url).getImage());
        } catch (Exception e) {
          // EMPTY
        }
      });
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

  static void launch(String host, String port, String editorUrl, DriverType type) {
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
    WebDriver driver;
    try {
      driver = createWebDriver(type);
    } catch (Exception e) {
      error(String.format("Error creating web driver for %s: %s", type, e));
      return;
    }
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
          Thread.sleep(1500L);
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

  static WebDriver createWebDriver(DriverType type) {
    switch (type) {
      case Chrome:
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver();
      case Firefox:
        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver();
      case Safari:
        WebDriverManager.safaridriver().setup();
        return new SafariDriver();
      case Edge:
        WebDriverManager.edgedriver().setup();
        return new EdgeDriver();
      default:
        throw new IllegalArgumentException("Not supported: " + type);
    }
  }

  static void error(String message) {
    log("Error: %s", message);
    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
  }
}
