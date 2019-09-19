package com.github.flussig.dacdoc.check;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * Tests relative link
 */
public class UrlCheck extends SingleExecutionCheck {
    private static final int REQUEST_TIMEOUT_MS = 5000;
    private static final String REQUEST_METHOD = "GET";
    private static Pattern mdUrlPattern = Pattern.compile(String.format("\\[(.*?)\\]\\((.*?)\\)"));
    private static X509TrustManager trustAllManager = new X509TrustManager() {
        public void checkClientTrusted(
            X509Certificate[] chain, String authType) throws CertificateException {}
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }};

    /**
     * extracting uri from markdown format for link
     * options:
     * direct uri: google.com
     * []() syntax: [mylink](google.com)
     * TODO: not supported now - [] syntax: [mylink] .... [mylink] = google.com
     */
    private static String extractMarkdownUri(String argument) {
        Matcher matcher = mdUrlPattern.matcher(argument);

        // [...](...)
        if(matcher.matches()) {
            return matcher.group(2);
        } else {
            return argument;
        }
    }

    public UrlCheck(String argument, File file) {
        super(argument, file);
    }

    @Override
    public CheckResult performCheck() {
        try {
            String uri = extractMarkdownUri(argument);

            URI parsedUri = URI.create(uri);

            if(parsedUri.isAbsolute()) {
                return executeAbsolutePath(uri);
            } else {
                return executeRelativePath(uri);
            }
        } catch(Exception e) {
            return new CheckResult(e.getMessage(), LocalDateTime.now(), CheckStatus.RED);
        }
    }

    private CheckResult executeRelativePath(String uri) {
        try {
            Path testPath = Paths.get(file.getParentFile().getPath(), uri);

            File testFile = new File(testPath.toUri());

            return testFile.exists() ?
                    new CheckResult("", LocalDateTime.now(), CheckStatus.GREEN) :
                    new CheckResult("", LocalDateTime.now(), CheckStatus.RED);
        } catch(Exception e) {
            return new CheckResult(e.getMessage(), LocalDateTime.now(), CheckStatus.RED);
        }
    }

    private CheckResult executeAbsolutePath(String uri) {
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(REQUEST_METHOD);
            con.setConnectTimeout(REQUEST_TIMEOUT_MS);
            con.setReadTimeout(REQUEST_TIMEOUT_MS);

            if(con instanceof HttpsURLConnection) {
                ((HttpsURLConnection)con).setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null,
                    new X509TrustManager[] { trustAllManager },
                    new SecureRandom());
                ((HttpsURLConnection)con).setSSLSocketFactory(context.getSocketFactory());
            }

            int responseCode = con.getResponseCode();

            if(responseCode > 299) {
                return new CheckResult("", LocalDateTime.now(), CheckStatus.RED);
            } else {
                return new CheckResult("", LocalDateTime.now(), CheckStatus.GREEN);
            }
        } catch(Exception e) {
            return new CheckResult(e.getMessage(), LocalDateTime.now(), CheckStatus.RED);
        }
    }
}
