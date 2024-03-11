# Description
This repository was created with the purpose of testing and learning about Spring Boot.

## Used Course
All the code in this repository (apart from some deprecated instances which were adapted to the most rencent version of Spring Boot in 2024) can be replicated by watching [this course](https://youtube.com/playlist?list=PL62G310vn6nFBIxp6ZwGnm8xMcGE3VA5H&si=m0bq-nTYjjGHUywE).

## Credits
_Spring Boot Essentials 2_ is a free course created by William Suane, owner of the YouTube channel [DevDojo](https://www.youtube.com/@DevDojoBrasil). All credits to him.

## Install
```bash
git clone https://github.com/AlbertAlvin8080/Spring-Boot-Essentials-2.git
```

```bash
cd Spring-Boot-Essentials-2
```

## Running the app
Run this docker command in the folder where the docker-compose file is located:
```bash
docker-compose up --build
```

Put this address into your browser:
- Username: albert
- Password: 1234
```bash
http://localhost:8080/swagger-ui/index.html
```

Now try to make some requests.

## Other functionalities
Prometheus is running at:
```bash
http://localhost:9090
```

Grafana is at:
```bash
http://localhost:3000
```

You can use them to see metadata related to the Spring app.
