package flashmonkey.utility;

import java.io.*;
import java.util.HashMap;

public abstract class VersionTimeStamp {


    private static final InnerOps<String> innerOps = new InnerOps<>();

    public static String getVersionBuildStamp() {
        //Dotenv dotenv = Dotenv.configure().load();

        String ver = innerOps.get("VERSION");
        String stamp = innerOps.get("BUILD_DATE");

        return "VERSION:  " + ver + "\nBUILD DATE:  " + stamp;
    }

    private static class InnerOps<T> {

        HashMap<String, String> valueMap = new HashMap<>();

        String get(String key) {
            if (valueMap.size() == 0) {
                buildEnvMap();
            }
            return valueMap.get(key);
        }

        void buildEnvMap() {
            String fileName = "version.env";
            //if (new File("/" + fileName).exists()) {

            try (BufferedInputStream input = new BufferedInputStream(getClass().getResourceAsStream("/" + fileName))) {
                String[] sAry = new String[2];
                String key = "";
                String val = "";
                //System.out.println("File exists: /" + fileName);
                StringBuilder sb = new StringBuilder();
                byte[] buffer = new byte[1024];

                int bytes = 0;
                String fileContent;

                while ((bytes = input.read(buffer)) != -1) {
                    fileContent = new String(buffer, 0, bytes);
                    sb.append(fileContent);
                }

                String[] nAry = sb.toString().split("\n");
                for (String s : nAry) {
                    sAry = s.split("=");
                    key = sAry[0];
                    val = sAry[1];
                    valueMap.put(key, val);
                }
            } catch (EOFException e) {
                // expected. do nothing
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
