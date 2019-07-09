package org.flussig.documentation.check;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Tests relative link
 */
public class UrlCheck extends SingleExecutionCheck {
    private static final int REQUEST_TIMEOUT_MS = 500;
    private static final String REQUEST_METHOD = "GET";

    private File readmeFile;
    private String uri;

    /**
     * extracting uri from markdown format for link
     * options:
     * direct uri: google.com
     * []() syntax: [mylink](google.com)
     * TODO: not supported now - [] syntax: [mylink] .... [mylink] = google.com
     */
    public static String extractMarkdownUri(String argument) {
        return null;
    }

    public UrlCheck(File readmeFile, String uri) {
        this.uri = uri;
        this.readmeFile = readmeFile;
    }

    @Override
    public CheckResult performCheck() {
        Path uriPath = Path.of(uri);

        if(uriPath.isAbsolute()) {
            return executeAbsolutePath();
        } else {
            return executeRelativePath();
        }
    }

    private CheckResult executeRelativePath() {
        try {
            Path testPath = Path.of(readmeFile.getPath(), uri);

            File testFile = new File(testPath.toUri());

            return testFile.exists() ? CheckResult.GREEN : CheckResult.RED;
        } catch(Exception e) {
            return CheckResult.RED;
        }
    }

    private CheckResult executeAbsolutePath() {
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(REQUEST_METHOD);
            con.setConnectTimeout(REQUEST_TIMEOUT_MS);
            con.setReadTimeout(REQUEST_TIMEOUT_MS);
            int responseCode = con.getResponseCode();

            if(responseCode > 299) {
                return CheckResult.RED;
            } else {
                return CheckResult.GREEN;
            }
        } catch(Exception e) {
            return CheckResult.RED;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlCheck that = (UrlCheck) o;
        return Objects.equals(readmeFile, that.readmeFile) &&
                Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(readmeFile, uri);
    }
}