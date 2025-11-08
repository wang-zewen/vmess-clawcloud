FROM ubuntu:22.04

RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    uuid-runtime \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY start.sh .
RUN chmod +x start.sh

EXPOSE 20041

CMD ["./start.sh"]
