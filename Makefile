dev: backend_stop backend_keystore backend_build backend_run
prod: backend_stop backend_build backend_run
deploy: backend_stop

backend_stop:
	-docker stop backend

backend_build:
	mvn -B -DskipTests -f ./backend package
	@docker build -t backend-server ./backend

backend_run:
	@docker run -p 8443:8443 --name backend --rm backend-server

backend_keystore:
	-mkdir backend/src/main/resources/keystore
	-rm backend/src/main/resources/keystore/*
	keytool -genkeypair -dname "cn=, ou=, o=, c=NO" -alias fullstack -storepass password -keypass password -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore backend/src/main/resources/keystore/cert_key.p12 -validity 3650

frontend_serve:
	@docker build -t frontend-server ./frontend
	@docker run -p 3000:3000 --name frontend --rm frontend-server