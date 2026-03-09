FROM postgres:15.17-trixie
RUN apt update
RUN apt install -y curl wget zip unzip build-essential zlib1g-dev
RUN curl -s "https://get.sdkman.io?ci=true" | bash
SHELL ["/bin/bash", "-c"]
RUN source "/root/.sdkman/bin/sdkman-init.sh" \
  && sdk install java 25.0.2-graalce \
  && sdk install gradle
ENV POSTGRES_PASSWORD=gtransfer
ENV POSTGRES_USER=gtransfer
ENV POSTGRES_DB=gtransfer
ENTRYPOINT ["/bin/bash"]
