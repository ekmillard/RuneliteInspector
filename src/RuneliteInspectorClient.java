import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;

/**
 * Simple launcher for the RuneLite Event Inspector maintained by
 * Polar <a href="https://github.com/Joshua-F">(Github)</a> and Kris <a href="https://github.com/z-kris">(Github)</a>
 *
 * See README for instructions on using the Jagex Launcher
 *
 * @author Ethan Ave {@literal <github.com/e-ave>}
 * @author Ethan Millard {@literal <github.com/ekmillard>}
 * @see <a href="https://github.com/ekmillard/RuneliteInspector">Forked from</a>
 */
public class RuneliteInspectorClient {

    private static final String DOWNLOAD_URL = "https://media.z-kris.com/runelite-event-inspector-client.jar";
    private static final String RL_DIR = System.getProperty("user.home") + File.separator + ".runelite";
    private static final String DOWNLOAD_DIR = System.getProperty("user.home") + File.separator + ".runelite_inspector_clients";
    private static final String FILENAME = "RuneLite.jar";
    private static final String[] LAUNCHER_VARS = {"JX_ACCESS_TOKEN", "JX_REFRESH_TOKEN", "JX_SESSION_ID", "JX_CHARACTER_ID", "JX_DISPLAY_NAME"};

    public static void main(String[] args) {
        try {

            // create download directory if it doesn't exist
            File dir = new File(DOWNLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // download the JAR file
            downloadJar();

            // check if the JAR file on the website is different than the one downloaded
            if (isJarDifferent()) {
                // replace the JAR file with the latest version
                replaceJar();
            }

            // run the JAR file
            runJar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadJar() throws IOException {
        System.out.println("Downloading JAR file...");

        URL url = new URL(DOWNLOAD_URL);
        Path path = Paths.get(DOWNLOAD_DIR, FILENAME);

        try (InputStream in = url.openStream()) {
            Files.copy(in, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static boolean isJarDifferent() throws IOException {
        System.out.println("Checking JAR file for updates...");

        URL url = new URL(DOWNLOAD_URL);
        Path path = Paths.get(DOWNLOAD_DIR, FILENAME);

        // calculate MD5 hash of the JAR file on the website
        String webHash = calculateMD5(url.openStream());

        // calculate MD5 hash of the downloaded JAR file
        String localHash = calculateMD5(Files.newInputStream(path.toFile().toPath()));

        // compare the hashes
        return !webHash.equals(localHash);
    }

    private static void replaceJar() throws IOException {
        System.out.println("Replacing JAR file...");

        URL url = new URL(DOWNLOAD_URL);
        Path path = Paths.get(DOWNLOAD_DIR, FILENAME);

        try (InputStream in = url.openStream()) {
            Files.copy(in, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String calculateMD5(InputStream is) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                md.update(buffer, 0, read);
            }
            byte[] md5 = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : md5) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setEnvironmentVariables(Map<String, String> env) {
        Properties props = getLauncherVars();
        for (String varName : LAUNCHER_VARS) {
            String value = getLauncherVar(props, varName);
            if(!value.isEmpty()) {
                env.put(varName, value);
                System.out.println("Set environment variable " + varName + "=" + value+" before launching client.");
            }
        }

    }

    public static String getLauncherVar(Properties launcherVariables, final String s) {
        String property = System.getenv().get(s);
        if (property == null) {
            property = launcherVariables.getProperty(s);
        }
        return property;
    }

    /**
     * Method from the RuneLite injected client that loads the credentials properties from the .runelite home directory.
     * The credentials are later set to the environment variables that the client uses to log in from the JX launcher.
     *
     * @return The Properties table containing the login information.
     */
    public static Properties getLauncherVars() {
        File credentialsFile = new File(RL_DIR + File.separator, System.getProperty("runelite.credentials.path", "credentials.properties"));
        Properties launcherVariables = new Properties();
        if (launcherVariables.isEmpty() && credentialsFile.exists()) {
            try {
                final FileInputStream fileInputStream = new FileInputStream(credentialsFile);
                try {
                    final InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                    try {
                        launcherVariables.load(inputStreamReader);
                        inputStreamReader.close();
                    } catch (Throwable t) {
                        try {
                            inputStreamReader.close();
                        }
                        catch (Throwable t2) {
                            t.addSuppressed(t2);
                        }
                        throw t;
                    }
                    fileInputStream.close();
                } catch (Throwable t3) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable t4) {
                        t3.addSuppressed(t4);
                    }
                    throw t3;
                }
            } catch (IOException ex) {
                System.err.println("unable to load credentials from disk"+ ex);
            }
            if (launcherVariables.size() > 0) {
                System.out.println("read "+launcherVariables.size()+" credentials from disk");
            }
        }
        return launcherVariables;
    }

    private static void runJar() throws IOException {

        Path path = Paths.get(DOWNLOAD_DIR, FILENAME);
        System.out.println("Running JAR file... " + path.toString());
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", path.toString());
        setEnvironmentVariables(pb.environment());
        pb.directory(new File(DOWNLOAD_DIR));
        System.out.println("Starting client...");
        pb.start();
    }
}
