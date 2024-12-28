FROM ubuntu:18.04

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Asia/Tokyo

RUN apt-get update \
 && apt-get install -y tzdata

RUN  apt-get update \
  && apt-get install -y git wget openjdk-17-jdk-headless
RUN  apt-get update \
  && apt-get install -y ruby

RUN wget https://github.com/pinterest/ktlint/releases/download/0.49.0/ktlint \
  && chmod a+x ktlint \
  && mv ktlint /usr/local/bin/

CMD ["/bin/bash"]
