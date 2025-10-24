FROM confluentinc/cp-kafka-connect:7.7.0

# Create plugin dirs
USER root
RUN mkdir -p /usr/share/java/debezium-postgres /usr/share/java/opensearch-sink

# 1) Debezium PG plugin (official plugin tar.gz)
#   version can be bumped; keep the *-plugin.tar.gz archive form
ADD https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/2.6.2.Final/debezium-connector-postgres-2.6.2.Final-plugin.tar.gz /tmp/debezium-pg.tar.gz
RUN tar -xzf /tmp/debezium-pg.tar.gz -C /usr/share/java/debezium-postgres && rm -f /tmp/debezium-pg.tar.gz

# 2) Confluent OpenSearch sink (download a fixed version zip)
#   If direct download is blocked, host the zip internally and replace the URL.
ADD https://d1i4a15mxbxib1.cloudfront.net/api/plugins/confluentinc/kafka-connect-opensearch/14.0.14/confluentinc-kafka-connect-opensearch-14.0.14.zip /tmp/opensearch-sink.zip
RUN unzip -q /tmp/opensearch-sink.zip -d /usr/share/java/opensearch-sink && rm -f /tmp/opensearch-sink.zip

# Ensure plugin path includes both
ENV CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/confluent-hub-components,/usr/share/java/debezium-postgres,/usr/share/java/opensearch-sink"

USER appuser