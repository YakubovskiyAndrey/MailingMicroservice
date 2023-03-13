docker build --build-arg VERSION_ID=1 -t mailing_service/app:0.0.1 .

docker run -it -d --env-file=.env -p8080:8080 mailing_service/app:0.0.1

docker-compose up --build --detach