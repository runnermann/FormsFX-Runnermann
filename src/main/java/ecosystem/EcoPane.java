package ecosystem;

import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.web.WebErrorEvent;


import javax.net.ssl.*;
import java.security.GeneralSecurityException;



public class EcoPane extends BorderPane {

    private BorderPane main;

    protected BorderPane getEcoPane() {
        // @TODO remove self signed certificate
        // @TODO remove self signed certificate
        // @TODO remove self signed certificate
        // @TODO remove self signed certificate
        // @TODO remove self signed certificate
        // ---------- for self signed cert... do  not use in deployed system ------------
        // Create a trust manager that does not validate certificate chains

        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        // create all trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        System.out.println("EcoPane set trustEverything Succeeded");


        // ----------------- end self signed cert --------------------- //
        // -----------------     start webview    --------------------- //
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        System.out.println("EcoPane line 66");

        // Load from local file.
        // Works to load first page, but following pages
        // need to come from the server.
        //	File f = new File(getClass().getClassLoader().getResource("java/main/index.html").getFile());
        //	engine.load(f.toURI().toString());

        // Load web page from remote
        engine.load("https://www.flashmonkey.xyz/101/1013549384");
        //engine.load("https://localhost:8080/1013549384");

        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if(Worker.State.SUCCEEDED.equals(newValue)) {
                System.out.println("worker.state succeeded!");
                // verify address is the correct address and there has not been
                // a redirect.
                // if(notRedirect) {
                // do some stuff;
                //} else {
                // reportASAP
                // }

                System.out.println("location after load: " + engine.getLocation());

                // before load address check

				/*if (!engine.locationProperty().equals(("http://jackRabbits.com"))) {
					System.out.println("Not a jackrabbit");
				}
				if (!engine.getLocation().equals("http://wrongLocation.com")) {
					System.out.println("Wrong location");
					System.exit(0);
				}*/
            }
        });

        VBox rBox = new VBox(webView);
        BorderPane bPane = new BorderPane();

       /* String onboardScript = "fetch(\"/-603024299\", {\n" +
                "                method: \"POST\",\n" +
                "                headers: {\n" +
                "                    \"Content-Type\": \"application/json\"\n" +
                "                }\n" +
                "            })\n" +
                "                .then(response => response.json())\n" +
                "                .then(data => {\n" +
                "                    if (data.url) {\n" +
                "                        window.location = data.url;\n" +
                "                    } else {\n" +
                "                        console.log(\"data\", data);\n" +
                "                    }\n" +
                "                });";

        */

        Button purchaseBtn = new Button("buy");
        purchaseBtn.setOnAction(e -> {
            String url = "https:/101/www.flashmonkey.xyz//-1780717326";

            //engine.executeScript(onboardScript);
            //engine.reload();
            engine.load(url);
        });


        VBox lBox = new VBox(purchaseBtn);
        lBox.setMinSize(100, 600);
        lBox.setAlignment(Pos.CENTER);
        bPane.setRight(rBox);
        bPane.setLeft(lBox);

        return bPane;
    }

}
