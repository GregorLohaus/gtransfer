package com.gregor_lohaus.gtransfer.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "files")
public class File {
  protected File() {}

  @Id
  private String id;
  private String path;
  private String name;
  private LocalDateTime expireyDateTime;
  private Integer downloadLimit;
  @Column(columnDefinition = "integer default 0")
  private int downloads = 0;
  public LocalDateTime getExpireyDateTime() {
	return expireyDateTime;
}
  public void setExpireyDateTime(LocalDateTime expireyDateTime) {
	this.expireyDateTime = expireyDateTime;
  }
  public String getId() {
	return id;
}
  public void setId(String id) {
	this.id = id;
  }
  public String getPath() {
	return path;
  }
  public void setPath(String path) {
	this.path = path;
  }
  public String getName() {
	return name;
  }
  public void setName(String name) {
	this.name = name;
  }
  public Integer getDownloadLimit() {
    return downloadLimit;
  }
  public void setDownloadLimit(Integer downloadLimit) {
    this.downloadLimit = downloadLimit;
  }
  public int getDownloads() {
    return downloads;
  }
  public void setDownloads(int downloads) {
    this.downloads = downloads;
  }
  public File(String id, String path, String name, LocalDateTime expDateTime) {
    this.path = path;
    this.name = name;
    this.id = id;
    this.expireyDateTime = expDateTime;
  }
}
