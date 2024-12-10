FROM tomee:10-jre17-webprofile

# Устанавливаем переменные окружения для PostgreSQL (опционально)
ENV POSTGRES_HOST=db
ENV POSTGRES_PORT=5432
ENV POSTGRES_DB=postgres_webapp
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres

ADD ojdbc11-23.3.0.23.09.jar /usr/local/tomee/lib/

# Копируем готовый .war файл в TomEE
COPY build/libs/Web3_TomEE-1.0-SNAPSHOT.war /usr/local/tomee/webapps/

# Копируем конфигурацию для TomEE
COPY src/main/resources/META-INF/tomee.xml /usr/local/tomee/conf/
COPY src/main/resources/META-INF/context.xml /usr/local/tomee/conf/

# Expose порт TomEE
EXPOSE 8080

# Старт TomEE
CMD ["catalina.sh", "run"]