package com.shoshii.java.driver.java.sample;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.cql.*;

import java.time.Duration;
import java.util.MissingResourceException;
import java.net.InetSocketAddress;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

public class TestConnect {
    private static String contact_point;
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
        session.close();
    }

    @Test
    public void testHello() {
        System.out.println("hello!");
    }

    @Test
    public void testSelect() {
        ResultSet rs = session.execute("select release_version from system.local");
        //  Extract the first row (which is the only one in this case).
        Row row = rs.one();

        // Extract the value of the first (and only) column from the row.
        Assert.assertFalse(row == null);
        String releaseVersion = row.getString("release_version");
        System.out.printf("Cassandra version is: %s%n", releaseVersion);
    }

    private CqlSession connect() {
        try {
            contact_point = System.getProperty("contact_point", "192.168.100.202");
            System.out.println("contact_point:" + contact_point);
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
        if (contact_point.isEmpty()) {
            contact_point = "192.168.100.202";
        }
        DriverConfigLoader loader =
                DriverConfigLoader.programmaticBuilder()
                    .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(30))
                    .startProfile("slow")
                    .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(30))
                    .endProfile()
                    .build();
        session = CqlSession.builder().addContactPoint(new InetSocketAddress(contact_point, 9042))
                .withLocalDatacenter("datacenter1")
                .withKeyspace(CqlIdentifier.fromCql("system"))
                .withConfigLoader(loader)
                .build();
        return session;
    }
}