package ecosystem;

import authcrypt.UserData;
import authcrypt.user.EncryptedAcct;
import authcrypt.user.EncryptedPerson;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.net.ssl.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;


public class EcoPane extends BorderPane {

    WebEngine engine = new WebEngine();

    private String[] deckIds = null;
    private ArrayList<HashMap<String, String>> cartList;

    public void setDeckIds(String[] idAry) {
        this.deckIds = idAry;
    }

    public void setCartList(ArrayList<HashMap<String, String>> cartList) {
        this.cartList = cartList;
    }


    protected BorderPane getPurchasePane() {
        System.out.println("EcoPane.getEcoPane called line 24");

        setTrustManager();

        // -----------------     start webview    --------------------- //
        WebView webView = new WebView();
        engine = webView.getEngine();
        //@todo Create the method to purchase multiple decks at a time.
        if(deckIds != null) {
            engine.setUserAgent(getJson(cartList));
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

        System.out.println("EcoPane.getPurchasePane line 62");

        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("EcoPane.getPurchasePane getting page called line 65. newValue: " + newValue);

            if(Worker.State.SUCCEEDED.equals(newValue)) {
                System.out.println("getPurchasePane worker.state succeeded!");
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
                System.out.println("failed");
                System.out.println("location after load: " + engine.getLocation());
                System.out.println("EcoPane. line 108 failed to contact server.");
            }
        });

        VBox rBox = new VBox(webView);
        //rBox.setMaxHeight(DeckMarketPane.getInstance().getMarketPane(). getBoundsInLocal().getHeight());
        BorderPane bPane = new BorderPane();

        /*
       String onboardScript = "fetch(\"/101/-603024299\", {\n" +
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

        rBox.maxHeightProperty().bind(DeckMarketPane.getInstance().getMarketPane().heightProperty().subtract(70));
        rBox.minHeightProperty().bind(DeckMarketPane.getInstance().getMarketPane().heightProperty().subtract(70));
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

            System.out.println("getOnboardPane() getting engine.LoadWorker called line 149");

            if(Worker.State.SUCCEEDED.equals(newValue)) {
                System.out.println("getOnboardPane worker.state succeeded!");

                System.out.println("location after load: " + engine.getLocation());

                //@todo finish, prevent redirects from potential malicious actors
            }
            else {
                System.out.println("EcoPane.getOnboardPane line 159 failed to contact server.");
                System.out.println("EcoPane.getOnboardPane line 160 newValue: <" + newValue + "> & SUCCEEDED was: <" + Worker.State.SUCCEEDED + ">");
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
    private String getJson( ArrayList<HashMap<String, String>> cartList) {
        //@TODO set this for an array of items
        HashMap<String, String> map = cartList.get(0);

        String json = "{" +
                ",\"user_name\":\"" + UserData.getUserName() + "\"" +
                ",\"deck_id\":\"" + map.get("deck_id") + "\"" +
                "}";

        System.out.println("Json looks like: " + json);
        return json;
    }
}