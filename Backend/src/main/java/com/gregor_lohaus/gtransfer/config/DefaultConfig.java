package com.gregor_lohaus.gtransfer.config;

import java.nio.file.Path;

import com.gregor_lohaus.gtransfer.config.types.Config;
import com.gregor_lohaus.gtransfer.config.types.DataSourceConfig;
import com.gregor_lohaus.gtransfer.config.types.JpaConfig;
import com.gregor_lohaus.gtransfer.config.types.MultipartConfig;
import com.gregor_lohaus.gtransfer.config.types.ServerConfig;
import com.gregor_lohaus.gtransfer.config.types.ServletConfig;
import com.gregor_lohaus.gtransfer.config.types.SpringConfig;
import com.gregor_lohaus.gtransfer.config.types.SslConfig;
import com.gregor_lohaus.gtransfer.config.types.StorageService;
import com.gregor_lohaus.gtransfer.config.types.StorageServiceType;
import com.gregor_lohaus.gtransfer.config.types.UploadConfig;

public class DefaultConfig {
  public static final Config config;
  static {
    Config c = new Config();

    StorageService ss = new StorageService();
    ss.type = StorageServiceType.LOCAL;
    ss.path = Path.of(System.getProperty("user.home"),".local","share","gtransfer").toString();
    c.storageService= ss;
    
    SpringConfig sc = new SpringConfig();
    DataSourceConfig dsc = new DataSourceConfig();
    dsc.password = "gtransfer";
    dsc.url = "jdbc:postgresql://localhost:5432/gtransfer";
    dsc.username = "gtransfer";
    sc.dataSourceConfig = dsc;

    JpaConfig jc = new JpaConfig();
    jc.ddlAuto = "update";
    jc.dialect = "org.hibernate.dialect.PostgreSQLDialect";
    jc.showSql = true;
    sc.jpaConfig = jc;

    MultipartConfig mc = new MultipartConfig();
    mc.maxFileSize = "10GB";
    mc.maxRequestSize = "10GB";
    ServletConfig svc = new ServletConfig();
    svc.multipartConfig = mc;
    sc.servletConfig = svc;

    c.springConfig = sc;

    ServerConfig svc2 = new ServerConfig();
    svc2.port = 8080;
    SslConfig ssl = new SslConfig();
    ssl.enabled = false;
    svc2.sslConfig = ssl;
    c.serverConfig = svc2;

    UploadConfig uc = new UploadConfig();
    uc.maxDownloadLimit = 100;
    uc.maxExpiryDays = 30;
    uc.cleanupEnabled = true;
    c.uploadConfig = uc;

    config = c;
  }
}
