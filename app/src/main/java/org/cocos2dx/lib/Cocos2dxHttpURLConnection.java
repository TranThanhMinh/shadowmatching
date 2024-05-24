package org.cocos2dx.lib;

import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import cz.msebera.android.httpclient.cookie.ClientCookie;
import cz.msebera.android.httpclient.protocol.HTTP;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/* loaded from: classes2.dex */
public class Cocos2dxHttpURLConnection {
    private static final String POST_METHOD = "POST";
    private static final String PUT_METHOD = "PUT";

    static HttpURLConnection createHttpURLConnection(String linkURL) {
        try {
            URL url = new URL(linkURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept-Encoding", HTTP.IDENTITY_CODING);
            urlConnection.setDoInput(true);
            return urlConnection;
        } catch (Exception e) {
            Log.e("URLConnection exception", e.toString());
            return null;
        }
    }

    static void setReadAndConnectTimeout(HttpURLConnection urlConnection, int readMiliseconds, int connectMiliseconds) {
        urlConnection.setReadTimeout(readMiliseconds);
        urlConnection.setConnectTimeout(connectMiliseconds);
    }

    static void setRequestMethod(HttpURLConnection urlConnection, String method) {
        try {
            urlConnection.setRequestMethod(method);
            if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) {
                urlConnection.setDoOutput(true);
            }
        } catch (ProtocolException e) {
            Log.e("URLConnection exception", e.toString());
        }
    }

    static void setVerifySSL(HttpURLConnection urlConnection, String sslFilename) {
        InputStream caInput;
        if (!(urlConnection instanceof HttpsURLConnection)) {
            return;
        }
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;
        try {
            if (sslFilename.startsWith("/")) {
                caInput = new BufferedInputStream(new FileInputStream(sslFilename));
            } else {
                String assetsfilenameString = sslFilename.substring("assets/".length());
                caInput = new BufferedInputStream(Cocos2dxHelper.getActivity().getAssets().open(assetsfilenameString));
            }
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            caInput.close();
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            httpsURLConnection.setSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            Log.e("URLConnection exception", e.toString());
        }
    }

    static void addRequestHeader(HttpURLConnection urlConnection, String key, String value) {
        urlConnection.setRequestProperty(key, value);
    }

    static int connect(HttpURLConnection http) {
        try {
            http.connect();
            return 0;
        } catch (IOException e) {
            Log.e("cocos2d-x debug info", "come in connect");
            Log.e("cocos2d-x debug info", e.toString());
            return 1;
        }
    }

    static void disconnect(HttpURLConnection http) {
        http.disconnect();
    }

    static void sendRequest(HttpURLConnection http, byte[] byteArray) {
        try {
            OutputStream out = http.getOutputStream();
            if (byteArray != null) {
                out.write(byteArray);
                out.flush();
            }
            out.close();
        } catch (IOException e) {
            Log.e("URLConnection exception", e.toString());
        }
    }

    static String getResponseHeaders(HttpURLConnection http) {
        Map<String, List<String>> headers = http.getHeaderFields();
        if (headers == null) {
            return null;
        }
        String header = "";
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            header = key == null ? header + listToString(entry.getValue(), ",") + "\n" : header + key + ":" + listToString(entry.getValue(), ",") + "\n";
        }
        return header;
    }

    static String getResponseHeaderByIdx(HttpURLConnection http, int idx) {
        Map<String, List<String>> headers = http.getHeaderFields();
        if (headers == null) {
            return null;
        }
        int counter = 0;
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (counter == idx) {
                String key = entry.getKey();
                if (key == null) {
                    String header = listToString(entry.getValue(), ",") + "\n";
                    return header;
                }
                String header2 = key + ":" + listToString(entry.getValue(), ",") + "\n";
                return header2;
            }
            counter++;
        }
        return null;
    }

    static String getResponseHeaderByKey(HttpURLConnection http, String key) {
        Map<String, List<String>> headers;
        if (key == null || (headers = http.getHeaderFields()) == null) {
            return null;
        }
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (key.equalsIgnoreCase(entry.getKey())) {
                if ("set-cookie".equalsIgnoreCase(key)) {
                    String header = combinCookies(entry.getValue(), http.getURL().getHost());
                    return header;
                }
                String header2 = listToString(entry.getValue(), ",");
                return header2;
            }
        }
        return null;
    }

    static int getResponseHeaderByKeyInt(HttpURLConnection http, String key) {
        String value = http.getHeaderField(key);
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    static byte[] getResponseContent(HttpURLConnection http) {
        InputStream in;
        try {
            in = http.getInputStream();
            String contentEncoding = http.getContentEncoding();
            if (contentEncoding != null) {
                if (contentEncoding.equalsIgnoreCase(AsyncHttpClient.ENCODING_GZIP)) {
                    in = new GZIPInputStream(http.getInputStream());
                } else if (contentEncoding.equalsIgnoreCase("deflate")) {
                    in = new InflaterInputStream(http.getInputStream());
                }
            }
        } catch (IOException e) {
            in = http.getErrorStream();
        } catch (Exception e2) {
            Log.e("URLConnection exception", e2.toString());
            return null;
        }
        try {
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            while (true) {
                int size = in.read(buffer, 0, 1024);
                if (size != -1) {
                    bytestream.write(buffer, 0, size);
                } else {
                    byte[] retbuffer = bytestream.toByteArray();
                    bytestream.close();
                    return retbuffer;
                }
            }
        } catch (Exception e3) {
            Log.e("URLConnection exception", e3.toString());
            return null;
        }
    }

    static int getResponseCode(HttpURLConnection http) {
        try {
            int code = http.getResponseCode();
            return code;
        } catch (IOException e) {
            Log.e("URLConnection exception", e.toString());
            return 0;
        }
    }

    static String getResponseMessage(HttpURLConnection http) {
        try {
            return http.getResponseMessage();
        } catch (IOException e) {
            String msg = e.toString();
            Log.e("URLConnection exception", msg);
            return msg;
        }
    }

    public static String listToString(List<String> list, String strInterVal) {
        if (list == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String str : list) {
            if (flag) {
                result.append(strInterVal);
            }
            if (str == null) {
                str = "";
            }
            result.append(str);
            flag = true;
        }
        return result.toString();
    }

    public static String combinCookies(List<String> list, String hostDomain) {
        Iterator<String> it;
        String str;
        StringBuilder sbCookies = new StringBuilder();
        String domain = hostDomain;
        String path = "/";
        String secure = "FALSE";
        String key = null;
        String value = null;
        String expires = null;
        Iterator<String> it2 = list.iterator();
        while (it2.hasNext()) {
            String str2 = it2.next();
            String[] parts = str2.split(";");
            int length = parts.length;
            int i = 0;
            while (i < length) {
                String part = parts[i];
                int firstIndex = part.indexOf("=");
                if (-1 == firstIndex) {
                    it = it2;
                    str = str2;
                } else {
                    it = it2;
                    String[] item = {part.substring(0, firstIndex), part.substring(firstIndex + 1)};
                    str = str2;
                    if (ClientCookie.EXPIRES_ATTR.equalsIgnoreCase(item[0].trim())) {
                        expires = str2Seconds(item[1].trim());
                    } else if (ClientCookie.PATH_ATTR.equalsIgnoreCase(item[0].trim())) {
                        path = item[1];
                    } else if (ClientCookie.SECURE_ATTR.equalsIgnoreCase(item[0].trim())) {
                        secure = item[1];
                    } else if (ClientCookie.DOMAIN_ATTR.equalsIgnoreCase(item[0].trim())) {
                        domain = item[1];
                    } else if (!ClientCookie.VERSION_ATTR.equalsIgnoreCase(item[0].trim()) && !"max-age".equalsIgnoreCase(item[0].trim())) {
                        key = item[0];
                        value = item[1];
                    }
                }
                i++;
                str2 = str;
                it2 = it;
            }
            Iterator<String> it3 = it2;
            if (domain == null) {
                domain = "none";
            }
            sbCookies.append(domain);
            sbCookies.append('\t');
            sbCookies.append("FALSE");
            sbCookies.append('\t');
            sbCookies.append(path);
            sbCookies.append('\t');
            sbCookies.append(secure);
            sbCookies.append('\t');
            sbCookies.append(expires);
            sbCookies.append("\t");
            sbCookies.append(key);
            sbCookies.append("\t");
            sbCookies.append(value);
            sbCookies.append('\n');
            it2 = it3;
        }
        return sbCookies.toString();
    }

    private static String str2Seconds(String strTime) {
        Calendar c = Calendar.getInstance();
        long milliseconds = 0;
        try {
            c.setTime(new SimpleDateFormat("EEE, dd-MMM-yy hh:mm:ss zzz", Locale.US).parse(strTime));
            milliseconds = c.getTimeInMillis() / 1000;
        } catch (ParseException e) {
            Log.e("URLConnection exception", e.toString());
        }
        return Long.toString(milliseconds);
    }
}
