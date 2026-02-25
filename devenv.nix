{ pkgs, lib, config, inputs, ... }:

{
  # https://devenv.sh/basics/
  env.GRAALVM_HOME = "${pkgs.graalvmPackages.graalvm-ce}";

  # https://devenv.sh/packages/
  packages = [
    pkgs.graalvmPackages.graalvm-ce
    pkgs.watchexec
  ];

  # https://devenv.sh/languages/
  languages.java.enable = true;
  languages.java.lsp.enable = true;
  languages.java.gradle.enable = true;
  languages.java.jdk.package = pkgs.jdk25_headless;
  # https://devenv.sh/processes/
  # processes.cargo-watch.exec = "cargo-watch";
  # process.manager.implementation = "mprocs";
  processes.watchbuild = {
    exec = "watchexec -r -e java,html,css,js -w ./Backend/src -- build-backend";
  };
  processes.runbin = {
    exec = "watchexec -r -w ./Backend/buildcompleted.at ./Backend/build/native/nativeCompile/gtransfer";
  };
  # https://devenv.sh/services/
  services.postgres.enable = true;
  services.postgres.listen_addresses = "localhost";
  services.postgres.port = 5432;
  services.postgres.initialDatabases = [
    {name="gtransfer";user="gtransfer";pass="gtransfer";}
  ];

  # https://devenv.sh/scripts/
  scripts.build-backend.exec = ''
    gradle -p ./Backend build && echo $(date) > ./Backend/buildcompleted.at
  '';

  enterShell = ''
  '';

  # https://devenv.sh/tasks/
  # tasks = {
  #   "myproj:setup".exec = "mytool build";
  #   "devenv:enterShell".after = [ "myproj:setup" ];
  # };

  # https://devenv.sh/tests/
  enterTest = ''
  '';

  # https://devenv.sh/git-hooks/
  # git-hooks.hooks.shellcheck.enable = true;

  # See full reference at https://devenv.sh/reference/options/
}
