backend: backend_stop backend_keystore backend_build backend_run
deploy: backend_stop

backend_stop:
	-docker stop backend

backend_build:
	mvn -B -DskipTests -f ./backend package
	@docker build -t backend-server ./backend

backend_run:
	@docker run -p 8080:8080 --name backend --rm backend-server

backend_keystore:
	-mkdir backend/src/main/resources/keystore
	-rm backend/src/main/resources/keystore/*
	keytool -genkeypair -dname "cn=, ou=, o=, c=NO" -alias fullstack -storepass password -keypass password -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore backend/src/main/resources/keystore/mykeystore.p12 -validity 3650
