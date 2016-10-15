package com.github.bingoohuang.sqlbus.utils;

import com.google.common.io.CharStreams;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Scanner;

import static com.google.common.base.Charsets.UTF_8;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/15.
 */
public class SqlBusUtils {
    static String hostname;

    static {
        hostname = initHostname();
    }

    public static String getHostname() {
        return hostname;
    }

    private static String initHostname() {
        try {
            return StringUtils.trim(execReadToString("hostname"));
        } catch (Exception e) {
            // ignore
        }

        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Throwable ex) {
            // ignore
        }

        throw new RuntimeException("unable to get hostname");
    }

    @SneakyThrows
    public static String execReadToString(String execCommand) {
        Process proc = Runtime.getRuntime().exec(execCommand);
        @Cleanup InputStream stream = proc.getInputStream();

        @Cleanup Scanner s = new Scanner(stream).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        return result;
    }

    public static String runShellScript(String shellScript) {
        return executeCommandLine(new String[]{"/bin/bash", "-c", shellScript});
    }

    @SneakyThrows
    public static String executeCommandLine(String[] cmd) {
        Process p = Runtime.getRuntime().exec(cmd);
        p.waitFor();

        Readable r = new InputStreamReader(p.getInputStream(), UTF_8);
        return CharStreams.toString(r);
    }
}
