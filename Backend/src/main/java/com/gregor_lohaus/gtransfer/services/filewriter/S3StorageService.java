package com.gregor_lohaus.gtransfer.services.filewriter;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.BucketLocationConstraint;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class S3StorageService extends AbstractStorageService {
  private final S3Client client;
  private final String bucket;
  private final String prefix;
  private final String region;

  public S3StorageService(
      String bucket,
      String region,
      String prefix,
      String endpoint,
      String accessKeyId,
      String secretAccessKey,
      boolean pathStyleAccessEnabled) {
    super(Path.of(""));
    if (bucket == null || bucket.isBlank()) {
      throw new IllegalArgumentException("S3 storage requires a bucket");
    }
    this.bucket = bucket;
    this.region = blankToDefault(region, "us-east-1");
    this.prefix = normalizePrefix(prefix);
    this.client = createClient(this.region, endpoint, accessKeyId, secretAccessKey, pathStyleAccessEnabled);
    ensureBucketExists();
  }

  private S3Client createClient(
      String region,
      String endpoint,
      String accessKeyId,
      String secretAccessKey,
      boolean pathStyleAccessEnabled) {
    S3ClientBuilder builder = S3Client.builder()
        .region(Region.of(blankToDefault(region, "us-east-1")))
        .serviceConfiguration(S3Configuration.builder()
            .pathStyleAccessEnabled(pathStyleAccessEnabled)
            .build());

    if (endpoint != null && !endpoint.isBlank()) {
      builder.endpointOverride(URI.create(endpoint));
    }

    if (accessKeyId != null && !accessKeyId.isBlank()) {
      if (secretAccessKey == null || secretAccessKey.isBlank()) {
        throw new IllegalArgumentException("S3 storage requires a secretAccessKey when accessKeyId is set");
      }
      builder.credentialsProvider(
          StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)));
    } else {
      builder.credentialsProvider(DefaultCredentialsProvider.create());
    }

    return builder.build();
  }

  private void ensureBucketExists() {
    try {
      client.headBucket(HeadBucketRequest.builder()
          .bucket(bucket)
          .build());
      return;
    } catch (S3Exception e) {
      if (e.statusCode() != 404) {
        throw e;
      }
    }

    try {
      client.createBucket(createBucketRequest());
    } catch (BucketAlreadyOwnedByYouException e) {
      return;
    } catch (BucketAlreadyExistsException e) {
      throw e;
    }
  }

  private CreateBucketRequest createBucketRequest() {
    CreateBucketRequest.Builder builder = CreateBucketRequest.builder()
        .bucket(bucket);

    if (!region.equals("us-east-1")) {
      builder.createBucketConfiguration(CreateBucketConfiguration.builder()
          .locationConstraint(BucketLocationConstraint.fromValue(region))
          .build());
    }

    return builder.build();
  }

  private String blankToDefault(String value, String defaultValue) {
    if (value == null || value.isBlank()) {
      return defaultValue;
    }
    return value;
  }

  private String normalizePrefix(String value) {
    if (value == null || value.isBlank()) {
      return "";
    }
    String normalized = value;
    while (normalized.startsWith("/")) {
      normalized = normalized.substring(1);
    }
    while (normalized.endsWith("/")) {
      normalized = normalized.substring(0, normalized.length() - 1);
    }
    return normalized;
  }

  private String key(String id) {
    if (prefix.isEmpty()) {
      return id;
    }
    return prefix + "/" + id;
  }

  @Override
  public OptionalLong put(String id, byte[] data) {
    try {
      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucket)
          .key(key(id))
          .contentLength((long) data.length)
          .build();
      client.putObject(request, RequestBody.fromBytes(data));
      return OptionalLong.of(data.length);
    } catch (SdkException e) {
      return OptionalLong.empty();
    }
  }

  @Override
  public Optional<byte[]> get(String id) {
    try {
      GetObjectRequest request = GetObjectRequest.builder()
          .bucket(bucket)
          .key(key(id))
          .build();
      ResponseBytes<GetObjectResponse> response = client.getObjectAsBytes(request);
      return Optional.of(response.asByteArray());
    } catch (NoSuchKeyException e) {
      return Optional.empty();
    } catch (SdkException e) {
      return Optional.empty();
    }
  }

  @Override
  public boolean delete(String id) {
    try {
      DeleteObjectRequest request = DeleteObjectRequest.builder()
          .bucket(bucket)
          .key(key(id))
          .build();
      client.deleteObject(request);
      return true;
    } catch (SdkException e) {
      return false;
    }
  }
}
