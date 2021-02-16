package ecosystem;

import authcrypt.UserData;
import authcrypt.user.EncryptedAcct;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.net.ssl.*;
import java.security.GeneralSecurityException;


public class EcoPane extends BorderPane {

    WebEngine engine = new WebEngine();

    private String[] deckIds = null;

    public void setDeckIds(String[] idAry) {
        this.deckIds = idAry;
    }


    protected BorderPane getPurchasePane() {
        System.out.println("EcoPane.getEcoPane called line 24");

        setTrustManager();

        // -----------------     start webview    --------------------- //
        WebView webView = new WebView();
        engine = webView.getEngine();
        //@todo Create the method to purchase multiple decks at a time.
        if(deckIds != null) {
            engine.setUserAgent(getJson(deckIds[0]));
        } else {
            // throw error
        }

        System.out.println("EcoPane line 68");

        // Load from local file.
        // Works to load first page, but following pages
        // need to come from the server.
        //	File f = new File(getClass().getClassLoader().getResource("java/main/index.html").getFile());
        //	engine.load(f.toURI().toString());
// @TODO change engine.load from local server to remote
        // Load web page from remote
        engine.load(VertxLink.REQ_PURCHASE.getLink());

        System.out.println("EcoPane line 80");

        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {

            System.out.println("getLoadWorker called line 86");

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
                //@todo finish prevent redirects from potential malicious actors

                // before load address check

                /*
				if (!engine.locationProperty().equals(("http://jackRabbits.com"))) {
					System.out.println("Not a jackrabbit");
				}
				if (!engine.getLocation().equals("http://wrongLocation.com")) {
					System.out.println("Wrong location");
					System.exit(0);
				}

                 */
            }
            else {
                System.out.println("EcoPane. line 108 failed to contact server.");
            }
        });

        VBox rBox = new VBox(webView);
        //rBox.setMaxHeight(DeckMarketPane.getInstance().getMarketPane(). getBoundsInLocal().getHeight());
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

        VBox lBox = new VBox();
        lBox.setMinSize(100, 600);
        lBox.setAlignment(Pos.CENTER);
        bPane.setRight(rBox);
        bPane.setLeft(lBox);


        rBox.maxHeightProperty().bind(DeckMarketPane.getInstance().getMarketPane().heightProperty().subtract(120));
        rBox.minHeightProperty().bind(DeckMarketPane.getInstance().getMarketPane().heightProperty().subtract(120));
    //    rBox.setMaxHeight(320);
        rBox.setMaxWidth(400);
        rBox.setId("payPane");

        return bPane;
    }


    protected BorderPane getOnboardPane() {
        System.out.println("EcoPane getOnboardPane called");
        setTrustManager();
        // -----------------     start webview    --------------------- //
        WebView webView = new WebView();


        engine = webView.getEngine();
        engine.load(VertxLink.ONBOARD.getLink());

        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {

            System.out.println("getLoadWorker called line 86");

            if(Worker.State.SUCCEEDED.equals(newValue)) {
                System.out.println("worker.state succeeded!");

                System.out.println("location after load: " + engine.getLocation());

                //@todo finish, prevent redirects from potential malicious actors
            }
            else {
                System.out.println("EcoPane. line 108 failed to contact server.");
            }
        });

        VBox rBox = new VBox(webView);
        BorderPane bPane = new BorderPane();

        VBox lBox = new VBox();
        lBox.setMinSize(100, 600);
        lBox.setAlignment(Pos.CENTER);
        bPane.setRight(rBox);
        bPane.setLeft(lBox);

        return bPane;
    }


    // ***** OTHER ***** //

    private void setTrustManager() {
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
    }

    //@TODO set getJson to real data
    private String getJson(String deckId) {
        EncryptedAcct acct = DeckMarketPane.getInstance().getAcct();
        String name     = "Jenny Rosen"; // let the user input this information in stripe.
        String userName = UserData.getUserName();
        //String deck_id  = "2";
        String currency = acct.getCurrency();

        String json = "{" +
                "\"real_name\":\"" + name + "\"" +
                ",\"user_name\":\"" + userName + "\"" +
                ",\"deck_id\":\"" + deckId + "\"" +
                ",\"currency\":\"" + currency + "\"" +
                "}";

        System.out.println("Json looks like: " + json);
        return json;
    }
}