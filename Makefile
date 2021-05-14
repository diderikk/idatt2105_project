backend: backend_stop backend_keystore backend_build backend_run

backend_stop:
	-docker stop backend

backend_build:
	mvn -B -f ./backend package
	@docker build -t backend-server ./backend

backend_run:
	@docker run -p 8080:8080 --name backend --rm backend-server

backend_keystore:
	-mkdir backend/src/main/resources/keystore
	-rm backend/src/main/resources/keystore/*
	keytool -genkeypair -dname "cn=, ou=, o=, c=NO" -alias fullstack -storepass password -keypass password -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore backend/src/main/resources/keystore/mykeystore.p12 -validity 3650
	
	# keytool -export -keystore backend/src/main/resources/keystore/mykeystore.p12 -alias fullstack -file backend/src/main/resources/keystore/certificate.crt -storepass password
	# openssl pkcs12 -in backend/src/main/resources/keystore/mykeystore.p12 -nodes -nocerts -out backend/src/main/resources/keystore/private.key -password pass:password
	# openssl rsa -in backend/src/main/resources/keystore/private.key -out backend/src/main/resources/keystore/nopassword.key
	# cat backend/src/main/resources/keystore/nopassword.key > backend/src/main/resources/keystore/server.pem
	# cat backend/src/main/resources/keystore/certificate.crt >> backend/src/main/resources/keystore/server.pem
