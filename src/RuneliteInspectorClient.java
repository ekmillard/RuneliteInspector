import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * @author Ethan Millard
 * @see <a href="https://www.rune-server.ee/members/264022-akimbo-azure/">Rune-Server profile</a>}
 */
public class RuneliteInspectorClient {

    private static final String DOWNLOAD_URL = "https://media.z-kris.com/runelite-event-inspector-client.jar";
    private static final String DOWNLOAD_DIR = System.getProperty("user.home") + File.separator + ".runelite_inspector_clients";
    private static final String FILENAME = "RuneLite.jar";

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

    private static void runJar() throws IOException {
        System.out.println("Running JAR file...");

        Path path = Paths.get(DOWNLOAD_DIR, FILENAME);
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", path.toString());
        pb.directory(new File(DOWNLOAD_DIR));
        pb.start();
    }
}
