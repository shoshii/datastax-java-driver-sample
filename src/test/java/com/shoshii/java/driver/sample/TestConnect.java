package com.shoshii.java.driver.java.sample;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.cql.*;

import java.time.Duration;
import java.net.InetSocketAddress;
import java.io.*;
import java.util.*;

import com.datastax.oss.driver.internal.core.auth.PlainTextAuthProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

public class TestConnect {
    private static CqlSession session;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        try {
            session = connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void tearDown() {
        if (session != null) {
            session.close();
        }
    }

    @Test
    public void testHello() {
        System.out.println("hello!");
    }

    @Test
    public void testSelect() throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            ResultSet rs = session.execute("select release_version from system.local");
            //  Extract the first row (which is the only one in this case).
            Row row = rs.one();

            // Extract the value of the first (and only) column from the row.
            Assert.assertFalse(row == null);
            String releaseVersion = row.getString("release_version");
            System.out.printf("%s Cassandra version is: %s%n", i, releaseVersion);
            Thread.sleep(1000);
        }
    }

    private CqlSession connect() {
        // 接続先
        String[] contact_points = {"cassandra-1", "cassandra-2", "cassandra-3"};
        //String[] contact_points = {"127.0.0.1", "127.0.0.2", "127.0.0.3"};

        List<InetSocketAddress> socketAddresses = new ArrayList<>();
        for (String contact_point : contact_points) {
            socketAddresses.add(new InetSocketAddress(contact_point, 9042));
        }

        // 設定ファイル読み込み
        // https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/configuration/reference/
        DriverConfigLoader loader =
                DriverConfigLoader.programmaticBuilder()
                    .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(30))
                    .startProfile("slow")
                    .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(30))
                    .endProfile()
                    .build();
        session = CqlSession.builder().addContactPoints(socketAddresses)
                .withAuthCredentials("cassandra", "cassandra")
                .withLocalDatacenter("datacenter1")
                .withKeyspace(CqlIdentifier.fromCql("system"))
                .withConfigLoader(loader)
                .build();
        return session;
    }
}