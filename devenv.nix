{ pkgs, lib, config, inputs, ... }:

{
  env.GRAALVM_HOME = "${pkgs.graalvmPackages.graalvm-ce}";

  packages = [
    pkgs.graalvmPackages.graalvm-ce
    pkgs.watchexec
  ];

  languages.java.enable = true;
  languages.java.lsp.enable = true;
  languages.java.gradle.enable = true;
  languages.java.jdk.package = pkgs.jdk25_headless;
  processes.watchbuild = {
    exec = "build-backend";
    watch = {
      paths = [./Backend/src];
      extensions = ["java"  "html"  "css"  "js"];
    };
  };
  processes.runbin = {
    exec = "./Backend/build/native/nativeCompile/gtransfer";
    watch = {
      paths = [ ./Backend/buildcompleted.at ];
    };
  };
  services.postgres.enable = true;
  services.postgres.listen_addresses = "localhost";
  services.postgres.port = 5432;
  services.postgres.initialDatabases = [
    {name="gtransfer";user="gtransfer";pass="gtransfer";}
  ];
  services.minio.enable = true;
  scripts.build-backend.exec = ''
    gradle -p ./Backend build && echo $(date) > ./Backend/buildcompleted.at
  '';

  enterTest = ''
  '';
}
